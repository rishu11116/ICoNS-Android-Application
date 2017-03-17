package app.racdeveloper.com.ICoNS;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by Rachit on 2/21/2017.
 */
public class MailToAdminActivity extends AppCompatActivity{

    private Button bSend;
    private EditText etMessage;
    private TextView tv;
    private String URL = Constants.URL + "basics/mail_to_admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_to_admin);

        tv = (TextView) findViewById(R.id.tv);
        tv.setText("@admin");
        etMessage = (EditText) findViewById(R.id.etMessage);
        bSend = (Button) findViewById(R.id.bMessageSend);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etMessage.getText().toString().equals("")){
                    sendMessage();
                }
                else
                    Toast.makeText(MailToAdminActivity.this, "Message Field Empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(MailToAdminActivity.this, "Message Successfully sent!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MailToAdminActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params= new HashMap<>();
                params.put("token", QueryPreferences.getToken(MailToAdminActivity.this));
                params.put("content", etMessage.getText().toString());
                return params;
            }
        };
        requestQueue.add(request);
        finish();
    }
}
