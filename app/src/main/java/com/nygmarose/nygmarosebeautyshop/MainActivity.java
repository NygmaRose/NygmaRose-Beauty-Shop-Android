package com.nygmarose.nygmarosebeautyshop;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.google.firebase.messaging.FirebaseMessaging;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    private WebView mWebView;
    SwipeRefreshLayout mySwipeRefreshLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView splashH = findViewById(R.id.splash_horizontal);
        ImageView splashV = findViewById(R.id.splash_vertical);
        // Set Orientation Splash
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //If portrait, only display portrait splash
            splashH.setVisibility(View.GONE);
        } else {
            //If horizontal, only display horizontal splash
            splashV.setVisibility(View.GONE);
        }
        // Create swipe container for swipe to refresh and WebView client + settings
        mySwipeRefreshLayout = this.findViewById(R.id.swipeContainer);
        mWebView = this.findViewById(R.id.activity_main_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new ShopWebViewClient());
        mWebView.loadUrl(getString(R.string.url));
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mWebView.reload();
                    }
                }
        );
        // Subscribe to default topic for mass messaging with HTTP send requests
        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String web = mWebView.getUrl();
        mWebView.loadUrl(web);
    }

    private class ShopWebViewClient extends WebViewClient{


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "Page loaded: " + url);
            super.onPageStarted(view, url, favicon);
            mySwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            ImageView splashH = findViewById(R.id.splash_horizontal);
            ImageView splashV = findViewById(R.id.splash_vertical);
            splashV.setVisibility(View.GONE);
            splashH.setVisibility(View.GONE);
            mySwipeRefreshLayout.setRefreshing(false);
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    { //if back key is pressed
        if((keyCode == KeyEvent.KEYCODE_BACK)&& mWebView.canGoBack())
        {
            mWebView.goBack();
            return true;

        }

        return super.onKeyDown(keyCode, event);

    }


    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set title
        alertDialogBuilder.setTitle("Exit");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you really want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
