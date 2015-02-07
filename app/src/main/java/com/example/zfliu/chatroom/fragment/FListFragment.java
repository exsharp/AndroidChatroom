package com.example.zfliu.chatroom.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.zfliu.chatroom.service.AppUtil;
import com.example.zfliu.chatroom.ChatActivity;
import com.example.zfliu.chatroom.R;
import com.example.zfliu.chatroom.friendlist.Apple;
import com.example.zfliu.chatroom.friendlist.ExpandableAdapter;

import java.util.LinkedList;

/**
 * Created by zfliu on 2/6/2015.
 */
public class FListFragment extends Fragment {

    private View friendListLayout;
    private ExpandableAdapter adapter;
    private ExpandableListView expandableListView;
    private LinkedList<String> groups = new LinkedList<String>();
    private LinkedList<LinkedList<Apple>> children = new LinkedList<>();
    private String userName="";
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppUtil app = (AppUtil)getActivity().getApplication();
        groups = app.getGroup();
        children = app.getChild();
        userName = app.getUserName();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        friendListLayout = inflater.inflate(R.layout.fragment_friend_list,container, false);

        return friendListLayout;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ExpandableAdapter(getActivity(),groups,children);
        expandableListView =(ExpandableListView)friendListLayout.findViewById(R.id.list1);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
                intent = new Intent(getActivity(),ChatActivity.class);
                intent.putExtra("ToWho",adapter.getChild(groupPosition,childPosition).toString());
                intent.putExtra("WHO",userName);
                startActivity(intent);
                return false;
            }
        });
    }


}
