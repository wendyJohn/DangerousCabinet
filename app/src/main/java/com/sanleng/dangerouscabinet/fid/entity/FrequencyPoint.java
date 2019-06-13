package com.sanleng.dangerouscabinet.fid.entity;

import java.util.ArrayList;
import java.util.List;

public class FrequencyPoint {
	/**
	 * type:
	 *    1.美标
	 *    2.国标1
	 *    3.国标2
	 *    4.欧标
	 *    5.自定义频点(跳频)
	 *    6.自定义频点(定频)
	 */
	private int type;
	
	/**
	 * 跳频频点 902.5Mhz - 927.0Mhz
	 */
	private List<Boolean> frequencyHopping = new ArrayList<Boolean>();
	
	/**
	 * 定频频点
	 */
	private double frequencyFixed;

	/**
	 * 定频频点
	 */
	public double getFrequencyFixed() {
		return frequencyFixed;
	}

	/**
	 * 定频频点
	 */
	public void setFrequencyFixed(double frequencyFixed) {
		this.frequencyFixed = frequencyFixed;
	}

	/**
	 * type:
	 *    1.美标
	 *    2.国标1
	 *    3.国标2
	 *    4.欧标
	 *    5.自定义频点(跳频)
	 *    6.自定义频点(定频)
	 */
	public int getType() {
		return type;
	}

	/**
	 * type:
	 *    1.美标
	 *    2.国标1
	 *    3.国标2
	 *    4.欧标
	 *    5.自定义频点(跳频)
	 *    6.自定义频点(定频)
	 */
	public void setType(int type) {
		this.type = type;
	}

	public List<Boolean> getFrequencyHopping() {
		return frequencyHopping;
	}

	public void setFrequencyHopping(List<Boolean> frequencyHopping) {
		this.frequencyHopping = frequencyHopping;
	}
}
