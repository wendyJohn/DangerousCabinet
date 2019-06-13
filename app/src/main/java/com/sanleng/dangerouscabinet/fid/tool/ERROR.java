package com.sanleng.dangerouscabinet.fid.tool;

/**
 * 
 * @author createData 2017-08-03
 * 
 */
public class ERROR {
	/**
	 * 读卡器返回正确结果
	 */
	public final static int R_OK = 0;
	/**
	 * 读卡器返回错误码
	 */
	public final static byte HOST_ERROR	= (byte) 0x80;
	/**
	 * 无效主机字符串
	 */
	public final static byte INVALID_HOST_IPADDRESS = -2;
	/**
	 * 无效串口号
	 */
	public final static byte INVALID_COMM_PORT = -3;
	/**
	 * 无效端口号或者波特率
	 */
	public final static byte INVALID_HOST_PORTORBANDRATE = -4;
	/**
	 * 空指针
	 */
	public final static byte INVALID_POINTER = -5;
	/**
	 * 无效的通信模式
	 */
	public final static byte INVALID_COMM_MODE = -6;
	/**
	 * 无效区域号
	 */
	public final static byte INVALID_BANK = -7;
	/**
	 * 无效的起始位置和大小
	 */
	public final static byte INVALID_BEGIN_SIZE = -8;
	/**
	 * 无效口令
	 */
	public final static byte INVALID_PASSWORD = -9;
	/**
	 * 无效访问口令
	 */
	public final static byte INVALID_ACCESS_PASSWORD = -10;
	/**
	 * 无效灭活口令
	 */
	public final static byte INVALID_KILL_PASSWORD = -11;
	/**
	 * 无效操作码
	 */
	public final static byte INVALID_OPCODE = -12;
	/**
	 * 无效的端口
	 */
	public final static byte INVALID_PORT_VALUE = -13;
	/**
	 * 无效状态值
	 */
	public final static byte INVALID_STATE_VALUE = -14;
	/**
	 * 无效模式值
	 */
	public final static byte INVALID_MODE_VALUE = -24;
	/**
	 * 无效日期
	 */
	public final static byte INVALID_CLOCK = -25;
	/**
	 * 无效参数
	 */
	public final static byte INVALID_PARAMETERS = -28;
	/**
	 * 网络连接失败
	 */
	public final static byte SOKET_CONNECT_FAIL = -15;
	/**
	 * 串口连接失败
	 */
	public final static byte COM_CONNECT_FAIL = -16;
	/**
	 * 获取天线失败
	 */
	public final static byte ERROR_GET_ANT = -26;
	/**
	 * 网络初始化错
	 */
	public final static byte ERROR_NET_INIT = -17;
	/**
	 * 串口初始化错
	 */
	public final static byte ERROR_COM_INIT = -18;
	/**
	 * 设备发送错
	 */
	public final static byte ERROR_DEV_SEND = -19;
	/**
	 * 设备接收错
	 */
	public final static byte ERROR_DEV_RECV = -20;
	/**
	 * 设备未连接
	 */
	public final static byte ERROR_DEV_CONNECT = -21;
	/**
	 * 操作失败
	 */
	public final static byte ERROR_OPER_FAIL = -23;
	/**
	 * 打开串口错
	 */
	public final static byte ERROR_OPEN_COMM = -24;
	/**
	 * 设置串口缓冲区
	 */
	public final static byte ERROR_SET_COMMBUFFER = -25;
	/**
	 * 设置串口超时结构
	 */
	public final static byte ERROR_SET_COMMTIMEOUT = -26;
	/**
	 * 设置DCB出错
	 */
	public final static byte ERROR_SET_DCB = -27;
	/**
	 * 错误结果
	 */
	public static int R_FAIL = -1;
	/**
	 * 全局变量，错误号
	 */
	public static int ErrorMessage = 0;

	public static String format(byte btErrorCode) {
		String strErrorCode = "";
		switch (btErrorCode) {
		case 16:
			strErrorCode = "";
			break;
		default:
			strErrorCode = "未知错误";
		}
		return strErrorCode;
	}
}