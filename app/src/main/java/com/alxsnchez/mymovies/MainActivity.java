package com.alxsnchez.mymovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

    private final ArrayList<JSONObject> mMoviesObjects = new ArrayList<>();

    private MoviesAdapter mMoviesAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
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
        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        else {
            mLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }

        mRecyclerView.setHasFixedSize(true);
        requestInformation(SORT_POPULAR);
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
        
        //
        // Obtain a portion of this code example from: http://www.vogella.com/tutorials/JavaLibrary-OkHttp/article.html
        // as I never used this library before.
        //
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
                            mMoviesObjects.clear();
                            for (int i=0; i < results.length(); i++)
                            {
                                try {
                                    JSONObject movie = results.getJSONObject(i);
                                    mMoviesObjects.add(movie);
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
                        mMoviesAdapter.setMovieData(MainActivity.this, mMoviesObjects);
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
    public void onListItemClick(JSONObject item) {
        Context context = MainActivity.this;
        Class destinationActivity = DetailActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra("json", item.toString());
        startActivity(intent);
    }

    private void showLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
