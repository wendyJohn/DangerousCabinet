
package com.sanleng.dangerouscabinet.fid.entity;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPortDevice;

public class RS232Controller {
    private static final String TAG = RS232Controller.class.getSimpleName();
    private static RS232Controller rs232Controller = null;
    private SerialPortDevice mSerialPort = null;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private RS232ReadCallback rs232ReadCallback;
    private ReadThread mReadThread = null;

    private String mFile;
    private int mBaud;
    private int mBits;
    private char mEvent;
    private int mStopBits;
    private boolean begin;

    public static RS232Controller getInstance() {
        LogUtil.d(TAG, "getInstance");

        if (null == rs232Controller) {
            rs232Controller = new RS232Controller();
        }
        return rs232Controller;
    }

    public int Rs232_Open(String file, int baud, int bits, char event,
                          int stopBits, RS232ReadCallback l) {
        LogUtil.d(TAG, "Rs232_Open");

        this.mFile = file;
        this.mBaud = baud;
        this.mBits = bits;
        this.mEvent = event;
        this.mStopBits = stopBits;
        this.rs232ReadCallback = l;
        Log.d("kaka","mEvent:"+mEvent+" mBits:"+mBits+" mStopBits:"+mStopBits);
        try {

            mSerialPort = new SerialPortDevice(file, mBaud, 0);
            mSerialPort.connect();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            begin = true;

            // 开启串口数据读线程
            this.mReadThread = new ReadThread();
            this.mReadThread.start();

            return 0;
        } catch (SecurityException e) {
            e.printStackTrace();
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void Rs232_Close() {
        LogUtil.d(TAG, "Rs232_Close");
        begin = false;
        if (mOutputStream != null || mInputStream != null) {
            try {
                mOutputStream.close();
                mInputStream.close();
                mOutputStream = null;
                mInputStream = null;
                if (null != mSerialPort) {
                    mSerialPort.close();
                    mSerialPort = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void Rs232_Write(final byte[] command) {

        if (command != null) {
            for (int i = 0; i < command.length; i++) {
//                LogUtil.d(TAG, "[" + i + "]=" + command[i]);
            }
        }

        if (null != mOutputStream && mSerialPort != null) {

            new Thread() {

                @Override
                public void run() {
                    try {
                        if (null == command) {
                            mOutputStream.write("".getBytes());
                            mOutputStream.flush();
                        } else {
                            mOutputStream.write(command);
                            mOutputStream.flush();
                        }
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            }.start();
        }
    }

    private void writeFile(File file, String value) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(value);
            writer.flush();
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void doRead() {
        int size = 0;
        try {
            if (mSerialPort != null && mInputStream != null) {
                int cout = mInputStream.available();
                byte[] buffer = new byte[cout];
                size = mInputStream.read(buffer);
                Thread.sleep(450);
                if (size > 0) {
                    if (null != rs232ReadCallback) {
                        rs232ReadCallback.RS232_Read(buffer);
                        buffer = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            while (begin) {
                try {
                    Thread.sleep(100);
                    int size = 0;
                    int coutq = 0;
                    int couth = 0;
                    if (null != mInputStream) {
                        coutq = mInputStream.available();
                    } else {
                        break;
                    }

                    while (coutq != couth) {
                        Thread.sleep(10);
                        couth = mInputStream.available();
                        Thread.sleep(10);
                        coutq = mInputStream.available();
                    }
                    byte[] buffer1 = new byte[couth];
                    size = mInputStream.read(buffer1);
                    if (size > 0) {
                        if (null != rs232ReadCallback) {
                            // 将数据回调给RS232ControllerActivity
                            rs232ReadCallback.RS232_Read(buffer1);
                            buffer1 = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
