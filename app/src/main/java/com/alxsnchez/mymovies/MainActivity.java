package com.alxsnchez.mymovies;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> mMoviesPosters = new ArrayList<String>();

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;

    final static String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    final static String API_PARAM = "api_key";
    final static String API_KEY = "YOUR_API_KEY_GOES_HERE";
    final static String SORT_PARAM = "sort_by";
    final static String SORT_POPULAR = "popularity.desc";
    final static String SORT_RATING = "vote_average.desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter();
        mRecyclerView.setAdapter(mMoviesAdapter);

        requestInformation();

        //// TODO: 30/06/2017 Add menu to change between popularity and rating
        //// TODO: 30/06/2017 Implement click handler for recyclerview
        //// TODO: 30/06/2017 Create new activity for details
        //// TODO: 30/06/2017 Polish loading timing
    }

    void requestInformation(){
        OkHttpClient client = new OkHttpClient();
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .appendQueryParameter(SORT_PARAM, SORT_POPULAR)
                .build();

        Request request = new Request.Builder().url(builtUri.toString()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray results = json.getJSONArray("results");
                        for (int i=0; i < results.length(); i++)
                        {
                            try {
                                JSONObject movie = results.getJSONObject(i);
                                String images = movie.getString("poster_path");
                                mMoviesPosters.add(images);
                            } catch (JSONException e) {
                                // Oops
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMoviesAdapter.setMovieData(mMoviesPosters, MainActivity.this);
                    }
                });
            }
        });
    }
}
