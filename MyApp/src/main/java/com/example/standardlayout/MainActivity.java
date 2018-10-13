package com.example.standardlayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_READ_CONTACTS_CODE = 2;
    private boolean isPause = false;    //当前activity的状态
    private Adapter adapter;
    private int longClickPosition;   //长按listview的item的位置
    private ListView mListView;
    private List<ConnectInfo> infos = new ArrayList<>();
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DBO(MainActivity.this);
        initInfo();    //初始化列表信息
        initView();    //初始化view
    }

    private void initInfo() {
        Cursor cursor = DBO.query("select * from connectInfo");
        infos.clear();        //清空
        //获得数据库联系人信息
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String phone = cursor.getString(2);
            ConnectInfo c = DBO.query(new ConnectInfo(name, phone));
            infos.add(c);
        }
        cursor.close();
    }

    private void initView() {
        Tool.getActionBar(MainActivity.this);        //获得actionbar
        Tool.getNavigationView(MainActivity.this);   //侧滑栏
        mDrawerLayout = findViewById(R.id.drawer_layout);
        adapter = Tool.setAdapter(MainActivity.this, infos);
        mListView = Tool.getListView(MainActivity.this, adapter, infos, R.id.id_Listview);
        //监听长按
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickPosition = position;
                registerForContextMenu(mListView);         //为listview注册弹出菜单
                openContextMenu(mListView);                //启用菜单
                return true;
            }
        });
        //监听点击
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                intent.putExtra("obj", infos.get(position));
                startActivity(intent);
            }
        });
    }

    //弹出菜单选项被点击
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //删除
            case 1:
                DBO.delete(infos.get(longClickPosition));
                updateListView();
                Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                break;
            //编辑
            case 2:
                Intent intent = new Intent(MainActivity.this, EditInfoActivity.class);
                intent.putExtra("mode", "edit");                    //传递模式
                intent.putExtra("obj", infos.get(longClickPosition));                    //传递对象
                startActivity(intent);
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }



    //ConnectMenu悬浮菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 0, "删除");
        menu.add(0, 2, 0, "编辑");
    }

    //actionbar上的东西
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater()获得该activity的MenuInflater，并通过inflate函数设置R.menu.menu作为该activity的menu
        getMenuInflater().inflate(R.menu.menu, menu);
        Tool.getSearchView(MainActivity.this, menu, R.id.search, mListView);        //获得搜索框item
        Tool.setAddItem(MainActivity.this, menu, R.id.add);        //增加选项
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(MainActivity.this, "haya", Toast.LENGTH_SHORT).show();
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    //暂停
    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    //继续
    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            isPause = false;
            updateListView();
        }
    }
    public void updateListView() {
        initInfo();
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_CONTACTS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Tool.importLocPhones(MainActivity.this);
                    updateListView();
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


}
