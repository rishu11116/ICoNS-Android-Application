package app.racdeveloper.com.ICoNS.commentOnNewsFeed;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import app.racdeveloper.com.ICoNS.R;

public class CommentsActivity extends AppCompatActivity implements AddCommentFragment.ListUpdateListener, CommentList.ListUpdateListenerOnEdit{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddCommentFragment addCommentFragment = new AddCommentFragment(getIntent().getExtras().getString("feedID"));
        CommentList listFragment  = new CommentList(this, getIntent().getExtras().getString("feedID"));
        fragmentTransaction.add(R.id.fragment_add,addCommentFragment);
        fragmentTransaction.add(R.id.fragment_list,listFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void listRefreshed() {
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_list, new CommentList(this, getIntent().getStringExtra("feedID")), null);
        transaction.commit();
    }

    @Override
    public void listRefreshedOnEdit() {
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_list, new CommentList(this, getIntent().getStringExtra("feedID")), null);
        transaction.commit();
    }
}
