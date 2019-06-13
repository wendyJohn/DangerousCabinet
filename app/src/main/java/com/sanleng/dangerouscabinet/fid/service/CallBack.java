package com.sanleng.dangerouscabinet.fid.service;

/**
 * 获取读取数据接口
 * 
 * @author zhuQixiang createDate 2017-10-25
 * 
 */
public interface CallBack {
	/**
	 * 循环读卡或者寻卡一次回调函数
	 * 
	 * @param data
	 *            电子标签数据
	 * @param antNo
	 *            天线编号
	 */
	@Deprecated
	void readData(String data, String antNo);

	/**
	 * 新增协议2018-11-30 循环读卡或者寻卡一次回调函数
	 * 
	 * @param data
	 *            EPC
	 * @param rssi
	 *            RSSI
	 * @param antNo
	 *            天线号 * @param deviceNo 设备号 * @param direction 方向
	 */
	void readData(String data, String rssi, String antNo, String deviceNo, String direction);
}
