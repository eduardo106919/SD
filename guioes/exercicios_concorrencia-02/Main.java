public class Main {

    static class Worker extends Thread {
        private final Controller controller;
        private final int resource;

        Worker(Controller controller, int resource) {
            this.controller = controller;
            this.resource = resource;
        }

        @Override
        public void run() {
            try {
                controller.request_resource(resource);

                System.out.println(Thread.currentThread().getName() + " USING resource " + resource);
                Thread.sleep(5000);

                controller.release_resource(resource);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Controller controller = new Controller();

        Thread[] threads = new Thread[6];

        // Mix access to resource 0 and 1
        threads[0] = new Worker(controller, 0);
        threads[1] = new Worker(controller, 1);
        threads[2] = new Worker(controller, 0);
        threads[3] = new Worker(controller, 1);
        threads[4] = new Worker(controller, 0);
        threads[5] = new Worker(controller, 1);

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Test finished.");
    }
}
