package com.haze.android.spark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class UserDetailedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    userViewAdapter adapter;
    ImageView close;
    ArrayList<HomeSpacecraft> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detailed);

        close = (ImageView)findViewById(R.id.closeImageView);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.userViewRecycler);
        SnapHelperOneByOne snapHelper = new SnapHelperOneByOne();
        snapHelper.attachToRecyclerView(recyclerView);
        Bundle extras = getIntent().getExtras();
        int pos = 0;
        if(extras != null) {
            pos = extras.getInt("position");
        }
        userList = ((GlobalContext)  getApplicationContext()).globaUserList;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        adapter = new userViewAdapter();
        recyclerView.setAdapter(adapter);
        adapter.userDetailList = this.userList;
        adapter.notifyDataSetChanged();
        recyclerView.invalidate();
        recyclerView.scrollToPosition(userList.size() - pos - 1);

    }

}

 class SnapHelperOneByOne extends LinearSnapHelper {

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY){

        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final View currentView = findSnapView(layoutManager);

        if( currentView == null ){
            return RecyclerView.NO_POSITION;
        }

        final int currentPosition = layoutManager.getPosition(currentView);

        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        return currentPosition;
    }
}