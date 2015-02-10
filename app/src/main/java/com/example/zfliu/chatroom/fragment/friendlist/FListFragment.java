package com.example.zfliu.chatroom.fragment.friendlist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zfliu.chatroom.files.FListFile;
import com.example.zfliu.chatroom.service.AppUtil;
import com.example.zfliu.chatroom.ChatActivity;
import com.example.zfliu.chatroom.R;
import com.example.zfliu.chatroom.friendlist.Apple;
import com.example.zfliu.chatroom.friendlist.ExpandableAdapter;
import com.example.zfliu.chatroom.service.SendMsg;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by zfliu on 2/6/2015.
 */
public class FListFragment extends Fragment {

    private FListFragment THIS;
    public View friendListLayout;
    public ExpandableAdapter adapter;
    public ExpandableListView expandableListView;
    public LinkedList<String> groups ;
    public boolean[] inExpand;
    public LinkedList<LinkedList<Apple>> children ;
    public Intent intent;
    public ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog= new ProgressDialog(getActivity());

        initBroadcastReceiver();

        setHasOptionsMenu(true);
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("好友列表");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        friendListLayout = inflater.inflate(R.layout.fragment_friend_list,container, false);
        return friendListLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter(new boolean[]{true});
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                inExpand[groupPosition] = true;
            }
        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                inExpand[groupPosition] = false;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("towho", adapter.getChild(groupPosition, childPosition).toString());
                startActivity(intent);
                return false;
            }
        });
        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int groupPosition,childPosition;
                int itemType = ExpandableListView.getPackedPositionType(id);
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                    childPosition = ExpandableListView.getPackedPositionChild(id);
                    groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    System.out.println("group  "+groupPosition);
                    System.out.println("child  "+childPosition);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final String [] items = new String []{"查看详细信息","删除该好友","移动到分组"};
                    builder.setTitle("对好友的操作").setItems(items,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            childOptionDialog COD = new childOptionDialog(childPosition,groupPosition);
                            switch (which){
                                case 0:
                                    Log.d("dialog",items[0]);
                                    COD.detail();
                                    break;
                                case 1:
                                    Log.d("dialog",items[1]);
                                    COD.delfriend();
                                    break;
                                case 2:
                                    Log.d("dialog",items[2]);
                                    COD.movfriend();
                                default:
                            }
                        }
                    }).show();
                    return true;
                } else if(itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    final EditText editText = new EditText(getActivity());
                    editText.setHint("输入要删除的分组的名称做确认");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("删除该组");
                    builder.setMessage("确定要删除吗？\n(组内好友将转移到第一个分组)");
                    builder.setView(editText);
                    builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String str = editText.getText().toString();
                            if (str.equals(groups.get(groupPosition))){
                                SendMsg sendMsg = new SendMsg("DELGROUP", getActivity());
                                sendMsg.setJSON(new String[]{groups.get(0),groups.get(groupPosition)});
                            }
                        }
                    }).setNegativeButton("取消",null).show();
                    System.out.println("group  "+groupPosition);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void initAdapter(boolean[] expand){
        FListFile fListFile = new FListFile(getActivity());
        try {
            groups= new LinkedList<String>();
            children = new LinkedList<>();
            groups = fListFile.getGroup();
            children = fListFile.getChild();
            inExpand = new boolean[groups.size()];
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter = new ExpandableAdapter(getActivity(),groups,children);
        expandableListView =(ExpandableListView)friendListLayout.findViewById(R.id.list1);
        expandableListView.setAdapter(adapter);
        for (int i = 1 ; i<expand.length;i++){
            if (expand[i])
                expandableListView.expandGroup(i);
        }
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_frag_friendlist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        progressDialog.setMessage("正在处理，请稍后");
        try{
            switch (id) {
                case R.id.action_addfriend:
                    addFriend();
                    break;
                case R.id.action_addgroup:
                    addGroup();
                    break;
                default:
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }
    private void addGroup() throws IOException{
        final EditText editText = new EditText(getActivity());
        AlertDialog.Builder inBuilder = new AlertDialog.Builder(getActivity());
        inBuilder.setTitle("输入要添加的组名").setView(editText);
        inBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = editText.getText().toString();
                SendMsg sendMsg = new SendMsg("ADDGROUP", getActivity());
                sendMsg.setJSON(new String[]{groupName});
            }
        }).setNegativeButton("取消",null).show();
    }
    private void addFriend() throws IOException {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.fragment_friend_list_grouplist,null);

        FListFile fListFile = new FListFile(getActivity());
        String [] selectGroup = fListFile.getGroupArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,selectGroup);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = (Spinner)view.findViewById(R.id.fragment_friendlist_friendgroup_spinner);
        final EditText editText = (EditText) view.findViewById(R.id.fragment_friendlist_inputET);
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);//设置默认显示
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //result.setText("你的选择是："+((TextView)view).getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请输入要添加的用户名").setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user = editText.getText().toString().trim();
                String friend = spinner.getSelectedItem().toString();
                if (user.equals("")) {
                    Toast.makeText(getActivity(), "输入不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    SendMsg sendMsg = new SendMsg("ADDFRIEND", getActivity());
                    sendMsg.setJSON(new String[]{user,friend});
                    progressDialog.show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        builder.show();
    }
    private void initBroadcastReceiver(){
        FLBroadcastReceiver receiver = new FLBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ADDFRIEND");
        filter.addAction("DELFRIEND");
        filter.addAction("ADDGROUP");
        filter.addAction("DELGROUP");
        filter.addAction("MOVFRIEND");
        getActivity().registerReceiver(receiver,filter);
    }
    private class FLBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (progressDialog!=null) {
                progressDialog.dismiss();
            }
            String state = intent.getStringExtra("STATE");
            Toast.makeText(context,state,Toast.LENGTH_SHORT).show();
            initAdapter(inExpand);
        }
    }
    private class childOptionDialog{
        private int cp,gp;
        childOptionDialog(int cp,int gp){
            this.cp = cp;
            this.gp = gp;
        }
        public void detail() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //builder
        }
        public void delfriend() {
            final EditText editText = new EditText(getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("删除好友").setView(editText);
            builder.setMessage("输入该好友的名字确认删除");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean can;
                    can = editText.getText().toString().equals(children.get(gp).get(cp).toString());
                    if (can) {
                        SendMsg sendMsg = new SendMsg("DELFRIEND", getActivity());
                        sendMsg.setJSON(new String[]{children.get(gp).get(cp).toString()});
                    }
                }
            }).setNegativeButton("取消", null).show();
        }
        private String select;
        public void movfriend() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("移动好友至...");
            final String [] mGroup = new String[groups.size()];
            for (int i = 0; i < groups.size(); i++) {
                mGroup[i]=groups.get(i).toString();
            }
            select = mGroup[0];
            builder.setSingleChoiceItems(mGroup,0,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    select = mGroup[which];
                }
            }).setPositiveButton("确定",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SendMsg sendMsg = new SendMsg("MOVFRIEND", getActivity());
                    String who = children.get(gp).get(cp).toString();
                    String from = groups.get(gp).toString();
                    sendMsg.setJSON(new String[]{who,from,select});
                }
            }).show();
        }
    }
}
