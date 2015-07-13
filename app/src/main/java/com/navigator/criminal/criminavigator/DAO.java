package com.navigator.criminal.criminavigator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by criminal on 13/07/15.
 */
public class DAO {

    private static final String TAG ="TAG" ;
    private SQLiteDatabase database;
    private SQLHelper sqlTable = null;


    //Constructor
    public DAO (Context ctx){
        if (sqlTable == null){
           createTable(ctx);
        }

    }

    //Create table
    public void createTable(Context ctx){
        //Call object helper to create table
        try{
            sqlTable = new SQLHelper(ctx, "DBNavigator", null, 1);
        }catch(Exception e){
            Log.d(TAG, e.getMessage());
        }

    }

    public ArrayList selectHistoric(Context ctx){
        Log.d(TAG, "select Method "+sqlTable);
        //Open to read
        database = sqlTable.getReadableDatabase();
        Log.d(TAG, "after get database");
        String selectAll ="SELECT * FROM info";

        ArrayList<String> rows = new ArrayList<String>();
        Cursor cursor=null;

        //get all historic
            try {
                cursor = database.rawQuery(selectAll, null);
            }catch(Exception e){
                Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
            }
            //Check if we have rows
            if (cursor.getCount()==0){

                Toast.makeText(ctx,"The table is empty",Toast.LENGTH_SHORT).show();
                database.close();
            }else{
                //Move to first row
                cursor.moveToFirst();
                //get data from cursor to array list
                do
                {
                    Log.d(TAG,"cursor all id: "+cursor.getInt(0));
                    //Generate a new string in the arraylist
                    rows.add(new String("Historic:\n"+"Cod: "+cursor.getInt(0)+"\n"+
                            "URL: [ "+cursor.getString(1)+" ]\n"+"favicon: "+cursor.getString(2)+"\n"+"Date: "+cursor.getString(3)+"\n"+
                                "Time: "+cursor.getString(4)));

                } while(cursor.moveToNext());
                database.close();
            }


        return rows;


    }
    //Method to insert historic url into table
    public void insertHistoric(Context ctx, String url){
        //Get date and hour
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        if (url.equals("")){
            Toast.makeText(ctx, "Empty URL", Toast.LENGTH_SHORT).show();
            return;

        }
        String insert = "INSERT INTO info VALUES(NULL,'"+url+"',NULL,'"+currentDate+"','"+currentTime+"')";

        try{
            //open to write database
            database = sqlTable.getWritableDatabase();
            //Execute the query
            database.execSQL(insert);

        }catch(Exception e){
            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        Toast.makeText(ctx,"New URL Historic", Toast.LENGTH_SHORT).show();
        database.close();


    }

    //Method to delete ALL HISTORIC from the table
    public void deleteHistoric(Context ctx){
        try{
            //open the database to write(delete)
            database = sqlTable.getWritableDatabase();
            //Execute query
            database.delete("info",null,null);
            //Close the database
            database.close();

        }catch(Exception e){
            Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        Toast.makeText(ctx,"Historic deleted",Toast.LENGTH_SHORT).show();
    }


}
