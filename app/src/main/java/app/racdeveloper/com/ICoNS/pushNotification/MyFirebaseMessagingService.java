package app.racdeveloper.com.ICoNS.pushNotification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.racdeveloper.com.ICoNS.Constants;
import app.racdeveloper.com.ICoNS.QueryPreferences;
import app.racdeveloper.com.ICoNS.R;
import app.racdeveloper.com.ICoNS.appStartup.SplashActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    public static boolean isOngoingSet = false;
    private static boolean isTypeWebView= false;
    private String onOpenWebViewUrl;
    private String title;
    private String content;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("debugt", "From: " + remoteMessage.getFrom());
        QueryPreferences.incrementNotificationID(MyFirebaseMessagingService.this, QueryPreferences.getNotificationID(MyFirebaseMessagingService.this));

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e("debugt", "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e("debugt", "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e("debugt", "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String title, String message) {
        if (!isAppIsInBackground(getApplicationContext())) {
            showSimpleNotificationMessage(title, message, null);
        }else{
            // If the app is in background, firebase itself handles the notification
        }    }

    private void handleDataMessage(JSONObject json) {
        Log.e("debugt", "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");
            String notifID = payload.getString("notifID");  //used to send notification count
            isOngoingSet = payload.getBoolean("setOngoing");  //customise setOngoing notification method
            isTypeWebView = payload.getBoolean("isTypeWebView");
            if(isTypeWebView){
                onOpenWebViewUrl = payload.getString("onOpenWebViewUrl");
            }
            else{
                content = payload.getString("content");
            }

            Log.e("debugt", "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);
            Log.e("debugt", "isType WebView: " + isTypeWebView);
            if(isTypeWebView)
                Log.e("debugt", "WebView Url: " + onOpenWebViewUrl);
            else
                Log.e("debugt", "Content : " + content);

            sendNotifCountToServer(notifID);

//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
//                pushNotification.putExtra("message", message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//                Log.d("debugtest", "Foreground : "+ message);
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
//            } else {
                // app is in background, show the notification in notification tray

            Log.d("debugtest", "Background : "+ message);
            // check for image attachment
            if (TextUtils.isEmpty(imageUrl)) {
                    showSimpleNotificationMessage(title, message, timestamp);
                } else {
                    // image is present, show notification with image
                    showCustomBigNotifWithImage(title, message, timestamp, imageUrl);
                }
//            }
        } catch (JSONException e) {
            Log.e("debugt", "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e("debugt", "Exception: " + e.getMessage());
        }
    }

    private void sendNotifCountToServer(final String notifID) {

        AsyncTask<Void,Void,Void> task =  new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... mVoid) {
                String URL= Constants.URL + "notifications/increment_delivered";
                RequestQueue requestQueue = Volley.newRequestQueue(MyFirebaseMessagingService.this);
                Map<String, String> param = new HashMap<>();
                Log.d("ppp=", "Sending notification ID");
                param.put("notifID", notifID);
                JsonObjectRequest object = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ppp=", "Got response back from server on sending NotifID");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                });
                requestQueue.add(object);
                return null;
            }
        };
        task.execute();
    }

    /**
     * Showing notification with text only
     */
    private void showSimpleNotificationMessage(String title, String message, String timeStamp) {
        Context mContext = getApplicationContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        int icon = R.mipmap.ic_launcher;
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);



        // CREATE A SEPARATE METHOD
        //
        // ADD YOUR INTENT CREATION CODE HERE
        //
        // THAT WILL WORK FOR SIMPLE AS WELL AS CUSTOM NOTIFICATION
        //
        // BASED ON PAYLOAD CONDITIONS


        Intent intent= intentAction(isTypeWebView);
//        Intent intent = new Intent(this, NotificationViewActivity.class);
//        // Send data to NotificationView Class
//        intent.putExtra("title", title);
//        intent.putExtra("message", message);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        long time;
        if(timeStamp == null) time = 0;
        else time = getTimeMilliSec(timeStamp);

        Notification notification = mBuilder
                .setSmallIcon(icon)
                .setTicker(title)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pIntent)
                .setStyle(inboxStyle)
                .setWhen(time)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(isOngoingSet)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(QueryPreferences.getNotificationID(this), notification);
    }

    /**
     * Showing notification with text and image
     */

    public void showCustomBigNotifWithImage(String title, String message, String timestamp, String imageUrl)
    {

        RemoteViews remoteView = new RemoteViews(getPackageName(),
                R.layout.big_notification);

        Intent intent= intentAction(isTypeWebView);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bitmap = getBitmapFromURL(imageUrl);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(getTimeMilliSec(timestamp))
                // Set Icon
                .setSmallIcon(R.mipmap.ic_launcher)
                // Set Ticker Message
                .setTicker("This is ticker message")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Set RemoteViews into Notification
                .setOngoing(isOngoingSet)
                .build();

        notification.bigContentView = remoteView;


        // Locate and set the Image into customnotificationtext.xml ImageViews
        remoteView.setImageViewBitmap(R.id.bigNotifImage,bitmap);
        remoteView.setImageViewResource(R.id.bigNotifIcon,R.mipmap.ic_launcher);

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteView.setTextViewText(R.id.bigNotifTitle,title);
        remoteView.setTextViewText(R.id.bigNotifBody,message);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(QueryPreferences.getNotificationID(this), notification);
    }

    private Intent intentAction(boolean isTypeWebView){
        Intent i;
        if(isTypeWebView){
            i= new Intent(this, NotificationWebview.class);
            i.putExtra("uri", onOpenWebViewUrl);
            return i;
        }
        else{
            if(!content.equals("")) {
                i = new Intent(this, NotificationViewActivity.class);
                i.putExtra("title", title);
                i.putExtra("content", content);
                return i;
            }
            else{
                i = new Intent(this, SplashActivity.class);
                return i;
            }
        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }
}