package com.mylook.mylook.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.mylook.mylook.R;

import java.util.HashMap;
import java.util.List;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context mcontext;

    private List<String> listDataGroup;
    private HashMap<String, List<String>> listDataChild;

    public ExpandableListViewAdapter(Context context, List<String> listDataGroup,
                                     HashMap<String, List<String>> listChildData) {
        this.mcontext = context;
        this.listDataGroup = listDataGroup;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataGroup.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mcontext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.article_list_view_row_child, null);
        }

        TextView textViewChild = convertView
                .findViewById(R.id.textViewChild);

        textViewChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataGroup.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataGroup.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mcontext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.article_list_view_row_group, null);
        }

        TextView textViewGroup = convertView
                .findViewById(R.id.textViewGroup);
        textViewGroup.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}