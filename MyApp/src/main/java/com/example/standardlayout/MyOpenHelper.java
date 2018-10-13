package com.example.standardlayout;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenHelper extends SQLiteOpenHelper {
    public static MyOpenHelper getSingDB(Context context) {
        //单例模式，使得多个activity能够使用同一个MyOpenHelper对象
        if (singDB == null) {
            singDB = new MyOpenHelper(context);
        }
        return singDB;
    }
    private static MyOpenHelper singDB;
    private final String createTableSQL =
            "CREATE TABLE connectInfo("
                    + "name VARCHAR(10),"
                    + "sex CHAR(1),"
                    + "phone VARCHAR(15),"
                    + "phone2 VARCHAR(15),"
                    + "avatar BLOB,"
                    + "qq VARCHAR(12),"
                    + "email VARCHAR(20),"
                    + "PRIMARY KEY(name,phone));";

    private MyOpenHelper(Context context) {
        super(context, "haya.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        db.execSQL(createTableSQL);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
