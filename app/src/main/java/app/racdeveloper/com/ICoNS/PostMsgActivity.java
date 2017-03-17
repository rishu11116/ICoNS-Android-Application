package app.racdeveloper.com.ICoNS;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

/**
 * Created by Rachit on 9/2/2016.
 */
public class PostMsgActivity extends AppCompatActivity {

    private static final String TAG="PostMsgActivity";
    private static final String URL= Constants.URL+"newsFeeds/post";
    boolean isImageSet= false;
    boolean isSendClicked= false;
    Intent shareIntent;

    EditText dataForPost, dataForUrl;
    ImageView imageForPost;
    CropImageView cropImageView;
    Button uploadImage, send, cropButton;
    Bitmap bitmap;
    private static String resultKey = null;
    private String mDataUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_msg);

        dataForPost= (EditText) findViewById(R.id.etPost);
        dataForUrl= (EditText) findViewById(R.id.etUrl);
        imageForPost= (ImageView) findViewById(R.id.ivPost);

        uploadImage= (Button) findViewById(R.id.bUpload);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select a Picture from"), 1);
            }
        });

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropButton = (Button) findViewById(R.id.Button_crop);

        shareIntent = getIntent();
        if(shareIntent!=null) {
            String text = shareIntent.getStringExtra("Text");
            if(text!=null){
                dataForPost.setText(text);
            }
            Uri mUri= shareIntent.getParcelableExtra("ImageUri");
            if(mUri!=null){
                isImageSet=true;
                imageForPost.setVisibility(View.GONE);
                cropImageView.setVisibility(View.VISIBLE);
                cropButton.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUri);
                    bitmap = getResizedBitmap(bitmap, 720);
                    cropImageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // Initialize the Crop button.
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = cropImageView.getCroppedImage();
                bitmap = getResizedBitmap(bitmap, 720);
                cropImageView.setVisibility(View.GONE);
                imageForPost.setVisibility(View.VISIBLE);
                imageForPost.setImageBitmap(bitmap);
                cropButton.setVisibility(View.GONE);
            }
        });

        send= (Button) findViewById(R.id.bSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataUrl = dataForUrl.getText().toString().trim();
                if (mDataUrl.length()<256) {
                    if (!isSendClicked) {
                        isSendClicked = true;
                        String data = dataForPost.getText().toString();
                        if (isImageSet) {
                            String imageString = getStringImage(bitmap);
                            postMessage(data, imageString);
                        }
                        else if (!data.equals("") || !mDataUrl.equals("")) {
                            postMessage(data, null);
                        }
                        else{
                            Toast.makeText(PostMsgActivity.this, "All Fields empty!", Toast.LENGTH_SHORT).show();
                            isSendClicked = false;
                        }
                    }
                }
                else{
                    Toast.makeText(PostMsgActivity.this, "Url should be less than 255 characters!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && data!=null){
            isImageSet=true;
            imageForPost.setVisibility(View.GONE);
            cropImageView.setVisibility(View.VISIBLE);
            cropButton.setVisibility(View.VISIBLE);
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = getResizedBitmap(bitmap, 720);
                cropImageView.setImageBitmap(bitmap);
                //imageForPost.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));

                //Picasso.with(getApplicationContext()).load(uri).resize(640,640).into(imageForPost);
                //Glide.with(getApplicationContext()).load(uri).override(640,640).into(imageForPost);
                //bitmap = getResizedBitmap(bitmap, 720);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void postMessage(String mText, String mImage){

        final RequestQueue requestQueue= Volley.newRequestQueue(PostMsgActivity.this);

        Map<String, String> param= new HashMap<>();
        param.put("token", QueryPreferences.getToken(PostMsgActivity.this));
        param.put("content", mText);
        if(!mDataUrl.equals(""))
            param.put("url", mDataUrl);
        if(isImageSet)
            param.put("image", mImage);

        JsonObjectRequest jor= new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                if(jsonObject!=null) {
                    try {
                        resultKey = jsonObject.getString("result");
                        if(resultKey.equals("success")){
                            Toast.makeText(PostMsgActivity.this, "Msg is successfully posted!!!n\n Refresh your page", Toast.LENGTH_SHORT).show();
                            if(shareIntent!=null){
                                Intent i = new Intent(PostMsgActivity.this, ProfileActivity.class);
                                startActivity(i);
                            }
                            finish();
                        }
                        else {
                            Toast.makeText(PostMsgActivity.this, "Submit Again!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Volley Error : "+volleyError);
            }
        });
        requestQueue.add(jor);
        //finish();
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
}
