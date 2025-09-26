
public class Increment implements Runnable {

    private long limit;

    public Increment(long limit) {
        this.limit = limit;
    }

    public void run() {
        for (long i = 1; i <= limit; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + i);
        }
    }

}
