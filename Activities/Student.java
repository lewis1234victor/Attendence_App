package com.google.android.gms.samples.vision.ocrreader;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.time.LocalTime;

public class Student {
    private String Namelast, Namefirst;
    private String tardy;

    public Student() {
        Namelast = "";
        Namefirst = "";
        tardy = "-1";
    }

    public String getNamelast() {

        return Namelast;
    }

    public void setNamelast(String namelast) {

        Namelast = namelast;
    }

    public String getNamefirst() {

        return Namefirst;
    }

    public void setNamefirst(String namefirst) {

        Namefirst = namefirst;
    }

    public void setTardy(LocalTime clockin, LocalTime classstart) {

        if(classstart.compareTo(clockin)>=0){
            Log.i("tardy","onTime");
            tardy = "1";
        }
        else {
            Log.i("tardy","noTime");
            tardy = "0";
        }

    }
}
