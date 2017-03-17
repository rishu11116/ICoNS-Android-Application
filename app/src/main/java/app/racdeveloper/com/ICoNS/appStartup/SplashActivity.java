package app.racdeveloper.com.ICoNS.appStartup;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.widget.ImageView;

import app.racdeveloper.com.ICoNS.LoginActivity;
import app.racdeveloper.com.ICoNS.PostMsgActivity;
import app.racdeveloper.com.ICoNS.ProfileActivity;
import app.racdeveloper.com.ICoNS.QueryPreferences;
import app.racdeveloper.com.ICoNS.R;

/**
 * Created by user on 09-08-2016.
 */
public class SplashActivity extends AppCompatActivity {

    ImageView img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img= (ImageView) findViewById(R.id.imageViewSplash);
        img.setImageResource(R.drawable.logo_splash);

        Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if(isNetworkAvailableAndConnected()) {
                    if(QueryPreferences.isIntroNeeded(SplashActivity.this)){
                        Intent i= new Intent(SplashActivity.this, IntroActivity.class);
                        startActivity(i);
                    }
                    else if (!QueryPreferences.isTokenSet(SplashActivity.this)) {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                    } else {
                        Intent receivedIntent=getIntent();
                        String receivedAction=receivedIntent.getAction();
                        if(receivedAction!=null) {
                            if (receivedAction.equals(Intent.ACTION_SEND)) {
                                Intent postIntent = new Intent(SplashActivity.this, PostMsgActivity.class);
                                String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                                Uri receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
                                if (receivedText != null) {
                                    postIntent.putExtra("Text", receivedText);
                                }
                                if (receivedUri != null) {
                                    postIntent.putExtra("ImageUri", receivedUri);
                                }
                                startActivity(postIntent);
                            }else {
                                Intent i = new Intent(SplashActivity.this, ProfileActivity.class);
                                startActivity(i);
                            }
                        }
                        else {
                            Intent i = new Intent(SplashActivity.this, ProfileActivity.class);
                            startActivity(i);
                        }
                    }
//                }
//                else{
//                    Toast.makeText(SplashActivity.this, "No Network!", Toast.LENGTH_SHORT).show();
//                }
            }
        },1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable= cm.getActiveNetworkInfo()!=null;
        boolean isNetworkConnected= isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==  KeyEvent.KEYCODE_BACK)
            showDialogOnExit();
        return super.onKeyDown(keyCode, event);
    }

    private void showDialogOnExit(){
        final AlertDialog.Builder builder= new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Do you want to exit?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}
