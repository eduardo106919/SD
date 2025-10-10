

public interface Bank {

    int balance(int id);
    boolean deposit(int id, int value);
    boolean withdraw (int id, int value);
    boolean transfer (int from, int to, int value);
    int totalBalance();

}
