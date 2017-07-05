package com.alxsnchez.mymovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler{

    private final ArrayList<String> mMoviesPosters = new ArrayList<>();
    private final ArrayList<String> mMoviesIds = new ArrayList<>();

    private MoviesAdapter mMoviesAdapter;
    private GridLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;

    private final static String BASE_URL = "https://api.themoviedb.org/3/movie";
    private final static String API_PARAM = "api_key";
    private final static String SORT_POPULAR = "popular";
    private final static String SORT_RATING = "top_rated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.main_progress_bar);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        requestInformation(SORT_POPULAR);

        //// TODO: 30/06/2017 Create new activity for details
        //// TODO: 30/06/2017 Polish loading timing
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by_popularity) {
            requestInformation(SORT_POPULAR);
        } else if (id == R.id.action_sort_by_rating) {
            requestInformation(SORT_RATING);
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestInformation(String requestType){
        showLoading();

        OkHttpClient client = new OkHttpClient();
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(requestType)
                .appendQueryParameter(API_PARAM, getString(R.string.API_KEY))
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

                    try{
                        String responseData = response.body().string();
                        try {
                            JSONObject json = new JSONObject(responseData);
                            JSONArray results = json.getJSONArray("results");
                            mMoviesPosters.clear();
                            mMoviesIds.clear();
                            for (int i=0; i < results.length(); i++)
                            {
                                try {
                                    JSONObject movie = results.getJSONObject(i);
                                    String image = movie.getString("poster_path");
                                    String id = String.valueOf(movie.getInt("id"));
                                    mMoviesPosters.add(image);
                                    mMoviesIds.add(id);
                                } catch (JSONException e) {
                                    // Oops
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }


                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMoviesAdapter.setMovieData(MainActivity.this, mMoviesPosters, mMoviesIds);
                        //
                        // Added as a way to show first movies posters when change the filter and not get stuck at the end of the recycler view
                        // if we were already there.
                        //
                        mLayoutManager.scrollToPositionWithOffset(0, 0);
                        hideLoading();
                    }
                });
            }
        });
    }

    @Override
    public void onListItemClick(String item) {
        Context context = MainActivity.this;
        Class destinationActivity = DetailActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra("MOVIE_ID", item);
        startActivity(intent);
    }

    private void showLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
