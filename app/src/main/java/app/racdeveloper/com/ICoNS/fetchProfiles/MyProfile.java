package app.racdeveloper.com.ICoNS.fetchProfiles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.racdeveloper.com.ICoNS.Constants;
import app.racdeveloper.com.ICoNS.QueryPreferences;
import app.racdeveloper.com.ICoNS.R;
import app.racdeveloper.com.ICoNS.pushNotification.NotificationWebview;

/**
 * Created by Rachit on 11/21/2016.
 */
public class MyProfile extends AppCompatActivity {
    View mProgressView;
    private MyProfileData myProfileData;
    RequestQueue requestQueue;
    String URL = Constants.URL + "basics/user_details";
    String searchString, searchRollno, token;

    ImageView userImage, fbContactButton, GithubContactButton, LinkedinContactButton, update;
    TextView userName, userBio, userBranch, userBatch, userId, userCouncils, userSkills, userHobbies, userBlood, userAddress,
            userEmail, userContact;

    private boolean isProfileLoaded=false;          // to keep knowledge about whether profile is loaded or not.. so that UpdateProfileActivity can be populated with fetched data
    String[] mBranch;           // To fetch branch list from resources

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.my_profile_activity);

        mBranch= getResources().getStringArray(R.array.branch);
        mProgressView = findViewById(R.id.progressbar);
        showProgress(true);

        userImage = (ImageView) findViewById(R.id.user_profile_photo);
        update = (ImageView) findViewById(R.id.profile_update);

        userName = (TextView) findViewById(R.id.user_profile_name);
        userBio = (TextView) findViewById(R.id.user_profile_short_bio);
        userBranch = (TextView) findViewById(R.id.user_profile_branch);
        userBatch = (TextView) findViewById(R.id.user_profile_batch);
        userId = (TextView) findViewById(R.id.user_profile_id);
        userCouncils = (TextView) findViewById(R.id.user_profile_councils);
        userSkills = (TextView) findViewById(R.id.user_profile_skills);
        userHobbies = (TextView) findViewById(R.id.user_profile_hobbies);
        userBlood = (TextView) findViewById(R.id.user_profile_blood);
        userAddress = (TextView) findViewById(R.id.user_profile_address);
        userEmail = (TextView) findViewById(R.id.user_profile_email);
        userContact = (TextView) findViewById(R.id.user_profile_contact);

        fbContactButton = (ImageView) findViewById(R.id.fb_link_image);
        GithubContactButton = (ImageView) findViewById(R.id.github_link_image);
        LinkedinContactButton = (ImageView) findViewById(R.id.linkedin_link_image);

        Intent i = getIntent();
        boolean isUpdateRequired = (i.getStringExtra("userRollno")==null);
        if(!isUpdateRequired)
            isUpdateRequired = (i.getStringExtra("userRollno")).equals(QueryPreferences.getRollNo(this));

        if(!isUpdateRequired){
            update.setVisibility(View.GONE);
            URL = Constants.URL + "basics/get_profile";
            searchString= i.getStringExtra("userProfileRequest");
            searchRollno= i.getStringExtra("userRollno");
            Toast.makeText(MyProfile.this, searchString + searchRollno, Toast.LENGTH_SHORT).show();
        }

        token= QueryPreferences.getToken(MyProfile.this);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isProfileLoaded)
                    createAlertDialog();
                else {
                    Snackbar snack= Snackbar.make(mProgressView.getRootView(), "\t\t\tCheck your Connection", Snackbar.LENGTH_SHORT);

                    View sbView = snack.getView();
                    sbView.setBackgroundColor(Color.rgb(25,25,112));
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setTextSize(18);
                    snack.show();
                }
            }
        });
        load_Profile();
    }

    private void createAlertDialog() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(MyProfile.this);
        builder.setTitle("Select your Choice");

        LinearLayout layout = new LinearLayout(MyProfile.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView profilePic = new TextView(MyProfile.this);
        profilePic.setTextSize(20);
        profilePic.setText("\n\n\t\t\t\tUpdate your Profile Pic");
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i= new Intent(MyProfile.this, UpdateProfilePic.class);
                startActivity(i);
            }
        });
        layout.addView(profilePic);

        final TextView profileDetails = new TextView(MyProfile.this);
        profileDetails.setTextSize(20);
        profileDetails.setText("\n\t\t\t\tUpdate your Profile Details");
        profileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i= new Intent(MyProfile.this, UpdateProfileDetails.class);
                Bundle b= new Bundle();
                b.putSerializable("MyProfileData", myProfileData);
                i.putExtras(b);
                startActivity(i);
            }
        });
        layout.addView(profileDetails);

        builder.setView(layout);
        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void contactToWeb(String contactURL) {
        Intent intent = new Intent(this, NotificationWebview.class);
        intent.putExtra("uri", contactURL);
        startActivity(intent);
    }

    private void load_Profile() {

        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {

            @Override
            protected Void doInBackground(Integer... integers) {
                requestQueue = Volley.newRequestQueue(MyProfile.this);
                Map<String, String> param = new HashMap<>();

                if(searchString!=null) {             //   post rollno and token
                    param.put("string", searchString);
                    param.put("rollno", searchRollno);
                }

                param.put("token", token);

                JsonObjectRequest object = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param),
                        new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        showProgress(false);
                        try {
                            if(jsonObject!=null) {
                                isProfileLoaded = true;
                                Log.i("ppppp", jsonObject.toString());

                                JSONObject profileObject = jsonObject.getJSONObject("details");

                                Log.i("ppppp", profileObject.toString());
                                    myProfileData = new MyProfileData();
                                if (!profileObject.getString("name").equals(""))
                                    myProfileData.setProfileName(profileObject.getString("name"));
                                if (profileObject.getString("imageUrl")!=null)
                                    if (!profileObject.getString("imageUrl").equals(""))
                                        myProfileData.setProfileImageUrl(profileObject.getString("imageUrl"));
                                if (profileObject.getString("status")!=null)
                                    myProfileData.setProfileStatus(profileObject.getString("status"));
                                if (!profileObject.getString("branch").equals(""))
                                    myProfileData.setProfileBranch(mBranch[Integer.parseInt(profileObject.getString("branch"))]);
                                if (!profileObject.getString("batch").equals(""))
                                    myProfileData.setProfileBatch(profileObject.getString("batch"));
                                if (!profileObject.getString("rollno").equals(""))
                                    myProfileData.setProfileId(profileObject.getString("rollno"));
                                if (profileObject.getString("councils")!=null)
                                    myProfileData.setProfileCouncils(profileObject.getString("councils"));
                                if (profileObject.getString("skills")!=null)
                                    myProfileData.setProfileSkills(profileObject.getString("skills"));
                                if (profileObject.getString("hobbies")!=null)
                                    myProfileData.setProfileHobbies(profileObject.getString("hobbies"));
                                if (profileObject.getString("bloodGroup")!=null)
                                    myProfileData.setProfileBlood(profileObject.getString("bloodGroup"));
                                if (!profileObject.getString("email").equals(""))
                                    myProfileData.setProfileEmail(profileObject.getString("email"));
                                if (profileObject.getString("homeCity")!=null)
                                    myProfileData.setProfileAddress(profileObject.getString("homeCity"));
                                if (profileObject.getString("fbLink")!=null)
                                    if (!profileObject.getString("fbLink").equals(""))
                                        myProfileData.setProfileFb(profileObject.getString("fbLink"));
                                if (profileObject.getString("githubLink")!=null)
                                    if (!profileObject.getString("githubLink").equals(""))
                                        myProfileData.setProfileGithub(profileObject.getString("githubLink"));
                                if (profileObject.getString("linkedinLink")!=null)
                                    if (!profileObject.getString("linkedinLink").equals(""))
                                        myProfileData.setProfileLinkedin(profileObject.getString("linkedinLink"));
                                if (profileObject.getString("phone")!=null)
                                    myProfileData.setProfileContact(profileObject.getString("phone"));
                                populateMyProfile();

                            }
                            else{
                                Toast.makeText(MyProfile.this, "FAIL!!!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    private void populateMyProfile(){
        if(myProfileData.getProfileImageUrl()!=null)
            if(!myProfileData.getProfileImageUrl().equals(""))
                Glide.with(MyProfile.this).load(myProfileData.getProfileImageUrl()).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(userImage);

        userName.setText(myProfileData.getProfileName());
        userBio.setText(myProfileData.getProfileStatus());
        userBranch.append("\t\t\t"+myProfileData.getProfileBranch());
        userBatch.append("\t\t\t"+myProfileData.getProfileBatch());
        userId.append("\t\t\t"+myProfileData.getProfileId());
        userCouncils.append("\t\t\t"+myProfileData.getProfileCouncils());
        userSkills.append("\t\t\t"+myProfileData.getProfileSkills());
        userHobbies.append("\t\t\t"+myProfileData.getProfileHobbies());
        userBlood.append("\t\t\t"+myProfileData.getProfileBlood());
        userAddress.append("\t\t\t"+myProfileData.getProfileAddress());
        userEmail.append("\t\t\t"+myProfileData.getProfileEmail());
        userContact.append("\t\t\t"+myProfileData.getProfileContact());

        if (myProfileData.getProfileFb() != null) {
            if (!myProfileData.getProfileFb().regionMatches(0, "http://", 0, 7) && !myProfileData.getProfileFb().regionMatches(0, "https://", 0, 8))
                myProfileData.setProfileFb("http://"+ myProfileData.getProfileFb());
            fbContactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contactToWeb(myProfileData.getProfileFb());
                }
            });
        }
        if (myProfileData.getProfileGithub() != null) {
            if (!myProfileData.getProfileGithub().regionMatches(0, "http://", 0, 7) && !myProfileData.getProfileGithub().regionMatches(0, "https://", 0, 8))
                myProfileData.setProfileGithub("http://"+ myProfileData.getProfileGithub());
            GithubContactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contactToWeb(myProfileData.getProfileGithub());
                }
            });
        }
        if (myProfileData.getProfileLinkedin() != null) {
            if (!myProfileData.getProfileLinkedin().regionMatches(0, "http://", 0, 7) && !myProfileData.getProfileLinkedin().regionMatches(0, "https://", 0, 8))
                myProfileData.setProfileLinkedin("http://"+ myProfileData.getProfileLinkedin());
            LinkedinContactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contactToWeb(myProfileData.getProfileLinkedin());
                }
            });
        }
    }

    private void showProgress(final boolean show) {
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

