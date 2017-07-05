package com.alxsnchez.mymovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private ArrayList<JSONObject> mMoviesObjects;
    private ImageView mImageView;
    private Context mContext;
    private final static String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";

    final private MoviesAdapterOnClickHandler mClickHandler;

    public interface MoviesAdapterOnClickHandler {
        void onListItemClick(JSONObject item);
    }

    public MoviesAdapter(MoviesAdapterOnClickHandler handler){
        mClickHandler = handler;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_layout;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
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
        if (mMoviesObjects != null){
            return mMoviesObjects.size();
        } else {
            return 0;
        }

    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{



        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.img_movie_poster);
            itemView.setOnClickListener(this);
        }

        void bind(int index){
            try {
                String posterPath = mMoviesObjects.get(index).getString("poster_path");
                Picasso.with(mContext).load(BASE_POSTER_URL + posterPath).error(R.drawable.empty_thumbnail).into(mImageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mClickHandler.onListItemClick(mMoviesObjects.get(clickedPosition));
        }
    }

    public void setMovieData(Context context, ArrayList<JSONObject> moviesObjects) {
        mMoviesObjects = moviesObjects;
        mContext = context;
        notifyDataSetChanged();
    }
}
