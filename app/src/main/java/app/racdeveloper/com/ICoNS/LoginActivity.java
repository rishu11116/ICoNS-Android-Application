package app.racdeveloper.com.ICoNS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * A login screen that offers login via collegeId/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG="LoginActivity";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private View mProgressView;
    private View mLoginFormView;
    private TextView signUp;
    static SQLiteDatabase db;
    List<String> listBook;
    private static String resultKey, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        try{
            SQLiteOpenHelper bencolDatabase= new BencolsDatabase(LoginActivity.this);
            db= bencolDatabase.getWritableDatabase();

            listBook = new ArrayList<String>();
            Cursor cursor;
            cursor= db.query("collegeId", new String[] {"_id", "CollegeId"}, null, null, null, null, "_id");
            boolean check=cursor.moveToFirst();     //if cursor has no value check is false
            do{
                if(check)
                    listBook.add(cursor.getString(1));
            }while(cursor.moveToNext());
        }
        catch (SQLException e) {
            Toast.makeText(LoginActivity.this, "Database Unavailable", Toast.LENGTH_SHORT).show();
        }

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        addEmailsToAutoComplete(listBook);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        signUp = (TextView) findViewById(R.id.tvSignUpForm);
        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Switch to Sign-Up SignUpActivity
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //showProgress(true);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            checkLogin(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() == 10;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return (password.length() > 5);
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_list_item_checked, emailAddressCollection);
        //simple_dropdown_item_1line   simple_list_item_checked
        mEmailView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==  KeyEvent.KEYCODE_BACK)
            showDialogOnExit();
        return super.onKeyDown(keyCode, event);
    }

    private void showDialogOnExit(){
        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Do you want to exit?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
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

    public void checkLogin(final String email,final String password){
        showProgress(true);
            // Simulate network access.

            SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
            String fcmToken = pref.getString("regId", null);

            final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            String url = Constants.URL+"login";
            Map<String, String> param = new HashMap<String, String>();
            param.put("rollno", email);
            param.put("password", password);
            param.put("fcmToken", "" + fcmToken);

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        showProgress(false);
                        Log.i(TAG, jsonObject.toString());
                        resultKey = jsonObject.getString("result");

                        Toast.makeText(LoginActivity.this, "Success " + resultKey, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, resultKey);

                        if (resultKey.equals("success")) {

                            token = jsonObject.getString("token");
                            //String table_name="collegeId", primary_key="_id", column_name="CollegeId";
                            //db.execSQL("SELECT * FROM "+table_name+" WHERE "+column_name+"="+mEmail+";");
                            ContentValues id= new ContentValues();
                            id.put("CollegeId", email);
                            db.insert("collegeId", null, id);
                            //db.execSQL("DELETE FROM "+table_name+" WHERE "+primary_key+" NOT IN(SELECT MIN("+primary_key+") FROM "+table_name+" GROUP BY "+column_name+");", null);
                            QueryPreferences.setToken(LoginActivity.this, token);

                            Toast.makeText(LoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                            startActivity(i);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Invalid login details", Toast.LENGTH_SHORT).show();
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mEmailView.setText("");
                            mPasswordView.setText("");
                            mPasswordView.requestFocus();
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

