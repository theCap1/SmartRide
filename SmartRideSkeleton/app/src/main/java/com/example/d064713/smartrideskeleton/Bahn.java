package com.example.d064713.smartrideskeleton;

import java.util.Calendar;
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
    public String tourId;
    public String stationName;
    public int Auslastung;
    public float[] Statistik;

    public Bahn(String tourIdValue, String LinieValue, String ZielValue, String AbfahrtValue, String VerspaetungValue, String VerkehrsmittelValue, String station){
        stationName = station;
        Linie = LinieValue;
        Ziel = ZielValue;
        Abfahrt = AbfahrtValue;
        Verspaetung = VerspaetungValue;
        Verkehrsmittel = VerkehrsmittelValue;
        //Auslastung = new Random().nextInt(3 - 0);
        tourId= tourIdValue;
        Statistik = getAuslastung();
        //System.out.println("tourid in der Klasse Bahn = " + tourId);
        Auslastung = getStatistics( tourIdValue )[getHour()];
        //System.out.println("StationName in der Klasse Bahn = " + stationName);


        if (Auslastung <= 3){
            Auslastung = 0;
        }else if (Auslastung <= 6){
            Auslastung = 1;
        }else{
            Auslastung = 2;
        }
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

    //get an partly random Array with 24h statistics for a connection
    public int[] getStatistics(String tourId){
        String[] parts = tourId.split( "-" );
        String[] chars = (tourId.replace("-", "")).split("(?!^)");
        String id = null;
        int[] values = null;
        if (stationName.equals( "Paradeplatz" ) || stationName.equals( "Duale Hochschule (MA)" )){
            id = "321123568573232457985433";

            String[] idArray = id.split("(?!^)");
            values = new int[idArray.length];
            for (int i = 0; i < idArray.length; i++) {
                values[i] = Integer.parseInt(idArray[i]);
            }
        }else {
            if (parts.length > 1)
                id = parts[1] + parts[2] + chars[7] + chars[1] + chars[7] + parts[1] + chars[9] + chars[5] + chars[7] + chars[1] + chars[1] + chars[7] + parts[1];
            else
                id = "321123568573232457985433";

            //System.out.println(id);

            String[] idArray = id.split("(?!^)");
            values = new int[idArray.length];
            for (int i = 0; i < idArray.length; i++) {
                values[i] = Integer.parseInt(idArray[i]);
            }
            values[17] = values[17]-2;
            values[18] = values[18]-1;
        }


        return values;
    }

    public int getHour(){
        Calendar calendar = Calendar.getInstance();
        String now = calendar.getTime().toString();
        //System.out.println( now );
        String[] date = now.split( " " );
        String time = date[3];
        int hour = Integer.parseInt( time.split( ":" )[0] );
        return hour;
    }

}
