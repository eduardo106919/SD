import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SumServer {

    public static class Storage {
        private int total = 0;
        private int count = 0;
        private Lock l = new ReentrantLock();

        public void addValue(int value) {
            this.l.lock();
            this.total += value;
            this.count++;
            this.l.unlock();
        }

        public float getCurrentAverage() {
            this.l.lock();
            try {
                if (this.count < 1)
                    this.count = 1;
                return this.total / (float) this.count;
            } finally {
                this.l.unlock();
            }
        }

    }

    public static void handleClient(Storage db, Socket socket) throws IOException {
        int total = 0;
        int count = 0;

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        String line;
        int value;
        while ((line = in.readLine()) != null) {
            System.out.println("[SERVER] received: " + line);
            try {
                value = Integer.parseInt(line);
                total += value;
                count++;

                db.addValue(value);

            } catch (NumberFormatException e) {
                System.out.println("[SERVER] " + line + " is not a valid number!");
            }
            out.println(total);
            out.flush();
        }

        if (count < 1)
            count = 1;

        String finalMesage = "[total= " + total + ", count=" + count + ", average=" + (total/ (float)count) + ", overall=" + db.getCurrentAverage() + "]";
        out.println(finalMesage);
        out.flush();

        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            List<Thread> threads = new ArrayList<>();
            Storage db = new Storage();

            while (true) {
                System.out.println("[SERVER] waiting for clients...");
                Socket socket = ss.accept();
                System.out.println("[SERVER] new client");
                Thread t = new Thread(() -> {
                    try {
                        handleClient(db, socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                t.start();
                threads.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
