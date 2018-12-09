package com.example.apple.mlkit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity2 extends YouTubeBaseActivity {
     YouTubePlayerView youTubePlayerView;
     Button bt;
     YouTubePlayer.OnInitializedListener onInitializedListener;
     TextView textView;
     String moodinfo;
     String playlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bt = (Button)findViewById(R.id.plybt);
        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeview);
        //Toast.makeText(MainActivity2.this, moodinfo,Toast.LENGTH_LONG).show();
        textView = (TextView)findViewById(R.id.textView2);

        Intent myintent = getIntent();
        moodinfo = myintent.getStringExtra("moodinfo");
        playlist = myintent.getStringExtra("playlist");

        textView.setText(moodinfo);

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.cuePlaylist(playlist);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayerView.initialize(YoutubeContain.getApiKey(), onInitializedListener);

            }
        });
    }
}
