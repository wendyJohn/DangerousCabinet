package com.sanleng.dangerouscabinet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.sanleng.dangerouscabinet.MyApplication;
import com.sanleng.dangerouscabinet.ui.bean.Dangerous;

import java.io.File;


public class DBHelpers extends SQLiteOpenHelper {
    private static DBHelpers instance;
    public final static int DATABASEVERSION = 1;

    private static Context mContext;

    public static final String mDbName = SDBHelper.DB_DIRS + File.separator + "dangerconfig.db";

    public DBHelpers(Context context) {
        super(context, mDbName, null, DATABASEVERSION);
        mContext = context;
    }

    public static DBHelpers getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new DBHelpers(MyApplication.instance);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public Cursor query(String sql, String[] args) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        return cursor;
    }

    //初始化本地数据库数据
    public void insert(Dangerous dangerous) {
        SQLiteDatabase db = getWritableDatabase();
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
        SQLiteDatabase db = getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("Staus", staus);
        String where = "Epc=" + "'" + epc + "'";
        return db.update("materialtable", updatedValues, where, null);
    }

    //更新危化品重量
    public int updatebalancedata(String epc, String balancedata) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("Balancedata", balancedata);
        String where = "Epc=" + "'" + epc + "'";
        return db.update("materialtable", updatedValues, where, null);
    }

    //危化品过秤记录
    public void insertweigh(String epc, String name, String weigh) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Epc", epc);
        values.put("Name", name);
        values.put("Currentweight", weigh);
        db.insert("operationalrecords", null, values);
    }

    //清空表数据
    public void delete() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("materialtable", null, null);
    }

    //清空操作记录表数据
    public void deleterecords() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("operationalrecords", null, null);
    }


}
