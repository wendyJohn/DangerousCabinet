package com.sanleng.dangerouscabinet.fid.serialportapi;

import android.util.Log;

import com.sanleng.dangerouscabinet.fid.dao.SerialPortsDao;
import com.sanleng.dangerouscabinet.fid.entity.Driver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android_serialport_api.SerialPortDevice;


public class SerialPortsDaoImpl implements SerialPortsDao {
	private static final String TAG = "SerialPort";
	private Vector<Driver> mDrivers = null;

	Vector<Driver> getDrivers() throws IOException {
		if (mDrivers == null) {
			mDrivers = new Vector<Driver>();
			LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
			String l;
			while ((l = r.readLine()) != null) {
				// Issue 3:
				// Since driver name may contain spaces, we do not extract
				// driver name with split()
				String drivername = l.substring(0, 0x15).trim();
				String[] w = l.split(" +");
				if ((w.length >= 5) && (w[w.length - 1].equals("serial"))) {
					Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length - 4]);
					mDrivers.add(new Driver(drivername, w[w.length - 4]));
				}
			}
			r.close();
		}
		return mDrivers;
	}

	public List<String> findSerialPorts() {
		List<String> devices = new ArrayList<String>();
		Iterator<Driver> itdriv;
		try {
			itdriv = getDrivers().iterator();
			while (itdriv.hasNext()) {
				Driver driver = itdriv.next();
				Vector<File> getDevs = driver.getDevices();
				if (getDevs == null) {
					return null;
				}
				Iterator<File> itdev = getDevs.iterator();
				while (itdev.hasNext()) {
					String device = itdev.next().getAbsolutePath();
					devices.add(device);
				}
			}
		} catch (IOException e) {
			return null;
		}
		return devices;
	}

	@Override
	public SerialPortDevice open(String port, int baudrate) {
		SerialPortDevice serialPorts = new SerialPortDevice(port, baudrate, 0);
		boolean connect = serialPorts.connect();
		if (connect) {
			return serialPorts;
		}
		return null;
	}

	@Override
	public boolean send(SerialPortDevice serialPorts, byte[] data) {
		boolean flag = false;
		OutputStream out = null;
		try {
			out = serialPorts.getOutputStream();
			out.write(data);
			Thread.sleep(100);
			out.flush();
			flag = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return flag;
	}

	@Override
	public byte[] read(SerialPortDevice serialPorts) {
		InputStream in = null;
		byte[] bytes = null;
		try {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			in = serialPorts.getInputStream();
			int bufflenth = in.available();
			while (bufflenth != 0) {
				bytes = new byte[bufflenth];
				in.read(bytes);
				bufflenth = in.available();
			}
		}catch (IOException e) {
			try {
				in.close();
			} catch (IOException e1) {

			}
			//throw new ReadDataFromSerialPortFailure();
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (IOException e) {
				//throw new SerialPortInputStreamCloseFailure();
				return null;
			}
		}
		return bytes;
	}

	@Override
	public void close(SerialPortDevice serialPorts) {
		serialPorts.close();
	}
}
