package app.racdeveloper.com.ICoNS.fetchProfiles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.racdeveloper.com.ICoNS.Constants;
import app.racdeveloper.com.ICoNS.QueryPreferences;
import app.racdeveloper.com.ICoNS.R;

/**
 * Created by Rachit on 11/23/2016.
 */
public class UpdateProfileDetails extends AppCompatActivity{
    private static String URL = Constants.URL + "basics/update_profile";
    MyProfileData dataToPopulate;           // to populate editText fields
    private static String resultKey = null;
    private View mProgressView;
    LinearLayout updateDetailLayout;
    EditText etName, etStatus, etContact, etEmail, etCouncil, etSkills, etHobbies, etBloodGroup, etAddress, etFacebookLink, etGitLink, etLinkedInLink;
    TextView tvSubmit;
    String name, contact, email, status, council, skills, hobbies, bloodGroup, address, facebookLink, githubLink, linkedinLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_details_activity);

        Bundle b= this.getIntent().getExtras();
        if (b!= null)
            dataToPopulate = (MyProfileData) b.getSerializable("MyProfileData");

        mProgressView= findViewById(R.id.progress_update);
        updateDetailLayout = (LinearLayout) findViewById(R.id.updateDetailsLayout);

        etName = (EditText) findViewById(R.id.etName_Details);
        etStatus = (EditText) findViewById(R.id.etStatus);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etContact = (EditText) findViewById(R.id.etContact_Details);
        etCouncil = (EditText) findViewById(R.id.etCouncils_Details);
        etSkills = (EditText) findViewById(R.id.etSkills_Details);
        etHobbies = (EditText) findViewById(R.id.etHobbies_Details);
        etBloodGroup = (EditText) findViewById(R.id.etBloodGroup_Details);
        etAddress = (EditText) findViewById(R.id.etAddress_Details);
        etFacebookLink = (EditText) findViewById(R.id.etFacebookLink_Details);
        etGitLink = (EditText) findViewById(R.id.etGitLink_Details);
        etLinkedInLink = (EditText) findViewById(R.id.etLinkedInLink_Details);

        if(b!=null) {
            etName.setText(dataToPopulate.getProfileName());
            etStatus.setText(dataToPopulate.getProfileStatus());
            etEmail.setText(dataToPopulate.getProfileEmail());
            etContact.setText(dataToPopulate.getProfileContact());
            etCouncil.setText(dataToPopulate.getProfileCouncils());
            etSkills.setText(dataToPopulate.getProfileSkills());
            etHobbies.setText(dataToPopulate.getProfileHobbies());
            etBloodGroup.setText(dataToPopulate.getProfileBlood());
            etAddress.setText(dataToPopulate.getProfileAddress());
            etFacebookLink.setText(dataToPopulate.getProfileFb());
            etGitLink.setText(dataToPopulate.getProfileGithub());
            etLinkedInLink.setText(dataToPopulate.getProfileLinkedin());
        }

        tvSubmit = (TextView) findViewById(R.id.tvSubmit_Details);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = etName.getText().toString();
                contact = etContact.getText().toString();
                status = etStatus.getText().toString().trim();
                email = etEmail.getText().toString();
                council = etCouncil.getText().toString();
                skills = etSkills.getText().toString();
                hobbies = etHobbies.getText().toString();
                bloodGroup = etBloodGroup.getText().toString();
                address = etAddress.getText().toString();
                facebookLink = etFacebookLink.getText().toString();
                githubLink = etGitLink.getText().toString();
                linkedinLink = etLinkedInLink.getText().toString();

                checkSubmitDetails();
                Toast.makeText(UpdateProfileDetails.this, "Submit is Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            updateDetailLayout.setVisibility(View.GONE);
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
            updateDetailLayout.setVisibility(View.GONE);
        }
    }

    private void checkSubmitDetails() {
        View focusView = null;
        boolean setProfileCheck=true;
        if(contact.equals("")){
            etContact.setError("Field Required");
            focusView = etContact;
            setProfileCheck=false;
        }
        else if(status.equals("")){
            etStatus.setError("Field Required");
            focusView = etStatus;
            setProfileCheck=false;
        }
        else if(skills.equals("")){
            etSkills.setError("Skills Required");
            focusView= etSkills;
            setProfileCheck=false;
        }
        else if (bloodGroup.equals("")){
            etBloodGroup.setError("Field Required");
            focusView= etBloodGroup;
            setProfileCheck=false;
        }
        else if(address.equals("")){
            etAddress.setError("Field Required");
            focusView= etAddress;
            setProfileCheck=false;
        }
        if(setProfileCheck){
            showProgress(true);
            setProfile();
        }else{
            focusView.requestFocus();
        }
    }

    private void setProfile(){

        final RequestQueue requestQueue= Volley.newRequestQueue(UpdateProfileDetails.this);
        Map<String, String> param= new HashMap<>();
        param.put("token", QueryPreferences.getToken(UpdateProfileDetails.this));
        param.put("name", name);
        param.put("phone", contact);
        param.put("email", email);
        param.put("status", status);
        param.put("councils", council);
        param.put("skills", skills);
        param.put("hobbies", hobbies);
        param.put("bloodGroup", bloodGroup);
        param.put("homeCity", address);
        param.put("fbLink", facebookLink);
        param.put("githubLink", githubLink);
        param.put("linkedinLink", linkedinLink);


        JsonObjectRequest jor= new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(jsonObject!=null) {
                    try {
                        resultKey = jsonObject.getString("result");

                        if(resultKey.equals("success")){
                            showProgress(false);
                            Toast.makeText(UpdateProfileDetails.this, "Profile Details are successfully updated!!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(UpdateProfileDetails.this, "Submit Again!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ppp", "Volley Error : "+volleyError);
            }
        });
        requestQueue.add(jor);
    }
}
