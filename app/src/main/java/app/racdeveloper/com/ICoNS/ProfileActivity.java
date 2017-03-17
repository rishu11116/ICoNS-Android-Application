package app.racdeveloper.com.ICoNS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.racdeveloper.com.ICoNS.aboutUs.DeveloperActivity;
import app.racdeveloper.com.ICoNS.appStartup.SplashActivity;
import app.racdeveloper.com.ICoNS.fetchProfiles.MyProfile;
import app.racdeveloper.com.ICoNS.fetchProfiles.UpdateProfilePic;
import app.racdeveloper.com.ICoNS.newsFeed.FeedItem;
import app.racdeveloper.com.ICoNS.newsFeed.NewsFeedController;
import app.racdeveloper.com.ICoNS.newsFeed.NewsFeedListAdapter;
import app.racdeveloper.com.ICoNS.pushNotification.NotificationList;
import app.racdeveloper.com.ICoNS.pushNotification.NotificationWebview;
import app.racdeveloper.com.ICoNS.questionPapers.QuestionChooserActivity;
import app.racdeveloper.com.ICoNS.resumesList.ResumeActivity;

/**
 * Created by user on 09-08-2016.
 */
public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    View searchLayout, newsFeedLayout;
    private SwipeRefreshLayout swipe;
    private ListView listView, searchList;
    private List<String> userNameList, userRollList;
    private NewsFeedListAdapter listAdapter;
    private static List<FeedItem> feedItems;
    private String URL_FEED = Constants.URL + "newsFeeds/get";
    private String resultViewerUrl = "http://results.odigos.in";
    TextView nameNav, emailNav; // Username and email on navigation bar
    ImageView profileNav;

    private View mProgressNewsFeedView;
    DrawerLayout drawerLayout;
    FloatingActionButton fabForMailToAdmin;

    private static int newestFeedID;      //keep record of newest updated record of newsFeed
    boolean isRefresh = false;
    private static int preLast = 0;  // keep count of last item of newsFeed
    private static int lastFeedID;       // keep timestamp of last item of newsFeed
    boolean isScroll = false;
    String[] mBranch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBranch = getResources().getStringArray(R.array.branchShort);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ProfileActivity.this, drawerLayout, toolbar, 0, 0);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        searchLayout= findViewById(R.id.searchLayout);
        searchList= (ListView) searchLayout.findViewById(R.id.lvSearchList);
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ProfileActivity.this, MyProfile.class);
                intent.putExtra("userProfileRequest", userNameList.get(i));
                intent.putExtra("userRollno", userRollList.get(i));
                startActivity(intent);
            }
        });

        newsFeedLayout= findViewById(R.id.container);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);        //Gives access to navigation bar header
        nameNav = (TextView) header.findViewById(R.id.tvUserName);       //part of navigation bar header layout
        emailNav = (TextView) header.findViewById(R.id.tvEmail);         //part of navigation bar header layout
        profileNav = (ImageView) header.findViewById(R.id.imageView);    //part of navigation bar header layout

        fabForMailToAdmin = (FloatingActionButton) findViewById(R.id.fabButton);
        fabForMailToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MailToAdminActivity.class));
            }
        });
        mProgressNewsFeedView = findViewById(R.id.news_feed_progress_bar);
        showProgress(true);

        listView = (ListView) findViewById(R.id.list);
        feedItems = new ArrayList<>();

