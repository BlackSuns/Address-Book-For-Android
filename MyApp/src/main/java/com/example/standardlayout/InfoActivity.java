package com.example.standardlayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InfoActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200; //权限请求码

    private TextView textViewName, textViewSex, textViewPhone, textViewPhone2, textViewQQ, textViewEmail;
    private ImageView imageViewAvatar;  //头像
    private ConnectInfo c = new ConnectInfo();
    private Button btnCall1, btnCall2;    //呼叫按钮
    private int firstClickCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initView();
        getConnectInfo();   //从数据库中获取联系人信息
        initInfos();   //为各组件设置text
    }

    private void callPhone(String phoneNum) {
        //检查拨打电话权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNum));
            startActivity(intent);
        }
    }

    private void getConnectInfo() {
        //接收主键信息
        Intent intent = getIntent();
        c = (ConnectInfo) intent.getSerializableExtra("obj");
        /*临时解决方案。。。
         *由于bitmap不能序列化，所以直接接收来的对象中头像都是null
         * 因此在这里在进行一次查询
         * */
        //c = DBO.query(c);

    }

    private void initInfos() {
        textViewName.setText(c.getName());
        textViewSex.setText(c.getSex());
        textViewPhone.setText(c.getPhone());
        textViewPhone2.setText(c.getPhone2());
        textViewQQ.setText(c.getqq());
        textViewEmail.setText(c.getEmail());
        //设置头像
        if (c.getAvatar() != null) {
            imageViewAvatar.setImageBitmap(c.getAvatar());
        }
    }

    private void initView() {
        btnCall1 = findViewById(R.id.id_info_call1);
        btnCall2 = findViewById(R.id.id_info_call2);
        //单击拨打电话
        btnCall1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查权限
                if (Tool.checkPermission(InfoActivity.this, Manifest.permission.CALL_PHONE)) {
                    callPhone(c.getPhone());
                } else {
                    firstClickCall = 1;
                    //请求权限
                    ActivityCompat.requestPermissions(InfoActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_CODE);
                }
            }
        });
        btnCall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tool.checkPermission(InfoActivity.this, Manifest.permission.CALL_PHONE)) {
                    if ( Tool.isNumeric(c.getPhone2())){
                        callPhone(c.getPhone2());
                    }else {
                        Toast.makeText(InfoActivity.this, "无效号码",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    firstClickCall = 2;
                    //请求权限
                    ActivityCompat.requestPermissions(InfoActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_CODE);
                }
            }
        });
        textViewName = findViewById(R.id.id_info_name);
        textViewSex = findViewById(R.id.id_info_sex);
        textViewPhone = findViewById(R.id.id_info_phone);
        textViewPhone2 = findViewById(R.id.id_info_phone2);
        textViewQQ = findViewById(R.id.id_info_qq);
        textViewEmail = findViewById(R.id.id_info_email);
        imageViewAvatar = findViewById(R.id.id_info_avatar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        MenuItem delItem = menu.findItem(R.id.delete);
        delItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DBO.delete(c);
                Log.println(Log.INFO, "haya", c.toString());
                Toast.makeText(InfoActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (firstClickCall == 1) {
                        callPhone(c.getPhone());
                    } else if (firstClickCall == 2) {
                        callPhone(c.getPhone2());
                    }
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
