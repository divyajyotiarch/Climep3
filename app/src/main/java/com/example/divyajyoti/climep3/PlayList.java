package com.example.divyajyoti.climep3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.AwarenessOptions;
import com.google.android.gms.awareness.SnapshotClient;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

public class PlayList extends AppCompatActivity {

    ListView listView;

    String playlist[];

    ImageButton back;

    View playlist_view;

    TextView textView,suggestionBar;

    HashMap<Integer,String>weatherCondition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        playlist_view  = (View)findViewById(R.id.playlist_view);

        suggestionBar = (TextView)findViewById(R.id.suggestion);

//        GoogleApiClient client = new GoogleApiClient.Builder(getApplicationContext()).addApi(Awareness.API).build();
//        client.connect();  No need of this


        playlist = new String[] {"Summer Songs","Winter Songs","Cool Songs","Rain Songs","Hot Songs"};
        listView = (ListView)findViewById(R.id.playlist);
        textView = (TextView)findViewById(R.id.playlistName);
        back = (ImageButton)findViewById(R.id.backtoMediaplayer);

        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.tv,playlist));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                TextView tv = (TextView) lv.getChildAt(position);
                String s = tv.getText().toString();
                //on select does nothing
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainActivity = new Intent(PlayList.this,MainActivity.class);
                startActivity(mainActivity);
            }
        });


        if (ContextCompat.checkSelfPermission(
                PlayList.this,android.Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    PlayList.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1
            );
            return;
        }


        weatherCondition = new HashMap<>();
        weatherCondition.put(0,"Can't say what's going outside");
        weatherCondition.put(1,"It's a clear day! Play any playlist!");
        weatherCondition.put(2,"It's cloudy outside! Happy songs is what you need!");
        weatherCondition.put(3,"Foggy Outside");
        weatherCondition.put(4,"Hazy outside");
        weatherCondition.put(5,"Icy outside");
        weatherCondition.put(6,"Rainy outside, have some coffee and Rain songs");
        weatherCondition.put(7,"Snowy outside");
        weatherCondition.put(8,"Storm coming, Chill songs?");
        weatherCondition.put(9,"Windy outside, wanna put some Long Drive playlist?");


        Awareness.getSnapshotClient(getApplicationContext())
                .getWeather().addOnCompleteListener(new OnCompleteListener<WeatherResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<WeatherResponse> task) {
                        if(task.isSuccessful())
                        {
                            WeatherResponse weatherResponse = task.getResult();

                            Weather weather = weatherResponse.getWeather();

                            int[] code = weather.getConditions();
                            String text = "";

                            for(int i=0;i<code.length;i++){
                                //Snackbar.make(playlist_view, weatherCondition.get(code[i]),Snackbar.LENGTH_SHORT).show();
                                text = text + "\n"+weatherCondition.get(code[i]);
                            }

                            float temperature = weather.getFeelsLikeTemperature(Weather.CELSIUS);

                            if(temperature > 35){
                                text = text + "\n" + "It is hot outside! Let's play some Cool songs";
                            }
                            int humid = weather.getHumidity();

                            if(humid>35){
                                text = text + "\n" + "Today's really humid day! What about Cool songs?";
                            }

                            suggestionBar.setText(text);

                        }
                        else{
                            Snackbar.make(playlist_view,"Task Unsuccessful",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });


        Awareness.getSnapshotClient(getApplicationContext()).getWeather()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(playlist_view,"Exception "+e.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });


    }



}
