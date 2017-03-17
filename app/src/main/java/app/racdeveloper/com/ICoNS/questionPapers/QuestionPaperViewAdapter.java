package app.racdeveloper.com.ICoNS.questionPapers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.racdeveloper.com.ICoNS.R;

/**
 * Created by Rachit on 10/19/2016.
 */
public class QuestionPaperViewAdapter extends RecyclerView.Adapter<QuestionPaperViewAdapter.ViewHolder> {

    private Context context;
    private List<QuestionPaperData> dataList;

    public QuestionPaperViewAdapter(Context context, List<QuestionPaperData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_card,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.questionName.setText(dataList.get(position).getPapername());
        holder.questionContributor.setText(dataList.get(position).getPapercontributor());
        // Glide.with(context).load(dataList.get(position).getPaperurl()).into(holder.questionImage);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView questionImage;
        public TextView questionName;
        public TextView questionContributor;

        public ViewHolder(View itemView)
        {
            super(itemView);
            questionImage = (ImageView)itemView.findViewById(R.id.paperImage);
            questionName = (TextView)itemView.findViewById(R.id.paperName);
            questionContributor = (TextView)itemView.findViewById(R.id.paperContributor);
        }
    }


}
