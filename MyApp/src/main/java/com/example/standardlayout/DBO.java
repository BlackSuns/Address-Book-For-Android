package com.example.standardlayout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class DBO {

    public static void delete(ConnectInfo c) {
        Log.println(Log.INFO, "haya", "删除-姓名：" + c.getName() + " 号码：" + c.getPhone());
        String sql = String.format("delete from connectInfo where name = '%s'and phone = '%s'", c.getName(), c.getPhone());
        db.execSQL(sql);
    }

    public static void insert(ConnectInfo c) {
        ContentValues values = new ContentValues();
        //存头像
        if (c.getAvatar() != null) {
            byte[] b = Tool.getBytes(c.getAvatar());
            values.put("avatar", b);
        }
        values.put("name", c.getName());
        values.put("sex", c.getSex());
        values.put("phone", c.getPhone());
        values.put("phone2", c.getPhone2());
        values.put("qq", c.getqq());
        values.put("email", c.getEmail());
        db.insert("connectInfo", null, values);
    }

    public static Cursor query(String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public static ConnectInfo query(ConnectInfo c) {
        String sql = String.format("SELECT *from connectInfo where name = '%s'and phone = '%s'", c.getName(), c.getPhone());
        Cursor cursor = db.rawQuery(sql, null);
        //Bitmap bitmap = null;
        if (cursor.moveToNext()) {
            byte b[] = cursor.getBlob(cursor.getColumnIndex("avatar"));
//            if (b != null) {
//                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            }
//            if (bitmap==null){
//                Log.println(Log.INFO,"haya","空" +b.length);
//            }else {
//                Log.println(Log.INFO,"haya","非空"+b.length);
//            }
            c = new ConnectInfo(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    b,
                    cursor.getString(5),
                    cursor.getString(6));
        }
        cursor.close();
        return c;
    }

    public static void update(ConnectInfo c, ConnectInfo cOld) {
        ContentValues values = new ContentValues();
        if (c.getAvatar() != null) {
            //创建byte输出流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //讲bitmap转换成byte流
            c.getAvatar().compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            values.put("avatar", b);
        }
        values.put("name", c.getName());
        values.put("sex", c.getSex());
        values.put("phone", c.getPhone());
        values.put("phone2", c.getPhone2());
        values.put("qq", c.getqq());
        values.put("email", c.getEmail());
        db.update("connectInfo", values, "name = ? and phone =?", new String[]{cOld.getName(), cOld.getPhone()});
        //db.execSQL(sql);
    }

    private static MyOpenHelper myOpenHelper;
    private static SQLiteDatabase db;

    public DBO(Context context) {
        myOpenHelper = MyOpenHelper.getSingDB(context);
        db = myOpenHelper.getReadableDatabase();
    }
}
