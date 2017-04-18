package com.palmaplus.nagrand.demo.adapter;

/**
 * Created by lchad on 2016/11/1.
 * Github: https://github.com/lchad
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.palmaplus.nagrand.demo.R;

import java.util.LinkedHashMap;

public class HomeAdapter extends BaseExpandableListAdapter {

    private LinkedHashMap<String, String[]> expandableData = new LinkedHashMap<>();

    private Context mContext;

    private ChildItemClickListener mChildClickListener;

    private String[] funcName;

    public HomeAdapter(Context context, String[] funcName, int[] funcArray) {
        this.mContext = context;
        this.funcName = funcName;
        for (int i = 0; i < funcName.length; i++) {
            expandableData.put(funcName[i], context.getResources().getStringArray(funcArray[i]));
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return expandableData.get(funcName[groupPosition])[childPosition];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_child, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);

        String name = expandableData.get(funcName[groupPosition])[childPosition];
        title.setText(name);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChildClickListener == null) {
                    return;
                }
                mChildClickListener.onItemClick(groupPosition, childPosition);
            }
        });
        convertView.setTag(name);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (expandableData == null || expandableData.size() == 0) {
            return 0;
        }
        String key = funcName[groupPosition];
        if (expandableData.get(key) != null) {
            return expandableData.get(key).length;
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return funcName[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return funcName.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupName = funcName[groupPosition];
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(groupName);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setChildClickListener(ChildItemClickListener childClickListener) {
        mChildClickListener = childClickListener;
    }

    public interface ChildItemClickListener {

        void onItemClick(int groupPosition, int childPosition);
    }
}