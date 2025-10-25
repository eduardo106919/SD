import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ReadWriteBank {

    private static class Account {
        private int balance;
        private Lock accountLock;

        Account(int balance) {
            this.balance = balance;
            this.accountLock = new ReentrantLock();
        }

        int balance() { return balance; }

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

    private Map<Integer, Account> map;
    private int nextId;
    private ReadWriteLock bankLock;
    private Lock readLock;
    private Lock writeLock;

    public ReadWriteBank() {
        this.map = new HashMap<Integer, Account>();
        this.nextId = 0;
        this.bankLock = new ReentrantReadWriteLock();
        this.readLock = this.bankLock.readLock();
        this.writeLock = this.bankLock.writeLock();
    }

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        writeLock.lock();
        try {
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        } finally {
            writeLock.unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        Account c;
        writeLock.lock();
        try {
            c = map.remove(id);
            if (c == null)
                return 0;

            c.accountLock.lock();

        } finally {
            writeLock.unlock();
        }

        try {
            return c.balance();
        } finally {
            c.accountLock.unlock();
        }

    }

    // account balance; 0 if no such account
    public int balance(int id) {
        Account c;
        readLock.lock();
        try {
            c = map.get(id);
            if (c == null)
                return 0;

            c.accountLock.lock();

        } finally {
            readLock.unlock();
        }

        try{
            return c.balance();
        } finally {
            c.accountLock.unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        Account c;
        readLock.lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;

            c.accountLock.lock();
        } finally {
            readLock.unlock();
        }

        try {
            return c.deposit(value);
        } finally {
            c.accountLock.unlock();
        }

    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        Account c;
        readLock.lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;

            c.accountLock.lock();
        } finally {
            readLock.unlock();
        }

        try {
            return c.withdraw(value);
        } finally {
            c.accountLock.unlock();
        }

    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        Account cfrom, cto;
        readLock.lock();
        try {
            cfrom = map.get(from);
            cto = map.get(to);
            if (cfrom == null || cto ==  null)
                return false;

            cfrom.accountLock.lock();
            cto.accountLock.lock();
        } finally {
            readLock.unlock();
        }

        try {
            return cfrom.withdraw(value) && cto.deposit(value);
        } finally {
            cfrom.accountLock.unlock();
            cto.accountLock.unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids) {
        int total = 0;
        Account[] accs = new Account[ids.length];

        readLock.lock();
        try {
            for (int i = 0; i < ids.length; i++) {
                Account c = map.get(ids[i]);
                if (c == null)
                    return 0;
                accs[i] = c;
                c.accountLock.lock();
            }
        } finally {
            readLock.unlock();
        }

        for (int i = 0; i < ids.length; i++) {
            total += accs[i].balance();
            accs[i].accountLock.unlock();
        }

        return total;
    }

}
