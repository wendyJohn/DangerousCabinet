package com.sanleng.dangerouscabinet.fid.entity;


public class AntStruct {
	public int state;
	/**
	 * 天线是否启用数组，四个天线对应数组四个变量，0是不启用，1是启用
	 */
	public byte[] enable;
	/**
	 * 天线运行时间数组，四个天线对应数组四个变量，时间取值范围50-10000
	 */
	public int[] dwellTime;
	/**
	 * 天线功率数组，四个天线对应数组四个变量，功率取值范围20-33
	 */
	public int[] power;

	public AntStruct() {
	}

	public AntStruct(int count) {
		state = count;
		if(count == 4 || count == 6){
			dwellTime = new int[4];
			power = new int[4];
			enable = new byte[4];
		}else{
			dwellTime = new int[1];
			power = new int[1];
			enable = new byte[count];
		}
	}
}
