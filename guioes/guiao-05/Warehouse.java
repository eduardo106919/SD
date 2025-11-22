import java.util.Set;

public interface Warehouse  {
    void supply(String item, int quantity) throws InterruptedException;
    void consume(Set<String> items) throws InterruptedException;
}
