package com.example.android.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button playButton, pauseButton, rewindButton, forwardButton, restartButton, stopButton;
    private MediaPlayer mediaPlayer;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();
    private int forwardTime = 15000;
    private int backwardTime = 15000;
    private SeekBar seekbar;
    private TextView songTitle, duration, currentPosition;

    public static int oneTimeOnly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        duration = (TextView) findViewById(R.id.duration);
        currentPosition = (TextView) findViewById(R.id.currentPosition);
        songTitle = (TextView) findViewById(R.id.title_of_song);

        playButton = (Button) findViewById(R.id.play);
        pauseButton = (Button) findViewById(R.id.pause);
        rewindButton = (Button) findViewById(R.id.rewind);
        forwardButton = (Button) findViewById(R.id.forward);
        restartButton = (Button) findViewById(R.id.restart);
        stopButton = (Button) findViewById(R.id.stop);

        pauseButton.setEnabled(false);

        seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekbar.setClickable(false);

        String resourceTitle = getResources().getString(R.string.resource_title);
        songTitle.setText(resourceTitle);

        /**
         * TODO: the media player should be initialized after the play button is clicked OR
         * create a new activity that displays a list of available songs
         *
         */
        // Create the mediaPlayer and set it to the media file

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    mediaPlayer.start();
                    pauseButton.setEnabled(true);
                    playButton.setEnabled(false);
                } else {
                    playMedia(R.raw.tomb_raider2);
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    Toast.makeText(getApplicationContext(), "Pausing sound",
                            Toast.LENGTH_SHORT).show();
                    mediaPlayer.pause();
                    pauseButton.setEnabled(false);
                    playButton.setEnabled(true);
                }
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    int temp = (int) startTime;

                    if((temp + forwardTime) <= finalTime) {
                        startTime += forwardTime;
                        mediaPlayer.seekTo((int) startTime);
                        Toast.makeText(getApplicationContext(), "You have jumped forward 15 seconds",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Cannot jump forward 15 seconds",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer != null) {
                    int temp = (int) startTime;

                    if((temp - backwardTime) > 0) {
                        startTime -= backwardTime;
                        mediaPlayer.seekTo((int) startTime);
                        Toast.makeText(getApplicationContext(), "You have jumped backward 15 seconds",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Cannot jump backward 15 seconds",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    mediaPlayer.reset();
                    mediaPlayer.seekTo(0);

                    pauseButton.setEnabled(false);
                    playButton.setEnabled(true);
                } else {
                    playMedia(R.raw.tomb_raider2);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    mediaPlayer.reset();
                    releaseMediaPlayer();

                    pauseButton.setEnabled(false);
                    playButton.setEnabled(true);

                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {

            if (mediaPlayer != null) {
                startTime = mediaPlayer.getCurrentPosition();
                currentPosition.setText(String.format(Locale.US, "%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        startTime))));
                seekbar.setProgress((int) startTime);
                myHandler.postDelayed(this, 100);
            }
        }
    };

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it
            mediaPlayer.release();

            // Set the media player to null, for our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer = null;

            //Reset the Play & Pause buttons
            pauseButton.setEnabled(false);
            playButton.setEnabled(true);
        }
    }

    private void playMedia(int resourceId) {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), resourceId);

        Toast.makeText(getApplicationContext(), "Playing sound",
                Toast.LENGTH_SHORT).show();
        mediaPlayer.start();

        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        if(oneTimeOnly == 0) {
            seekbar.setMax((int) finalTime);
            oneTimeOnly = 1;
        }

        duration.setText(String.format(Locale.US,"%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime))));

        currentPosition.setText(String.format(Locale.US,"%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime))));

        seekbar.setProgress((int) startTime);
        myHandler.postDelayed(UpdateSongTime, 100);
        pauseButton.setEnabled(true);
        playButton.setEnabled(false);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getApplicationContext(), "I'm done!", Toast.LENGTH_SHORT).show();
                releaseMediaPlayer();
            }
        });
    }

}