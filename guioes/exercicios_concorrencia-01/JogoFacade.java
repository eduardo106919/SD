import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JogoFacade implements Jogo {

    private Lock l;
    private Condition waitForPlayers;
    private int players;
    private Partida p;
    private int round;


    public JogoFacade() {
        this.l = new ReentrantLock();
        this.waitForPlayers = this.l.newCondition();
        this.players = 0;
        this.round = 0;
    }

    public Partida participa() {
        this.l.lock();
        try {
            int currentRound = this.round;
            this.players++;

            if (this.players < 6) {

                // first player
                if (this.players == 1) {
                    try {
                        this.waitForPlayers.await(2, TimeUnit.MINUTES);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // if there are at least 4 players after two minutes
                    if (this.players >= 4 && this.players < 6) {
                        this.round++;
                        this.players = 0;
                        this.p = new PartidaFacade(this.players);
                        this.waitForPlayers.signalAll();
                        return this.p;
                    }
                }

                // wait for all 6 players
                while (this.round == currentRound)
                    this.waitForPlayers.await();

            } else {
                this.round++;
                this.players = 0;
                this.p = new PartidaFacade(this.players);
                this.waitForPlayers.signalAll();
            }

            return this.p;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            this.l.unlock();
        }
    }

}
