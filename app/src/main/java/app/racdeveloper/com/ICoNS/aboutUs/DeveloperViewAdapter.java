package app.racdeveloper.com.ICoNS.aboutUs;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.racdeveloper.com.ICoNS.R;
import app.racdeveloper.com.ICoNS.pushNotification.NotificationWebview;

/**
 * Created by Rachit on 2/6/2017.
 */

public class DeveloperViewAdapter extends RecyclerView.Adapter<DeveloperViewAdapter.ViewHolder>{
    private Context context;
    private List<DeveloperData> developerList;

    public DeveloperViewAdapter(Context context, List<DeveloperData> developerDataList) {
        this.context=context;
        developerList=developerDataList;
    }

    @Override
    public DeveloperViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactcard,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeveloperViewAdapter.ViewHolder holder, final int position) {

        holder.developerPhoto.setImageResource(developerList.get(position).getDeveloperImage());
        holder.developerName.setText(developerList.get(position).getDeveloperName());
        holder.developerBranch.setText(developerList.get(position).getDeveloperBranch());
        holder.developerFbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,NotificationWebview.class);
                intent.putExtra("uri",developerList.get(position).getDeveloperFBUrl());
                context.startActivity(intent);
            }
        });

        holder.developerLinkedInImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,NotificationWebview.class);
                intent.putExtra("uri",developerList.get(position).getDeveloperLinkedInUrl());
                context.startActivity(intent);
            }
        });

        holder.developerTwitterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,NotificationWebview.class);
                intent.putExtra("uri",developerList.get(position).getDeveloperTwitterUrl());
                context.startActivity(intent);
            }
        });

        holder.developerGoogleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,NotificationWebview.class);
                intent.putExtra("uri",developerList.get(position).getDeveloperGoogleUrl());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return developerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView developerPhoto;
        public TextView developerName;
        public TextView developerBranch;
        public ImageView developerFbImage;
        public ImageView developerLinkedInImage;
        public ImageView developerTwitterImage;
        public ImageView developerGoogleImage;

        public ViewHolder(View itemView) {
            super(itemView);

            developerPhoto=(ImageView)itemView.findViewById(R.id.cardImage);
            developerName=(TextView)itemView.findViewById(R.id.cardName);
            developerBranch=(TextView)itemView.findViewById(R.id.cardBranch);
            developerFbImage=(ImageView)itemView.findViewById(R.id.cardFbImage);
            developerLinkedInImage=(ImageView)itemView.findViewById(R.id.cardLinkedinImage);
            developerTwitterImage=(ImageView)itemView.findViewById(R.id.cardTwitterImage);
            developerGoogleImage=(ImageView)itemView.findViewById(R.id.cardGoogleImage);

        }
    }
}
