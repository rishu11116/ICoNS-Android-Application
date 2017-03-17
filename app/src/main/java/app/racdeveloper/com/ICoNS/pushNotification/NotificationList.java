package app.racdeveloper.com.ICoNS.pushNotification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.racdeveloper.com.ICoNS.Constants;
import app.racdeveloper.com.ICoNS.R;

/**
 * Created by Rachit on 1/16/2017.
 */
public class NotificationList extends AppCompatActivity{

    ListView notificationList;
    List<String> list;
    TextView tvCheck;           //if notifications loaded.... visibility gone

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        tvCheck = (TextView) findViewById(R.id.tvCheck);
        notificationList = (ListView) findViewById(R.id.lvNotification);
        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(NotificationList.this, "gcfhgvjh", Toast.LENGTH_SHORT).show();
            }
        });
        getNotificationList();
    }

    private void getNotificationList() {
        final String[] resultKey = new String[1];
        final RequestQueue requestQueue= Volley.newRequestQueue(this);
        String url= Constants.URL + "notifications/get";
        Map<String, String> param= new HashMap<>();
        //param.put("token", QueryPreferences.getToken(this));

        JsonObjectRequest jor= new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    
                    resultKey[0] = jsonObject.getString("result");
                    //Log.i("ppp", resultKey[0]);

                    if (resultKey[0].equals("success")) {
                        tvCheck.setVisibility(View.GONE);
                        list = new ArrayList<>();
                        JSONArray notificationArray= jsonObject.getJSONArray("notifications");
                        int length= notificationArray.length();
                        for (int i=0;i<length;i++) {
                            //Title content createdAT
                            JSONObject notifFound= notificationArray.getJSONObject(i);
                            String entity= "\n" + notifFound.getString("title") + "\n" + notifFound.getString("message")
                                                + "\n" + notifFound.getString("timestamp") + "\n";
                            list.add(entity);
                        }
                        ArrayAdapter<String> adapter= new ArrayAdapter<>(NotificationList.this,
                                android.R.layout.simple_list_item_1, list);
                        notificationList.setAdapter(adapter);
                    }
                    else {
                        Toast.makeText(NotificationList.this, "No Record Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(NotificationList.this, "Error loading notifications...", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jor);
    }

}
