package app.racdeveloper.com.ICoNS.fetchProfiles;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import app.racdeveloper.com.ICoNS.Constants;
import app.racdeveloper.com.ICoNS.ProfileActivity;
import app.racdeveloper.com.ICoNS.QueryPreferences;
import app.racdeveloper.com.ICoNS.R;

/**
 * Created by Rachit on 11/22/2016.
 */
public class UpdateProfilePic extends AppCompatActivity{
    private static String URL= Constants.URL + "basics/update_profile";
    private static String resultKey = null;

    CropImageView cropProfile;
    Button bProfileUpload, bCrop, bSendProfilePic;
    Bitmap bitmap;
    boolean isImageSet= false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_pic_activity);

        cropProfile= (CropImageView) findViewById(R.id.CropProfileImage);

        bProfileUpload= (Button) findViewById(R.id.bProfileUpload);
        bProfileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select Profile Pic"), 1);
            }
        });
        bCrop= (Button) findViewById(R.id.bCrop);
        bCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap= cropProfile.getCroppedImage();
                bitmap = getResizedBitmap(bitmap, 720);
                cropProfile.setImageBitmap(bitmap);
                bCrop.setVisibility(View.GONE);
            }
        });
        bSendProfilePic= (Button) findViewById(R.id.bSendProfilePic);
        bSendProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isImageSet){
                    String profilePicString= getStringImage(bitmap);
                    setProfile(profilePicString);
                    Toast.makeText(UpdateProfilePic.this, "Send is clicked...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && data!=null){
            isImageSet=true;
            bCrop.setVisibility(View.VISIBLE);
            Uri uri= data.getData();
            try{
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = getResizedBitmap(bitmap, 720);
                cropProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);          //compress image to 50% quality
        byte[] imageBytes= baos.toByteArray();
        String encodedImage= Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void setProfile(String profilePicString){

        final RequestQueue requestQueue= Volley.newRequestQueue(UpdateProfilePic.this);

        Map<String, String> param= new HashMap<>();
        param.put("token", QueryPreferences.getToken(UpdateProfilePic.this));
        param.put("rollno", QueryPreferences.getRollNo(this));
        param.put("image", profilePicString);

        JsonObjectRequest jor= new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                if(jsonObject!=null) {
                    try {
                        resultKey = jsonObject.getString("result");

                        if(resultKey.equals("success")){
                            Toast.makeText(UpdateProfilePic.this, "Profile Pic is successfully set!!!", Toast.LENGTH_SHORT).show();
                            alertToUpdateDetails();
                            //finish();
                        }
                        else {
                            Toast.makeText(UpdateProfilePic.this, "Submit Again!", Toast.LENGTH_SHORT).show();
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

    private void alertToUpdateDetails(){
        final AlertDialog.Builder builder= new AlertDialog.Builder(UpdateProfilePic.this);
        builder.setTitle("Your profile pic is successfully set");

        LinearLayout layout = new LinearLayout(UpdateProfilePic.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView textView= new TextView(UpdateProfilePic.this);
        textView.setText("\n\t\t\tPlease update your details as well...");
        textView.setTextSize(18);

        layout.addView(textView);
        builder.setView(layout);
        builder.setPositiveButton("UPDATE DETAILS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                Intent i = new Intent(UpdateProfilePic.this, UpdateProfileDetails.class);
                startActivity(i);
            }
        });

        builder.setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
//                finishAffinity();
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        builder.show();
    }
}
