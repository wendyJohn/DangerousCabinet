package com.sanleng.dangerouscabinet.fid.serialportapi;

import com.sanleng.dangerouscabinet.fid.dao.SerialPortsDao;
import com.sanleng.dangerouscabinet.fid.service.SerialPortsService;

import java.util.List;

import android_serialport_api.SerialPortDevice;


public class SerialPortsServiceImpl implements SerialPortsService {

	SerialPortsDao dao = (SerialPortsDao) new SerialPortsDaoImpl();
	
	@Override
	public List<String> findSerialPorts() {
		return dao.findSerialPorts();
	}

	@Override
	public SerialPortDevice open(String port, int baudrate) {
		return dao.open(port, baudrate);
	}

	@Override
	public void close(SerialPortDevice serialPorts) {
		dao.close(serialPorts);
	}

	@Override
	public boolean send(SerialPortDevice serialPorts, byte[] data) {
		return dao.send(serialPorts, data);
	}

	@Override
	public byte[] read(SerialPortDevice serialPorts) {
		return dao.read(serialPorts);
	}
}
