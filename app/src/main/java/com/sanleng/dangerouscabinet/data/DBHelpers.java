package com.sanleng.dangerouscabinet.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sanleng.dangerouscabinet.ui.bean.Dangerous;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class DBHelpers extends SQLiteOpenHelper {
    private static final String DB_NAME = "dangerconfig.db";//数据库名字
    private static final int DB_VERSION = 1; //数据库版本号
    private SQLiteDatabase db;
    private String filePath;  //数据库存储路径
    public DBHelpers(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // 初始化数据库路径 (这个路径不能改变，因为是保存在内部安装文件夹中的，自己写的创建数据库也在这个路径下)
        filePath = "//data//data//" + context.getPackageName() + "//databases";
        //必须实例化的时候调用此方法，否则会报错
        initExternalDB(context);
        db = getWritableDatabase();
    }

    /**
     * 初始化外部数据库
     *
     * @param context
     */
    private void initExternalDB(Context context) {
        InputStream is = null;
        FileOutputStream fos = null;
        // 打开Assets目录下的数据库文件
        try {
            is = context.getAssets().open("databases/" + DB_NAME);
            // 存放数据库的目录不存在，则创建
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
            // 准备将外部库写入系统目录
            File dbFile = new File(filePath + "//" + DB_NAME + "//");
            // 数据库文件不存在则写入
            SharedPreferences preferences = context.getSharedPreferences("dbVersion", 0);
            int dbVersion = preferences.getInt("dbVersion", 1);
            if (!dbFile.exists() || DB_VERSION > dbVersion) {
                fos = new FileOutputStream(dbFile);
                byte[] buf = new byte[1024];
                int len = -1;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    fos.flush();
                }
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("dbVersion", DB_VERSION);
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            onCreate(db);
        }
    }

    public Cursor query(String sql, String... selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }

    //初始化本地数据库数据
    public void insert(Dangerous dangerous) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Epc", dangerous.getEpc());
        values.put("Ant", dangerous.getAnt());
        values.put("Staus", dangerous.getStaus());
        values.put("Ids", dangerous.getIds());
        values.put("StationName", dangerous.getStationName());
        values.put("StorageLocation", dangerous.getStorageLocation());
        values.put("StationId", dangerous.getStationId());
        values.put("Name", dangerous.getName());
        values.put("Balancedata", dangerous.getBalancedata());
        values.put("Equation", dangerous.getEquation());
        values.put("Acidbase", dangerous.getAcidbase());
        values.put("Type", dangerous.getType());
        values.put("CurrentWeight", dangerous.getCurrentWeight());
        values.put("Manufacturer", dangerous.getManufacturer());
        values.put("Describe", dangerous.getDescribe());
        values.put("ChioUnitCode", dangerous.getChioUnitCode());
        values.put("ChioBuildCode", dangerous.getChioBuildCode());
        values.put("ChioFloorCode", dangerous.getChioFloorCode());
        values.put("ChioRoomCode", dangerous.getChioRoomCode());
        values.put("ChioUnitName", dangerous.getChioUnitName());
        values.put("ChioBuildName", dangerous.getChioBuildName());
        values.put("ChioFloorName", dangerous.getChioFloorName());
        values.put("ChioRoomName", dangerous.getChioRoomName());
        db.insert("materialtable", null, values);
    }

    //更新危化品状态
    public int update(String epc, String staus) {
        db = getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("Staus", staus);
        String where = "Epc=" + "'" + epc + "'";
        return db.update("materialtable", updatedValues, where, null);
    }

    //更新危化品重量
    public int updatebalancedata(String epc, String balancedata) {
        db = getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("Balancedata", balancedata);
        String where = "Epc=" + "'" + epc + "'";
        return db.update("materialtable", updatedValues, where, null);
    }

    //危化品过秤记录
    public void insertweigh(String epc, String name, String weigh) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Epc", epc);
        values.put("Name", name);
        values.put("Currentweight", weigh);
        db.insert("operationalrecords", null, values);
    }

    //清空表数据
    public void delete() {
        db = getWritableDatabase();
        db.delete("materialtable", null, null);
    }

    //清空操作记录表数据
    public void deleterecords() {
        db = getWritableDatabase();
        db.delete("operationalrecords", null, null);
    }


}
