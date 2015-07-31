package com.navigator.criminal.criminavigator;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {



    private EditText editUrl;
    private WebView wV;
    private ImageView favicon;
    private Button goButton;
    private ProgressBar progress;
    private DAO dao;
    private int error = 0;
    private String imageUrl;

    //This lets vibrate on click button actions
    Vibrator vibe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create dao object and create table
        dao = new DAO(this);
        dao.createTable(this);
        editUrl = (EditText)findViewById(R.id.edtUrl);
        favicon = (ImageView)findViewById(R.id.imageView);
        goButton = (Button)findViewById(R.id.goButton);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        wV = (WebView)findViewById(R.id.webView);
        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;
        //Go button listener
        goButton.setOnClickListener(this);
        //Check internet connection
         if(internetConnection()){
             String initPage = "www.google.es";
             this.webViewSettings(initPage);
         }

        Log.d(BaseUtils.TAG, "OnCreate");


    }



    @Override
    public void onResume(){
        super.onResume();
        Log.d(BaseUtils.TAG, "onResume");
        internetConnection();

    }



    //Check connection
    private boolean internetConnection(){
        if(!isConnected()) {
            //Show Toast
            Toast.makeText(this,"YOU ARE NOT CONNECTED TO INTERNET",Toast.LENGTH_LONG).show();
            //Open data roaming settings
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
            startActivity(intent);
            return false;
        }
        return true;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        if (wV.canGoBack()){
            wV.goBack();
        }else {
            moveTaskToBack(true);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);


        WebView.HitTestResult result = wV.getHitTestResult();
         Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // Get link-URL.
                String url = (String) msg.getData().get("url");

                // Do something with it.
                if (url != null){
                    imageUrl = url;
                }
            }
        };


        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId()){
                    case BaseUtils.ID_SAVE_IMAGE:
                        Toast.makeText(MainActivity.this,"Save",Toast.LENGTH_SHORT).show();
                        Log.d(BaseUtils.TAG, imageUrl);
                        saveImage(imageUrl);
                }
                return true;
            }
        };
        if (result.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            // Menu options for an image.
            //set the header title to the image url
            Message msg = mHandler.obtainMessage();
            wV.requestFocusNodeHref(msg);
            menu.setHeaderTitle(result.getExtra());
            menu.add(0, BaseUtils.ID_SAVE_IMAGE, 0, "Save Image").setOnMenuItemClickListener(handler);
            //menu.add(0, ID_VIEWIMAGE, 0, "View Image").setOnMenuItemClickListener(handler);
        } else if (result.getType() == WebView.HitTestResult.ANCHOR_TYPE ||
                result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            // Menu options for a hyperlink.
            //set the header title to the link url
            menu.setHeaderTitle(result.getExtra());
            menu.add(0, BaseUtils.ID_SAVE_LINK, 0, "Save Link").setOnMenuItemClickListener(handler);
            //menu.add(0, ID_SHARELINK, 0, "Share Link").setOnMenuItemClickListener(handler);
        }

    }
    private  void saveImage(String imageUrl) {
        //Log.d(BaseUtils.TAG,imageUrl);
        try {
            URL url = new URL(imageUrl);
            InputStream input = url.openStream();
            try {

                File storagePath = Environment.getExternalStorageDirectory();
                OutputStream output = new FileOutputStream(storagePath + imageUrl);
                try {
                    byte[] buffer = new byte[1500];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output.write(buffer, 0, bytesRead);
                    }
                } finally {
                    output.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void webViewSettings(String url){
        //register webView for context menus
        this.registerForContextMenu(wV);
        wV.loadUrl("http://"+url);
        WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
        wV.getSettings().setJavaScriptEnabled(true);
        wV.getSettings().setBuiltInZoomControls(true);
        wV.getSettings().setSupportZoom(true);
        wV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
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

        wV.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {



                return super.shouldOverrideUrlLoading(view, url);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                editUrl.setText(url);
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.INVISIBLE);

                //if not errors
                if(error==0){
                    //insert historic
                    dao.insertHistoric(MainActivity.this, wV.getUrl());
                }
                error=0;

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failUrl) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(description).setPositiveButton("OK",null).setTitle("Web Page Error! "+failUrl);
                builder.show();
                error = 1;


            }
        });
    }

    //Check internet connection
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
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
                vibe.vibrate(60); // 60 is time in ms
                Intent historicIntent = new Intent(MainActivity.this,HistoricActivity.class);
                historicIntent.setAction(BaseUtils.HISTORIC_INTENT);
                startActivityForResult(historicIntent, 1);

                break;
            case R.id.action_left:

                if(wV.canGoBack()){
                    vibe.vibrate(60); // 60 is time in ms
                    wV.goBack();
                }else{
                    Toast.makeText(this,"Nothing to load",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_right:
               if(wV.canGoForward()){
                   vibe.vibrate(60); // 60 is time in ms
                   wV.goForward();
               }else{
                   Toast.makeText(this,"Nothing to load",Toast.LENGTH_SHORT).show();
               }
                break;
            case R.id.bookmarks:
                vibe.vibrate(60); // 60 is time in ms
                //Insert into bookmarks the active URL
                String url = wV.getUrl();
                dao.insertBookmark(this,wV.getUrl());
                break;
            case R.id.seeBookmarks:
                Intent bookmarkIntent = new Intent(MainActivity.this,HistoricActivity.class);
                bookmarkIntent.setAction(BaseUtils.BOOKMARKS_INTENT);
                startActivityForResult(bookmarkIntent,1);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String extras = data.getStringExtra(BaseUtils.URL);

                editUrl.setText(extras);
                editUrl.setSelection(editUrl.getText().length());
                favicon.setImageBitmap(null);
                Log.d(BaseUtils.TAG, "onActivityResult"+wV);
                //Load de url if connection exists
                if(internetConnection()) {

                    wV.loadUrl(extras);
                }

            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goButton:
                vibe.vibrate(60); // 60 is time in ms
                String url = editUrl.getText().toString();
                Log.d(BaseUtils.TAG,"url from edtx: "+url);
                favicon.setImageBitmap(null);
                wV.loadUrl("http://"+url);


        }

    }




}