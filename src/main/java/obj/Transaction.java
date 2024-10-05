package obj;

import core.ActiveTx;
import core.KVEngine;
import core.Version;
import util.ProtostuffUtil;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @Author: liyanlong
 * @Date: 2024-10-04 18:38
 **/

public class Transaction {
    // 当前事务使用的引擎
    private KVEngine kv;

    // 事务版本号
    private Long version;

    // 当前事务启动时的活跃事务 不包括自己
    private HashSet<Long> activeXid;


    // 开启事务
    public Transaction begin(KVEngine kv) {

        Long nextVersion = Version.acquireNextVersion();

        ActiveTx.setLock();

        try {
            HashSet<Long> activeTx = new HashSet<>(ActiveTx.activeTx.keySet());

            HashSet<Long> clone = (HashSet<Long>) activeTx.clone();

            ActiveTx.activeTx.put(nextVersion, new ArrayList<>());

            this.activeXid = clone;
            this.kv = kv;
            this.version = nextVersion;

        } finally {
            ActiveTx.releaseLock();
        }

        return this;

    }

    public void set(byte[] key, byte[] value) {
        write(key, value);
    }

    public void delete(byte[] key) {
        write(key, null);
    }

    private void write(byte[] key, byte[] value) {
        kv.lock();
        try {
            TreeMap<byte[], byte[]> kvEngine = kv.getEngineData();
            for (byte[] bKey : kvEngine.descendingMap().keySet()) {
                Key objKey = ProtostuffUtil.deserializer(bKey, Key.class);
                if (Arrays.equals(objKey.getRawKey(), key)) {
                    if (!isVisible(objKey.getVersion())) {
                        throw new RuntimeException("key is locked");
                    }
                    break;
                }
            }
            //write into ActiveTx
            ActiveTx.setLock();
            HashMap<Long, List<byte[]>> activeTx = ActiveTx.activeTx;

            activeTx.compute(this.version, (k, v) -> {
                if (v == null) {
                    ArrayList<byte[]> bytes = new ArrayList<>();
                    bytes.add(key);
                    return bytes;
                } else {
                    v.add(key);
                    return v;
                }
            });
            //write into engineData
            Key nKey = new Key(key, this.version);
            kvEngine.put(ProtostuffUtil.serializer(nKey), value);
        } finally {
            if (ActiveTx.isLock()) {
                ActiveTx.releaseLock();
            }
            if (kv.isLock()) {
                kv.unlock();
            }
        }
    }

    // 判断一个版本的数据对当前事务是否可见
    // 1. 如果是另一个活跃事务的修改，则不可见
    // 2. 如果版本号比当前大，则不可见
    private boolean isVisible(Long version) {
        if (activeXid.contains(version)) {
            return false;
        }
        return version <= this.version;
    }

    //read data
    public byte[] get(byte[] key) {
        kv.lock();
        try {
            for (Map.Entry<byte[], byte[]> entry : kv.getEngineData().descendingMap().entrySet()) {
                byte[] bKey = entry.getKey();
                byte[] bValue = entry.getValue();
                Key deserializer = ProtostuffUtil.deserializer(bKey, Key.class);
                if (Arrays.equals(deserializer.getRawKey(), key)
                        && isVisible(deserializer.getVersion())) {
                    return bValue;
                }
            }
        } finally {
            if (kv.isLock()) {
                kv.unlock();
            }
        }
        return null;
    }

    public void commit() {
        ActiveTx.setLock();
        try {
            HashMap<Long, List<byte[]>> activeTx = ActiveTx.activeTx;

            activeTx.remove(this.version);

        } finally {
            if (ActiveTx.isLock()) {

                ActiveTx.releaseLock();
            }
        }
    }

    public void rollback() {
        ActiveTx.setLock();
        try {
            HashMap<Long, List<byte[]>> activeTx = ActiveTx.activeTx;

            List<byte[]> keys = activeTx.get(this.version);

            kv.lock();
            for (byte[] key : keys) {
                kv.getEngineData().remove(ProtostuffUtil.serializer(new Key(key, this.version)));
            }
            ActiveTx.activeTx.remove(this.version);
        } finally {
            if (kv.isLock()) {
                kv.unlock();
            }
            if (ActiveTx.isLock()) {
                ActiveTx.releaseLock();
            }
        }
    }

    /**
     * print all visible data
     */
    public void printAll() {
        kv.lock();
        try {
            TreeMap<byte[], byte[]> engineData = kv.getEngineData();
            TreeMap<byte[], byte[]> record = KVEngine.initMap();

            for (Map.Entry<byte[], byte[]> entry : engineData.entrySet()) {
                byte[] bKey = entry.getKey();
                byte[] bValue = entry.getValue();
                Key deserializer = ProtostuffUtil.deserializer(bKey, Key.class);
                if (isVisible(deserializer.getVersion())) {
                    record.put(deserializer.getRawKey(), bValue);
                }
            }
            record.forEach((k, v) -> {
                if (v != null) {
                    System.out.printf("%s=%s ", new String(k), new String(v));
                }
            });
            System.out.println();
        } finally {
            if (kv.isLock()) {
                kv.unlock();
            }
        }
    }

}
