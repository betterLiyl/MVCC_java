package core;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: liyanlong
 * @Date: 2024-10-04 18:55
 **/

public class ActiveTx {

    public static HashSet<Long> activeTx = new HashSet<>(16);

    private static final ReentrantLock lock = new ReentrantLock();

    public static void setLock() {
        lock.lock();
    }

    public static void releaseLock() {
        lock.unlock();
    }
}
