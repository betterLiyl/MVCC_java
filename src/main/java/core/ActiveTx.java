package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: liyanlong
 * @Date: 2024-10-04 18:55
 **/

public class ActiveTx {
    /**
     * 当前活跃的事务 id，及其已经写入的 key 信息
     */
    public static HashMap<Long, List<byte[]>> activeTx = new HashMap<>(16);

    private static final ReentrantLock lock = new ReentrantLock();

    public static void setLock() {
        lock.lock();
    }

    public static boolean isLock() {
        return lock.isHeldByCurrentThread();
    }
    public static void releaseLock() {
        lock.unlock();
    }
}
