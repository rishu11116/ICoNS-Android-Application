package app.racdeveloper.com.ICoNS.commentOnNewsFeed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.racdeveloper.com.ICoNS.QueryPreferences;
import app.racdeveloper.com.ICoNS.R;

public class CommentViewAdapter extends RecyclerView.Adapter<CommentViewAdapter.ViewHolder> {

    private Context context;
    private List<CommentData> dataList;

    public CommentViewAdapter(Context context, List<CommentData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Picasso.with(context).load(dataList.get(position).getCommentUserImageUrl()).into(holder.UserImage);
        holder.UserName.setText(dataList.get(position).getCommentUserName());
        holder.UserComment.setText(dataList.get(position).getCommentText());
        if (!dataList.get(position).getCommentUserRollNo().equals(QueryPreferences.getRollNo(context)))
            holder.editCommentOptions.setVisibility(View.GONE);
        holder.bindHolder(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView UserImage;
        public ImageView editCommentOptions;
        public TextView UserName;
        public TextView UserComment;
        CommentData commentData;

        public ViewHolder(final View itemView) {
            super(itemView);
            UserImage = (ImageView) itemView.findViewById(R.id.commentUserImage);
            UserName = (TextView) itemView.findViewById(R.id.commentUserName);
            UserComment = (TextView) itemView.findViewById(R.id.commentText);

//            if(QueryPreferences.isTokenSet()) {
            editCommentOptions = (ImageView) itemView.findViewById(R.id.editCommentOption);
            editCommentOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CommentList(itemView.getContext()).editCommentOptions(commentData);
                }
            });
        }

        public void bindHolder(CommentData commentData) {
            this.commentData = commentData;
        }
    }

}