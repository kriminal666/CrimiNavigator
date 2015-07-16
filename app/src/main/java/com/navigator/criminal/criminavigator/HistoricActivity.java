package com.navigator.criminal.criminavigator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by criminal on 12/07/15.
 */
public class HistoricActivity extends ActionBarActivity {

    //Table historic
    private static final String HISTORIC = "info";
    //Table bookmarks
    private static final String BOOKMARKS = "bookmarks";
    private ListView listV;
    private DAO dao;
    private String inflateMenu;
    //This lets vibrate on click button actions
    Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(BaseUtils.TAG, "oncreate Historic");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historic_layout);
        listV = (ListView)findViewById(R.id.historic);
        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;
        dao = new DAO(this);
        Log.d(BaseUtils.TAG, "historic intent action: " + getIntent().getAction());
        switch (getIntent().getAction()){
            case BaseUtils.HISTORIC_INTENT:
                inflateMenu = BaseUtils.HISTORIC_INTENT;
                getHistoric();
                break;
            case BaseUtils.BOOKMARKS_INTENT:
                inflateMenu = BaseUtils.BOOKMARKS_INTENT;
                getBookmarks();
                break;
        }

        //On item selected
        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                vibe.vibrate(60); // 60 is time in ms
                getUrl(item);

            }
        });


    }

    /**
     * Get URL and go back to the first activity
     * @param item
     */
    private void getUrl(String item) {
        String item1 = item.replaceAll("\n", " ");
        String url = item1.substring(item1.indexOf("[") + 1, item1.indexOf("]")).trim();
        //Go back to the first activity to load de url
        Intent intent = new Intent();
        intent.putExtra(BaseUtils.URL,url);
        setResult(RESULT_OK, intent);
        //End this activity
        finish();


    }

    private void getBookmarks(){
        ArrayList<String> listHistoric = dao.selectBookmarks(this);
        listV.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, listHistoric));

    }


    /**
     * get the historic from database
     */
    private void getHistoric() {

        ArrayList<String> listHistoric = dao.selectHistoric(this);
        listV.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, listHistoric));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(BaseUtils.TAG, "inflatemenu");
        switch (inflateMenu) {
            case BaseUtils.HISTORIC_INTENT:
                getMenuInflater().inflate(R.menu.menu_historic, menu);
                break;
            case BaseUtils.BOOKMARKS_INTENT:
                getMenuInflater().inflate(R.menu.menu_bookmarks, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.deleteHistoric:
                vibe.vibrate(60); // 60 is time in ms
                dao.deleteTable(this, HISTORIC);
                listV.setAdapter(null);
                break;
            case R.id.deleteBookmarks:
                vibe.vibrate(60); // 60 is time in ms
                //Ask if sure to delete
                if (askDelete(BOOKMARKS)){
                    //if true, delete
                    dao.deleteTable(this,BOOKMARKS);
                }
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     *
     * ask if sure to delete
     * @param table table name to delete
     * @return
     */
    private boolean askDelete(String table){
        final boolean[] result = new boolean[1];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("DELETE");
        builder.setMessage("Are you sure to delete all " + table + "?");
        builder.setIcon(R.drawable.advise40);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                vibe.vibrate(60); // 60 is time in ms
                //iF YES then true
                result[0] = true;

                dialog.dismiss();
            }

        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                vibe.vibrate(60); // 60 is time in ms
                //if cancel then false
                result[0] = false;
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return result[0];
    }
}
