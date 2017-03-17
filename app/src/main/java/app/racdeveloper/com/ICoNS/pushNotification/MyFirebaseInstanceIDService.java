package app.racdeveloper.com.ICoNS.pushNotification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.racdeveloper.com.ICoNS.Constants;
import app.racdeveloper.com.ICoNS.QueryPreferences;

/**
 * Created by Rachit on 10/16/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private static final String URL= Constants.URL + "";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d("debugt", "Broadcast is sent through on token refresh");
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String fcmToken) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + fcmToken);
        final String token= QueryPreferences.getToken(this);
        AsyncTask<Void, Void, Void> task= new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                RequestQueue requestQueue = Volley.newRequestQueue(MyFirebaseInstanceIDService.this);

                Map<String, String> params= new HashMap<>();
                params.put("token", token);
                params.put("fcmToken", "" + fcmToken);
                JsonObjectRequest jor= new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
                requestQueue.add(jor);
                return null;
            }
        };
        if(token!=null)
            task.execute();
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}
