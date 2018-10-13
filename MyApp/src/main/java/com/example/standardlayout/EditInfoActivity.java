package com.example.standardlayout;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class EditInfoActivity extends AppCompatActivity {
    private static final int PERMISSION_SELECT_PHOTO = 1;
    private static final int PERMISSION_REQUEST_IMAGE_CAPTURE_CODE = 2; //拍照
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_PHOTO = 2;

    private Bitmap bitmap;  //存图片
    private EditText editTextName, editTextPhone, editTextPhone2, editTextQQ, editTextEmail;
    private ImageView imageViewAvatar;
    private RadioButton radioButtonMan, radioButtonWomen;
    private ConnectInfo c;
    private ConnectInfo cOld;
    private String mode;
    private Intent intent;

    public void add() {
        if (getConnectInfoFromView()) {
            try {
                DBO.insert(c);
                finish();
                Toast.makeText(EditInfoActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(EditInfoActivity.this, "添加出错:" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }

    }

    private void chooseMode() {
        //获取模式添加或编辑模式
        intent = getIntent();
        mode = intent.getStringExtra("mode");
        if (mode.equals("add")) {
            Toast.makeText(EditInfoActivity.this, "添加", Toast.LENGTH_SHORT).show();
        } else if (mode.equals("edit")) {
            Toast.makeText(EditInfoActivity.this, "编辑", Toast.LENGTH_SHORT).show();
            edit();
        }
    }

    private void edit() {

        cOld = (ConnectInfo) intent.getSerializableExtra("obj");
        /*临时解决方案。。。
         *由于bitmap不能序列化，所以直接接收来的对象中头像都是null
         * 因此在这里再进行一次查询，
         * */
        //cOld = DBO.query(cOld);
        editTextName.setText(cOld.getName());
        editTextPhone.setText(cOld.getPhone());
        editTextPhone2.setText(cOld.getPhone2());
        editTextQQ.setText(cOld.getqq());
        editTextEmail.setText(cOld.getEmail());

        if (cOld.getAvatar() != null) {
            imageViewAvatar.setImageBitmap(cOld.getAvatar());
        }
        if (cOld.getSex() != null) {
            if (cOld.getSex().equals("男")) {
                radioButtonMan.setChecked(true);
            } else {
                radioButtonWomen.setChecked(true);
            }
        }
    }

    private boolean getConnectInfoFromView() {
        if (editTextName.getText().toString().trim().length() == 0
                || editTextPhone.getText().toString().length() == 0) {
            Toast.makeText(EditInfoActivity.this, "姓名、手机号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Tool.isNumeric(editTextPhone.getText().toString().trim())
                || !Tool.isNumeric(editTextPhone2.getText().toString().trim())) {
            Toast.makeText(EditInfoActivity.this, "号码无效", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Tool.isEmoji(editTextName.getText().toString().trim())) {
            Toast.makeText(EditInfoActivity.this, "名字包含emoji", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Tool.isQQ(editTextQQ.getText().toString().trim())) {
            Toast.makeText(EditInfoActivity.this, "QQ无效", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Tool.isEmail(editTextEmail.getText().toString().trim())) {
            Toast.makeText(EditInfoActivity.this, "无效邮箱", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (radioButtonMan.isChecked()) {
                c.setSex("男");
            } else if (radioButtonWomen.isChecked()) {
                c.setSex("女");
            }
            c.setName(editTextName.getText().toString().trim());
            c.setPhone(editTextPhone.getText().toString().trim());
            c.setPhone2(editTextPhone2.getText().toString().trim());
            c.setqq(editTextQQ.getText().toString().trim());
            c.setEmail(editTextEmail.getText().toString().trim());
            if (bitmap != null) {
                c.setAvatar(Tool.getBytes(bitmap));
            }
        }
        return true;
    }

    private void initView() {
        editTextName = findViewById(R.id.edit_name);
        editTextPhone = findViewById(R.id.edit_phone);
        editTextPhone2 = findViewById(R.id.edit_phone2);
        editTextQQ = findViewById(R.id.edit_qq);
        editTextEmail = findViewById(R.id.edit_email);
        radioButtonMan = findViewById(R.id.radio_man);
        radioButtonWomen = findViewById(R.id.radio_women);
        imageViewAvatar = findViewById(R.id.id_add_avatar);
        //为头像单击注册菜单
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(imageViewAvatar);
                openContextMenu(imageViewAvatar);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SELECT_PHOTO:
                //获取图片
                if (resultCode == RESULT_OK && data != null) {
                    ContentResolver cr = EditInfoActivity.this.getContentResolver();
                    Uri uri = data.getData();
                    try {
                        bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    imageViewAvatar.setImageBitmap(bitmap);
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                //获取拍照后的照片
                if (resultCode == RESULT_OK && data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageViewAvatar.setImageBitmap(imageBitmap);
                    c.setAvatar(Tool.getBytes(imageBitmap));
                }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                //打开相册
                if (Tool.checkPermission(EditInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Tool.openSelectPhoto(EditInfoActivity.this, REQUEST_SELECT_PHOTO);
                } else {
                    ActivityCompat.requestPermissions(EditInfoActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_SELECT_PHOTO);
                }
                break;
            case 2:
                //打开相机
                Tool.openCamera(EditInfoActivity.this, REQUEST_IMAGE_CAPTURE);
                break;
        }
        return super.onContextItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        c = new ConnectInfo();
        initView();
        chooseMode();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 0, "替换");
        menu.add(0, 2, 0, "拍照");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        MenuItem saveItem = menu.findItem(R.id.save);
        saveItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mode.equals("add")) {
                    add();
                }
                if (mode.equals("edit")) {
                    save();
                    Toast.makeText(EditInfoActivity.this, "保存", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //检测打开相册权限
            case PERMISSION_SELECT_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Tool.openSelectPhoto(EditInfoActivity.this, REQUEST_SELECT_PHOTO);
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_IMAGE_CAPTURE_CODE:
                Toast.makeText(this, "相机打开了", Toast.LENGTH_SHORT);
                break;

        }
    }

    private void save() {
//        DBO.delete(c);
//        add();
        if (getConnectInfoFromView()) {
            DBO.update(c, cOld);
            finish();
        }
    }
}
