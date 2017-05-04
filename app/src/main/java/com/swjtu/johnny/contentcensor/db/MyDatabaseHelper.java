package com.swjtu.johnny.contentcensor.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Johnny on 2017/4/5.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;

    private static final String CREATE_ARTICLE = "create table Article ("
            +"id integer primary key autoincrement,"
            +"title text,"
            +"content text,"
            +"state text,"
            +"time text)";

    private static final String CREATE_USER = "create table User ("
            +"id integer primary key autoincrement,"
            +"username text,"
            +"password text)";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ARTICLE);
        sqLiteDatabase.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exit Article");
        sqLiteDatabase.execSQL("drop table if exit User");
        onCreate(sqLiteDatabase);
    }
}
