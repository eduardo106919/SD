import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Manager {

    private static class Player {

        String name;
        Raid raid = null;
        int minPlayers;

        Player(String n, int m) {
            name = n;
            minPlayers = m;
        }

    }

    private List<Player> waiting;
    private Lock l;
    private Condition c;
    private final int R;

    private List<Raid> activeRaids;
    private Lock lr;
    private Condition cr;

    Manager(int R) {
        this.R = R;
        this.waiting = new ArrayList<>();
        this.l = new ReentrantLock();
        this.c = l.newCondition();

        this.activeRaids = new ArrayList<>();
        this.lr = new ReentrantLock();
        this.cr = this.lr.newCondition();
    }

    Raid join(String name, int minPlayers) throws InterruptedException {
        l.lock();
        try {
            Player p = new Player(name, minPlayers);
            waiting.add(p);
            while (p.raid == null) {
                formRaid();
                c.await();
            }

            return p.raid;
        } finally {
            l.unlock();
        }

    }

    void formRaid() {
        int maxMin = 0;
        List<Player> group = new ArrayList<>();

        for (Player wp : waiting) {
            group.add(wp);
            maxMin = Math.max(maxMin, wp.minPlayers);
            if (group.size() >= maxMin) {
                // form raid
                List<String> names = new ArrayList<>();
                for (Player p : group)
                    names.add(p.name);

                Raid raid = new Raid(names, this);
                for (Player p : group)
                    p.raid = raid;

                waiting.removeAll(group);
                c.signalAll();
                return;
            }
        }
    }

    void waitStartRaid(Raid r) throws InterruptedException {
        lr.lock();
        try {
            if (activeRaids.size() < R) {
                r.on = true;
                activeRaids.add(r);
                cr.signalAll();
            } else {
                while (!r.on)
                    cr.await();
            }
        } finally {
            lr.unlock();
        }
    }

    void leaveRaid(Raid r) {
        lr.lock();
        try {
            activeRaids.remove(r);
            cr.signalAll();
        } finally {
            lr.unlock();
        }
    }

}
