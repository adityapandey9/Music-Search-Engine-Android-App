package com.aditya.pandey.musicplayer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Favorite extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private RecyclerView mRecyclerView;
    private ArrayList<ApiStruct> mArrayList;
    private DataAdapter mAdapter;
    private TextView textView;

    public Favorite() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Favorite newInstance(int number) {
        Favorite fragment = new Favorite();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_viewt);
        textView = (TextView) view.findViewById(R.id.nodata);
        ArrayList<SongList> data = new ArrayList<>(SongList.find(SongList.class, "FAV = ?", "1"));
//        Toast.makeText(getContext(), "Size: "+data.size(), Toast.LENGTH_LONG).show();
        if(data.size() > 0) {
            mArrayList = new ArrayList<ApiStruct>();
            for (SongList obj: data) {
                ApiStruct obj1 = new ApiStruct();
                obj1.setSong(obj.song);
                obj1.setArtists(obj.artists);
                obj1.setUrl(obj.url);
                obj1.setCoverImage(obj.cover_image);
                mArrayList.add(obj1);
            }
            mAdapter = new DataAdapter(mArrayList, data, getContext());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        }
        initViews();
        return view;
    }

    private void initViews(){
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
    }
}
