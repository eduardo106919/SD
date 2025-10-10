
import java.util.Random;

public class Main {

    private static class Transfer implements Runnable {
        private Bank b;
        private int accs;
        private int iters;

        public Transfer(Bank b, int accs, int iters) {
            this.b = b;
            this.accs = accs;
            this.iters = iters;
        }

        @Override
        public void run() {
            Random rand = new Random();
            for (int m = 0; m < iters; m++) {
                int from = rand.nextInt(accs);
                int to = rand.nextInt(accs);
                b.transfer(from, to, 1);
            }
        }
    }

    private static class Monitor implements Runnable {

        private Bank b;
        private int reference_balance;
        private int iters;

        public Monitor(Bank b, int reference, int iters) {
            this.b = b;
            this.reference_balance = reference;
            this.iters = iters;
        }

        @Override
        public void run() {
            int balance = 0;
            for (int i = 0; i < iters; i++) {
                balance = b.totalBalance();
                if (balance != reference_balance)
                    System.out.println("[ERROR] unexpected balance: " + balance + " (expected " + reference_balance + ")");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        test1();
        test2();
        test3();
    }

    private static void test1() throws InterruptedException {
        System.out.println("----- [Test 1] -----");

        int ACCS = 10;
        int ITERS = 100000;
        Bank b = new BankGlobalLock(ACCS);
        for (int i = 0; i < ACCS; i++)
            b.deposit(i, 1000);

        int start_balance = b.totalBalance();
        System.out.println("start balance: " + start_balance);

        Thread t1 = new Thread(new Transfer(b, ACCS, ITERS));
        Thread t2 = new Thread(new Transfer(b, ACCS, ITERS));

        long start_t = System.currentTimeMillis();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        long end_t = System.currentTimeMillis();

        int end_balance = b.totalBalance();
        System.out.println("end balance:   " + end_balance);

        if (start_balance != end_balance)
            System.out.println("-> Unexpected balance");
        else
            System.out.println("-> Test OK");
        System.out.println("time: " + (end_t - start_t) + " ms");

    }

    /*
    the problem in transfer() is:
        - the window between withdraw() and deposit() is open, and another thread can take that oportunity
          and change the state of the bank
        - to see this, remove the locks in BankGlobalLock.transfer(), but keep the ones in BankGlobalLock.totalBalance()
    the problem in totalBalance() is:
        - when calling totalBalance(), other threads can run bank operations and change the state of bank,
          and the objetive of this method is to take a "screenshot" of the current state of the bank
     */
    private static void test2() throws InterruptedException {
        System.out.println("----- [Test 2] -----");

        int ACCS = 10;
        int ITERS = 100000;
        int VAL = 10;
        Bank b = new BankGlobalLock(ACCS);
        for (int i = 0; i < ACCS; i++)
            b.deposit(i, 1000);

        int start_balance = b.totalBalance();
        System.out.println("start balance: " + start_balance);

        Thread t1 = new Thread(new Transfer(b, ACCS, ITERS));
        Thread t2 = new Thread(new Transfer(b, ACCS, ITERS));
        Thread t3 = new Thread(new Monitor(b, start_balance, ITERS));

        long start_t = System.currentTimeMillis();
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        long end_t = System.currentTimeMillis();

        int end_balance = b.totalBalance();
        System.out.println("end balance:   " + end_balance);

        if (end_balance != start_balance)
            System.out.println("-> Unexpected balance");
        else
            System.out.println("-> Test OK");
        System.out.println("time: " + (end_t - start_t) + " ms");

    }

    private static void test3() throws InterruptedException {
        System.out.println("----- [Test 3] -----");

        int ACCS = 10;
        int ITERS = 100000;
        int VAL = 10;
        Bank b = new BankGlobalLock(ACCS);
        for (int i = 0; i < ACCS; i++)
            b.deposit(i, 1000);

        int start_balance = b.totalBalance();
        System.out.println("start balance: " + start_balance);

        Thread t1 = new Thread(new Transfer(b, ACCS, ITERS));
        Thread t2 = new Thread(new Transfer(b, ACCS, ITERS));
        Thread t3 = new Thread(new Monitor(b, start_balance, ITERS));

        long start_t = System.currentTimeMillis();
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        long end_t = System.currentTimeMillis();

        int end_balance = b.totalBalance();
        System.out.println("end balance:   " + end_balance);

        if (end_balance != start_balance)
            System.out.println("-> Unexpected balance");
        else
            System.out.println("-> Test OK");
        System.out.println("time: " + (end_t - start_t) + " ms");

    }

}
