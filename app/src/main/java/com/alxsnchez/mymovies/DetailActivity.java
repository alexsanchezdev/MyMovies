package com.alxsnchez.mymovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {

    private final static String BASE_URL = "https://api.themoviedb.org/3/movie";
    private final static String API_PARAM = "api_key";
    private final static String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";

    private RelativeLayout mContainerLayout;

    private TextView mTitleTextView;
    private ImageView mPosterImageView;
    private TextView mDateTextView;
    private TextView mScoreTextView;
    private TextView mOverviewTextView;
    private ProgressBar mDetailProgressBar;
    private TextView mErrorMessage;

    private String movieTitle;
    private String posterPath;
    private String releaseDate;
    private Double voteAverage;
    private String movieOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mContainerLayout = (RelativeLayout) findViewById(R.id.container_layout);

        mTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mPosterImageView = (ImageView) findViewById(R.id.iv_poster_detail);
        mDateTextView = (TextView) findViewById(R.id.tv_movie_date);
        mScoreTextView = (TextView) findViewById(R.id.tv_movie_score);
        mOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        mDetailProgressBar = (ProgressBar) findViewById(R.id.detail_progress_bar);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        Intent intent = getIntent();
        if (intent.hasExtra("MOVIE_ID")){
            hideInformation();
            String movieId = intent.getStringExtra("MOVIE_ID");
            requestInformation(movieId);

        }
    }

    private void requestInformation(String movieId){
        OkHttpClient client = new OkHttpClient();
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(API_PARAM, getString(R.string.API_KEY))
                .build();

        Request request = new Request.Builder().url(builtUri.toString()).build();
        client.newCall(request).enqueue(new Callback() {

            boolean errorStatus = false;

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call,final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    errorStatus = true;
                } else {
                    try {
                        String responseData = response.body().string();
                        try {
                            JSONObject json = new JSONObject(responseData);
                            movieTitle = json.getString("original_title");
                            posterPath = json.getString("poster_path");
                            releaseDate = json.getString("release_date");
                            voteAverage = json.getDouble("vote_average");
                            movieOverview = json.getString("overview");
                            errorStatus = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }



                }

                DetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (errorStatus){
                            mDetailProgressBar.setVisibility(View.INVISIBLE);
                            mErrorMessage.setText(R.string.error_message);
                        } else {
                            mTitleTextView.setText(movieTitle);
                            Picasso.with(DetailActivity.this).load(BASE_POSTER_URL + posterPath).error(R.drawable.empty_thumbnail).into(mPosterImageView);
                            mDateTextView.setText(calculateYear(releaseDate));
                            mScoreTextView.setText(String.format(Locale.getDefault(), "%.1f/10", voteAverage));
                            mOverviewTextView.setText(movieOverview);
                            showInformation();
                        }

                    }
                });
            }
        });
    }

    private String calculateYear(String date){
        List<String> dateArray = Arrays.asList(date.split("-"));
        return dateArray.get(0);
    }

    private void showInformation(){
        mContainerLayout.setVisibility(View.VISIBLE);
        mDetailProgressBar.setVisibility(View.INVISIBLE);
    }

    private void hideInformation(){
        mDetailProgressBar.setVisibility(View.VISIBLE);
        mContainerLayout.setVisibility(View.GONE);
    }

}
