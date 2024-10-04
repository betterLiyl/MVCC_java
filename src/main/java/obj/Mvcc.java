package obj;

import core.KVEngine;

/**
 * @Author: liyanlong
 * @Date: 2024-10-04 18:39
 **/

public class Mvcc {
    private KVEngine kv;

    public Mvcc(KVEngine kv) {
        this.kv = kv;
    }

    public Transaction begin_transaction() {
        Transaction txn = new Transaction();
        return txn.begin(kv);
    }
}
