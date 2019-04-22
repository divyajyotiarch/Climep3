package com.example.divyajyoti.climep3;

public class MusicUtilities {

    public static final int MAX_PROGRESS = 100;

    public String milliSecondstoTime(long milliseconds){

        String finalTimerString="";
        String secondsString="";

        int hours = (int) (milliseconds/(1000*60*60));
        int minutes = (int) (milliseconds%(1000*60*60))/(1000*60);
        int seconds = (int) ((milliseconds%(1000*60*60))%(1000*60)/1000);

        if(hours>0){
            finalTimerString = hours + ":";
        }
        else if(seconds<10){
           secondsString = "0"+seconds;
        }
        else
        {
            secondsString=""+seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    public int getProgressSeekBar(long currDuration,long totalDuration){

        Double progress = (double) 0;
        progress = ((double)currDuration/totalDuration)*100; //MAX_PROGRESS changed to 100 - works fine

        return progress.intValue();
    }

    public int progressToTimer(int progress,int duration){
        int currDuration;
        duration = duration/1000;
        currDuration = (int)((double)progress/MAX_PROGRESS)*duration;

        return currDuration*1000;

    }

}
