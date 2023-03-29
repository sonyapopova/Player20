package com.example.player20;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity implements Runnable {

    private MediaPlayer mediaPlayer = null;
    private SeekBar seekBar;
    private boolean wasPlaying = false;
    private FloatingActionButton fabPlayPause;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabPlayPause = findViewById(R.id.fabPlayPause);
        tvStatus = findViewById(R.id.tvStatus);
        seekBar = findViewById(R.id.seekBar);
        fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));

        fabPlayPause.setOnClickListener(view -> {
            if (getMediaPlayer().isPlaying()) {
                getMediaPlayer().pause();
                fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
            } else {
                getMediaPlayer().seekTo(seekBar.getProgress());
                getMediaPlayer().start();
                fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_pause));
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                tvStatus.setVisibility(View.VISIBLE);
                int seconds = (int) Math.ceil(progress/1000f);

                long MM = (seconds % 3600) / 60;
                long SS = seconds % 60;
                String timeInHHMMSS = String.format("%02d:%02d", MM, SS);
                tvStatus.setText(timeInHHMMSS);
                double percentTrack = progress / (double) seekBar.getMax();
                tvStatus.setX(seekBar.getX() + Math.round(seekBar.getWidth()*percentTrack*0.92));

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tvStatus.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (getMediaPlayer() != null && getMediaPlayer().isPlaying()) {
                    getMediaPlayer().seekTo(seekBar.getProgress());
                }
            }
        });

        getMediaPlayer();
        new Thread(this).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }


    private void clearMediaPlayer() {
        getMediaPlayer().stop();
        getMediaPlayer().release();
        mediaPlayer = null;
    }


    @Override
    public void run() {
        while (true) {
            try {
                if (getMediaPlayer().isPlaying()) {
                    seekBar.setProgress(getMediaPlayer().getCurrentPosition());
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                return;
            }
        }
    }


    MediaPlayer getMediaPlayer() {
        if (mediaPlayer==null) {
            try {
                mediaPlayer = new MediaPlayer();
                AssetFileDescriptor descriptor = getAssets().openFd("Вагнер - Полет Валькирий.mp3");
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();

                mediaPlayer.prepare();
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());

            } catch (Exception ex) {}
        }
        return mediaPlayer;
    }

}