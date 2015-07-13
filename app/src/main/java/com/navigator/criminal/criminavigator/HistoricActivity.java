package com.navigator.criminal.criminavigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

    private static final String URL = "URL" ;
    private ListView listV;
    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historic_layout);
        listV = (ListView)findViewById(R.id.historic);
        dao = new DAO(this);
        getHistoric();
        //On item selected
        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                getUrl(item);

            }
        });


    }

    private void getUrl(String item) {
        String item1 = item.replaceAll("\n", " ");
        String url = item1.substring(item1.indexOf("[") + 1, item1.indexOf("]"));
        //Go back to the first activity to load de url
        Intent intent = new Intent();
        intent.putExtra(URL,url);
        setResult(RESULT_OK, intent);
        //End this activity
        finish();


    }


    //Get the historic
    private void getHistoric() {

        ArrayList<String> listHistoric = dao.selectHistoric(this);
        listV.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, listHistoric));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.deleteHistoric) {
            dao.deleteHistoric(this);
            listV.setAdapter(null);
        }

        return super.onOptionsItemSelected(item);
    }
}
