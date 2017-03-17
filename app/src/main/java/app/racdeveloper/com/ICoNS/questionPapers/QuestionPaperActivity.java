package app.racdeveloper.com.ICoNS.questionPapers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
 * Created by Rachit on 10/19/2016.
 */
public class QuestionPaperActivity extends AppCompatActivity {

    DownloadManager manager;

    TextView tvCheck;           //if no question paper found.... visibility up
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private View mProgressView;
    private QuestionPaperViewAdapter adapter;
    private List<QuestionPaperData> data_list;
    RequestQueue requestQueue;
    String URL = Constants.URL + "questionPapers/get";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_paper);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvCheck = (TextView) findViewById(R.id.tvCheck);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mProgressView = findViewById(R.id.question_progress);
        data_list = new ArrayList<>();
        showProgress(true);
        load_question_papers(0);

        gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new QuestionPaperViewAdapter(this,data_list);
        recyclerView.setAdapter(adapter);
    }

    public void download(final View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to download this question paper ?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                int itemPosition= recyclerView.getChildAdapterPosition(view);
                String url = data_list.get(itemPosition).getPaperurl();

                String title = data_list.get(itemPosition).getPapername();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setTitle(title);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title+ url.substring(url.lastIndexOf(".")));
                manager.enqueue(request);
                Toast.makeText(QuestionPaperActivity.this,"Downloading " + title, Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void load_question_papers(int id) {

        AsyncTask<Integer,Void,Void> task =  new AsyncTask<Integer, Void, Void>() {

            @Override
            protected Void doInBackground(Integer... integers) {

                requestQueue = Volley.newRequestQueue(QuestionPaperActivity.this);
                Map<String, String> param = new HashMap<>();
                param.put("branch", QuestionChooserActivity.branch);
                param.put("semester", QuestionChooserActivity.semester);
                JsonObjectRequest object = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("pp",response.toString());
                            showProgress(false);
                            JSONArray papersArray = response.getJSONArray("papers");
                            Log.d("pp", ""+papersArray);
                            for (int i = 0; i < papersArray.length(); i++) {
                                JSONObject paperObject = papersArray.getJSONObject(i);
                                QuestionPaperData questionPaper = new QuestionPaperData(paperObject.getInt("id"),
                                        paperObject.getString("subject"), paperObject.getString("url"),
                                        paperObject.getString("contributor"));
                                data_list.add(questionPaper);
                            }
                            Log.d("pp", "This is data list item "+data_list.get(0).toString());
                        } catch (JSONException e) {
                            tvCheck.setVisibility(View.VISIBLE);
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
        task.execute(id);
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
