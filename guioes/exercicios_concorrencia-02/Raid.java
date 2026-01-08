import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Raid {

    int count;
    static Lock l;
    final List<String> names;
    boolean on;
    Manager manager;

    Raid(List<String> ns, Manager m) {
        names = ns;
        this.count = ns.size();
        on = false;
        l = new ReentrantLock();
        manager = m;
    }

    List<String> players() {
        return names;
    }

    void turnOn() {
        l.lock();
        on = true;
        l.unlock();
    }

    void turnOff() {
        l.lock();
        on = false;
        l.unlock();
    }

    void waitStart() throws InterruptedException {
        manager.waitStartRaid(this);
    }

    void leave() {
        l.lock();
        try {
            count--;
            if (count == 0)
                manager.leaveRaid(this);
        } finally {
            l.unlock();
        }
    }

}
