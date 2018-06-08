package com.example.d064713.smartrideskeleton;

import java.util.Date;
import java.util.Random;

/**
 * Created by D064713 on 29.05.2018.
 */

public class Bahn {

    public String Linie;
    public String Ziel;
    public String Abfahrt;
    public String Verspaetung;
    public String Verkehrsmittel;
    public int Auslastung;
    public float[] Statistik;

    public Bahn(String LinieValue, String ZielValue, String AbfahrtValue, String VerspaetungValue, String VerkehrsmittelValue){
        Linie = LinieValue;
        Ziel = ZielValue;
        Abfahrt = AbfahrtValue;
        Verspaetung = VerspaetungValue;
        Verkehrsmittel = VerkehrsmittelValue;
        Auslastung = new Random().nextInt(3 - 0);
        Statistik = getAuslastung();
    }

    //TODO: implementation
    public float[] getAuslastung(){
        float[] stats = {};
        return stats;
    }

    public String toString(){
        if(Verspaetung.equals("0")){
            return Linie + " @ " + Ziel + " Auslastung: " + Auslastung + " " + Abfahrt;
        }else{
            return Linie + " @ " + Ziel + " Auslastung: " + Auslastung + " " + Abfahrt + " +" + Verspaetung;
        }
    }

}
