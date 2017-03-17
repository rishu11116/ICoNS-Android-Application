package app.racdeveloper.com.ICoNS.commentOnNewsFeed;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import app.racdeveloper.com.ICoNS.QueryPreferences;
import app.racdeveloper.com.ICoNS.R;

public class CommentList extends Fragment {

    public RecyclerView recyclerView;
    private TextView tvCheck;
    public CommentViewAdapter adapter;
    public static List<CommentData> data_list;
    RequestQueue requestQueue;
    Context context;
    String feedID;

    interface ListUpdateListenerOnEdit{
        void listRefreshedOnEdit();
    }
    ListUpdateListenerOnEdit listenerList;

    public CommentList(){

    }

    @SuppressLint("ValidFragment")
    public CommentList(Context context) {
        this.context = context;
    }

    @SuppressLint("ValidFragment")
    public CommentList(Context context, String feedID) {
        this.context = context;
        this.feedID = feedID;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity= null;
        if(context instanceof Activity)
            activity= (Activity) context;
        this.listenerList= (ListUpdateListenerOnEdit) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comment_list, container, false);

//        listenerList = (ListUpdateListenerOnEdit) context;
        tvCheck = (TextView) v.findViewById(R.id.tvCheck);
        recyclerView = (RecyclerView)v.findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        data_list = new ArrayList<>();
        loadComments();
        adapter = new CommentViewAdapter(getActivity(),data_list);
        recyclerView.setAdapter(adapter);
        return v;
    }

    private void loadComments() {
        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Integer... integers) {

                requestQueue = Volley.newRequestQueue(getContext());
                String URL = Constants.URL + "newsFeeds/comments/get";

                String token = QueryPreferences.getToken(getContext());
                Map<String, String> param = new HashMap<>();
                param.put("feedID",feedID);
                param.put("token",token);
                JsonObjectRequest object = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray commentArray = response.getJSONArray("comments");
                            if (commentArray.length()!=0)
                                tvCheck.setVisibility(View.GONE);
                            for (int i = 0; i < commentArray.length(); i++) {
                                JSONObject commentObject = commentArray.getJSONObject(i);

                                CommentData commentData = new CommentData();
                                commentData.setCommentID(commentObject.getInt("id"));
                                commentData.setCommentUserImageUrl(commentObject.getString("authorImageLink"));
                                commentData.setCommentUserName(commentObject.getString("authorName"));
                                commentData.setCommentUserRollNo(commentObject.getString("authorRollno"));
                                commentData.setCommentText(commentObject.getString("content"));
                                //timestamp;

                                data_list.add(commentData);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                            }
                        }
                );
                requestQueue.add(object);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        };
        task.execute();
    }

    public void editCommentOptions(final CommentData comment) {


        final AlertDialog.Builder[] builder = {new AlertDialog.Builder(context)};
        builder[0].setTitle("Select your Choice");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView deleteComment = new TextView(context);
        deleteComment.setTextSize(20);
        deleteComment.setText("\n\t\t\t\tDelete your Comment");

        deleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int index="how to get position of that item";
                deleteComment(comment.getCommentID());
            }
        });

        final TextView changeComment = new TextView(context);
        changeComment.setTextSize(20);
        changeComment.setText("\n\t\t\t\tChange your comment\n");

        changeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Change Comment");
                alert.setMessage("Submit your comment");

                final EditText inputNewComment = new EditText(context);
                alert.setView(inputNewComment);

                alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editComment(inputNewComment.getText().toString(), comment.getCommentID());
                    }
                });

                alert.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                alert.show();
            }
        });
        layout.addView(deleteComment);
        layout.addView(changeComment);
        builder[0].setView(layout);
        builder[0].show();
    }

    private void editComment(String newComment, int commentID) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Constants.URL + "newsFeeds/comments/edit";
        String token = QueryPreferences.getToken(context);
//        String feedID = getActivity().getIntent().getExtras().getString("feedID");
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNDA0MzEwMDM1IiwiaXNzIjoiaHR0cDpcL1wvbG9jYWxob3N0XC9sYXJhdmVsLXByb2plY3RzXC93ZWJhcGlcL3B1YmxpY19odG1sXC9hcGlcL2xvZ2luIiwiaWF0IjoxNDc5OTU1NTgyLCJleHAiOjE1MTE0OTE1ODIsIm5iZiI6MTQ3OTk1NTU4MiwianRpIjoiOTY5MDQyZTgzYmRkMTk0ZGZmNzg2MTdiMDA5YjlhZjAifQ.viRCvuaYYTTF2CBCedyYuw-U4a3BpEYUAYf6Jyjm-Co";
//        String feedID="10";
        // how to get the commentId
        Map<String, String> param = new HashMap<String, String>();
//        param.put("feedID",feedID);
        param.put("token", token);
        param.put("newContent", newComment);
        param.put("commentID", String.valueOf(commentID));
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        Toast.makeText(context, "Your comment is successfully edited", Toast.LENGTH_SHORT).show();
                        listenerList = (ListUpdateListenerOnEdit) context;
                        listenerList.listRefreshedOnEdit();
                    } else {
                        String error = jsonObject.getString("error");
                        Toast.makeText(context, "Failed to edit comment because of " + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        requestQueue.add(jor);
    }

    public void deleteComment(int commentID) {

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Constants.URL + "newsFeeds/comments/delete";
        String token = QueryPreferences.getToken(context);
//        String feedID = getActivity().getIntent().getExtras().getString("feedID");
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNDA0MzEwMDM1IiwiaXNzIjoiaHR0cDpcL1wvbG9jYWxob3N0XC9sYXJhdmVsLXByb2plY3RzXC93ZWJhcGlcL3B1YmxpY19odG1sXC9hcGlcL2xvZ2luIiwiaWF0IjoxNDc5OTU1NTgyLCJleHAiOjE1MTE0OTE1ODIsIm5iZiI6MTQ3OTk1NTU4MiwianRpIjoiOTY5MDQyZTgzYmRkMTk0ZGZmNzg2MTdiMDA5YjlhZjAifQ.viRCvuaYYTTF2CBCedyYuw-U4a3BpEYUAYf6Jyjm-Co";
//        String feedID = "10";
        // how to get the commentId
        Map<String, String> param = new HashMap<String, String>();
        param.put("token", token);
        param.put("commentID", String.valueOf(commentID));
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        Toast.makeText(context, "Your comment is successfully deleted", Toast.LENGTH_SHORT).show();
                        listenerList = (ListUpdateListenerOnEdit) context;
                        listenerList.listRefreshedOnEdit();
                    } else {
                        String error = jsonObject.getString("error");
                        Toast.makeText(context, "Failed to delete comment because of " + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        requestQueue.add(jor);
    }
}
