package com.sanleng.dangerouscabinet.fid.dao;

import com.sanleng.dangerouscabinet.fid.entity.AntStruct;
import com.sanleng.dangerouscabinet.fid.entity.FrequencyPoint;
import com.sanleng.dangerouscabinet.fid.entity.Multichannel16_32Ant;
import com.sanleng.dangerouscabinet.fid.entity.MultichannelAnt4;
import com.sanleng.dangerouscabinet.fid.service.CallBack;
import com.sanleng.dangerouscabinet.fid.service.CallBackStopReadCard;
import com.sanleng.dangerouscabinet.fid.tool.RFIDException;

import java.nio.ByteBuffer;
import java.util.Map;

public class ReaderDaoImpl implements ReaderDao {
	
	@Override
	public Reader serialPortConnect(String portName, int baudRate) {
		return new Reader().serialPortConnect(portName, baudRate);
	}

	@Override
	public boolean disconnect(Reader reader) {
		return reader.disconnect(reader);
	}

	@Override
	public String version(Reader reader) {
		return reader.version(reader);
	}

	@Override
	@Deprecated
	public boolean invOnce(Reader reader,CallBack callBack) {
		return reader.invOnce(reader,callBack);
	}

	@Override
	@Deprecated
	public boolean beginInv(Reader reader,CallBack CallBack) {
		return reader.beginInv(reader,CallBack);
	}

	@Override
	@Deprecated
	public boolean stopInv(Reader reader,CallBackStopReadCard callBackStopReadCard) {
		return reader.stopInv(reader,callBackStopReadCard);
	}


	
	@Override
	public AntStruct getAnt(Reader reader) {
		if (null == reader) {
			return null;
		}
		if (!reader.deviceConnected) {
			return null;
		}
		AntStruct struct = new AntStruct(reader.getChannel());
		ByteBuffer total = ByteBuffer.allocate(100);
		reader.getAnt(reader, total);
		byte[] buffer = total.array();
		if (struct.state == 4 || struct.state == 6) {
			return new MultichannelAnt4().ant4(reader, buffer);
		} else {
			return new Multichannel16_32Ant().ant32(reader, buffer);
		}
	}

	@Override
	public boolean setAnt(Reader reader, AntStruct ant) {
		return reader.setAnt(reader, ant);
	}

	@Override
	public boolean writeTagData(Reader reader, int bank, int begin, int length, String data, byte[] password) {
		if (null == reader) {
			return false;
		}
		if (bank < 0 || begin < 0 || length < 0) {
			try {
				throw new RFIDException("bank/begin/length Must be a positive integer！");
			} catch (RFIDException e) {
				e.printStackTrace();
			}
		}
		if (bank > 3) {
			try {
				throw new RFIDException("bank cannot be anything but 0-3");
			} catch (RFIDException e) {
				e.printStackTrace();
			}
		}
		if (bank == 1 && (begin + length > 8 || begin < 2)) {
			try {
				throw new RFIDException("When writing the contents of the EPC region, begin must start at 2, and begin(write the address in the region) + length(the length to be written) has a value of no more than 8.Check the input parameter values");
			} catch (RFIDException e) {
				e.printStackTrace();
			}
		}
		if (bank == 0 && (begin + length > 4)) {
			try {
				throw new RFIDException("When writing the tenure contents, begin(the address in the write field)+length(the length to be written) does not exceed 4.Check the input parameter values");
			} catch (RFIDException e) {
				e.printStackTrace();
			}
		}
		if (data.length() != 4 * length) {
			try {
				throw new RFIDException("The length of data must be length * 4");
			} catch (RFIDException e) {
				e.printStackTrace();
			}
		}
		if (password == null || "".equals(password)) {
			for (int i = 0; i < 4; ++i) {
				password[0] = (byte) 0;
			}
		}
		return reader.writeTagData(reader, bank, begin, length, data, password);
	}

	@Override
	public boolean lockTag(Reader reader, byte locktType, byte lockBank,byte[] password) {
		return reader.lockTag(reader, locktType, lockBank, password);
	}

	@Override
	public int getBuzzer(Reader reader) {
		return reader.getBuzzer(reader);
	}

	@Override
	public boolean setBuzzer(Reader reader, byte state) {
		return reader.setBuzzer(reader, state);
	}

