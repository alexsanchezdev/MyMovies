package com.alxsnchez.mymovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = (TextView) findViewById(R.id.tv_movie_title);

        Intent intent = getIntent();
        if (intent.hasExtra("MOVIE_ID")){
            String title = intent.getStringExtra("MOVIE_ID");
            mTitleTextView.setText(title);
        }
    }
}
