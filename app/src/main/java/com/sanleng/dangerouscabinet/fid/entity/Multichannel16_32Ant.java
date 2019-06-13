package com.sanleng.dangerouscabinet.fid.entity;

import com.sanleng.dangerouscabinet.fid.dao.Reader;
import com.sanleng.dangerouscabinet.fid.tool.BitOperation;
import com.sanleng.dangerouscabinet.fid.util.DataConvert;

import java.nio.ByteBuffer;

public class Multichannel16_32Ant {
	
	public static byte[] reverse32Ant(byte[] ant, int start, int end, int start2,int end2) {
		byte[] antten = new byte[8];
		int count = 0;
		for (int i = end; i >= start; i--) {
			antten[count] = ant[i];
			count++;
		}
		for (int i = end2; i >= start2; i--) {
			antten[count] = ant[i];
			count++;
		}
		return antten;
	}

	public byte[] setAnt32(AntStruct antStruct) {
		ByteBuffer sendAnt = ByteBuffer.allocate(36);
		byte[] antenner = new byte[4];
		
		byte[] ant1 = reverse32Ant(antStruct.enable, 16, 19, 0, 3);
		byte[] ant2 = reverse32Ant(antStruct.enable, 20, 23, 4, 7);
		byte[] ant3 = reverse32Ant(antStruct.enable, 24, 27, 8, 11);
		byte[] ant4 = reverse32Ant(antStruct.enable, 28, 31, 12, 15);
		
		antenner[0] = BitOperation.bytesToByte(ant1);
		antenner[1] = BitOperation.bytesToByte(ant2);
		antenner[2] = BitOperation.bytesToByte(ant3);
		antenner[3] = BitOperation.bytesToByte(ant4);

		byte[] time = new byte[4];
		time = DataConvert.intToByteArray(antStruct.dwellTime[0]);
		byte[] power = new byte[4];
		power = DataConvert.intToByteArray(antStruct.power[0] * 10);

		sendAnt.put(antenner);
		for (int i = 0; i < power.length; i++) {
			sendAnt.put(time);
		}
		for (int i = 0; i < power.length; i++) {
			sendAnt.put(power);
		}
		return sendAnt.array();
	}
	
	public AntStruct ant32(Reader reader, byte[] buffer) {
		if (null == reader) {
			return null;
		}
		AntStruct struct = new AntStruct(reader.getChannel());
		for (int i = 0; i < 4; i++) {
			int result = DataConvert.byteToInt(buffer[i]);
			for (int j = 0; j < 4; j++) {
				struct.enable[i * 4 + j] = (byte) ((result & (byte) Math.pow(0x02, j)) >> j);
			}
		}
		byte[] time = new byte[4];
		System.arraycopy(buffer, 4 + 3 * 4, time, 0, 4);
		struct.dwellTime[0] = DataConvert.bytesToInt(time, 0);

		byte[] power = new byte[4];
		System.arraycopy(buffer, 4 + 7 * 4, power, 0, 4);
		struct.power[0] = DataConvert.bytesToInt(power, 0) / 10;

		if (reader.getChannel() == 16) {
			return struct;
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int result = DataConvert.byteToInt(buffer[i]);
				struct.enable[i * 4 + j + 16] = (byte) ((result & (byte) Math.pow(0x02, j + 4)) >> j + 4);
			}
		}
		return struct;
	}
	
	public static byte[] reverse16Ant(byte[] ant, int start, int end) {
		byte[] antten = new byte[8];
		int count = 4;
		for (int i = end; i >= start; i--) {
			antten[count] = ant[i];
			count++;
		}
		return antten;
	}
	
	public byte[] setAnt16(AntStruct antStruct) {
		ByteBuffer sendAnt = ByteBuffer.allocate(36);
		byte[] antenner = new byte[4];
		
		byte[] ant1 = reverse16Ant(antStruct.enable, 0, 3);
		byte[] ant2 = reverse16Ant(antStruct.enable, 4, 7);
		byte[] ant3 = reverse16Ant(antStruct.enable, 8, 11);
		byte[] ant4 = reverse16Ant(antStruct.enable, 12, 15);
		
		antenner[0] = BitOperation.bytesToByte(ant1);
		antenner[1] = BitOperation.bytesToByte(ant2);
		antenner[2] = BitOperation.bytesToByte(ant3);
		antenner[3] = BitOperation.bytesToByte(ant4);

		byte[] time = new byte[4];
		time = DataConvert.intToByteArray(antStruct.dwellTime[0]);
		
		byte[] power = new byte[4];
		power = DataConvert.intToByteArray(antStruct.power[0] * 10);

		sendAnt.put(antenner);
		for (int i = 0; i < power.length; i++) {
			sendAnt.put(time);
		}
		for (int i = 0; i < power.length; i++) {
			sendAnt.put(power);
		}
		return sendAnt.array();
	}
}
