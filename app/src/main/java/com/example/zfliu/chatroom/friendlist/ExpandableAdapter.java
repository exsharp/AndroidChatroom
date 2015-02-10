package com.example.zfliu.chatroom.friendlist;

/**
 * Created by sharp on 2/3/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zfliu.chatroom.R;

import java.util.LinkedList;

/**
 * Created by sharp on 2/3/2015.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {

    private LinkedList<String> groups;
    private LinkedList<LinkedList<Apple>> children = null;
    private LayoutInflater mInflater;


    public ExpandableAdapter(Context context,LinkedList<String> groups,LinkedList<LinkedList<Apple>> children ){
        this.groups = groups;
        this.children = children;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groups.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groups.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return children.get(groupPosition).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_group_view, null);
        }

        TextView groupName = (TextView) convertView.findViewById(R.id.group_name);
        groupName.setText(groups.get(groupPosition));

        ImageView indicator = (ImageView) convertView.findViewById(R.id.group_indicator);
        TextView onlineNum = (TextView) convertView.findViewById(R.id.online_count);
        onlineNum.setText(getChildrenCount(groupPosition) + "/"+ getChildrenCount(groupPosition));

        if (isExpanded) {
            indicator.setImageResource(R.drawable.indicator_expanded);
        } else {
            indicator.setImageResource(R.drawable.indicator_unexpanded);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_view, null);
        }

        //头像
        //TextView prot = (TextView) convertView.findViewById(R.id.icon);
        //prot.setText();
        //姓名
        TextView tv = (TextView) convertView.findViewById(R.id.contact_list_item_name);
        tv.setText(getChild(groupPosition, childPosition).toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition,int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

};
