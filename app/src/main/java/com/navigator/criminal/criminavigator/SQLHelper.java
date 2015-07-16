package com.navigator.criminal.criminavigator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by criminal on 12/07/15.
 */
public class SQLHelper extends SQLiteOpenHelper {

    private String tableHistoric = "CREATE TABLE info (cod integer NOT NULL PRIMARY KEY,"
            +"url TEXT,favicon BLOB, date TEXT, time TEXT)";
    private String dropHistoric = "DROP TABLE IF EXISTS info";

    private String tableBookmarks = "CREATE TABLE bookmarks (cod integer NOT NULL PRIMARY KEY," +
            "url TEXT)";
    private String dropBookmarks = "DROP TABLE IF EXISTS bookmarks";

    //Constructor
    public SQLHelper(Context ctx,String database,SQLiteDatabase.CursorFactory cursor, int version){
        super(ctx,database,cursor,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Execute create table
        db.execSQL(tableHistoric);
        Log.d(BaseUtils.TAG, "After create historic table");
        db.execSQL(tableBookmarks);
        Log.d(BaseUtils.TAG, "After create bookmarks table");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //We just delete the old table and create new
        db.execSQL(dropHistoric);
        db.execSQL(dropBookmarks);
        //Create new one
        db.execSQL(tableHistoric);
        db.execSQL(tableBookmarks);

    }
}
