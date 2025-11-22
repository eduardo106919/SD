import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class WarehouseCooperative implements Warehouse {
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

    /*
    problema: se alguem tiver muitos elementos vai ser mais dificil sair, comparado a pessoas com menos elementos
     */
    // Errado se faltar algum produto...
    public void consume(Set<String> items) throws InterruptedException {
        l.lock();
        try {
            String[] prodIds = items.toArray(new String[0]);

            // garantir que tenho stock em todos os produtos
            for (int i = 0; i < items.size(); i++) {
                Product p = get(prodIds[i]);
                while (p.quantity == 0) {
                    System.out.println(Thread.currentThread().getName() + " waiting for " + prodIds[i]);
                    p.waitForProduct.await();
                    i = 0; // voltar ao inicio do ciclo
                }
            }

            for (String s : items) {
                get(s).quantity--;
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

