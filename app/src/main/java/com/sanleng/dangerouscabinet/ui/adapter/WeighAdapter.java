package com.sanleng.dangerouscabinet.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.ui.bean.DangerousChemicals;

import java.util.List;

public class WeighAdapter extends BaseAdapter {

    private Context context;//上下文对象
    private List<DangerousChemicals> dataList;//ListView显示的数据

    /**
     * 构造器
     *
     * @param context  上下文对象
     * @param dataList 数据
     */
    public WeighAdapter(Context context, List<DangerousChemicals> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //判断是否有缓存
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weigh, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int i=position+1;
        viewHolder.sort.setText(i+"");
        viewHolder.name.setText(dataList.get(position).getName());
        viewHolder.weights.setText(dataList.get(position).getBalancedata());
        return convertView;
    }

    /**
     * ViewHolder类
     */
    private final class ViewHolder {

        TextView sort;//排序
        TextView name;//名称
        TextView weights;//重量

        /**
         * 构造器
         *
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {
            sort = view.findViewById(R.id.sort);
            name = view.findViewById(R.id.name);
            weights = view.findViewById(R.id.weights);
        }
    }
}

