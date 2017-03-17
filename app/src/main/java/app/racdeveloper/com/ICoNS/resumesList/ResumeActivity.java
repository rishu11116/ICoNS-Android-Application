package app.racdeveloper.com.ICoNS.resumesList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import app.racdeveloper.com.ICoNS.Constants;
import app.racdeveloper.com.ICoNS.FilePath;
import app.racdeveloper.com.ICoNS.QueryPreferences;
import app.racdeveloper.com.ICoNS.R;

public class ResumeActivity extends AppCompatActivity {

    DownloadManager manager;
    private View mProgressView;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabResume;
    private ResumeViewAdapter adapter;
    private List<ResumeData> data_list;
    RequestQueue requestQueue;
    String URL = Constants.URL + "resumes/get";
    String URL_UPLOAD = Constants.URL + "resumes/upload";

    private Uri filePath;
    String[] mBranch;           // To fetch branch list from resources

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBranch= getResources().getStringArray(R.array.branchShort);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coLayout);
        fabResume = (FloatingActionButton) findViewById(R.id.fabResume);
        fabResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "     Upload your Resume", Snackbar.LENGTH_LONG)
                        .setAction("UPLOAD", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                i.setType("application/pdf");
                                i.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(Intent.createChooser(i, "Select your Resume PDF"), 1);
                            }
                        });

                // Changing message text color
                snackbar.setActionTextColor(Color.WHITE);

                // Changing action button text color
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(Color.rgb(25,25,112));
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(18);
                snackbar.show();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mProgressView = findViewById(R.id.resume_progress);
        data_list = new ArrayList<>();
        showProgress(true);
        load_resume(0);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new ResumeViewAdapter(this, data_list);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK  && data!=null){
             filePath = data.getData();

            Snackbar snack= Snackbar.make(coordinatorLayout, ""+ filePath.toString(), Snackbar.LENGTH_INDEFINITE)
                    .setAction("SEND", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uploadMultipart();
                        }
                    });
            snack.setActionTextColor(Color.WHITE);

            View sbView = snack.getView();
            sbView.setBackgroundColor(Color.rgb(25,25,112));
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(16);
            snack.show();
        }
    }

    public void uploadMultipart() {
        //getting name for the image

        //getting the actual path of the image
        String path = FilePath.getPath(this, filePath);
        Toast.makeText(this, ""+ path, Toast.LENGTH_LONG).show();

        if (path == null) {
            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, URL_UPLOAD)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("token", QueryPreferences.getToken(this))
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void download(final View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to download this resume ?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                int itemPosition= recyclerView.getChildAdapterPosition(view);
                String url = data_list.get(itemPosition).getResumeUrl();
                String title = data_list.get(itemPosition).getResumeName();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setTitle("Downloading Resume of " + title);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Resume of " + title + url.substring(url.lastIndexOf(".")));
                manager.enqueue(request);
                Toast.makeText(ResumeActivity.this,"Downloading Resume of " + title, Toast.LENGTH_SHORT).show();
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

    private void load_resume(int id) {
        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {

            @Override
            protected Void doInBackground(Integer... integers) {

                requestQueue = Volley.newRequestQueue(ResumeActivity.this);
                Map<String, String> param = new HashMap<>();
                //param.put("token", PreferenceManager.getDefaultSharedPreferences(ResumeActivity.this).getString("token", null));
                JsonObjectRequest object = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("pppp","inOn response");
                            showProgress(false);
                            JSONArray resumeArray = response.getJSONArray("resume");
                            for (int i = 0; i < resumeArray.length(); i++) {
                                JSONObject resumeObject = resumeArray.getJSONObject(i);
                                ResumeData resume = new ResumeData(resumeObject.getInt("id"), resumeObject.getString("name"),
                                        mBranch[Integer.parseInt(resumeObject.getString("branch"))], resumeObject.getString("batch"),
                                        resumeObject.getString("url"));
                                data_list.add(resume);
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
                });
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
