package com.example.d064713.smartrideskeleton;

import java.util.Date;
import java.util.Random;

/**
 * Created by D064713 on 29.05.2018.
 */

public class Bahn {

    public String Ziel;
    public Date Abfahrt;
    public int Auslastung;
    public float[] Statistik;

    public Bahn(String ZielValue, Date AbfahrtValue){
        Ziel = ZielValue;
        Abfahrt = AbfahrtValue;
        Auslastung = new Random().nextInt(3 - 0);
        Statistik = getAuslastung();
    }

    public float[] getAuslastung(){
        float[] stats = {};
        return stats;
    }

    public String toString(){
        return Ziel + " " + Abfahrt + " Auslastung: " + Auslastung;
    }

}
