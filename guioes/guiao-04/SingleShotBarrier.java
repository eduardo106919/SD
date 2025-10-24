import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class SingleShotBarrier {

    private int N;
    private int current;
    private Lock l;
    private Condition cond;

    public SingleShotBarrier(int N) {
        this.current = 0;
        this.N = N;
        this.l = new ReentrantLock();
        this.cond = l.newCondition();
    }

    void await() throws InterruptedException {
        try {
            l.lock();

            current++;

            if (current < N) {
                while (current < N)
                    cond.await();
            } else
                cond.signalAll();

        } finally {
            l.unlock();
        }

    }

}
