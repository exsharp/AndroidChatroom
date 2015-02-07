package com.example.zfliu.chatroom.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zfliu.chatroom.R;

/**
 * Created by zfliu on 2/6/2015.
 */
public class InfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View contactsLayout = inflater.inflate(R.layout.fragment_info, container, false);
        return contactsLayout;
    }
}
