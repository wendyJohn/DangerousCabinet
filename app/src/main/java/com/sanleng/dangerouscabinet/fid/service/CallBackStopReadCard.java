package com.sanleng.dangerouscabinet.fid.service;



/**
 * 获取读取数据接口
 * 
 * @author zhuQixiang createDate 2017-10-25
 * 
 */
public interface CallBackStopReadCard {
	/**
	 * 循环读卡或者寻卡一次回调函数
	 * 
	 * @param data
	 *            电子标签数据
	 * @param antNo
	 *            天线编号s
	 */
	void stopReadCard(boolean result);
}
