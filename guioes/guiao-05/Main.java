import java.util.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Warehouse warehouse = new WarehouseSelfish();
        // Warehouse warehouse = new WarehouseCooperative();

        // Initial supply
        warehouse.supply("A", 2);
        warehouse.supply("B", 1);
        warehouse.supply("C", 1);

        System.out.println("Initial supply done.");

        // Define multiple consumers
        List<Thread> consumers = new ArrayList<>();

        consumers.add(new Thread(new Consumer(warehouse, Set.of("A", "B", "C"))));
        consumers.add(new Thread(new Consumer(warehouse, Set.of("A", "C"))));
        consumers.add(new Thread(new Consumer(warehouse, Set.of("B", "C"))));

        // Start all consumer threads
        for (Thread t : consumers) {
            t.start();
        }

        // Give consumers time to partially consume items
        Thread.sleep(2000);

        System.out.println("Supplying missing items...");

        // Supply missing items
        warehouse.supply("B", 2);
        warehouse.supply("C", 2);

        // Wait for all consumers to finish
        for (Thread t : consumers) {
            t.join();
        }

        System.out.println("All consumers finished. Test complete.");
    }

    // Consumer Runnable
    static class Consumer implements Runnable {
        private final Warehouse warehouse;
        private final Set<String> items;

        public Consumer(Warehouse warehouse, Set<String> items) {
            this.warehouse = warehouse;
            this.items = items;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " requesting " + items);
                warehouse.consume(items);
                System.out.println(Thread.currentThread().getName() + " finished consuming " + items);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
