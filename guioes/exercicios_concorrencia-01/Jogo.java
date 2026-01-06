import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Jogo {

    int nrJogadores = 0;
    Partida p = null;
    Lock l = new ReentrantLock();
    Condition c = l.newCondition();

    void inicarDoisMinutos() {
        new Thread(() -> {
            try {
                Thread.sleep(30000);

                l.lock();

                if (nrJogadores >= 4) {
                    p = new Partida(nrJogadores);
                    nrJogadores = 0;

                    c.signalAll();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                l.unlock();
            }


        }).start();
    }

    Partida participa() {
        l.lock();
        try {
            nrJogadores++;
            Partida temp = p;
            if (nrJogadores == 1)
                inicarDoisMinutos();

            if (nrJogadores < 6) {
                while (temp == p)
                    c.await();
                
                return p;
            }

            p = new Partida(nrJogadores);
            nrJogadores = 0;
            c.signalAll();
            
            return p;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            l.unlock();
        }
    }

}