package app.racdeveloper.com.ICoNS.newsFeed;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Rachit on 10/7/2016.
 */
public class FindImageView extends ImageView {
    public interface ResponseObserver {
        void onError();

        void onSuccess();
    }

    private ResponseObserver responseObserver;

    public void setResponseObserver(ResponseObserver obj){
        responseObserver = obj;
    }

    private String mUrl;
    private int mDefaultImageId;
    private int mErrorImageId;
    private ImageLoader mImageLoader;
    private ImageLoader.ImageContainer mImageContainer;

    public FindImageView(Context context) {
        super(context, null);
    }

    public FindImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public FindImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageUrl(String url, ImageLoader imageLoader) {
        mUrl = url;
        mImageLoader = imageLoader;
        loadImageIfNecessary(false);
    }

    public void setDefaultImageResId(int defaultImageId){
        mDefaultImageId = defaultImageId;
    }

    public void setErrorImageResId(int errorImageId){
        mErrorImageId = errorImageId;
    }

    private void loadImageIfNecessary(final boolean isInLayoutPass) {
        final int width = getWidth();
        int height = getHeight();

        boolean isFullyWrapContent = (getLayoutParams()!=null)
                && getLayoutParams().height== LinearLayout.LayoutParams.WRAP_CONTENT
                && getLayoutParams().width == LinearLayout.LayoutParams.WRAP_CONTENT;

        if(width==0 && height==0 && !isFullyWrapContent)
            return;

        if(TextUtils.isEmpty(mUrl)){
            if(mImageContainer!=null){
                mImageContainer.cancelRequest();
                mImageContainer=null;
            }
            setDefaultImageOrNull();
            return;
        }

        if(mImageContainer!=null && mImageContainer.getRequestUrl()!= null){
            if(mImageContainer.getRequestUrl().equals(mUrl)){
                return;
            }
            else{
                mImageContainer.cancelRequest();
                setDefaultImageOrNull();
            }
        }

        ImageLoader.ImageContainer newContainer = mImageLoader.get(mUrl,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean isImmediate) {
                        if(isImmediate && isInLayoutPass){
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    onResponse(imageContainer, false);
                                }
                            });
                            return;
                        }

                        int bWidth=0, bHeight=0;
                        if(imageContainer.getBitmap()!=null){
                            setImageBitmap(imageContainer.getBitmap());
                            bWidth = imageContainer.getBitmap().getWidth();
                            bHeight = imageContainer.getBitmap().getHeight();
                            adjustImageAspect(bHeight, bWidth);
                        }
                        else if(mDefaultImageId!=0){
                            setImageResource(mDefaultImageId);
                        }
                        if(responseObserver!=null)
                            responseObserver.onSuccess();
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (mErrorImageId != 0) {
                            setImageResource(mErrorImageId);
                        }
                        if (responseObserver != null) {
                            responseObserver.onError();
                        }
                    }
                });
        mImageContainer = newContainer;
    }

    private void setDefaultImageOrNull() {
        if(mDefaultImageId!=0){
            setDefaultImageResId(mDefaultImageId);
        }
        else
            setImageBitmap(null);
    }

    private void adjustImageAspect(int bHeight, int bWidth) {
        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) getLayoutParams();

        if(bHeight==0 || bWidth==0)
            return;

        int sWidth = getWidth();
        int new_height = sWidth*bHeight/bWidth;
        params.width= sWidth;
        params.height= new_height;
        setLayoutParams(params);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if(mImageContainer!=null){
            mImageContainer.cancelRequest();
            setImageBitmap(null);
            mImageContainer=null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }
}
