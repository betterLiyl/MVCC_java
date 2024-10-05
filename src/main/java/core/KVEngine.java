package core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: liyanlong
 * @Date: 2024-10-04 18:40
 **/

public class KVEngine {

    private TreeMap<byte[], byte[]> engineData = initMap();

    public TreeMap<byte[], byte[]> getEngineData() {
        return engineData;
    }

    public KVEngine() {
        new KVEngine(null);
    }

    public KVEngine(TreeMap<byte[], byte[]> engineData) {
        //Key-Value Engine define
        //k : Key序列化
        //v : Value序列化
        if (engineData != null) {
            this.engineData = engineData;
        }
    }

    public static TreeMap<byte[], byte[]> initMap() {
        return new TreeMap<>((b1, b2) -> {
            // 先比较长度
            if (b1.length != b2.length) {
                return Integer.compare(b1.length, b2.length);
            }
            // 长度相同，逐个比较字节
            for (int i = 0; i < b1.length; i++) {
                int result = Byte.compare(b1[i], b2[i]);
                if (result != 0) {
                    return result;
                }
            }
            return 0; // 完全相同
        });
    }


    private final ReentrantLock lock = new ReentrantLock();

    public void lock() {
        lock.lock();
    }

    public boolean isLock() {
        return lock.isLocked();
    }

    public void unlock() {
        lock.unlock();
    }

    @Override
    public KVEngine clone() {
        return new KVEngine((TreeMap<byte[], byte[]>) this.engineData.clone());
    }
}
