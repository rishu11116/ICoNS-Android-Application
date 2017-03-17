package app.racdeveloper.com.ICoNS.newsFeed;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import app.racdeveloper.com.ICoNS.R;
import app.racdeveloper.com.ICoNS.commentOnNewsFeed.CommentsActivity;
import app.racdeveloper.com.ICoNS.fetchProfiles.MyProfile;
import app.racdeveloper.com.ICoNS.pushNotification.NotificationWebview;

/**
 * Created by Rachit on 10/7/2016.
 */
public class NewsFeedListAdapter extends BaseAdapter {

    private static String TAG="NewsFeedListAdapter";
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    ImageLoader imageLoader;

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;            //     set to 200
    NetworkImageView expandedImageView;
    View newsFeedLayout;            // to blur the newsfeed on showing expanded image

    public NewsFeedListAdapter(Activity activity, List<FeedItem> feedItems){
        if(NewsFeedController.getInstance() == null)
            Log.i(TAG, "null");
        imageLoader= NewsFeedController.getInstance().getImageLoader();
        this.activity = activity;
        this.feedItems = feedItems;
        mShortAnimationDuration = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(inflater==null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view==null)
            view = inflater.inflate(R.layout.news_feed_item, null);
        if(imageLoader==null)
            imageLoader= NewsFeedController.getInstance().getImageLoader();

        newsFeedLayout = activity.findViewById(R.id.list);

        final TextView name, timestamp, statusMsg, url, commentCount;
        name = (TextView) view.findViewById(R.id.author);
        timestamp = (TextView) view.findViewById(R.id.timestamp);
        statusMsg = (TextView) view.findViewById(R.id.txtStatusMsg);
        url = (TextView) view.findViewById(R.id.txtUrl);
        commentCount = (TextView) view.findViewById(R.id.tvComment);

        final NetworkImageView profilepic = (NetworkImageView) view.findViewById(R.id.profilePic);
        profilepic.setDefaultImageResId(R.drawable.default_profile_pic);

        final FindImageView findImageView= (FindImageView) view.findViewById(R.id.feedImage1);

        final FeedItem item = feedItems.get(position);

        name.setText(item.getAuthor());
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MyProfile.class);
                intent.putExtra("userProfileRequest", item.getAuthor());
                intent.putExtra("userRollno", item.getAuthorRollno());        //item.getAuthorRollno();
                activity.startActivity(intent);
            }
        });

        if(item.getCommentCount()>0)
            commentCount.setText(item.getCommentCount() + " Comment");
        if (item.getCommentCount()>1)
            commentCount.setText(item.getCommentCount() + " Comments");

        commentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, CommentsActivity.class).putExtra("feedID", item.getId()+""));
            }
        });

        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        if(!TextUtils.isEmpty(item.getContent())){
            statusMsg.setText(item.getContent());
            statusMsg.setVisibility(View.VISIBLE);
        }
        else{
            statusMsg.setVisibility(View.GONE);
        }



        if(item.getUrl()!=null){
            if(!item.getUrl().regionMatches(0, "http://", 0, 7) && !item.getUrl().regionMatches(0, "https://", 0, 8))              // to add at beginning "https://" to url
                item.setUrl("http://"+item.getUrl());
//            url.setTextColor(Color.BLUE);
            url.setText(Html.fromHtml("<a href=\""+ item.getUrl() +"\">"+ item.getUrl() +"</a>"));
//            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, NotificationWebview.class).putExtra("uri", item.getUrl()));
                }
            });
            url.setVisibility(View.VISIBLE);
        }
        else{
            url.setVisibility(View.GONE);
        }

        // Load the high-resolution "zoomed-in" image.
        expandedImageView = (NetworkImageView) activity.findViewById(R.id.expanded_image);

        if (item.getProfilePic()!=null) {
            if (!item.getProfilePic().equals("")) {
                profilepic.setImageUrl(item.getProfilePic(), imageLoader);

                profilepic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        expandedImageView.setImageUrl(item.getProfilePic(), imageLoader);
                        zoomImageFromThumb(profilepic, item.getProfilePic());
                    }
                });
            }
        }

        if(item.getImage()!=null){
            findImageView.setImageUrl(item.getImage(), imageLoader);
            findImageView.setVisibility(View.VISIBLE);
            findImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandedImageView.setImageUrl(item.getImage(), imageLoader);
                    zoomImageFromThumb(findImageView, item.getImage());
                }
            });
            findImageView.setResponseObserver(new FindImageView.ResponseObserver() {
                @Override
                public void onError() { }
                @Override
                public void onSuccess() { }
            });
        }
        else{
            findImageView.setVisibility(View.GONE);
        }
        return view;
    }

    private void zoomImageFromThumb(final View imageLoaderView, String imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        imageLoaderView.getGlobalVisibleRect(startBounds);

        imageLoaderView.getRootView().findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.

        newsFeedLayout.setAlpha(0.3f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        newsFeedLayout.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        newsFeedLayout.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
