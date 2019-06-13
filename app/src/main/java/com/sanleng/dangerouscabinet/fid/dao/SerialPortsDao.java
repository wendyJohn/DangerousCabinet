package com.sanleng.dangerouscabinet.fid.dao;

import java.util.List;

import android_serialport_api.SerialPortDevice;


public interface SerialPortsDao {

	List<String> findSerialPorts();

	SerialPortDevice open(String port, int baudrate);

	boolean send(SerialPortDevice serialPorts, byte[] data);

	byte[] read(SerialPortDevice serialPorts);

	void close(SerialPortDevice serialPorts);
}
