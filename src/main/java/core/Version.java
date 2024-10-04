package core;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: liyanlong
 * @Date: 2024-10-04 19:01
 **/

public class Version {

    public static AtomicLong version = new AtomicLong(0);

    public static Long acquireNextVersion() {
        return version.addAndGet(1L);
    }
}
