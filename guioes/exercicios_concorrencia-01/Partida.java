import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Partida {

    int nrTentativas = 100;
    final int nrJogadores;
    Lock l;
    boolean terminou;
    final long inicio;
    int valor;

    Partida(int nrJ) {
        nrJogadores = nrJ;
        l = new ReentrantLock();
        terminou = false;
        inicio = System.currentTimeMillis();
        valor = (new Random()).nextInt(1,100);
    }

    int numeroJogadores() { return nrJogadores; }

    boolean passouUmMinuto() {
        long x = System.currentTimeMillis() - inicio;
        return x >= 60000;
    }

    String adivinha(int n) {
        if (passouUmMinuto()) return "TEMPO";
        
        l.lock();
        try {

            if (terminou) return "PERDEU";
            if (nrTentativas == 0) return "TENTATIVAS";

            nrTentativas--;

            if (n == valor) {
                terminou = true;
                return "GANHOU";
            }

            if (valor > n) return "MAIOR";
            return "MENOR";

        } finally {
            l.unlock();
        }

    }


}