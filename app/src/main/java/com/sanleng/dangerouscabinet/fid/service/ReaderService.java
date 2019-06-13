package com.sanleng.dangerouscabinet.fid.service;

import com.sanleng.dangerouscabinet.fid.dao.Reader;
import com.sanleng.dangerouscabinet.fid.entity.AntStruct;
import com.sanleng.dangerouscabinet.fid.entity.FrequencyPoint;

import java.util.Map;

public interface ReaderService {
	/**
	 * 1.串口连接
	 * @param portName
	 * @param baudRate
	 * @return Reader|null
	 */
	Reader serialPortConnect(String portName, int baudRate);
	/**
	 * 2.断开连接
	 * @param reader
	 * @return true|false
	 */
	boolean disconnect(Reader reader);

	/**
	 * 3.获取版本号
	 * @param reader
	 * @return value|null
	 */
	String version(Reader reader);
	/**
	 * 4.单次寻卡
	 * @param reader
	 * @return true|false
	 */
	@Deprecated
	boolean invOnce(Reader reader, CallBack callBack);

	/**
	 * 5.连续寻卡
	 * @param reader
	 * @return true|false
	 */
	@Deprecated
	boolean beginInv(Reader reader, CallBack callBack);
	/**
	 * 6.停止寻卡
	 * @param reader
	 * @return true|false
	 */
	@Deprecated
	boolean stopInv(Reader reader, CallBackStopReadCard callBackStopReadCard);
	/**
	 * 7.获取天线
	 * @param reader
	 * @return AntStruct|null
	 */
	AntStruct getAnt(Reader reader);
	/**
	 * 8.设置天线
	 * @param reader
	 * @param ant 天线号
	 * @return true|false
	 */
	boolean setAnt(Reader reader, AntStruct ant);
	/**
	 * 9.指定区域写入数据
	 * @param reader
	 * @param bank
	 * @param begin
	 * @param length
	 * @param data
	 * @param password
	 * @return true|false
	 */
	boolean writeTagData(Reader reader, int bank, int begin, int length, String data, byte[] password);
	/**
	 * 10.指定区域读取数据
	 * @param reader
	 * @param bank 区域
	 * @param begin 起始地址
	 * @param length 长度
	 * @param password
	 * @return value|null
	 */
	String readTagData(Reader reader, byte bank, byte begin, byte length, byte[] password);
	/**
	 * 11.锁标签
	 * @param reader
	 * @param lockType
	 * @param lockBank
	 * @param password
	 * @return ture|false
	 */
	boolean lockTag(Reader reader, byte lockType, byte lockBank, byte[] password);
	/**
	 * 12.获取蜂鸣器状态(0.关闭|1.打开)
	 * @param reader
	 * @return
	 */
	int getBuzzer(Reader reader);
	/**
	 * 13.设置蜂鸣器状态(0.关闭|1.打开)
	 * @param reader
	 * @param state
	 * @return ture|false
	 */
	boolean setBuzzer(Reader reader, byte state);
	/**
	 * 14.设置工作模式
	 * @param reader
	 * @param mode (01主从模式；02定时模式；03触发模式)
	 * @return true|false
	 */
	boolean setWorkMode(Reader reader, int mode);
	/**
	 * 15.读取工作模式
	 * @param reader
	 * @return value(01主从模式；02定时模式；03触发模式)|-1
	 */
	int getWorkMode(Reader reader);
	/**
	 * 16.设定触发延时
	 * @param reader
	 * @param trigTime
	 * @return true|false
	 */
	boolean setTrigModeDelayTime(Reader reader, byte trigTime);
	/**
	 * 17.读取触发延时
	 * @param reader
	 * @return value|-1
	 */
	int getTrigModeDelayTime(Reader reader);
	/**
	 * 18.读取设备号
	 * @param reader
	 * @return value|null
	 */
	String getDeviceNo(Reader reader);
	/**
	 * 19.设置设备号
	 * @param reader
	 * @param deviceNo
	 * @return true|false
	 */
	boolean setDeviceNo(Reader reader, int deviceNo);
	/**
	 * 20.销毁标签
	 * @param reader
	 * @param accessPwd  访问密码
	 * @param killPwd	  销毁密码
	 * @return 	true|false
	 */
	boolean killTag(Reader reader, byte[] accessPwd, byte[] killPwd);
	/**
	 * 21.设置输出口模式
	 * @param reader
	 * @param outputMode(1 COM 2 NET 3 RS485 4 WIFI/USB->COM)
	 * @return true|false
	 */
	boolean setOutputMode(Reader reader, byte outputMode);
	/**
	 * 22.获取输出口模式
	 * @param reader
	 * @return value|-1
	 */
	int getOutputMode(Reader reader);
	/**
	 * 23.获取相邻判别
	 * @param reader
	 * @param trigTime  neighJudgeTime
	 * @return
	 */
	Map<String,Byte> getNeighJudge(Reader reader);
	/**
	 * 24.设置相邻判别
	 * @param reader
	 * @param neighJudgeTime
	 * @return true|false
	 */
	boolean setNeighJudge(Reader reader, byte neighJudgeTime);
	/**
	 * 25.获取触发延时
	 * @param reader
	 * @param state
	 */
	int getRelayAutoState(Reader reader);
	/**
	 * 26.设置触发延时
	 * @param reader
	 * @param time
	 * @return
	 */
	boolean setRelayAutoState(Reader reader, byte time);

