package io.merklex.dcn;

import org.bouncycastle.util.encoders.Hex;

import java.nio.ByteBuffer;

public class SolidityBuffers {
    public static final byte[] NEGATIVE_BUFFER = new byte[32];

    static {
        for (int i = 0; i < NEGATIVE_BUFFER.length; i++) {
            NEGATIVE_BUFFER[i] = -1;
        }
    }

    public static void jump(ByteBuffer buffer, int count) {
        buffer.position(buffer.position() + count);
    }

    public static void putUInt32(ByteBuffer buffer, int value) {
        jump(buffer, 32 - 4);
        buffer.putInt(value);
    }

    public static void putUInt64(ByteBuffer buffer, long value) {
        jump(buffer, 32 - 8);
        buffer.putLong(value);
    }

    public static void putInt64(ByteBuffer buffer, long value) {
        if (value < 0) {
            buffer.put(NEGATIVE_BUFFER);
            jump(buffer, -8);
        } else {
            jump(buffer, 32 - 8);
        }
        buffer.putLong(value);
    }

    public static void putInt96(ByteBuffer buffer, int majorValue, long minorValue) {
        if (majorValue < 0) {
            buffer.put(NEGATIVE_BUFFER);
            jump(buffer, -8 - 4);
        } else {
            jump(buffer, 32 - 8 - 4);
        }
        buffer.putInt(majorValue);
        buffer.putLong(minorValue);
    }

    public static void putBytes(ByteBuffer buffer, String bytes) {
        byte[] decode = Hex.decode(bytes);
        if (decode.length <= 32) {
            throw new IllegalArgumentException("bytes cannot be over 32 words");
        }
        jump(buffer, 32 - decode.length);
        buffer.put(decode);
    }
}
