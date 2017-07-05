package com.alxsnchez.mymovies;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private final static String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w500/";

    private ImageView mPosterImageView;
    private TextView mDateTextView;
    private TextView mScoreTextView;
    private TextView mOverviewTextView;
    private CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPosterImageView = (ImageView) findViewById(R.id.iv_poster_detail);
        mDateTextView = (TextView) findViewById(R.id.tv_movie_date);
        mScoreTextView = (TextView) findViewById(R.id.tv_movie_score);
        mOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent.hasExtra("json")){
            try {
                JSONObject movie = new JSONObject(intent.getStringExtra("json"));
                setData(movie);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setData(JSONObject jsonObject){
        try{
            String originalTitle = jsonObject.getString("original_title");
            String posterPath = jsonObject.getString("poster_path");
            String releaseDate = jsonObject.getString("release_date");
            Double voteAverage = jsonObject.getDouble("vote_average");
            String overview = jsonObject.getString("overview");

            mCollapsingToolbar.setTitle(originalTitle);
            Picasso.with(DetailActivity.this).load(BASE_POSTER_URL + posterPath).error(R.drawable.empty_thumbnail).into(mPosterImageView);
            mDateTextView.setText(calculateYear(releaseDate));
            mScoreTextView.setText(String.format(Locale.getDefault(), "%.1f/10", voteAverage));
            mOverviewTextView.setText(overview);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String calculateYear(String date){
        List<String> dateArray = Arrays.asList(date.split("-"));
        return dateArray.get(0);
    }

}
