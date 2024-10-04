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

    //Key-Value Engine define
    //k : Key序列化
    //v : Value序列化
    public static final TreeMap<byte[], byte[]> KVEngine = new TreeMap<>(new Comparator<byte[]>() {
        @Override
        public int compare(byte[] b1, byte[] b2) {
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
        }
    });;

    private final static ReentrantLock lock = new ReentrantLock();

    public static void lock() {
        lock.lock();
    }
    public static void unlock() {
        lock.unlock();
    }

//    @Override
//    protected KVEngine clone() throws CloneNotSupportedException {
//        Object clone = KVEngine.clone();
//        return super.clone();
//    }
}
