package com.sanleng.dangerouscabinet.fid.entity;

import android.content.Context;

import com.sanleng.dangerouscabinet.MyApplication;
import com.sanleng.dangerouscabinet.fid.service.CallBacks;
import com.sanleng.dangerouscabinet.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class Balance implements RS232ReadCallback {
    private volatile static Balance instance;
    private static final String TAG = Balance.class
            .getSimpleName();
    private String file = "/dev/ttysWK2";
    private int baud = 9600;
    private int bits = 8;
    private char event = 0;
    private int stopbits = 1;
    RS232Controller rs232Controller = new RS232Controller();
    private Boolean result = true;
    private static final byte OPEN = 0X02;
    public static InputStream in = null;
    private int tmp = 0;
    private byte[] locker = new byte[8];
    public static synchronized Balance getInstance() {
        if (instance == null) {
            synchronized (Balance.class) {
                instance = new Balance();
            }
        }
        return instance;
    }

    public void init() {
        rs232Controller = RS232Controller.getInstance();
        connect();
    }

    private void connect() {
        if (null == rs232Controller) {
            rs232Controller = RS232Controller.getInstance();
        }
        if (rs232Controller != null) {
            // 通过RS232Controller实例获取串口
            int flag = rs232Controller.Rs232_Open(file, baud, bits, event,
                    stopbits, this);
            if (flag == 0) {
                LogUtil.d(TAG, "connect_Success");
            } else {
                LogUtil.d(TAG, "connect_Failure");
            }
        }
    }


    public void disconnect() {
        if (null != rs232Controller) {
            rs232Controller.Rs232_Close();
            rs232Controller = null;
        }
    }

    private void send(byte[] data) {
        rs232Controller.Rs232_Write(data);
    }

    public void open(int id) {
        byte[] bc = {(byte) 0x68, 0X01, 0X01, 0X02, 0X00, 0X00, (byte) 0x16};
        result = true;
        bc[2] = (byte) (id);
        bc[3] = OPEN;
        send(bc);
    }

    @Override
    public void RS232_Read(byte[] data) {
        try {
            tmp = data.length;
            if (tmp == 7) {
                if ((data[0] == (byte) 0x68) && (data[6] == (byte) 0x16)) {
                    {
                        int id = (int) data[2] - 1;
                        if (data[3] == 0x02)  // 开锁操作反馈状态
                        {
                            if ((data[4] == 0x00) && (data[5] == 0x00)) {
                                locker[id] = 0;
                            } else {
                                locker[id] = 1;
                            }
                        }
                        if (data[3] == 0x04)  // 查询操作反馈状态
                        {
                            if ((data[4] == 0x00) && (data[5] == 0x00)) {
                                locker[id] = 0;
                            } else {
                                locker[id] = 1;
                            }
                        }
                    }
                }

                int i, locksum = 0;
                for (i = 0; i < locker.length; i++) {
                    locksum = locksum + locker[i];
                }

                if (locksum == 8) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                try {
                    String balancedata=new String(data, "GB2312");
                    MessageEvent messageEvent = new MessageEvent(MyApplication.MESSAGE_BANLANCEDATA);
                    messageEvent.setMessage(balancedata);
                    EventBus.getDefault().post(messageEvent);
                } catch (UnsupportedEncodingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