	@Override
	public boolean setWorkMode(Reader reader, int mode) {
		return reader.setWorkMode(reader, mode);
	}

	@Override
	public int getWorkMode(Reader reader) {
		return reader.getWorkMode(reader);
	}

	@Override
	public boolean setTrigModeDelayTime(Reader reader, byte trigTime) {
		return reader.setTrigModeDelayTime(reader, trigTime);
	}

	@Override
	public int getTrigModeDelayTime(Reader reader) {
		return reader.getTrigModeDelayTime(reader);
	}

	@Override
	public Map<String,Byte> getNeighJudge(Reader reader) {
		return reader.getNeighJudge(reader);
	}

	@Override
	public boolean setNeighJudge(Reader reader, byte neighJudgeTime) {
		return reader.setNeighJudge(reader, neighJudgeTime);
	}

	@Override
	public String getDeviceNo(Reader reader) {
		return reader.getDeviceNo(reader);
	}

	@Override
	public boolean setDeviceNo(Reader reader, int deviceNo) {
		return reader.setDeviceNo(reader, deviceNo);
	}

	@Override
	public int getOutputMode(Reader reader) {
		return reader.getOutputMode(reader);
	}

	@Override
	public boolean setOutputMode(Reader reader, byte outputMode) {
		return reader.setOutputMode(reader, outputMode);
	}

	@Override
	public boolean killTag(Reader reader, byte[] accessPwd, byte[] killPwd) {
		return reader.killTag(reader, accessPwd, killPwd);
	}

	@Override
	public int getRelayAutoState(Reader reader) {
		return reader.getRelayAutoState(reader);
	}

	@Override
	public boolean setRelayAutoState(Reader reader, byte time) {
		return reader.setRelayAutoState(reader, time);
	}

	@Override
	public boolean setDeviceConfig(Reader reader, byte[] para) {
		return reader.setDeviceConfig(reader, para);
	}

	@Override
	public String readTagData(Reader reader, byte bank, byte begin, byte length, byte[] password) {
		return reader.readTagData(reader, bank, begin, length,password);
	}
	
	/**************2018-12-03 new add ****************/
	
	@Override
	public boolean invOnceV2(Reader reader, CallBack callBack) {
		return reader.invOnceV2(reader,callBack);
	}

	@Override
	public boolean beginInvV2(Reader reader, CallBack callBack) {
		return reader.beginInvV2(reader,callBack);
	}

	@Override
	public boolean stopInvV2(Reader reader,CallBackStopReadCard callBackStopReadCard) {
		return reader.stopInvV2(reader,callBackStopReadCard);
	}
	
	@Override
	public Map<String, Integer> getInvPatternConfig(Reader reader) {
		return reader.getInvPatternConfig(reader);
	}

	@Override
	public boolean setInvPatternConfig(Reader reader, byte session,	byte qValue, byte tagFocus, byte abValue) {
		return reader.setInvPatternConfig(reader, session,qValue, tagFocus, abValue);
	}

	@Override
	public boolean factoryDataReset(Reader reader) {
		return reader.factoryDataReset(reader);
	}

	@Override
	public Map<String, Boolean> getInvOutPutData(Reader reader) {
		return reader.getInvOutPutData(reader);
	}

	@Override
	public boolean setInvOutPutData(Reader reader, byte antenna, byte rssi,byte deviceNo, byte accessDoorDirection) {
		return reader.setInvOutPutData(reader, antenna, rssi,deviceNo, accessDoorDirection);
	}

	@Override
	public Map<String,Byte> getAntState(Reader reader) {
		return reader.getAntState(reader);
	}

	@Override
	public FrequencyPoint getFrequency(Reader reader) {
		return reader.getFrequency(reader);
	}

	@Override
	public boolean setFrequency(Reader reader,int type,double frequencyFixed,boolean[] frequencyHopping) {
		return reader.setFrequency(reader,type,frequencyFixed,frequencyHopping);
	}

	@Override
	public boolean setGPIO(Reader reader,byte port, byte state) {
		return reader.setGPIO(reader, port, state);
	}

	@Override
	public Map<String, Boolean> getGPIO(Reader reader) {
		return reader.getGPIO(reader);
	}
}
