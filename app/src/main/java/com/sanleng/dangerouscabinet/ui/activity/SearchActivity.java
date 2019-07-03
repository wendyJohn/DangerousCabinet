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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.sanleng.dangerouscabinet.MainActivity;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.ui.adapter.DangerousChemicalsAdapter;
import com.sanleng.dangerouscabinet.ui.adapter.SimpleArrayAdapter;
import com.sanleng.dangerouscabinet.ui.bean.DangerousChemicals;
import com.sanleng.dangerouscabinet.utils.CharacterParser;
import com.sanleng.dangerouscabinet.utils.DetailsDialog;
import com.sanleng.dangerouscabinet.utils.PinyinComparator;
import com.sanleng.dangerouscabinet.utils.TTSUtils;

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
    private Spinner spinnera;
    private Spinner spinnerb;
    private Spinner spinnerc;
    private SimpleArrayAdapter adapter;
    private List<String> lista;
    private List<String> listb;
    private List<String> listc;
    private String spinnerstra="";
    private String spinnerstrb="";
    private String spinnerstrc="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_search);
        initView();
        addData(spinnerstra, spinnerstrb, spinnerstrc);
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

        spinnera = (Spinner) this.findViewById(R.id.spinnera);
        adapter = new SimpleArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, getDataSourcea());
        spinnera.setAdapter(adapter);
        //默认选中最后一项
        spinnera.setSelection(lista.size() - 1, true);
        spinnera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = lista.get(position);
                if(str.equals("柜内")){
                    spinnerstra="in";
                }else{
                    spinnerstra="out";
                }
                addData(spinnerstra, spinnerstrb, spinnerstrc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerb = (Spinner) this.findViewById(R.id.spinnerb);
        adapter = new SimpleArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, getDataSourceb());
        spinnerb.setAdapter(adapter);
        //默认选中最后一项
        spinnerb.setSelection(listb.size() - 1, true);
        spinnerb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerstrb = listb.get(position);
                addData(spinnerstra, spinnerstrb, spinnerstrc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerc = (Spinner) this.findViewById(R.id.spinnerc);
        adapter = new SimpleArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, getDataSourcec());
        spinnerc.setAdapter(adapter);
        //默认选中最后一项
        spinnerc.setSelection(listc.size() - 1, true);
        spinnerc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerstrc = listc.get(position);
                addData(spinnerstra, spinnerstrb, spinnerstrc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_search;
    }

    //获取全部危化品信息
    private void addData(String states, String acidbases, String types) {
        mOpenHelper = new DBHelpers(this);
        mylist = new ArrayList<>();
        String str = "";
        if (states.equals("") && acidbases.equals("") && types.equals("")) {
            str = "select * from materialtable";
        }
        if (!states.equals("") && acidbases.equals("") && types.equals("")) {
            str = "select * from materialtable where Staus=" + "'" + states + "'";
        }

        if (states.equals("") && !acidbases.equals("") && types.equals("")) {
            str = "select * from materialtable where Acidbase=" + "'" + acidbases + "'";
        }

        if (states.equals("") && acidbases.equals("") && !types.equals("")) {
            str = "select * from materialtable where Type=" + "'" + types + "'";
        }

        if (!states.equals("") && !acidbases.equals("") && types.equals("")) {
            str = "select * from materialtable where Staus=" + "'" + states + "'" + "and Acidbase=" + "'" + acidbases + "'";
        }

        if (!states.equals("") && acidbases.equals("") && !types.equals("")) {
            str = "select * from materialtable where Staus=" + "'" + states + "'" + "and Type=" + "'" + types + "'";
        }

        if (states.equals("") && !acidbases.equals("") && !types.equals("")) {
            str = "select * from materialtable where Acidbase=" + "'" + acidbases + "'" + "and Type=" + "'" + types + "'";
        }

        if (!states.equals("") && !acidbases.equals("") && !types.equals("")) {
            str = "select * from materialtable where Staus=" + "'" + states + "'" + "and Acidbase=" + "'" + acidbases + "'" + "and Type=" + "'" + types + "'";
        }

        Cursor cursor = mOpenHelper.query(str, null);
        while (cursor.moveToNext()) {
            String epc = cursor.getString(cursor.getColumnIndex("Epc"));
            String name = cursor.getString(cursor.getColumnIndex("Name"));
            String ids = cursor.getString(cursor.getColumnIndex("Ids"));
            String balancedata = cursor.getString(cursor.getColumnIndex("Balancedata"));
            String equation = cursor.getString(cursor.getColumnIndex("Equation"));
            String type = cursor.getString(cursor.getColumnIndex("Type"));
            String acidbase = cursor.getString(cursor.getColumnIndex("Acidbase"));
            String specifications = cursor.getString(cursor.getColumnIndex("CurrentWeight"));
            String state = cursor.getString(cursor.getColumnIndex("Staus"));
            String describe = cursor.getString(cursor.getColumnIndex("Describe"));

            DangerousChemicals bean = new DangerousChemicals();
            bean.setRfid(epc);
            bean.setName(name);
            bean.setIds(ids);
            bean.setBalancedata(balancedata);
            bean.setEquation(equation);
            bean.setType(acidbase + "    " + type);
            bean.setSpecifications(specifications);
            bean.setState(state);
            bean.setDescribe(describe);
            mylist.add(bean);
        }
        cursor.close();

        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(3);
        dangerousChemicalsAdapter = new DangerousChemicalsAdapter(SearchActivity.this, mylist);
        gridView.setAdapter(dangerousChemicalsAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String epc = ((DangerousChemicals) dangerousChemicalsAdapter.getItem(position)).getRfid();
                String name = ((DangerousChemicals) dangerousChemicalsAdapter.getItem(position)).getName();
                String equation = ((DangerousChemicals) dangerousChemicalsAdapter.getItem(position)).getEquation();
                String balancedata = ((DangerousChemicals) dangerousChemicalsAdapter.getItem(position)).getBalancedata();
                String describe = ((DangerousChemicals) dangerousChemicalsAdapter.getItem(position)).getDescribe();
                DetailsDialog detailsDialog=new DetailsDialog(SearchActivity.this,epc,name,equation,balancedata,describe);
                detailsDialog.show();
                TTSUtils.getInstance().speak(describe);
            }
        });
    }

    public List<String> getDataSourcea() {
        lista = new ArrayList<String>();
        lista.add("柜内");
        lista.add("柜外");
        lista.add("全部");
        return lista;
    }

    public List<String> getDataSourceb() {
        listb = new ArrayList<String>();
        listb.add("强酸");
        listb.add("强碱");
        listb.add("化学品属性");
        return listb;
    }

    public List<String> getDataSourcec() {
        listc = new ArrayList<String>();
        listc.add("固体");
        listc.add("液体");
        listc.add("气体");
        listc.add("化学品状态");
        return listc;
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
