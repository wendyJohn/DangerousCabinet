package com.sanleng.dangerouscabinet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.sanleng.dangerouscabinet.MyApplication;

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

	//插入更新数据库数据
//	public void insert(ArchitectureBean architectureBean) {
//		SQLiteDatabase db = getWritableDatabase();
//		ContentValues values = new ContentValues();
//		values.put("Epc", architectureBean.getEpc());
//		values.put("Ant", architectureBean.getAnt());
//		values.put("Staus", architectureBean.getState());
//		values.put("Ids", architectureBean.getIds());
//		values.put("StationName", architectureBean.getStationName());
//		values.put("StorageLocation", architectureBean.getStorageLocation());
//		values.put("StationId", architectureBean.getStationId());
//		values.put("Name", architectureBean.getName());
//		db.insert("materialtable", null, values);
//	}

	//更新物资状态
	public int update(String epc, String staus) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues updatedValues = new ContentValues();
		updatedValues.put("Staus", staus);
		String where = "Epc=" +"'" + epc + "'";
		return db.update("materialtable", updatedValues, where, null);
	}
	//清空表数据
	public void delete() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete("materialtable", null, null);
	}


	//更新配置天线
	public int updateant(String ant, String location) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues updatedValues = new ContentValues();
		updatedValues.put("Ant", ant);
		String where = "Position=" +"'" + location + "'";
		return db.update("anttable", updatedValues, where, null);
	}
}
