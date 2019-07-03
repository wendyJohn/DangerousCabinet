package com.sanleng.dangerouscabinet.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.ui.bean.DangerousChemicals;

import java.util.List;


/**
 * 危化品列表数据适配器
 *
 * @author QiaoShi
 */
public class DangerousChemicalsAdapter extends BaseAdapter {

    private Context context;
    private List<DangerousChemicals> list;

    public DangerousChemicalsAdapter(Context context, List<DangerousChemicals> list) {
        super();
        this.context = context;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<DangerousChemicals> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (list != null) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold hold;
        if (convertView == null) {
            hold = new ViewHold();
            convertView = LayoutInflater.from(context).inflate(R.layout.dangerouschemicals_item, null);
            convertView.setTag(hold);
        } else {
            hold = (ViewHold) convertView.getTag();
        }
        hold.rfid = convertView.findViewById(R.id.rfid);
        hold.name = convertView.findViewById(R.id.name);
        hold.equation = convertView.findViewById(R.id.equation);
        hold.weight = convertView.findViewById(R.id.weight);
        hold.type = convertView.findViewById(R.id.type);
        hold.specifications = convertView.findViewById(R.id.specifications);
        hold.blinears = convertView.findViewById(R.id.blinears);

        hold.rfid.setText(list.get(position).getRfid());
        hold.name.setText(list.get(position).getName());
        hold.equation.setText(list.get(position).getEquation());
        hold.weight.setText("当前重量    " + list.get(position).getBalancedata());
        hold.specifications.setText("规格重量    " + list.get(position).getSpecifications());
        hold.type.setText(list.get(position).getType());
        String state=list.get(position).getState();
        if(state.equals("in")){
            hold.blinears.setBackground(context.getResources().getDrawable(R.mipmap.bottle_in));
        }
        if(state.equals("out")){
            hold.blinears.setBackground(context.getResources().getDrawable(R.mipmap.bottle_out));
        }
        return convertView;
    }

    static class ViewHold {
        public TextView rfid;
        public TextView name;
        public TextView equation;
        public TextView weight;
        public TextView type;
        public TextView specifications;
        public LinearLayout blinears;


    }

    /**
     * 刷新数据
     *
     * @param array
     */
    public void refreshData(List<DangerousChemicals> array) {
        this.list = array;
        notifyDataSetChanged();
    }

}
