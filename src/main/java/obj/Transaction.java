package obj;

import core.ActiveTx;
import core.KVEngine;
import core.Version;
import util.ProtostuffUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: liyanlong
 * @Date: 2024-10-04 18:38
 **/

public class Transaction {
    // 当前事务使用的引擎
    private KVEngine kv;

    // 事务版本号
    private Long version;

    // 当前事务启动时的活跃事务
    private HashSet<Long> activeXid;


    // 开启事务
    public Transaction begin(KVEngine kv) {

        Long nextVersion = Version.acquireNextVersion();

        ActiveTx.setLock();

        this.kv = kv;
        this.version = nextVersion;
        HashSet<Long> activeTx = ActiveTx.activeTx;
        HashSet<Long> clone = (HashSet<Long>) activeTx.clone();
        clone.add(nextVersion);
        this.activeXid = clone;

        ActiveTx.releaseLock();
        return this;

    }

    private void write(byte[] key, byte[] value){
        KVEngine.lock();
        TreeMap<byte[], byte[]> kvEngine = KVEngine.KVEngine;
        byte[] v = kvEngine.get(key);
//        if(v != null && v.length != 0){
//            ProtostuffUtil.deserializer()
//        }
    }

}