//        new FetchNewsFeed().execute();

        listAdapter = new NewsFeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(ProfileActivity.this, "App is Refreshed!!!", Toast.LENGTH_SHORT).show();
                fetchUserDetails();                    // to update navigation bar if it is not loaded

                //updateData();
                isRefresh = false;
                new FetchNewsFeed().execute();
                swipe.setRefreshing(false);
            }
        });
        //ends

        new FetchNewsFeed().execute();

        final SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id: " + regId);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if (lastItem == totalItemCount) {
                    if (preLast != lastItem) {
                        preLast = lastItem;
                        isScroll = true;
                        Log.d("debugt", "listView On Scroll is called");

                        // Fetch next few records on reaching last item
                        new FetchNewsFeed().execute();
                    }
                }
            }
        });

        fetchUserDetails();
    }

    public class FetchNewsFeed extends AsyncTask<Void, Void, List<FeedItem>> {

        @Override
        protected List<FeedItem> doInBackground(Void... params) {

            try {
                Cache cache = NewsFeedController.getInstance().getRequestQueue().getCache();
                Cache.Entry entry = cache.get(URL_FEED);

                if (entry != null) {
                    //fetch data from cache
                    try {
                        String data = new String(entry.data, "UTF-8");
                        try {
                            parseJsonFeed(new JSONObject(data));
                            Log.i("ppp", "In Cache");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    //make fresh volley request and getting json
                    Map<String, String> param = new HashMap<>();
                    if (isRefresh && newestFeedID != 0)
                        param.put("newestFeedID", String.valueOf(newestFeedID));
                    if (isScroll && lastFeedID != 0)
                        param.put("lastFeedID", String.valueOf(lastFeedID));

                    param.put("token", QueryPreferences.getToken(ProfileActivity.this));
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL_FEED, new JSONObject(param), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.i(TAG, " Json data : " + jsonObject.toString());
                            VolleyLog.d(TAG, "Response: " + jsonObject.toString());
                            if (jsonObject != null) {
                                String result = null;
                                try {
                                    result = jsonObject.getString("result");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (result.equals("success"))
                                    parseJsonFeed(jsonObject);
                                else
                                    Toast.makeText(ProfileActivity.this, "" + result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
                            Toast.makeText(ProfileActivity.this, "No Network Available", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Adding request to volley request queue
                    NewsFeedController.getInstance().addToRequestQueue(req);
                }

            } catch (Exception e) {
                return null;
            }
            return feedItems;
        }

        @Override
        protected void onPostExecute(List<FeedItem> feedItems) {
            showProgress(false);
            //listAdapter = new NewsFeedListAdapter(ProfileActivity.this, feedItems);
            //listView.setAdapter(listAdapter);
            //listAdapter.notifyDataSetChanged();

            //setAdapter();
        }
    }

    private void setAdapter() {
        if (isRefresh) {
            isRefresh = false;
            // add item at the top
            Log.d("debugt", "new items are refreshed");
            listAdapter.notifyDataSetChanged();
        } else if (isScroll) {
            isScroll = false;
            // add item at the bottom
            Log.d("debugt", "more items are loaded");
            listAdapter.notifyDataSetChanged();
        } else {
            Log.d("debugt", " list is created");
            listAdapter = new NewsFeedListAdapter(this, feedItems);
            listView.setAdapter(listAdapter);
        }
    }

    private void parseJsonFeed(JSONObject jsonObject) {

        List<FeedItem> list = new ArrayList<>();
        try {
            JSONArray feedArray = jsonObject.getJSONArray("feed");
            int length = feedArray.length();
//            if(isRefresh)
//                length -= 1;
            if (!isScroll)
                newestFeedID = ((JSONObject) feedArray.get(0)).getInt("id");
            if (!isRefresh)
                lastFeedID = ((JSONObject) feedArray.get(length - 1)).getInt("id");

            for (int i = 0; i < length; i++) {
//                JSONObject feedObj = (JSONObject) feedArray.get(i);
                JSONObject feedObj = feedArray.getJSONObject(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));

                Log.i("ppp", String.valueOf(item.getId()));
                item.setAuthor(feedObj.getString("author"));
                item.setAuthorRollno(feedObj.getString("authorRollno"));
                item.setContent(feedObj.getString("content"));
                if (!feedObj.getString("authorImage").equals(""))
                    item.setProfilePic(feedObj.getString("authorImage"));
                item.setTimeStamp(feedObj.getString("timestamp"));
                item.setCommentCount(Integer.parseInt(feedObj.getString("commentCount")));
                if(!feedObj.getString("url").equals(""))
                    item.setUrl(feedObj.getString("url"));
//                String image = feedObj.isNull("image") ? null : feedObj.getString("image");
                String image = feedObj.getString("image");
                if (!image.equals("null"))
                    item.setImage(image);
                else item.setImage(null);
                //feedItems.add(item);
                list.add(item);
            }
            if (isRefresh) {              //isRefresh is true
                feedItems.addAll(0, list);
            } else if (isScroll) {
                feedItems.addAll(list);
            } else {
                feedItems = list;
            }
            //listAdapter.notifyDataSetChanged();
            setAdapter();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //ends

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_profile_share_drawer, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("pppp", "Query submitted");
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newTextQuery) {
                Log.d("pppp", "Query text changed");
                if(!newTextQuery.equals("")) {
                    newsFeedLayout.setVisibility(View.INVISIBLE);
                    searchLayout.setVisibility(View.VISIBLE);
                    setSearchViewList(newTextQuery);
                }else{
                    newsFeedLayout.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setQueryHint("Search any profile...");
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(i);
        } else if (id==R.id.shareMenu) {
            View screen = getWindow().getDecorView().getRootView();
            screen.setDrawingCacheEnabled(true);
            Bitmap screenShot = screen.getDrawingCache();
            String filePath = Environment.getExternalStorageDirectory()
                    + File.separator + "Pictures/Screenshot.png";
            File imagePath = new File(filePath);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(imagePath);
                screenShot.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e("GREC", e.getMessage(), e);
            } catch (IOException e) {
                Log.e("GREC", e.getMessage(), e);
            }
            Uri myUri = Uri.parse("file://" + filePath);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            // shareIntent.addCategory(Intent.CATEGORY_HOME);
            shareIntent.putExtra(Intent.EXTRA_STREAM,myUri);
            PackageManager packageManager=getPackageManager();
            if(packageManager.resolveActivity(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)==null) {
                final AlertDialog alertDialog=new AlertDialog.Builder(ProfileActivity.this).create();
                alertDialog.setTitle("Warning");
                alertDialog.setMessage("Sorry,you don't have any apps in your device which will be able to handle such operation");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            else {
                startActivity(Intent.createChooser(shareIntent, "Share screenshot using"));
            }
        }
        else if (id == R.id.action_logout){
            QueryPreferences.setToken(ProfileActivity.this, null);
            Intent i = new Intent(ProfileActivity.this, SplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSearchViewList(String searchQuery){

        final String[] resultKey = new String[1];
        final RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        String url = Constants.URL + "basics/search";
        Map<String, String> param = new HashMap<String, String>();
        param.put("token", QueryPreferences.getToken(this));
        param.put("string", searchQuery);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    showProgress(false);
                    Log.i(TAG, jsonObject.toString());
                    resultKey[0] = jsonObject.getString("result");
                    Log.i(TAG, resultKey[0]);

                    if (resultKey[0].equals("success")) {
                        userNameList = new ArrayList<>();
                        userRollList = new ArrayList<>();
                        JSONArray recordList= jsonObject.getJSONArray("records");
                        int length= recordList.length();
                        for (int i=0;i<length;i++) {
                            JSONObject userFound= recordList.getJSONObject(i);
                            String entity = userFound.getString("name")+ ", " +mBranch[Integer.parseInt(userFound.getString("branch"))]+ " ( " +userFound.getString("batch") +" )";
                            userNameList.add(entity);
                            userRollList.add(userFound.getString("rollno"));
                        }
                        ArrayAdapter<String> adapter= new ArrayAdapter<>(ProfileActivity.this,
                                android.R.layout.simple_list_item_1, userNameList);
                        searchList.setAdapter(adapter);
                    }
                    else {
                        Toast.makeText(ProfileActivity.this, "No Record Found", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();

        if (id == R.id.nav_add_post) {
            Intent i = new Intent(ProfileActivity.this, PostMsgActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_question_paper) {
            Intent i = new Intent(ProfileActivity.this, QuestionChooserActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_resumes) {
            Intent i = new Intent(ProfileActivity.this, ResumeActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            QueryPreferences.setToken(ProfileActivity.this, null);
            Intent i = new Intent(ProfileActivity.this, SplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else if (id == R.id.nav_aboutUs) {
            Intent i = new Intent(ProfileActivity.this, DeveloperActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_profile) {
            Intent i = new Intent(ProfileActivity.this, MyProfile.class);
            startActivity(i);
        } else if (id == R.id.nav_notification) {
            Intent i = new Intent(ProfileActivity.this, NotificationList.class);
            startActivity(i);
        } else if (id == R.id.nav_resultViewer) {
            startActivity(new Intent(ProfileActivity.this, NotificationWebview.class).putExtra("uri", resultViewerUrl));
        }
        return true;
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

            mProgressNewsFeedView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressNewsFeedView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressNewsFeedView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressNewsFeedView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        newestFeedID = 0;
        lastFeedID = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchNewsFeed().execute();
    }

    @Override
    public void onBackPressed() {
        // Prompt to close the app
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            showDialogOnExit();
    }

    private void showDialogOnExit() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to exit?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
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

    public void fetchUserDetails() {
        final String[] name = new String[1];
        final String[] email = new String[1];
        final String[] imageUrl = {new String()};
        AsyncTask<Void, Void, Void> userDetailsTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String URL = Constants.URL + "basics/user_details";
                String mToken = QueryPreferences.getToken(ProfileActivity.this);
                RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
                Map<String, String> params = new HashMap<>();
                params.put("token", mToken);

                JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (jsonObject != null) {
                            Log.i("ppppp", jsonObject.toString());
                            try {
                                Log.d("pppp", "Details are updating");
                                JSONObject details = jsonObject.getJSONObject("details");
                                name[0] = details.getString("name");
                                email[0] = details.getString("email");
                                imageUrl[0] = details.getString("imageUrl");
                                if (email[0].length()>16){
                                    email[0] = email[0].substring(0,20)+"...";
                                }
                                if(details.getString("rollno")!=null)
                                    QueryPreferences.setRollNo(ProfileActivity.this, details.getString("rollno"));
                                Log.d("pppp", "" + name[0] + " " + email[0]);
                                nameNav.setText(name[0]);
                                emailNav.setText(email[0]);
                                if (imageUrl[0].equals("")) {
                                    Intent i = new Intent(ProfileActivity.this, UpdateProfilePic.class);
                                    startActivity(i);
                                } else {
                                    Glide.with(ProfileActivity.this)
                                            .load(imageUrl[0])
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true).into(profileNav);
//                                    Picasso.with(profileNav.getContext()).load(imageUrl[0]).into(profileNav);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error in updating details : " + e);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        VolleyLog.e("Error : " + volleyError);
                    }
                });
                requestQueue.add(jor);
                return null;
            }
        };
        userDetailsTask.execute();
    }
}
