package com.example.ticketmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MovieInfoAdapter extends BaseAdapter {

    MainActivity mainActivity;
    ArrayList<MainActivity.Movie> movies;

    public MovieInfoAdapter(MainActivity context, ArrayList<MainActivity.Movie> movies){
        this.mainActivity = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = mainActivity.getLayoutInflater();
        convertView = layoutInflater.inflate(R.layout.reuse_component,null);
        LinearLayout reuse_movies_linear = (LinearLayout)convertView.findViewById(R.id.reuse_movies_linear);
        ((TextView)reuse_movies_linear.findViewById(R.id.reuse_title_movie_txt)).setText(movies.get(i).getTitle().trim());
        ((TextView)reuse_movies_linear.findViewById(R.id.reuse_price_movie_txt)).setText(movies.get(i).getTicketPrice().trim());
        ((ImageView)reuse_movies_linear.findViewById(R.id.reuse_imgview)).setImageBitmap(movies.get(i).pic);

        return reuse_movies_linear;
    }
}
