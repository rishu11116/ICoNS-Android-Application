package app.racdeveloper.com.ICoNS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

/**
 * Created by user on 09-08-2016.
 */
public class SignUpActivity extends AppCompatActivity {

    private static String URL = Constants.URL + "register";

    private EditText rollno, name, email, password, confirmpass;
    private Button signup;
    private View mProgressView;

    //variables used in spinner
    private boolean isSpinnerInitial = true;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    public static String branch = null, batch = null;
    int branchPosition=0, batchPosition=0;
    //spinner ends

//    RelativeLayout imageUploadLayout;
    LinearLayout formLayout;
//    boolean isImageSet= false;
//    ImageView imageIdUpload;
//    Bitmap bitmap=null;
    private String error, result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        formLayout = (LinearLayout) findViewById(R.id.email_login_form);

        rollno = (EditText) findViewById(R.id.etUserId);
        rollno.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    name.requestFocus();
                    return true;
                }
                return false;
            }
        });

//        imageIdUpload= (ImageView) findViewById(R.id.imagePreview);
//        imageUploadLayout= (RelativeLayout) findViewById(R.id.imageLayout);
//        imageUploadLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent();
//                i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (i.resolveActivity(getPackageManager())!=null)
//                    startActivityForResult(i, 1);
//                i.setType("image/*");
//                i.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(i, "Select a Picture from"), 1);
//            }
//        });

        name = (EditText) findViewById(R.id.etName);
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    email.requestFocus();
                    return true;
                }
                return false;
            }
        });
        email= (EditText) findViewById(R.id.etEmail);
        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    //Close the soft keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        password = (EditText) findViewById(R.id.etPassword);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    confirmpass.requestFocus();
                    return true;
                }
                return false;
            }
        });
        confirmpass = (EditText) findViewById(R.id.etConfirmPassword);
        confirmpass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        mProgressView= findViewById(R.id.progress_register);

        signup = (Button) findViewById(R.id.bSignUp);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

        spinner = (Spinner) findViewById(R.id.spinnerbranch);
        adapter = ArrayAdapter.createFromResource(this, R.array.branch, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                branchPosition = position;

                if (isSpinnerInitial) {
                    branch = String.valueOf(position);
                    isSpinnerInitial = false;
                } else {
                    if
                            (position == 0) ;
                    else {
                        branch = String.valueOf(position);
                        Toast.makeText(getBaseContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner = (Spinner) findViewById(R.id.spinnerbatch);
        adapter = ArrayAdapter.createFromResource(this, R.array.batch, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                batchPosition = position;

                if (isSpinnerInitial) {
                    batch = String.valueOf(2011+position);
                    isSpinnerInitial = false;
                } else {
                    if
                            (position == 0) ;
                    else {
                        batch = String.valueOf(2011+position);
                        Toast.makeText(getBaseContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==1 && data!=null){
//            isImageSet=true;
//
//            Uri uri = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                bitmap = getResizedBitmap(bitmap, 720);
//                imageIdUpload.setImageBitmap(getResizedBitmap(bitmap, 30));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        float bitmapRatio = (float) width / (float) height;
//        if (bitmapRatio > 1) {
//            width = maxSize;
//            height = (int) (width / bitmapRatio);
//        } else {
//            height = maxSize;
//            width = (int) (height * bitmapRatio);
//        }
//
//        return Bitmap.createScaledBitmap(image, width, height, true);
//    }
//    private String getStringImage(Bitmap bitmap){
//        ByteArrayOutputStream baos= new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);          //compress image of 40% quality
//        byte[] imageBytes= baos.toByteArray();
//        String encodedImage= Base64.encodeToString(imageBytes, Base64.DEFAULT);
//        return encodedImage;
//    }

    private void attemptSignUp() {
        boolean cancel=false;
        View focusView = null;
        String user = rollno.getText().toString();
        String username = name.getText().toString();
        String emailId= email.getText().toString();
        String pass = password.getText().toString();
        String confirmPassword = confirmpass.getText().toString();

        // Check for valid user Id
        if (TextUtils.isEmpty(user) || user.length() != 10) {
            cancel=true;
            rollno.setError("Invalid username");
            focusView = rollno;
        }

        //Check for name
        else if(TextUtils.isEmpty(username)){
            cancel=true;
            name.setError("Enter your Name");
            focusView = name;
        }

        //email checker
        else if(TextUtils.isEmpty(emailId)){
            cancel=true;
            email.setError("Enter your Email");
            focusView = email;
        }

        //Match password checker
        else if(!pass.equals(confirmPassword) || pass.length()<6){
            cancel=true;
            password.setError("Password Mismatch");
            focusView=password;
        }

        //branch and batch spinner check
        else if(branchPosition <= 0 && batchPosition <= 0){
            cancel=true;
            focusView=null;
            Toast.makeText(SignUpActivity.this, "Branch or Batch is not selected.", Toast.LENGTH_SHORT).show();
        }

//        //image uploaded checker
//        else if(bitmap==null){
//            cancel=true;
//            focusView=null;
//            Toast.makeText(SignUpActivity.this, "Upload your id proof", Toast.LENGTH_SHORT).show();
//        }

        if(cancel){
            if (focusView!=null)
                focusView.requestFocus();
        }
        else {
            //Toast.makeText(SignUpActivity.this, "Everything alright!!", Toast.LENGTH_SHORT).show();
            checkRegister(user, username, pass, emailId);
        }
    }


    public void checkRegister(String mRollno, String mName, String mPassword, String mEmail){

        showProgress(true);
        // Simulate network access.
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        String fcmToken = pref.getString("regId", null);

        final RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);

        Map<String, String> param = new HashMap<String, String>();
        param.put("rollno", mRollno);
        param.put("name", mName);
        param.put("password", mPassword);
        param.put("email", mEmail);
        param.put("branch", branch);
        param.put("batch", batch);
//        param.put("image", imageString);
        param.put("fcmToken", fcmToken);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    showProgress(false);
                    result= jsonObject.getString("result");

                    if(result.equals("fail")) {
                        error = jsonObject.getString("error");
                        Toast.makeText(SignUpActivity.this, "fail and " + error, Toast.LENGTH_SHORT).show();
                        //mProgressBar.setVisibility(View.GONE);
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "Your request is successfully submitted!! You will be notified on confirmation", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                    finish();
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

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            formLayout.setVisibility(View.GONE);
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
            formLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==  KeyEvent.KEYCODE_BACK) {
            //showDialogOnExit();
            finish();
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(i);
        }
        //showDialogOnExit();
        return super.onKeyDown(keyCode, event);
    }

    private void showDialogOnExit(){
        final AlertDialog.Builder builder= new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
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
}
