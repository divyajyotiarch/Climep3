package com.example.divyajyoti.climep3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

public class MainActivity extends AppCompatActivity {

    private View parent_view;

    private AppCompatSeekBar seek_progress_bar;

    private FloatingActionButton play_btn;

    private TextView song_curr_duration, song_total_duration;

    private CircularImageView music_disk;

    private MediaPlayer mp3;

    private Handler mHandler = new Handler();

    private MusicUtilities utils;

    private boolean isMovingSeekbar = false;

    int seekValue=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMusicPlayerComponents();
    }

    public void setMusicPlayerComponents(){

        parent_view = (View)findViewById(R.id.parent_view);
        seek_progress_bar = (android.support.v7.widget.AppCompatSeekBar)findViewById(R.id.seek_bar);
        play_btn = findViewById(R.id.play_btn);
        song_curr_duration = (TextView)findViewById(R.id.song_curr_duration);
        song_total_duration = (TextView) findViewById(R.id.song_end_duration);
        music_disk = findViewById(R.id.disk_image);

        mp3 = new MediaPlayer();

        mp3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play_btn.setImageResource(R.drawable.ic_play_arrow_black_28dp);
            }
        });

        try{
            mp3.setAudioStreamType(AudioManager.STREAM_MUSIC);
            AssetFileDescriptor afd = getAssets().openFd("faded.mp3");
            mp3.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            afd.close();
            mp3.prepare();

        }
        catch (Exception e){
            Snackbar.make(parent_view,"Could not load audio file.",Snackbar.LENGTH_LONG).show();
        }

        utils=new MusicUtilities();

        seek_progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mHandler.removeCallbacks(mUpdateTimeTask);
                int duration = mp3.getDuration();
                int currPosition = utils.progressToTimer(seekBar.getProgress(),duration);
                mp3.seekTo(currPosition);
                mHandler.post(mUpdateTimeTask);
            }
        });

        buttonPlayAction();

    }

    private void buttonPlayAction()
    {
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp3.isPlaying()){
                    mp3.pause();
                    play_btn.setImageResource(R.drawable.ic_play_arrow_black_28dp);
                }
                else
                {
                    mp3.start();
                    play_btn.setImageResource(R.drawable.ic_pause_black_28dp);
                    mHandler.post(mUpdateTimeTask);
                }

                rotateDisk();
            }
        });
    }

    protected void controlClick(View v)
    {
        int id = v.getId();
        switch(id)
        {
            case R.id.skip_next_btn:
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view,"next",Snackbar.LENGTH_LONG).show();
                break;

            case R.id.skip_previous_btn:
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view,"previous",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.repeat_btn:
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view,"repeat",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.shuffle_btn:
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view,"shuffle",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.play_list:
                Intent playlist = new Intent(MainActivity.this,PlayList.class);
                startActivity(playlist);
                break;
        }
    }

    private boolean toggleButtonColor(ImageButton bt)
    {
        String selected = (String) bt.getTag(bt.getId());
        if(selected!=null){
            bt.setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_ATOP);
            bt.setTag(bt.getId(),null);
            return false;
        }
        else
        {
            bt.setTag(bt.getId(),"selected");
            bt.setColorFilter(getResources().getColor(R.color.colorWhite),PorterDuff.Mode.SRC_ATOP);
            return true;
        }
    }

    private void rotateDisk(){

            if(!mp3.isPlaying())    return;
            music_disk.animate().setDuration(100).rotation(music_disk.getRotation()+2f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rotateDisk();
                    super.onAnimationEnd(animation);
                }
            });
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            mUpdateTimeAndSeekbar();
            if(mp3.isPlaying()){
                mHandler.postDelayed(this,100);
            }
        }
    };

    private void mUpdateTimeAndSeekbar(){

        long totalDuration = mp3.getDuration();
        long currDuration = mp3.getCurrentPosition();

        int progress = utils.getProgressSeekBar(currDuration,totalDuration);
        seek_progress_bar.setProgress(progress);

        song_total_duration.setText(utils.milliSecondstoTime(totalDuration));
        song_curr_duration.setText(utils.milliSecondstoTime(currDuration));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
        }
        else{
            Snackbar.make(parent_view,item.getTitle(),Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp3.release();
        }


}

