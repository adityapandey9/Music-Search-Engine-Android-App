package com.aditya.pandey.musicplayer;


import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.orm.SugarRecord;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private ArrayList<ApiStruct> mArrayList;
    private DataAdapter mAdapter;
    EditText searchView;
    private ArrayList<SongList> data;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(int number) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view);
        searchView = (EditText) view.findViewById(R.id.searchView);
//        SugarRecord.deleteAll(SongList.class);
//        ArrayList<SongList> datas = new ArrayList<>(SongList.find(SongList.class, "DOWN = ? and FAV = ?", "1", "1"));
        data = new ArrayList<>(SongList.listAll(SongList.class));
        if(SongList.count(SongList.class) > 0) {
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
        }else {
            loadJSON(data);
        }
        initViews();
        search(searchView);
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        return view;
    }

    private void initViews(){
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void loadJSON(ArrayList<SongList> datas){
        ApiInterface apiService = getApiService();
        Call<ArrayList<ApiStruct>> call = apiService.getAllSongs();
        call.enqueue(new Callback<ArrayList<ApiStruct>>() {
            @Override
            public void onResponse(Call<ArrayList<ApiStruct>> call, Response<ArrayList<ApiStruct>> response) {
                if (response.code() == 200) {
                    mArrayList = response.body();
                    mAdapter = new DataAdapter(mArrayList, data, getContext());
                    mRecyclerView.setAdapter(mAdapter);
                    for (ApiStruct obj: mArrayList) {
                        SongList list = new SongList(obj, 0, 0, 0);
                        list.save();
                    }
                } else {
                    Toast.makeText(getContext(), "Sorry, some error occur. Response: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ApiStruct>> call, Throwable t) {
                Toast.makeText(getContext(), "Error Occur: "+ t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void search(EditText searchView) {

        searchView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                mAdapter.getFilter().filter(s);
            }
        });
    }

    public static ApiInterface getApiService() {
        return ApiService.getClient("http://starlord.hackerearth.com").create(ApiInterface.class);
    }
}
