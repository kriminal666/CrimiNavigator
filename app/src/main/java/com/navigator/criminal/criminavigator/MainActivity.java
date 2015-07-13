package com.navigator.criminal.criminavigator;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import static android.widget.Toast.*;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText editUrl;
    private WebView wV;
    private ImageView favicon;
    private Button goButton;
    private ProgressBar progress;
    private DAO dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Create dao object and create table
        dao = new DAO(this);
        //dao.createTable(this);
        editUrl = (EditText)findViewById(R.id.edtUrl);
        favicon = (ImageView)findViewById(R.id.imageView);
        goButton = (Button)findViewById(R.id.goButton);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        wV = (WebView)findViewById(R.id.webView);
        //Go button listener
        goButton.setOnClickListener(this);


        wV.loadUrl("https://www.google.es");
        wV.getSettings().setJavaScriptEnabled(true);
        wV.getSettings().setBuiltInZoomControls(true);
        wV.getSettings().setSupportZoom(true);
        wV.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                favicon.setImageBitmap(icon);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                getWindow().setTitle(title);
            }
        });

        wV.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view ,String url){
                progress.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failUrl){
                Toast.makeText(getApplicationContext(),"Error: "+description,Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings :
                return true;

            case R.id.historic:
                Intent intent = new Intent(MainActivity.this,HistoricActivity.class);
                startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goButton:
                String url = editUrl.getText().toString();
                wV.loadUrl("http://"+url);
                //insert historic
                dao.insertHistoric(this,url);

        }

    }




}