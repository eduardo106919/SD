

public class SingleShotMain {

    public static void main(String[] args) throws InterruptedException {
        int N = 10;
        Thread[] threads = new Thread[N];

        SingleShotBarrier b = new SingleShotBarrier(N);

        for (int i = 0; i < N; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                System.out.println("[" + threads[finalI].getName() + "] enter (" + System.currentTimeMillis() + " ms)");
                try {
                    b.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("[" + threads[finalI].getName() + "] exit (" + System.currentTimeMillis() + " ms)");
            });
        }

        for (int i = 0; i < N; i++) {
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
