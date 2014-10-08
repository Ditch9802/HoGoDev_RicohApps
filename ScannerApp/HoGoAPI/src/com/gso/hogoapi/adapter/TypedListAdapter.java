package com.gso.hogoapi.adapter;


import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class TypedListAdapter<T> extends BaseAdapter {

    private List<T> data;

    public void add(T item) {
        if (data == null) {
            data = new ArrayList<T>();
        }
        data.add(item);
    }

    public void remove(int position) {
        if (data == null || data.size() <= position) {
            return;
        }

        data.remove(position);
    }

    public void remove(T item) {
        if (data == null || data.size() == 0) {
            return;
        }

        data.remove(item);
    }

    public void changeDataSet(List<T> newData) {
        if (data == newData) {
            return;
        }

        if (data != null && data.size() > 0) {
            data.clear();
            System.gc();
        }

        data = newData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public T getItem(int position) {
        if (data == null || data.size() <= position) {
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
