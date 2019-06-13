package com.sanleng.dangerouscabinet.fid.dao;

import com.sanleng.dangerouscabinet.fid.entity.AntStruct;
import com.sanleng.dangerouscabinet.fid.entity.FrequencyPoint;
import com.sanleng.dangerouscabinet.fid.service.CallBack;
import com.sanleng.dangerouscabinet.fid.service.CallBackStopReadCard;

import java.util.Map;

public interface ReaderDao {
	Reader serialPortConnect(String portName, int baudRate);

	boolean disconnect(Reader reader);
	
	String version(Reader reader);
	
	@Deprecated
	boolean invOnce(Reader reader, CallBack callBack);

	@Deprecated
	boolean beginInv(Reader reader, CallBack callBack);

	@Deprecated
	boolean stopInv(Reader reader, CallBackStopReadCard callBackStopReadCard);
	
	AntStruct getAnt(Reader reader);
	
	boolean setAnt(Reader reader, AntStruct ant);
	
	boolean writeTagData(Reader reader, int bank, int begin, int length, String data, byte[] password);

	String readTagData(Reader reader, byte bank, byte begin, byte length, byte[] password);

	boolean lockTag(Reader reader, byte locktType, byte lockBank, byte[] password);

	int getBuzzer(Reader reader);

	boolean setBuzzer(Reader reader, byte state);

	boolean setWorkMode(Reader reader, int mode);

	int getWorkMode(Reader reader);

	boolean setTrigModeDelayTime(Reader reader, byte trigTime);

	int getTrigModeDelayTime(Reader reader);

	String getDeviceNo(Reader reader);

	boolean setDeviceNo(Reader reader, int deviceNo);

	boolean setDeviceConfig(Reader reader, byte[] para);

	boolean killTag(Reader reader, byte[] accessPwd, byte[] killPwd);

	boolean setOutputMode(Reader reader, byte outputMode);

	int getOutputMode(Reader reader);

	Map<String,Byte> getNeighJudge(Reader reader);

	boolean setNeighJudge(Reader reader, byte neighJudgeTime);

	int getRelayAutoState(Reader reader);

	boolean setRelayAutoState(Reader reader, byte time);

	/**************2018-12-03 new add ****************/

	boolean invOnceV2(Reader reader, CallBack callBack);

	boolean beginInvV2(Reader reader, CallBack callBack);

	boolean stopInvV2(Reader reader, CallBackStopReadCard callBackStopReadCard);

	Map<String, Integer> getInvPatternConfig(Reader reader);

	boolean setInvPatternConfig(Reader reader, byte session, byte qValue, byte tagFocus, byte abValue);

	boolean factoryDataReset(Reader reader);

	Map<String, Boolean> getInvOutPutData(Reader reader);

	boolean setInvOutPutData(Reader reader, byte antenna, byte rssi, byte deviceNo, byte accessDoorDirection);

	Map<String,Byte> getAntState(Reader reader);

	FrequencyPoint getFrequency(Reader reader);

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
