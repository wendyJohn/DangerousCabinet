package com.sanleng.dangerouscabinet.utils;

import android.content.Context;
import android.database.Cursor;

import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.model.StorckModel;
import com.sanleng.dangerouscabinet.ui.bean.DangerousChemicals;

import java.util.ArrayList;
import java.util.List;

public class StockData {
    //库存信息
    public static void Stock(Context context, StorckModel storckModel, String state) {
        List<DangerousChemicals> stocklist = new ArrayList<>();
        DBHelpers mOpenHelper = new DBHelpers(context);
        Cursor cursor = mOpenHelper.query("select * from materialtable where Staus=" + "'" + state + "'", null);
        while (cursor.moveToNext()) {
            String epcs = cursor.getString(cursor.getColumnIndex("Epc"));
            String staus = cursor.getString(cursor.getColumnIndex("Staus"));
            String Name = cursor.getString(cursor.getColumnIndex("Name"));
            String Balancedata = cursor.getString(cursor.getColumnIndex("Balancedata"));
            String Equation = cursor.getString(cursor.getColumnIndex("Equation"));
            String Acidbase = cursor.getString(cursor.getColumnIndex("Acidbase"));
            String Type = cursor.getString(cursor.getColumnIndex("Type"));
            String CurrentWeight = cursor.getString(cursor.getColumnIndex("CurrentWeight"));
            String Manufacturer = cursor.getString(cursor.getColumnIndex("Manufacturer"));

            DangerousChemicals bean = new DangerousChemicals();
            bean.setRfid(epcs);
            bean.setName(Name);
            bean.setEquation(Equation);
            bean.setAcidbase(Acidbase);
            bean.setType(Type);
            bean.setBalancedata(Balancedata);
            bean.setSpecifications(CurrentWeight);
            bean.setManufacturer(Manufacturer);
            stocklist.add(bean);
        }
        cursor.close();
        storckModel.StokSuccess(state, stocklist);
    }
}
