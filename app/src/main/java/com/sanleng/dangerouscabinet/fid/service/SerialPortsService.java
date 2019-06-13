package com.sanleng.dangerouscabinet.fid.service;

import java.util.List;

import android_serialport_api.SerialPortDevice;


public interface SerialPortsService {
	public List<String> findSerialPorts();

	public SerialPortDevice open(String port, int baudrate);

	public void close(SerialPortDevice serialPorts);

	public boolean send(SerialPortDevice serialPorts, byte[] data);

	public byte[] read(SerialPortDevice serialPorts);
}
