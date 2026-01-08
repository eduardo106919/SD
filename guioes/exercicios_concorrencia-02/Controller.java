import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Controller {

    private static final int T = 10;

    private final Lock lock = new ReentrantLock(true);
    private final Condition cond = lock.newCondition();

    private int currentResource = -1;   // -1 = none
    private int active = 0;

    private final int[] waiting = new int[2];
    private int turn = 0;

    public int request_resource(int i) {
        lock.lock();
        try {
            waiting[i]++;

            while (
                    // outro recurso a ser consumido
                    (currentResource != -1 && currentResource != i) ||
                    // capacidade cheia
                    active == T ||
                    // existem threads à espera do outro recurso e é a sua vez
                    (currentResource == i && waiting[1 - i] > 0 && turn != i)
            ) {
                cond.await();
            }

            waiting[i]--;

            currentResource = i;
            active++;

            return i;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void release_resource(int i) {
        lock.lock();
        try {
            active--;

            if (active == 0) {
                currentResource = -1;

                if (waiting[1 - i] > 0) {
                    turn = 1 - i;
                }
            }

            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
