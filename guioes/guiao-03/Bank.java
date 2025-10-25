import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Bank {

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

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;
    private Lock bankLock = new ReentrantLock();

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        bankLock.lock();
        try {
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        } finally {
            bankLock.unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        Account c;
        bankLock.lock();
        try {
            c = map.remove(id);
            if (c == null)
                return 0;

            c.accountLock.lock();

        } finally {
            bankLock.unlock();
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
        bankLock.lock();
        try {
            c = map.get(id);
            if (c == null)
                return 0;

            c.accountLock.lock();

        } finally {
            bankLock.unlock();
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
        bankLock.lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;

            c.accountLock.lock();
        } finally {
            bankLock.unlock();
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
        bankLock.lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;

            c.accountLock.lock();
        } finally {
            bankLock.unlock();
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
        /*
        Account cfrom, cto;
        cfrom = map.get(from);
        cto = map.get(to);
        if (cfrom == null || cto ==  null)
            return false;
        return cfrom.withdraw(value) && cto.deposit(value);
         */
        Account cfrom, cto;

        bankLock.lock();
        try {
            cfrom = map.get(from);
            cto = map.get(to);
            if (cfrom == null || cto ==  null)
                return false;

            cfrom.accountLock.lock();
            cto.accountLock.lock();
        } finally {
            bankLock.unlock();
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

        bankLock.lock();
        try {
            for (int i = 0; i < ids.length; i++) {
                Account c = map.get(ids[i]);
                if (c == null)
                    return 0;
                accs[i] = c;
                c.accountLock.lock();
            }
        } finally {
            bankLock.unlock();
        }

        for (int i = 0; i < ids.length; i++) {
            total += accs[i].balance();
            accs[i].accountLock.unlock();
        }

        return total;
    }

}
