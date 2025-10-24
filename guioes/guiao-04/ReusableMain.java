

public class ReusableMain {

    private static class Operation implements Runnable {

        private ReusableBarrier b;

        public Operation(ReusableBarrier b) {
            this.b = b;
        }

        public void run() {
            System.out.println("[" + Thread.currentThread().getName() + "] waiting (" + System.currentTimeMillis() + " ms)");
            try {
                b.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("[" + Thread.currentThread().getName() + "] exit (" + System.currentTimeMillis() + " ms)");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("[" + Thread.currentThread().getName() + "] waiting (" + System.currentTimeMillis() + " ms)");
            try {
                b.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("[" + Thread.currentThread().getName() + "] exit (" + System.currentTimeMillis() + " ms)");
        }

    }

    public static void main(String[] args) throws InterruptedException {
        int N = 10;
        Thread[] threads = new Thread[N];

        ReusableBarrier b = new ReusableBarrier(N);

        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(new Operation(b));
            threads[i].start();

            try {
                // 1 second
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        for (int i = 0; i < N; i++) {
            threads[i].join();
        }

    }

}
