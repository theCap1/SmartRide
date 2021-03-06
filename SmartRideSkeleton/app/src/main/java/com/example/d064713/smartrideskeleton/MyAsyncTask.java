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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by D064713 on 08.01.2018.
 */

public class MyAsyncTask extends AsyncTask<String, Integer, String> {

    ArrayList<Bahn> mArray;
    ArrayAdapter<Bahn> mAdapter;
    ListView messageFeed;
    String stationName;

    public MyAsyncTask(ArrayList<Bahn> aArray, ArrayAdapter<Bahn> aAdapter, ListView aMessageFeed, String station){
        mArray = aArray;
        mAdapter = aAdapter;
        messageFeed = aMessageFeed;
        stationName = station;
    }

    //wird in Activity Thread ausgeführt
    @Override
    protected void onPostExecute(String s) {
        //System.out.println("onPostExecute gets: " + s);
        try {

            mArray.clear();
            JSONObject rnvJourney = new JSONObject(s);
            JSONArray listOfDepartures = (JSONArray)rnvJourney.get("listOfDepartures");

            //fill list with departure information retrieved from rnv database
            for(int i = 0; i < listOfDepartures.length(); i++){
                String Linie = String.valueOf(listOfDepartures.getJSONObject(i).get("lineLabel"));
                String Ziel = String.valueOf(listOfDepartures.getJSONObject(i).get("direction"));
                String Zeit = String.valueOf(listOfDepartures.getJSONObject(i).get("time"));
                String tourId = String.valueOf( listOfDepartures.getJSONObject( i ).get( "tourId" ) );
                //split departure time and delay
                String ZeitUndVerspaetung [] = Zeit.split("\\+");
                String Verkehrsmittel = String.valueOf(listOfDepartures.getJSONObject(i).get("transportation"));
                //check if delay information is available
                if(ZeitUndVerspaetung.length>1){
                    mArray.add(new Bahn(tourId, Linie, Ziel, ZeitUndVerspaetung[0], ZeitUndVerspaetung[1], Verkehrsmittel, stationName));
                }else{
                    mArray.add(new Bahn(tourId, Linie, Ziel, ZeitUndVerspaetung[0], "0", Verkehrsmittel, stationName));
                }
            }

            mAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
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

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return result;
    }
}

