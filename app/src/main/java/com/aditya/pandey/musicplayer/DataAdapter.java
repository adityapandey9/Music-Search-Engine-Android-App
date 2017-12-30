package com.aditya.pandey.musicplayer;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.koushikdutta.ion.Ion;
import com.like.LikeButton;
import com.like.OnLikeListener;
import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> implements Filterable {
    private ArrayList<ApiStruct> mArrayList;
    private ArrayList<ApiStruct> mFilteredList;
    private ArrayList<SongList> data;
    Context context;

    public DataAdapter(ArrayList<ApiStruct> arrayList, ArrayList<SongList> button_data, Context context) {
        mArrayList = arrayList;
        mFilteredList = arrayList;
        this.context = context;
        this.data = button_data;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {
        viewHolder.tv_name.setText(mFilteredList.get(i).getSong());
        viewHolder.tv_version.setText(mFilteredList.get(i).getArtists());
        viewHolder.likeButton.setLiked(data.get(i).isFav());
//        Loading image from net and also set Placeholder with ION library
        Ion.with(viewHolder.album_image)
                .placeholder(R.drawable.ic_icons8_image_file_512)
                .load(mFilteredList.get(i).getCoverImage());
        viewHolder.download_url.setText(mFilteredList.get(i).getUrl());
        viewHolder.likeButton.setTag(viewHolder);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mArrayList;
                } else {

                    ArrayList<ApiStruct> filteredList = new ArrayList<>();

                    for (ApiStruct ApiStruct : mArrayList) {

                        if (ApiStruct.getSong().toLowerCase().contains(charString) || ApiStruct.getArtists().toLowerCase().contains(charString)) {

                            filteredList.add(ApiStruct);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<ApiStruct>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name,tv_version,download_url;
        private ImageView album_image;
        private ImageButton download,share;
        private LikeButton likeButton;
        public View view;
        private SimpleExoPlayerView simpleExoPlayerView;
        private SimpleExoPlayer player;
        private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
        private static final int BUFFER_SEGMENT_COUNT = 256;

        public ViewHolder(View view, final Context context) {
            super(view);

            tv_name = (TextView)view.findViewById(R.id.tv_name);
            tv_version = (TextView)view.findViewById(R.id.tv_version);
            album_image = (ImageView)view.findViewById(R.id.album_image);
            download = (ImageButton)view.findViewById(R.id.imageButton2);
            share = (ImageButton)view.findViewById(R.id.imageButton3);
            download_url = (TextView)view.findViewById(R.id.downloadurl);
            likeButton = (LikeButton)view.findViewById(R.id.imageButton);
            simpleExoPlayerView = (SimpleExoPlayerView)view.findViewById(R.id.player_view);

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name =  mFilteredList.get(getAdapterPosition()).getSong();
                    String url = mFilteredList.get(getAdapterPosition()).getUrl();
                    SongList list = new SongList();
                    if(getAdapterPosition() == 0) {
                        list = SongList.first(SongList.class);
                    } else {
                        list = SongList.findById(SongList.class, getAdapterPosition());
                    }
                    list.time = System.currentTimeMillis() % 1000;
                    list.down = 1;
                    list.save();
                    download(name, url, context);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name =  mFilteredList.get(getAdapterPosition()).getSong();
                    share(context, name);
                }
            });
            likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    String name =  mFilteredList.get(getAdapterPosition()).getSong();
                    SongList list = new SongList();
                    if(getAdapterPosition() == 0) {
                        list = SongList.first(SongList.class);
                    } else {
                        list = SongList.findById(SongList.class, getAdapterPosition());
                    }
                    list.fav = 1;
                    list.save();
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    SongList list = new SongList();
                    if(getAdapterPosition() == 0) {
                        list = SongList.first(SongList.class);
                    } else {
                        list = SongList.findById(SongList.class, getAdapterPosition());
                    }
                    list.fav = 0;
                    list.save();
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(simpleExoPlayerView.getVisibility() == View.VISIBLE)
                        {
                            simpleExoPlayerView.setVisibility(View.GONE);
                            player.setPlayWhenReady(false); //run file/link when ready to play.
                            return;
                        }
                        simpleExoPlayerView.setVisibility(View.VISIBLE);
//// 1. Create a default TrackSelector
                        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
                        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

// 2. Create a default LoadControl
                        LoadControl loadControl = new DefaultLoadControl();
                        RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
// 3. Create the player
                        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
////Set media controller
                        simpleExoPlayerView.setUseController(true);
                        simpleExoPlayerView.requestFocus();

// Bind the player to the view.
                        simpleExoPlayerView.setPlayer(player);
                        Uri mp4VideoUri = Uri.parse(mFilteredList.get(getAdapterPosition()).getUrl());

//Produces DataSource instances through which media data is loaded.
                        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                                Util.getUserAgent(context, "Music Player"),
                                null /* listener */,
                                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                                true /* allowCrossProtocolRedirects */
                        );

                        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, null, httpDataSourceFactory);
//Produces Extractor instances for parsing the media data.
                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                        MediaSource mediaSource = new ExtractorMediaSource(mp4VideoUri,
                                dataSourceFactory,
                                extractorsFactory,
                                null,
                                null);

                        player.setPlayWhenReady(true); //run file/link when ready to play.
                        player.prepare(mediaSource);
                    }catch (Exception e){
                        Toast.makeText(context, "Exp: "+e, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        public void share(Context context, String name){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Get the song " + name + " From Music Player App.");
            context.startActivity(Intent.createChooser(intent, "Share"));
        }

        public void download(String name, String url, Context context){
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(url);
            request.setTitle(name);
            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name+".mp3");
            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }

    }

}