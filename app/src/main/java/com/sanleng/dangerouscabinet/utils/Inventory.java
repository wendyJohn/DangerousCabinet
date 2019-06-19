package com.sanleng.dangerouscabinet.utils;

import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.fid.entity.EPC;
import com.sanleng.dangerouscabinet.fid.serialportapi.ReaderServiceImpl;
import com.sanleng.dangerouscabinet.fid.service.CallBack;
import com.sanleng.dangerouscabinet.fid.service.CallBackStopReadCard;
import com.sanleng.dangerouscabinet.fid.service.ReaderService;
import com.sanleng.dangerouscabinet.fid.tool.ReaderUtil;
import com.sanleng.dangerouscabinet.fid.util.DataFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 盘点物资
 */
public class Inventory extends AppCompatActivity {
    ReaderService readerService = new ReaderServiceImpl();
    private List<EPC> listEPC;
    private List<String> listEpc;
    private DBHelpers mOpenHelper;
    //正常盘点数据
    public void invOnce() {
        mOpenHelper = new DBHelpers(this);
        listEPC = new ArrayList<>();
        listEpc = new ArrayList<>();

        if (null == ReaderUtil.readers) {
            System.out.println("请先连接设备");
            return;
        }
        listEPC.removeAll(listEPC);
        listEpc.removeAll(listEpc);
        readerService.invOnceV2(ReaderUtil.readers, new ReadData());
        new Handler().postDelayed(new Runnable() {
            public void run() {
                System.out.println("=======EPC大小========" + listEPC.size());
                if (listEPC.size() <= 0) {
                    readerService.stopInvV2(ReaderUtil.readers, new StopReadData());
                    return;
                }
                // 等待2000毫秒后获取卡号，并比对。
                for (int i = 0; i < listEPC.size(); i++) {
                    String epc = listEPC.get(i).getEpc();
                    String myant = listEPC.get(i).getAnt();
                    listEpc.add(epc);
                }

                JSONArray array = new JSONArray();
                Cursor cursor = mOpenHelper.query("select * from materialtable", null);
                while (cursor.moveToNext()) {
                    String epcs = cursor.getString(cursor.getColumnIndex("Epc"));
                    String staus = cursor.getString(cursor.getColumnIndex("Staus"));
                    String ant = cursor.getString(cursor.getColumnIndex("Ant"));
                    String ids = cursor.getString(cursor.getColumnIndex("Ids"));
                    String StationName = cursor.getString(cursor.getColumnIndex("StationName"));
                    String StationId = cursor.getString(cursor.getColumnIndex("StationId"));
                    String StorageLocation = cursor.getString(cursor.getColumnIndex("StorageLocation"));
                    JSONObject object = new JSONObject();
                    Boolean exists = ((List) listEpc).contains(epcs);
                    if (exists) {
                        System.out.println(epcs + "有卡号信息！");
                        //当有卡时判断状态，如果是出库状态则认为现在为入库。
//                        if (staus.equals("emergencystation_out")) {
//                            System.out.println(epcs + "物资入库");
//                            mOpenHelper.update(epcs, "emergencystation_in");
//                            try {
//                                object.put("ids", ids);
//                                object.put("state", "emergencystation_in");
//                                object.put("agentName", PreferenceUtils.getString(this, "FaceUserName"));
//                                object.put("stationId", StationId);
//                                object.put("storageLocation", StorageLocation);
//                                array.put(object);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
                    }
                    //当无卡时则认为现在为出库，并修改物资为出库状态。
//                    else {
//                        if (staus.equals("emergencystation_in")) {
//                            System.out.println(epcs + "物资出库");
//                            mOpenHelper.update(epcs, "emergencystation_out");
//                            try {
//                                object.put("ids", ids);
//                                object.put("state", "emergencystation_out");
////                                object.put("agentName", PreferenceUtils.getString(this, "Faceone"));
//                                object.put("stationId", StationId);
//                                object.put("storageLocation", StorageLocation);
//                                array.put(object);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
                }
                cursor.close();
                //提交盘点数据
                System.out.println("========Array大小============" + array.length());
                if (array.length() > 0) {
//                    Submission(array);
                }
            }
        }, 2000);
    }

    class ReadData implements CallBack {
        @Override
        public void readData(String data, String antNo) {
            addToList(listEPC, data, antNo);
        }

        @Override
        public void readData(String data, String rssi, String antNo, String deviceNo, String direction) {
            addToList(listEPC, data, rssi, antNo, deviceNo, direction);
        }
    }

    class StopReadData implements CallBackStopReadCard {
        @Override
        public void stopReadCard(final boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (result) {
                        System.out.println("======停止读卡成功=======");
                    } else {
                        System.out.println("======停止读卡失败=======");
                    }
                }
            });
        }
    }

    private void addToList(final List<EPC> listEPC2, final String epc, final String ant) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataFilter.dataFilter(listEPC2, epc, ant);

            }
        });
    }

    private void addToList(final List<EPC> listEPC2, final String epc, final String rssi, final String ant, final String deviceNo, final String direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataFilter.dataFilter(listEPC2, epc, rssi, ant, deviceNo, direction);
            }
        });
    }
}
