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
 * 危化品存放数据适配器
 *
 * @author QiaoShi
 */
public class StorageAdapter extends BaseAdapter {

    private Context context;
    private List<DangerousChemicals> list;

    public StorageAdapter(Context context, List<DangerousChemicals> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.storage_item, null);
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
        hold.state = convertView.findViewById(R.id.state);
        hold.bottle = convertView.findViewById(R.id.bottle);
        hold.usernamea = convertView.findViewById(R.id.usernamea);
        hold.usernameb = convertView.findViewById(R.id.usernameb);
        hold.manufacturer = convertView.findViewById(R.id.manufacturer);

        hold.rfid.setText("RFID:"+list.get(position).getRfid());
        hold.rfid.setTextColor(context.getResources().getColor(R.color.actionsheet_blue));
        hold.name.setText(list.get(position).getName());
        hold.equation.setText(list.get(position).getEquation());
        hold.weight.setText("当前重量 | " + list.get(position).getBalancedata());
        hold.specifications.setText("规格重量 | " + list.get(position).getSpecifications());
        hold.usernamea.setText("操作人 | " + list.get(position).getUsernamea());
        hold.usernameb.setText("操作人 | " + list.get(position).getUsernameb());
        hold.manufacturer.setText("厂商: " + list.get(position).getManufacturer());
        hold.type.setText(list.get(position).getAcidbase());
        String state=list.get(position).getState();
        if(state.equals("in")){
            hold.state.setBackground(context.getResources().getDrawable(R.mipmap.storage_in));
        }
        if(state.equals("out")){
            hold.state.setBackground(context.getResources().getDrawable(R.mipmap.storage_out));
        }
        String bottle=list.get(position).getType();
        if(bottle.equals("固体")){
            hold.bottle.setBackground(context.getResources().getDrawable(R.mipmap.solidbottle));
        }
        if(bottle.equals("液体")){
            hold.bottle.setBackground(context.getResources().getDrawable(R.mipmap.liquidbottle));
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
        public TextView usernamea;
        public TextView usernameb;
        public TextView manufacturer;
        public ImageView state;
        public ImageView bottle;
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
