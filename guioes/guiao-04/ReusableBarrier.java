import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReusableBarrier {

    private int N;
    private int current;
    private int round;
    private Lock l;
    private Condition cond;

    public ReusableBarrier(int N) {
        this.current = 0;
        this.round = 0;
        this.N = N;
        this.l = new ReentrantLock();
        this.cond = l.newCondition();
    }

    void await() throws InterruptedException {
        try {
            l.lock();
            int currentRound = this.round;

            current++;

            if (current < N) {
                while (this.round == currentRound)
                    cond.await();
            } else {
                this.round++;
                current = 0;
                cond.signalAll();
            }

        } finally {
            l.unlock();
        }

    }

}
