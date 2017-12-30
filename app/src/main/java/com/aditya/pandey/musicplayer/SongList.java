package com.aditya.pandey.musicplayer;

import com.orm.SugarRecord;

public class SongList extends SugarRecord {
    String song,url,artists,cover_image;
    long time;
    int fav,down;

    //Default Constructor is important
    public SongList(){}

    //Adding the Song data in database Constructor
    public SongList(ApiStruct obj, long time, int favorite, int down){
        this.song = obj.getSong();
        this.url = obj.getUrl();
        this.artists = obj.getArtists();
        this.cover_image = obj.getCoverImage();
        this.time = time;
        this.fav = favorite;
        this.down = down;
    }

    public boolean isFav(){
        return (this.fav == 1);
    }

    public boolean isDown(){
        return (this.down == 1);
    }
}