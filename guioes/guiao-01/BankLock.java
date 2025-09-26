import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankLock {

    private Lock l;
    private Bank b;

    public BankLock(Bank b) {
        this.b = b;
        this.l = new ReentrantLock();
    }

    public void depositLock(int value) {
        l.lock();
        b.deposit(value);
        l.unlock();
    }

    public int balanceLock() {
        try {
            l.lock();
            return b.balance();
        } finally {
            l.unlock();
        }
    }

}
