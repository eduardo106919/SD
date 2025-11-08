import java.util.Random;

public class Main {

    private static class Operation implements Runnable {

        private Jogo j;

        public Operation(Jogo j) {
            this.j = j;
        }

        public void run() {
            Random rand = new Random();
            System.out.println(Thread.currentThread().getName() + " is waiting...");
            Partida p = j.participa();

            String out = "";
            int v = 0;
            do {
                v = rand.nextInt(100) + 1;
                out = p.adivinha(v);
                System.out.println(Thread.currentThread().getName() + " guessed " + v + ": " + out);
                try {
                    int t = rand.nextInt(3) + 1;
                    Thread.sleep(1000 * t);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (!out.equals("PERDEU") && !out.equals("TENTATIVAS") && !out.equals("GANHOU") && !out.equals("TEMPO"));

            System.out.println(Thread.currentThread().getName() + " exited!");
        }

    }

    public static void main(String[] args) throws InterruptedException {
        int N = 16;
        Jogo j = new JogoFacade();

        Thread[] threads = new Thread[N];
        for (int i = 0; i < 4; i++) {
            threads[i] = new Thread(new Operation(j));
            threads[i].start();
            Thread.sleep(1000);
        }

        // Thread.sleep(1000 * 120);

        for (int i = 4; i < 10; i++) {
            threads[i] = new Thread(new Operation(j));
            threads[i].start();
            Thread.sleep(1000);
        }

        // Thread.sleep(1000 * 20);

        for (int i = 10; i < N; i++) {
            threads[i] = new Thread(new Operation(j));
            threads[i].start();
            Thread.sleep(1000);
        }

        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }

    }

}