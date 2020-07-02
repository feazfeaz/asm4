package com.example.ticketmovies;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MoviesInfoFragment extends Fragment {
//    private static final String TAG = MoviesInfoFragment.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_movies_info,null,false);
        GridView gridView = (GridView)fragmentView.findViewById(R.id.frag_info_gridview);
        //get adapter from main Activity
        gridView.setAdapter(MainActivity.movieInfoAdapter);
        return fragmentView;
    }
}
