
import java.util.concurrent.locks.Lock;

public class BankIndividualLock implements Bank {

    private static class Account {

        private int balance;
        public Lock l;

        Account(int balance) {
            this.balance = balance;
        }

        int balance() {
            return balance;
        }

        boolean deposit(int value) {
            balance += value;
            return true;
        }

        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }

    }

    // bank slots and vector of accounts
    private int slots;
    private Account[] accounts;

    public BankIndividualLock(int n) {
        slots = n;
        accounts =  new Account[slots];
        for (int i = 0; i < slots; i++)
            accounts[i] = new Account(0);
    }

    // account balance
    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        try {
            accounts[id].l.lock();
            return accounts[id].balance();
        } finally {
            accounts[id].l.unlock();
        }
    }

    // deposit
    public boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        try {
            accounts[id].l.lock();
            return accounts[id].deposit(value);
        } finally {
            accounts[id].l.unlock();
        }
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        try {
            accounts[id].l.lock();
            return accounts[id].withdraw(value);
        } finally {
            accounts[id].l.unlock();
        }
    }

    // transfer
    public boolean transfer(int from, int to, int value) {
        if (from < 0 || to < 0 || from >= slots || to >= slots)
            return false;

        try {
            accounts[from].l.lock();
            accounts[to].l.lock();

            if (accounts[from].withdraw(value))
                return accounts[to].deposit(value);
            return false;
        } finally {
            accounts[from].l.unlock();
            accounts[to].l.unlock();
        }
    }

    // total balance
    public int totalBalance() {
        int sum = 0;
        try {
            for (int i = 0; i < slots; i++) {
                accounts[i].l.lock();
                sum += accounts[i].balance();
                accounts[i].l.unlock();
            }
            return sum;
        } finally {

        }
    }

}
