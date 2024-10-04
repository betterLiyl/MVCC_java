package obj;

import util.ProtostuffUtil;

import java.util.List;

/**
 * @Author: liyanlong
 * @Date: 2024-10-04 18:38
 **/

public class Key {

    private byte[] rawKey;

    private Long version;

    public byte[] encode(Key key) {
        return ProtostuffUtil.serializer(key);
    }

    public Key decode(byte[] bytes) {
        return ProtostuffUtil.deserializer(bytes, Key.class);
    }
}
