import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PartidaFacade implements Partida {

    private final int players;
    private Lock l;
    private boolean winner;
    private int attempts;
    private final long start;
    private final int value;

    public PartidaFacade(int n) {
        this.players = n;
        this.l = new ReentrantLock();
        this.winner = false;
        this.attempts = 100;
        this.start = System.currentTimeMillis();
        Random rand = new Random();
        this.value = rand.nextInt(100) + 1;
    }

    public int numeroJogadores() {
        return this.players;
    }

    public String adivinha(int n) {
        String out = "";

        final int end = 60 * 1000;
        if ((System.currentTimeMillis() - this.start) >= end)
            return "TEMPO";
        if (n < this.value)
            out = "MAIOR";
        else
            out = "MENOR";

        this.l.lock();

        if (this.attempts > 0) {
            this.attempts--;
            if (this.winner)
                out = "PERDEU";
            else if (n == this.value) {
                this.winner = true;
                out = "GANHOU";
            }
        } else
            out = "TENTATIVAS";

        this.l.unlock();

        return out;
    }
}
