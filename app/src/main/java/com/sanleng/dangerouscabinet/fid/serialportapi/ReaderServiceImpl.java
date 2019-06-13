package com.sanleng.dangerouscabinet.fid.serialportapi;

import com.sanleng.dangerouscabinet.fid.dao.Reader;
import com.sanleng.dangerouscabinet.fid.dao.ReaderDao;
import com.sanleng.dangerouscabinet.fid.dao.ReaderDaoImpl;
import com.sanleng.dangerouscabinet.fid.entity.AntStruct;
import com.sanleng.dangerouscabinet.fid.entity.FrequencyPoint;
import com.sanleng.dangerouscabinet.fid.service.CallBack;
import com.sanleng.dangerouscabinet.fid.service.CallBackStopReadCard;
import com.sanleng.dangerouscabinet.fid.service.ReaderService;

import java.util.Map;

public class ReaderServiceImpl implements ReaderService {

	ReaderDao dao = new ReaderDaoImpl();

	@Override
	public Reader serialPortConnect(String portName, int baudRate) {
		return dao.serialPortConnect(portName,baudRate);
	}

	@Override
	public boolean disconnect(Reader reader) {
		if(null == reader){
			return false;
		}
		return dao.disconnect(reader);
	}

	@Override
	public String version(Reader reader) {
		if(null == reader){
			return null;
		}
		return dao.version(reader);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean invOnce(Reader reader,CallBack callBack) {
		if(null == reader){
			return false;
		}
		return dao.invOnce(reader,callBack);
	}
	
	@Override
	@Deprecated
	public boolean beginInv(Reader reader,CallBack getReadData) {
		if(null == reader){
			return false;
		}
		return dao.beginInv(reader,getReadData);
	}

	@Override
	@Deprecated
	public boolean stopInv(Reader reader,CallBackStopReadCard callBackStopReadCard) {
		if(null == reader){
			return false;
		}
		return dao.stopInv(reader,callBackStopReadCard);
	}
	
	@Override
	public AntStruct getAnt(Reader reader) {
		if(null == reader){
			return null;
		}
		return dao.getAnt(reader);
	}

	@Override
	public boolean setAnt(Reader reader,AntStruct ant) {
		if(null == reader){
			return false;
		}
		return dao.setAnt(reader, ant);
	}

	@Override
	public boolean writeTagData(Reader reader, int bank, int begin, int length, String data, byte[] password) {
		if(null == reader){
			return false;
		}
		return dao.writeTagData(reader, bank, begin, length, data, password);
	}
	
	@Override
	public String readTagData(Reader reader, byte bank, byte begin, byte length, byte[] password) {
		if(null == reader){
			return null;
		}
		return dao.readTagData(reader,bank,begin,length,password);
	}

	@Override
	public boolean lockTag(Reader reader, byte locktType, byte lockBank,byte[] password) {
		if(null == reader){
			return false;
		}
		return dao.lockTag(reader, locktType, lockBank, password);
	}

	@Override
	public int getBuzzer(Reader reader) {
		if(null == reader){
			return -1;
		}
		return dao.getBuzzer(reader);
	}

	@Override
	public boolean setBuzzer(Reader reader, byte state) {
		if(null == reader){
			return false;
		}
		return dao.setBuzzer(reader, state);
	}

	@Override
	public boolean setWorkMode(Reader reader, int mode) {
		if(null == reader){
			return false;
		}
		return dao.setWorkMode(reader, mode);
	}

	@Override
	public int getWorkMode(Reader reader) {
		if(null == reader){
			return -1;
		}
		return dao.getWorkMode(reader);
	}

	@Override
	public boolean setTrigModeDelayTime(Reader reader, byte trigTime) {
		if(null == reader){
			return false;
		}
		return dao.setTrigModeDelayTime(reader, trigTime);
	}

	@Override
	public int getTrigModeDelayTime(Reader reader) {
		if(null == reader){
			return -1;
		}
		return dao.getTrigModeDelayTime(reader);
	}


	@Override
	public String getDeviceNo(Reader reader) {
		if(null == reader){
			return null;
		}
		return dao.getDeviceNo(reader);
	}

	@Override
	public boolean setDeviceNo(Reader reader, int deviceNo) {
		if(null == reader){
			return false;
		}
		return dao.setDeviceNo(reader, deviceNo);
	}

	@Override
	public boolean killTag(Reader reader, byte[] accessPwd, byte[] killPwd) {
		if(null == reader){
			return false;
		}
		return dao.killTag(reader, accessPwd, killPwd);
	}

	@Override
	public boolean setOutputMode(Reader reader, byte outputMode) {
		if(null == reader){
			return false;
		}
		return dao.setOutputMode(reader, outputMode);
	}

	@Override
	public int getOutputMode(Reader reader) {
		if(null == reader){
			return -1;
		}
		return dao.getOutputMode(reader);
	}

	@Override
	public Map<String,Byte> getNeighJudge(Reader reader) {
		if(null == reader){
			return null;
		}
		return dao.getNeighJudge(reader);
	}

	@Override
	public boolean setNeighJudge(Reader reader, byte neighJudgeTime) {
		if(null == reader){
			return false;
		}
		return dao.setNeighJudge(reader, neighJudgeTime);
	}

	@Override
	public int getRelayAutoState(Reader reader) {
		if(null == reader){
			return -1;
		}
		return dao.getRelayAutoState(reader);
	}

	@Override
	public boolean setRelayAutoState(Reader reader, byte time) {
		if(null == reader){
			return false;
		}
		return dao.setRelayAutoState(reader, time);
	}
    /*********************2018-11-30 新增协议 zhu********************************/
	@Override
	public boolean invOnceV2(Reader reader, CallBack callBack) {
		if(null == reader){
			return false;
		}
		return dao.invOnceV2(reader, callBack);
	}
	@Override
	public boolean beginInvV2(Reader reader, CallBack callBack) {
		if(null == reader){
			return false;
		}
		return dao.beginInvV2(reader,callBack);
	}

	@Override
	public boolean stopInvV2(Reader reader,	CallBackStopReadCard callBackStopReadCard) {
		if(null == reader){
			return false;
		}
		return dao.stopInvV2(reader,callBackStopReadCard);
	}

	/**
	 * 获取寻卡模式配置_2018-11-30 new add
	 * (byte session,byte qValue,byte tagFocus,byte abValue)
	 */
	@Override
	public Map<String, Integer> getInvPatternConfig(Reader reader) {
		if(null == reader){
			return null;
		}
		return dao.getInvPatternConfig(reader);
	}

	@Override
	public boolean setInvPatternConfig(Reader reader, byte session,byte qValue, byte tagFocus, byte abValue) {
		if(null == reader){
			return false;
		}
		return dao.setInvPatternConfig(reader, session, qValue, tagFocus, abValue);
	}

	@Override
	public boolean factoryDataReset(Reader reader) {
		if(null == reader){
			return false;
		}
		return dao.factoryDataReset(reader);
	}

	/**
	 * 读取循卡输出数据_2018-11-30 new add
	 * (byte antenna,byte rssi,byte deviceNo,byte accessDoorDirection)
	 */
	@Override
	public Map<String, Boolean> getInvOutPutData(Reader reader) {
		if(null == reader){
			return null;
		}
		return dao.getInvOutPutData(reader);
	}

	@Override
	public boolean setInvOutPutData(Reader reader, byte antenna, byte rssi,byte deviceNo, byte accessDoorDirection) {
		if(null == reader){
			return false;
		}
		return dao.setInvOutPutData(reader, antenna, rssi, deviceNo, accessDoorDirection);
	}

	@Override
	public Map<String,Byte> getAntState(Reader reader) {
		if(null == reader){
			return null;
		}
		return dao.getAntState(reader);
	}

	@Override
	public FrequencyPoint getFrequency(Reader reader) {
		if(null == reader){
			return null;
		}
		return dao.getFrequency(reader);
	}

	@Override
	public boolean setFrequency(Reader reader,int type,double frequencyFixed,boolean[] frequencyHopping) {
		if(null == reader){
			return false;
		}
		return dao.setFrequency(reader,type,frequencyFixed,frequencyHopping);
	}

	@Override
	public boolean setGPIO(Reader reader,byte port, byte state){
		if(null == reader){
			return false;
		}
		return dao.setGPIO(reader,port,state);
	}

	@Override
	public Map<String, Boolean> getGPIO(Reader reader) {
		if(null == reader){
			return null;
		}
		return dao.getGPIO(reader);
	}
}
