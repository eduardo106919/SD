
public class Main {

    public static void main(String[] args) {
        // exercise1(10, 100);
        // exercise2(10, 1000, 100);
        // exercise3(10, 1000, 100);

        extra(10, 1000000, 1);
        extra(20, 1000000, 1);
        extra(30, 1000000, 1);
        extra(40, 1000000, 1);
        extra(50, 1000000, 1);
    }

    private static void exercise1(int N, long I) {
        System.out.println("*** Exercise 1 ***");
        Thread[] threads = new Thread[N];

        // create the threads
        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(new Increment(I));
            threads[i].setName("Thread[" + i + "]");
        }

        // start the threads
        for (int i = 0; i < N; i++)
            threads[i].start();

        // wait for the threads to finish
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            }
        }

        System.out.println(Thread.currentThread().getName() + ": finished!");
    }

    private static void exercise2(int N, long I, int V) {
        Bank b = new Bank();

        Thread[] threads = new Thread[N];
        // create the threads
        for (int i = 0; i < N; i++)
            threads[i] = new Thread(() -> {for (long j = 0; j < I; j++) b.deposit(V);});

        // start the threads
        for (int i = 0; i < N; i++)
            threads[i].start();

        // wait for the threads to finish
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            }
        }

        System.out.println("*** Exercise 2 ***");
        System.out.println("Expected value: " + N * I * V);
        System.out.println("Actual value: " + b.balance());
    }

    private static void exercise3(int N, long I, int V) {
        Bank bank = new Bank();
        BankLock b = new BankLock(bank);

        Thread[] threads = new Thread[N];
        // create the threads
        for (int i = 0; i < N; i++)
            threads[i] = new Thread(() -> {for (long j = 0; j < I; j++) b.depositLock(V);});

        // start the threads
        for (int i = 0; i < N; i++)
            threads[i].start();

        // wait for the threads to finish
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            }
        }

        System.out.println("*** Exercise 3 ***");
        System.out.println("Expected value: " + N * I * V);
        System.out.println("Actual value: " + bank.balance());
    }

    private static void extra(int N, long I, int V) {
        System.out.println("\n*** Extra Exercise (" + N + " threads) ***");

        // without locks

        Bank bank1 = new Bank();
        Thread[] threads = new Thread[N];
        // create the threads
        for (int i = 0; i < N; i++)
            threads[i] = new Thread(() -> {for (long j = 0; j < I; j++) bank1.deposit(V);});

        long global = System.currentTimeMillis();
        // start the threads
        for (int i = 0; i < N; i++)
            threads[i].start();

        // wait for the threads to finish
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            }
        }

        global = System.currentTimeMillis() - global;

        System.out.println("-> WITHOUT LOCKS");
        System.out.println("\tGlobal time:  " + global +  " ms");
        System.out.println("\tBank balance: " + bank1.balance());


        // with locks

        Bank bank2 = new Bank();
        BankLock b = new BankLock(bank2);

        // create the threads
        for (int i = 0; i < N; i++)
            threads[i] = new Thread(() -> {for (long j = 0; j < I; j++) b.depositLock(V);});

        global = System.currentTimeMillis();
        // start the threads
        for (int i = 0; i < N; i++)
            threads[i].start();

        // wait for the threads to finish
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            }
        }

        global = System.currentTimeMillis() - global;

        System.out.println("-> WITH LOCKS");
        System.out.println("\tGlobal time:  " + global +  " ms");
        System.out.println("\tBank balance: " + b.balanceLock());
    }

}