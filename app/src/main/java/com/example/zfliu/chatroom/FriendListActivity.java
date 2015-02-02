package com.example.zfliu.chatroom;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import com.example.zfliu.chatroom.friendlist.IphoneTreeView;
import com.example.zfliu.chatroom.friendlist.IphoneTreeView.IphoneTreeHeaderAdapter;


public class FriendListActivity extends ActionBarActivity {

    private LayoutInflater mInflater;
    private IphoneTreeView iphoneTreeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mInflater = LayoutInflater.from(this);
        iphoneTreeView = (IphoneTreeView) findViewById(R.id.iphone_tree_view);
        iphoneTreeView.setHeaderView(getLayoutInflater().inflate(R.layout.list_head_view, iphoneTreeView, false));
        iphoneTreeView.setGroupIndicator(null);
        iphoneTreeView.setAdapter(new IphoneTreeViewAdapter());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class IphoneTreeViewAdapter extends BaseExpandableListAdapter implements IphoneTreeHeaderAdapter {
        // Sample data set. children[i] contains the children (String[]) for
        // groups[i].
        private HashMap<Integer, Integer> groupStatusMap;
        private String[] groups = { "第一组", "第二组", "第三组", "第四组" };
        private String[][] children = {
                { "1", "2", "3", "4", "5", "6",
                        "7", "8", "9", "10", "11", "12" },
                { "Ace", "Bandit", "Cha-Cha", "Deuce", "Ba hamas", "China",
                        "Dominica", "Jim", "LiMing", "Jodan" },
                { "Fluffy", "Snuggles", "Ecuador", "Ecuador", "Jim", "LiMing","Jodan" },
                { "Goldy", "Bubbles", "Iceland", "Iran", "Italy", "Jim",
                        "LiMing", "Jodan" } };

        public IphoneTreeViewAdapter() {
            // TODO Auto-generated constructor stub
            groupStatusMap = new HashMap<Integer, Integer>();
        }

        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_view, null);
            }
            TextView tv = (TextView) convertView
                    .findViewById(R.id.contact_list_item_name);
            tv.setText(getChild(groupPosition, childPosition).toString());
            TextView state = (TextView) convertView
                    .findViewById(R.id.cpntact_list_item_state);
            state.setText("aaaaaaaaaaaa");
            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_group_view, null);
            }
            TextView groupName = (TextView) convertView
                    .findViewById(R.id.group_name);
            groupName.setText(groups[groupPosition]);

            ImageView indicator = (ImageView) convertView
                    .findViewById(R.id.group_indicator);
            TextView onlineNum = (TextView) convertView
                    .findViewById(R.id.online_count);
            onlineNum.setText(getChildrenCount(groupPosition) + "/"
                    + getChildrenCount(groupPosition));
            if (isExpanded) {
                indicator.setImageResource(R.drawable.indicator_expanded);
            } else {
                indicator.setImageResource(R.drawable.indicator_unexpanded);
            }
            return convertView;
        }

        @Override
        public int getTreeHeaderState(int groupPosition, int childPosition) {
            final int childCount = getChildrenCount(groupPosition);
            if (childPosition == childCount - 1) {
                return PINNED_HEADER_PUSHED_UP;
            } else if (childPosition == -1
                    && !iphoneTreeView.isGroupExpanded(groupPosition)) {
                return PINNED_HEADER_GONE;
            } else {
                return PINNED_HEADER_VISIBLE;
            }
        }

        @Override
        public void configureTreeHeader(View header, int groupPosition,
                                        int childPosition, int alpha) {
            // TODO Auto-generated method stub
            ((TextView) header.findViewById(R.id.group_name))
                    .setText(groups[groupPosition]);
            ((TextView) header.findViewById(R.id.online_count))
                    .setText(getChildrenCount(groupPosition) + "/"
                            + getChildrenCount(groupPosition));
        }

        @Override
        public void onHeadViewClick(int groupPosition, int status) {
            // TODO Auto-generated method stub
            groupStatusMap.put(groupPosition, status);
        }

        @Override
        public int getHeadViewClickStatus(int groupPosition) {
            if (groupStatusMap.containsKey(groupPosition)) {
                return groupStatusMap.get(groupPosition);
            } else {
                return 0;
            }
        }
    }
}
