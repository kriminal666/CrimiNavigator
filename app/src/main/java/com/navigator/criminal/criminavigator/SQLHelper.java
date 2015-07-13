package com.navigator.criminal.criminavigator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by criminal on 12/07/15.
 */
public class SQLHelper extends SQLiteOpenHelper {

    private String tableCreate = "CREATE TABLE info (cod integer NOT NULL PRIMARY KEY,"
            +"url TEXT,favicon BLOB, date TEXT, time TEXT)";
    private String dropTable = "DROP TABLE IF EXISTS info";

    //Constructor
    public SQLHelper(Context ctx,String database,SQLiteDatabase.CursorFactory cursor, int version){
        super(ctx,database,cursor,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Execute create table
        db.execSQL(tableCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //We just delete the old table and create new
        db.execSQL(dropTable);
        //Create new one
        db.execSQL(tableCreate);

    }
}
