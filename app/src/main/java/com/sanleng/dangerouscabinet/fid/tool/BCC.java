package com.sanleng.dangerouscabinet.fid.tool;

public class BCC {
    public static byte checkSum(byte[] startcode, int length) {
        byte sum = 0x00;
        for (int i = 0; i < length; ++i) {
            sum ^= startcode[i];
        }
        return sum;
    }

    public static byte checkSum(byte[] sourceData, int start, int length) {
        byte result = 0x00;
        for (int i = start; i < length - start; i++) {
            result ^= sourceData[i];
        }
        return result;
    }
}
