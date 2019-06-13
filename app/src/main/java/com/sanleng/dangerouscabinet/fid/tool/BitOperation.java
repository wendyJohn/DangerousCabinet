package com.sanleng.dangerouscabinet.fid.tool;


import com.sanleng.dangerouscabinet.fid.util.DataConvert;

public class BitOperation {
	
	public static final byte[] BIT = { (byte) 0x80, 0x40, 0x20, 0x10, 0x08,0x04, 0x02, 0x01 };

	public static boolean[] byteToBooleans(byte param) {
		boolean[] result = new boolean[8];
		int value = DataConvert.byteToInt(param);
		for (int i = 0; i < result.length; i++) {
			int index = result.length - i - 1;
			result[i] = (value & BIT[i]) >> index == 1 ? true : false;
		}
		return result;
	}
	
	public static byte booleansReversalToByte(boolean[] enable, int start, int length) {
		byte value = 0;
		int index = 0;
		for (int i = start; i < length; i++) {
			int temp = enable[i] == true ? 1 : 0;
			if (temp == 1) {
				value |= temp << index;
			}
			index++;
		}
		return value;
	}

	public static byte bytesToByte(byte[] enable) {
		byte ant = 0;
		int length = 8;
		for (int i = 0; i < length; i++) {
			if (enable[i] == 1) {
				ant |= BIT[i];
			}
		}
		return ant;
	}
}
