package app.racdeveloper.com.ICoNS.pushNotification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.racdeveloper.com.ICoNS.R;

/**
 * Created by Rachit on 11/21/2016.
 */
public class NotificationWebview extends AppCompatActivity {

    ProgressBar mProgress;
    WebView mWebView;
    Uri mUriString;
    TextView tvLoad;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_webview);

        Intent i = getIntent();
        mUriString= Uri.parse(i.getStringExtra("uri"));

        mProgress= (ProgressBar) findViewById(R.id.webview_progress_bar);
        mProgress.setMax(100);

        tvLoad= (TextView) findViewById(R.id.tvLoad);

        mWebView= (WebView) findViewById(R.id.web_view_page);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100) {
                    mProgress.setVisibility(View.GONE);
                    tvLoad.setVisibility(View.GONE);
                }
                else{
                    mProgress.setVisibility(View.VISIBLE);
                    mProgress.setProgress(newProgress);
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        mWebView.loadUrl(String.valueOf(mUriString));
    }
}
