package com.sanleng.dangerouscabinet.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.sanleng.dangerouscabinet.MainActivity;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.ui.adapter.DangerousChemicalsAdapter;
import com.sanleng.dangerouscabinet.ui.bean.DangerousChemicals;
import com.sanleng.dangerouscabinet.utils.CharacterParser;
import com.sanleng.dangerouscabinet.utils.PinyinComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索操作
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private AutoCompleteTextView search_edit;
    private ImageView back;
    private DBHelpers mOpenHelper;
    private List<DangerousChemicals> mylist;
    private GridView gridView;
    private DangerousChemicalsAdapter dangerousChemicalsAdapter;
    //汉字转换成拼音的类
    private CharacterParser characterParser;
    // 根据拼音来排列ListView里面的数据类
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_search);
        initView();
        addData();
    }

    //初始化
    private void initView() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        search_edit = findViewById(R.id.search_edit);//搜索输入框
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                filterData(s.toString().trim());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_search;
    }


    private void addData() {
        mOpenHelper = new DBHelpers(this);
        mylist = new ArrayList<>();
        Cursor cursor = mOpenHelper.query("select * from materialtable", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("Name"));
            String ids = cursor.getString(cursor.getColumnIndex("Ids"));
            String balancedata = cursor.getString(cursor.getColumnIndex("Balancedata"));
            DangerousChemicals bean = new DangerousChemicals();
            bean.setName(name);
            bean.setIds(ids);
            bean.setBalancedata(balancedata);
            mylist.add(bean);
        }
        cursor.close();

        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(2);
        dangerousChemicalsAdapter = new DangerousChemicalsAdapter(SearchActivity.this, mylist);
        gridView.setAdapter(dangerousChemicalsAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = ((DangerousChemicals) dangerousChemicalsAdapter.getItem(position)).getName();
                System.out.println(name);
            }
        });
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<DangerousChemicals> filterDateList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mylist;
        } else {
            filterDateList.clear();
            for (DangerousChemicals names : mylist) {
                String name = names.getName();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(names);
                }
            }
        }
        try {
            dangerousChemicalsAdapter.updateListView(filterDateList);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回首页
            case R.id.back:
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                break;
        }
    }
}
