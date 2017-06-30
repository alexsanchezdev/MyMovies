package com.alxsnchez.mymovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by alxsn on 30/06/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    public ArrayList<String> mMoviesPosters;
    public ImageView mImageView;
    public Context mContext;
    public final static String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        holder.bind(position);

        //
        // Added as a way to prevent position change during my emulation. Not tested on a real device.
        //
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        if (mMoviesPosters != null){
            return mMoviesPosters.size();
        } else {
            return 0;
        }

    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {



        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.img_movie_poster);

        }

        void bind(int index){
            Picasso.with(mContext).load(BASE_POSTER_URL + mMoviesPosters.get(index)).into(mImageView);
        }
    }

    public void setMovieData(ArrayList<String> movieData, Context context) {
        mMoviesPosters = movieData;
        mContext = context;
        notifyDataSetChanged();
    }
}
