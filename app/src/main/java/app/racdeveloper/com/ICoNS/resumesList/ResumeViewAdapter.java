package app.racdeveloper.com.ICoNS.resumesList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.racdeveloper.com.ICoNS.R;

public class ResumeViewAdapter extends RecyclerView.Adapter<ResumeViewAdapter.ViewHolder> {

    private Context context;
    private List<ResumeData> dataList;

    public ResumeViewAdapter(Context context, List<ResumeData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.resume_card,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.resumeName.setText(dataList.get(position).getResumeName());
        holder.resumeBranch.setText(dataList.get(position).getResumeBranch());
        holder.resumeBatch.setText(dataList.get(position).getResumeBatch());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView resumeImage;
        public TextView resumeName;
        public TextView resumeBranch;
        public TextView resumeBatch;

        public ViewHolder(View itemView)
        {
            super(itemView);
            resumeImage = (ImageView)itemView.findViewById(R.id.resumeImage);
            resumeName = (TextView)itemView.findViewById(R.id.resumeName);
            resumeBranch = (TextView)itemView.findViewById(R.id.resumeBranch);
            resumeBatch = (TextView)itemView.findViewById(R.id.resumeBatch);
        }
    }

}