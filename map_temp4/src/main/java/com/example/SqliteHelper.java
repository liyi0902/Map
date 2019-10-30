package com.example;



import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class SqliteHelper extends SQLiteAssetHelper  {
    private static final String DATABASE_NAME = "Project_DB.db";
    private static final int DATABASE_VERSION = 3;
    //private static String path = "data/data/com.example/databases/" ;

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }


}