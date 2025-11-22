import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class WarehouseSelfish implements Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    private Lock l = new ReentrantLock();

    private class Product {
        // condition relativa ao lock do warehouse
        private Condition waitForProduct = l.newCondition();
        private int quantity = 0;
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null)
            return p;

        p = new Product();
        map.put(item, p);

        return p;
    }

    public void supply(String item, int quantity) throws InterruptedException {
        l.lock();
        try {
            Product p = get(item);
            p.quantity += quantity;
            // sinalizar apenas quem espera pelo produto
            p.waitForProduct.signalAll();

            //waitForAll.signalAll();
        } finally {
            l.unlock();
        }
    }

    // Errado se faltar algum produto...
    public void consume(Set<String> items) throws InterruptedException {
        l.lock();
        try {
            for (String s : items) {
                Product p = get(s);
                while (p.quantity == 0) {
                    System.out.println(Thread.currentThread().getName() + " waiting for " + s);
                    p.waitForProduct.await();
                }

                p.quantity--;
            }
        } finally {
            l.unlock();
        }
    }

    public void getQuantities() {
        l.lock();
        try {
            map.entrySet().forEach(e -> System.out.println("[" + e.getKey() + ", " + e.getValue().quantity + "]"));
        } finally {
            l.unlock();
        }
    }

}
