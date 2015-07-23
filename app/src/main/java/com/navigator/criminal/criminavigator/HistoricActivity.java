package com.navigator.criminal.criminavigator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by criminal on 12/07/15.
 */
public class HistoricActivity extends ActionBarActivity implements View.OnClickListener {

    //Table historic
    private static final String HISTORIC = "info";
    //Table bookmarks
    private static final String BOOKMARKS = "bookmarks";
    private ListView listV;
    private DAO dao;
    private String inflateMenu;
    private boolean askResult = false;
    private String whatDelete;
    //This lets vibrate on click button actions
    Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(BaseUtils.TAG, "oncreate Historic");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historic_layout);
        listV = (ListView) findViewById(R.id.historic);
        Button deleteSomething = (Button)findViewById(R.id.btnDeleteSomething);
        deleteSomething.setOnClickListener(this);
        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        dao = new DAO(this);
        Log.d(BaseUtils.TAG, "historic intent action: " + getIntent().getAction());
        switch (getIntent().getAction()) {
            case BaseUtils.HISTORIC_INTENT:
                deleteSomething.setText(R.string.historicDelete);
                whatDelete = HISTORIC;
                inflateMenu = BaseUtils.HISTORIC_INTENT;
                getHistoric();
                break;
            case BaseUtils.BOOKMARKS_INTENT:
                whatDelete = BOOKMARKS;
                deleteSomething.setText(R.string.bookmarksDelete);
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

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Get URL and go back to the first activity
     *
     * @param item
     */
    private void getUrl(String item) {
        String item1 = item.replaceAll("\n", " ");
        String url = item1.substring(item1.indexOf("[") + 1, item1.indexOf("]")).trim();
        //Go back to the first activity to load de url
        Intent intent = new Intent();
        intent.putExtra(BaseUtils.URL, url);
        setResult(RESULT_OK, intent);
        //End this activity
        finish();


    }


    private void getBookmarks() {
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
        switch (id) {
            case R.id.deleteHistoric:
                vibe.vibrate(60); // 60 is time in ms
                deleteHistoric();
                break;
            case R.id.deleteBookmarks:
                vibe.vibrate(60); // 60 is time in ms
                //Ask if sure to delete
                  askDelete(BOOKMARKS);

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete all historic from database
     */
    private void deleteHistoric() {
        dao.deleteTable(this, HISTORIC);
        listV.setAdapter(null);
    }


    /**
     * ask if sure to delete
     *
     * @param table table name to delete
     * @return
     */
    private boolean askDelete(final String table) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("DELETE");
        builder.setMessage("Are you sure to delete all " + table + "?");
        builder.setIcon(R.drawable.advise40);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                vibe.vibrate(60); // 60 is time in ms
                //iF YES then delete
                switch (table) {
                    case HISTORIC:
                        break;
                    case BOOKMARKS:
                        dao.deleteTable(HistoricActivity.this, BOOKMARKS);
                        listV.setAdapter(null);
                }

                dialog.dismiss();
            }

        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                vibe.vibrate(60); // 60 is time in ms
                dialog.dismiss();

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return askResult;
    }


    @Override
    public void onClick(View v) {
        vibe.vibrate(60); // 60 is time in ms
        int id = v.getId();
        switch (id){
            case R.id.btnDeleteSomething:
                switch (whatDelete){
                    case HISTORIC:
                       deleteHistoric();
                        break;
                    case BOOKMARKS:
                        askDelete(BOOKMARKS);
                        break;
                }
        }
    }
}
