package app.racdeveloper.com.ICoNS.commentOnNewsFeed;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.racdeveloper.com.ICoNS.Constants;
import app.racdeveloper.com.ICoNS.QueryPreferences;
import app.racdeveloper.com.ICoNS.R;


@SuppressLint("ValidFragment")
public class AddCommentFragment extends Fragment {

    private static String URL = Constants.URL + "newsFeeds/comments/post";
    EditText addCommentEditText;
    String feedID;
    private boolean isAddClicked = false;           // Add button once clicked can't be clicked again
    @SuppressLint("ValidFragment")
    public AddCommentFragment(String feedID) {
        this.feedID = feedID;
        Log.i("pppp", this.feedID+"");
    }

    interface ListUpdateListener{
        void listRefreshed();
    }
    ListUpdateListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        Activity activity= null;
//        if(context instanceof Activity)
//            activity= (Activity) context;
        this.listener= (ListUpdateListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_comment, container, false);

        addCommentEditText = (EditText) v.findViewById(R.id.addCommentEditText);
        addCommentEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    addCommentEditText.setHint("");
                    return false;
                }
        });

        Button addCommentButton = (Button) v.findViewById(R.id.addCommentButton);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addCommentEditText.getText().toString().equals("") && !isAddClicked) {
                    isAddClicked = true;
                    submitComment(addCommentEditText.getText().toString());
                }
            }
        });
        return v;
    }

    public void submitComment(String comment) {

        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String token = QueryPreferences.getToken(getContext());
        Map<String, String> param = new HashMap<String, String>();

        Log.i("pppp", feedID+"");

        param.put("feedID",feedID);
        param.put("token", token);
        param.put("content", comment);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        addCommentEditText.setText("");
                        Toast.makeText(getContext(), "Your comment is successfully submitted", Toast.LENGTH_SHORT).show();
                        listener.listRefreshed();
                    } else {
                        String error = jsonObject.getString("error");
                        Toast.makeText(getContext(), "Failed to add comment because of " + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isAddClicked = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                isAddClicked = false;
            }
        });
        requestQueue.add(jor);
    }
}
