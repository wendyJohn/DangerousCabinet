package com.sanleng.dangerouscabinet.ui.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class SimpleArrayAdapter <T> extends ArrayAdapter {
    //构造方法
    public SimpleArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }
    //复写这个方法，使返回的数据没有最后一项
    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }

}
