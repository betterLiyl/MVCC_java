package obj;

import util.ProtostuffUtil;


/**
 * @Author: liyanlong
 * @Date: 2024-10-04 18:38
 **/

public class Key {

    private byte[] rawKey;

    private Long version;

    public byte[] getRawKey() {
        return rawKey;
    }

    public Long getVersion() {
        return version;
    }

    public void setRawKey(byte[] rawKey) {
        this.rawKey = rawKey;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Key() {
    }

    public Key(byte[] rawKey, Long version) {
        this.rawKey = rawKey;
        this.version = version;
    }
}
