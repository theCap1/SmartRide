package com.example.d064713.smartrideskeleton;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D064713 on 01.06.2018.
 */

public class StationenAsyncTask extends AsyncTask<String, Integer, String> {

    HashMap Stationsliste;
    AsyncResponse delegate = null;

    //for callback function
    public interface AsyncResponse{
        void processFinish(HashMap<String, String> output, ArrayList<String> outputKeys);
    }

    public StationenAsyncTask(HashMap aStationsListe){
        Stationsliste = aStationsListe;
    }

    //wird in Activity Thread ausgeführt
    @Override
    protected void onPostExecute(String s) {
        //System.out.println("onPostExecute gets: " + s);
        try {

            Stationsliste = new HashMap<String, String>();
            ArrayList<String> Stationsnamen = new ArrayList<String>();
            JSONObject rnvHaltestellenPaket = new JSONObject(s);
            JSONArray rnvHaltestellen = (JSONArray)rnvHaltestellenPaket.get("stations");
            //store station name and id key value pairs in hashmap
            for(int i = 0; i < rnvHaltestellen.length(); i++){
                String Stationsname = String.valueOf(rnvHaltestellen.getJSONObject(i).get("longName"));
                String StationsID = String.valueOf(rnvHaltestellen.getJSONObject(i).get("hafasID"));
                //all keys are lower case to enable case insensitivity in case of manual user input for station name
                Stationsliste.put(Stationsname.toLowerCase(), StationsID);
                Stationsnamen.add(Stationsname);
            }
            //System.out.println("Stationsnamen are " + Stationsnamen);
            //execute callback function to pass hashmap and station names to MainActivity
            delegate.processFinish(Stationsliste, Stationsnamen);

        } catch (JSONException e) {
            e.printStackTrace();
            delegate.processFinish(new HashMap<String, String>(), new ArrayList<String>());
        }
    }

    //wird in Cloud Thread ausgeführt
    @Override
    protected String doInBackground(String... urls) {

        String stringUrl = urls[0];
        String result = "";
        String inputLine;

        try {
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("RNV_API_TOKEN", "81hv7puf457n36jtfidspum0si");

            connection.connect();

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

            reader.close();
            streamReader.close();

            result = stringBuilder.toString();

        }  catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}

