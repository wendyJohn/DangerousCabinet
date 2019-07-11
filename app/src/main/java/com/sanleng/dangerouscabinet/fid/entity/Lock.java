package com.sanleng.dangerouscabinet.fid.entity;

import android.content.Context;
import android.os.Handler;


import com.sanleng.dangerouscabinet.MyApplication;
import com.sanleng.dangerouscabinet.fid.service.CallBacks;
import com.sanleng.dangerouscabinet.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.util.List;

public class Lock implements RS232ReadCallback {
    private volatile static Lock instance;
    private static final String TAG = Lock.class
            .getSimpleName();
    private String file = "/dev/ttysWK0";
    private int baud = 9600;
    private int bits = 8;
    private char event = 0;
    private int stopbits = 1;
    RS232Controller rs232Controllers = new RS232Controller();
    // 通过RS232Controller实例获取串口
    int flag = rs232Controllers.Rs232_Open(file, baud, bits, event, stopbits, this);

    private Boolean result = true;
    private byte[] abc = {(byte) 0x68, 0X01, 0X01, 0X02, 0X00, 0X00, (byte) 0x16};
    private static final byte OPEN = 0X02;
    private static final byte CHECK = 0X04;
    public static InputStream in = null;
    private int tmp = 0;
    private byte[] locker = new byte[8];
    final String OPENA = "68010102ffff16";
    final String OPENB = "68010202ffff16";
    final String OPENC = "68010302ffff16";
    final String OPEND = "68010402ffff16";
    final String OPENE = "68010502ffff16";
    final String OPENF = "68010602ffff16";
    final String OPENG = "68010702ffff16";
    private Context context;
    public long start;
    private int i;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            // 要做的事情
            abc[3] = 0X01;
            send(abc);   //发送数据
            handler.postDelayed(this, 5000);
        }
    };

    public static synchronized Lock getInstance() {
        if (instance == null) {
            synchronized (Lock.class) {
                instance = new Lock();
            }
        }
        return instance;
    }

    public void init() {
        rs232Controllers = RS232Controller.getInstance();
        connect();
    }

    private void connect() {
        if (null == rs232Controllers) {
            rs232Controllers = RS232Controller.getInstance();
        }
        if (rs232Controllers != null) {
            // 通过RS232Controller实例获取串口
            int flag = rs232Controllers.Rs232_Open(file, baud, bits, event,
                    stopbits, this);
            if (flag == 0) {
                LogUtil.d(TAG, "connect_Success");
            } else {
                LogUtil.d(TAG, "connect_Failure");
            }
        }
    }


    public void disconnect() {
        if (null != rs232Controllers) {
            rs232Controllers.Rs232_Close();
            rs232Controllers = null;
        }
    }

    private void send(byte[] data) {
        rs232Controllers.Rs232_Write(data);
    }

    //开启查询门锁状态
    public void checkstatus() {
        handler.postDelayed(runnable, 5000);// 每5秒执行一次runnable.
    }

    //关闭查询门锁状态
    public void closestatus() {
        handler.removeCallbacks(runnable);//关闭定时器
    }

    public void setLocksStatus(CallBacks callBacks, String question) {
        System.out.println("执行操作--->" + question);
        //需要先查询获取门锁状态 只要有一个是开着 就发送查询命令
        while (result) {
            //send 查询命令
            for (int i = 0; i < 8; i++) {
                abc[2] = (byte) (i + 1);
                abc[3] = CHECK;
                send(abc);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        callBacks.locksStatus(result);

    }

    public void open(int id) {
        byte[] bc = {(byte) 0x68, 0X01, 0X01, 0X02, 0X00, 0X00, (byte) 0x16};
        result = true;
        bc[2] = (byte) (id);
        bc[3] = OPEN;
        send(bc);
    }

    //发送开门指令
    public void sends(byte[] bcs) {
        if (flag == 0) {
            LogUtil.d(TAG, "connect_Success");
            System.out.println("========connect_Success=========");
            result = true;
            send(bcs);
        } else {
            init();
            System.out.println("========connect_Failure=========");
            LogUtil.d(TAG, "connect_Failure");
        }

    }

    //循环发送开门指令
    public void inits(List<String> orderList, Context context) throws InterruptedException {
        this.context = context;
        if (flag == 0) {
            System.out.println("========connect_Success=========");
            LogUtil.d(TAG, "connect_Success");
            result = true;
        } else {
            init();
            System.out.println("========connect_Failure=========");
            LogUtil.d(TAG, "connect_Failure");
        }
        for (String order : orderList) {
            switch (order) {
                case "A":
                    sendA();
                    Thread.sleep(1000);
                    break;
                case "B":
                    sendB();
                    Thread.sleep(1000);
                    break;
                case "C":
                    sendC();
                    Thread.sleep(1000);
                    break;
                case "D":
                    sendD();
                    Thread.sleep(1000);
                    break;
                case "E":
                    sendE();
                    Thread.sleep(1000);
                    break;
                case "F":
                    sendF();
                    Thread.sleep(1000);
                    break;
                case "G":
                    sendG();
                    Thread.sleep(1000);
                    break;
                default:
                    break;

            }
        }
    }

    public void sendA() {
        byte[] sendData = HexToByteArr(OPENA);
        sends(sendData);
        System.out.println("===========A门已打开================");
    }

    public void sendB() {
        byte[] sendData = HexToByteArr(OPENB);
        sends(sendData);
        System.out.println("===========B门已打开================");
    }

    public void sendC() {
        byte[] sendData = HexToByteArr(OPENC);
        sends(sendData);
        System.out.println("===========C门已打开================");
    }

    public void sendD() {
        byte[] sendData = HexToByteArr(OPEND);
        sends(sendData);
        System.out.println("===========D门已打开================");
    }

    public void sendE() {
        byte[] sendData = HexToByteArr(OPENE);
        sends(sendData);
        System.out.println("===========E门已打开================");
    }

    public void sendF() {
        byte[] sendData = HexToByteArr(OPENF);
        sends(sendData);
        System.out.println("===========F门已打开================");
    }

    public void sendG() {
        byte[] sendData = HexToByteArr(OPENG);
        sends(sendData);
        System.out.println("===========G门已打开================");
    }

    public static byte[] HexToByteArr(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen) == 1) {
            ++hexlen;
            result = new byte[hexlen / 2];
            inHex = "0" + inHex;
        } else {
            result = new byte[hexlen / 2];
        }
        int j = 0;

        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            ++j;
        }
        return result;
    }

    public static int isOdd(int num) {
        return num & 1;
    }

    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }


    @Override
    public void RS232_Read(byte[] data) {
        try {
            tmp = data.length;
            if (tmp == 7) {
                if ((data[0] == (byte) 0x68) && (data[6] == (byte) 0x16)) {
                    int id = (int) data[2] - 1;
                    if (data[3] == 0x02)  // 开锁操作反馈状态
                    {
                        System.out.println("收到数据长度：" + data.length);
                        System.out.println("收到开锁反馈：" + bytesToHexString(data));
                        if ((data[4] == 0x00) && (data[5] == 0x00)) {
                            System.out.println(id + 1 + "号门开了");
                            i=0;
                        }
                    }
                }
            } else if (tmp == 15) {
                if ((data[0] == (byte) 0x68) && (data[14] == (byte) 0x16)) {
                    if (data[3] == 0x01)  // 查询操作反馈状态
                    {
                        System.out.println("收到数据长度：" + data.length);
                        System.out.println("收到查询反馈：" + bytesToHexString(data));
                        String lockstatus = Integer.toBinaryString((data[10] & 0xFF) + 0x100).substring(1);
                        System.out.println("门状态：" + lockstatus);
                        i++;
                        System.out.println("========时间========"+i);
                        if(i==5){
                            MessageEvent messageEvent = new MessageEvent(MyApplication.MESSAGE_LOCKSTATE);
                            EventBus.getDefault().post(messageEvent);
                            i=0;
                        }

                        if (data[10] == -1) {
                            System.out.println("门都已关闭");
                            MessageEvent messageEvent = new MessageEvent(MyApplication.MESSAGE_LOCKDATA);
                            EventBus.getDefault().post(messageEvent);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        try {
//            tmp = data.length;
//            if (tmp == 7) {
//                if ((data[0] == (byte) 0x68) && (data[6] == (byte) 0x16)) {
//                    System.out.println(new String(Arrays.toString(data)));
//                    {
//                        int id = (int) data[2] - 1;
//                        if (data[3] == 0x02)  // 开锁操作反馈状态
//                        {
//                            if ((data[4] == 0x00) && (data[5] == 0x00)) {
//                                locker[id] = 0;
//                            } else {
//                                locker[id] = 1;
//                            }
//                        }
//                        if (data[3] == 0x04)  // 查询操作反馈状态
//                        {
//                            if ((data[4] == 0x00) && (data[5] == 0x00)) {
//                                locker[id] = 0;
//                            } else {
//                                locker[id] = 1;
//                            }
//                        }
//                    }
//                }
//
//                int i, locksum = 0;
//                for (i = 0; i < locker.length; i++) {
//                    locksum = locksum + locker[i];
//                }
//
//                if (locksum == 8) {
//                    result = false;
//                } else {
//                    result = true;
//                }
//            } else {
//                try {
//                    System.out.println(new String(data, "GB2312"));
//                } catch (UnsupportedEncodingException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                }
//            }
//            System.out.println("收到" + tmp + "个字节");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

/*        String tempStr = null;
        tempStr = new String(data, 0, data.length);
        Message msg = new Message();
        LogUtil.d(TAG,"read:"+ LogUtil.byte2hexString(data,data.length));
        msg.obj = tempStr;
        msg.what = READMESSAGE;

        mHandler.sendMessage(msg);*/
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
