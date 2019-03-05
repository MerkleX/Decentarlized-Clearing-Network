package io.merklex.dcn;

import org.agrona.MutableDirectBuffer;

public class BufferToHex {
    public static String ToHex(MutableDirectBuffer buffer, int offset, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0x");
        for (int i = offset; i < offset + length; i++) {
            stringBuilder.append(String.format("%02x", buffer.getByte(i) & 0xFF));
        }

        return stringBuilder.toString();
    }
}
