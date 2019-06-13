package com.sanleng.dangerouscabinet.fid.ui;

import android.content.Context;
import android.widget.TextView;


import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.fid.entity.EPC;

import java.util.List;

public class ListViewAdapterWithViewHolder  extends ListViewAdapter<EPC> {

    // MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public ListViewAdapterWithViewHolder(Context context, List<EPC> datas) {
        super(context, datas, R.layout.listview_item);
    }

    @Override
    public void convert(ViewHolder holder, EPC bean) {
        ((TextView) holder.getView(R.id.tv_id)).setText(String.valueOf(bean.getId()));
        ((TextView) holder.getView(R.id.tv_epc)).setText(bean.getEpc());
        ((TextView) holder.getView(R.id.tv_rssi)).setText(bean.getRssi());
        ((TextView) holder.getView(R.id.tv_antenna)).setText(String.valueOf(bean.getAnt()));
        ((TextView) holder.getView(R.id.tv_device_no)).setText(bean.getDeviceNo());
        ((TextView) holder.getView(R.id.tv_direction)).setText(bean.getDirection());
        ((TextView) holder.getView(R.id.tv_count)).setText(String.valueOf(bean.getCount()));
    }
}