	/**
	 * 27.单次寻卡_2018-11-30 new add
	 * @param reader
	 * @return true|false
	 */
	boolean invOnceV2(Reader reader, CallBack callBack);

	/**
	 * 28.连续寻卡_2018-11-30 new add
	 * @param reader
	 * @return true|false
	 */

	boolean beginInvV2(Reader reader, CallBack callBack);
	/**
	 * 29.停止寻卡_2018-11-30 new add
	 * @param reader
	 * @return true|false
	 */
	boolean stopInvV2(Reader reader, CallBackStopReadCard callBackStopReadCard);

	/**
	 * 30.获取寻卡模式配置_2018-11-30 new add
	 * @param reader
	 * @return (byte session,byte qValue,byte tagFocus,byte abValue)
	 */
	Map<String,Integer> getInvPatternConfig(Reader reader);

	/**
	 * 31.设置寻卡模式配置_2018-11-30 new add
	 * @param reader
	 * @param session
	 * @param qValue
	 * @param tagFocus
	 * @param abValue
	 * @return true|false
	 */
	boolean setInvPatternConfig(Reader reader, byte session, byte qValue, byte tagFocus, byte abValue);

	/**
	 * 32.恢复出厂设置_2018-11-30 new add
	 * @param reader
	 * @return true|false
	 */
	boolean factoryDataReset(Reader reader);

	/**
	 * 33.读取循卡输出数据格式_2018-11-30 new add
	 * @param reader
	 * @return (Boolean antenna,Boolean rssi,Boolean deviceNo,Boolean accessDoorDirection)
	 */
	Map<String,Boolean> getInvOutPutData(Reader reader);

	/**
	 * 34.配置循卡输出数据_2018-11-30 new add
	 * @param reader
	 * @param antenna
	 * @param rssi
	 * @param deviceNo
	 * @param accessDoorDirection
	 * @return true|false
	 */
	boolean setInvOutPutData(Reader reader, byte antenna, byte rssi, byte deviceNo, byte accessDoorDirection);

	/**
	 * 35.获取天线状态_2018-11-30 new add
	 * @param reader
	 * @return Map<String,Boolean>
	 */
	Map<String,Byte> getAntState(Reader reader);

	/**
	 * 36.读取自定义下的当前频点_2018-11-30 new add
	 * @param reader
	 * @return list<byte>
	 */
	FrequencyPoint getFrequency(Reader reader);

	/**
	 * 37.设置自定义下的当前频点_2018-11-30 new add
	 *
	 * @param reader
	 * @param type
	 *            1.美标(920.00-928.00)  |
	 *            2.国标1(920.50-924.50) |
	 *            3.国标2(840.50-844.50) |
	 *            4.欧标(866.00-867.50)  |
	 *            5.自定义频点(跳频) |
	 *            6.自定义频点(定频) |
	 * @param frequencyData
	 * 				type = 5   (byte[7]  Or 50bit(1byte = 8bit)) = boolean[50]|
	 * 				type = 6   (byte[3]  = double)|
	 * 				other type (null)
	 * @return true|false
	 */
	boolean setFrequency(Reader reader, int type, double frequencyFixed, boolean[] frequencyHopping);

	/**
	 * SET General Purpose Input/Output
	 */
	boolean setGPIO(Reader reader, byte port, byte state);
	
	/**
	 * GET General Purpose Input/Output
	 */
	Map<String,Boolean> getGPIO(Reader reader);
}
