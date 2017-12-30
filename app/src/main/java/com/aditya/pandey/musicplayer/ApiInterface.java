package com.aditya.pandey.musicplayer;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("/studio")
    Call<ArrayList<ApiStruct>> getAllSongs();
} 