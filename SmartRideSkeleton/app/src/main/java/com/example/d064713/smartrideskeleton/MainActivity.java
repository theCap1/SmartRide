package com.example.d064713.smartrideskeleton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, StationenAsyncTask.AsyncResponse {

    ConstraintLayout mainScreen;

    AutoCompleteTextView Suchfeld;
    Button clearButton;
    TextView label;
    //map containing station and id value pairs
    HashMap<String, String> Stationen;
    //ArrayList containing station names
    ArrayList<String> Stationsnamen;
    //ArrayList containing search history
    ArrayList<String> Historie;
    //ArrayAdapter for filling suggestions
    ArrayAdapter<String> SuggestionsAdapter;
    //ListView containing departures
    ListView Stationsliste;
    //ArrayList containing departures data
    ArrayList<Bahn> Bahnen;
    ArrayAdapter<Bahn> BahnAdapter;

    LinearLayout layout;

    @Override
    protected void onStop() {
        super.onStop();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < Historie.size(); i++){
            stringBuilder.append(Historie.get(i)).append(";");
        }
        SharedPreferences prefs= getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor e=prefs.edit();
        e.putString("Suchhistorie", stringBuilder.toString());
        e.commit();
    }

    //TODO: remove empty elemet from front of the list

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs= getPreferences(MODE_PRIVATE);
        String HistorieString=prefs.getString("Suchhistorie", "");
        String[] HistorieArray = HistorieString.split(";");
        //System.out.println("historia erreay: " + Arrays.toString(HistorieArray));
        Historie = new ArrayList<String>(Arrays.asList(HistorieArray));
        SuggestionsAdapter.addAll(Historie);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainScreen = findViewById(R.id.constraintLayout);
        mainScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Suchfeld.clearFocus();
                hideKeyboard();
                return false;
            }
        });
        //retrieve stations from API - for providing suggestions
        getStations();
        Suchfeld = findViewById(R.id.searchField);
        SuggestionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        Suchfeld.setAdapter(SuggestionsAdapter);
        //action listener - upon choosing suggestion from popup (under search field)
        //implementation in onItemClick
        Suchfeld.setOnItemClickListener(this);
        //button listener - upon pressing the search button on keyboard
        Suchfeld.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String station = Suchfeld.getText().toString();
                    getConnections(station);
                    return true;
                }
                return false;
            }
        });
        //focus change listener - show suggestion list of search history upon selecting the text view
        Suchfeld.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    Suchfeld.setText("");
                    Suchfeld.showDropDown();
                }
            }
        });
        //text change listener - detects changes in input field (search)
        Suchfeld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int beforeCharCount, int afterCharCount) {
                //clear button only appears if there is at least one character in the search field
                if(charSequence.length()==1){
                    clearButton.setVisibility(View.VISIBLE);
                    SuggestionsAdapter.clear();
                    SuggestionsAdapter.addAll(Stationsnamen);
                }else if(charSequence.length()==0){
                    clearButton.setVisibility(View.GONE);
                    SuggestionsAdapter.clear();
                    SuggestionsAdapter.addAll(Historie);
                }
                //if a deletion has been made
                if(afterCharCount < beforeCharCount){
                    Stationsliste.setVisibility(View.GONE);
                    label.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        clearButton = findViewById(R.id.clearButton);
        label = findViewById(R.id.label);
        Historie = new ArrayList<String>();
        //initialize ArrayList holding departures
        Bahnen = new ArrayList<Bahn>();
        //custom array adapter to allow editing of text color for Ampelsystem
        BahnAdapter = new ArrayAdapter<Bahn>(this, android.R.layout.simple_list_item_1, Bahnen){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                //the Bahn in a list element at given position
                Bahn connection = getItem(position);

                //the listview element
                View view = super.getView(position, convertView, parent);
                //the textview within the listview element
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                //Bahn-related data
                String connectionInfo = connection.toString();
                //Ampelsystem with unicode characters
                connectionInfo = connectionInfo.replace("Auslastung: 0","<font color='#00cc00'>\u2b24</font>");
                connectionInfo = connectionInfo.replace("Auslastung: 1","<font color='#ffcc00'>\u2b24</font>");
                connectionInfo = connectionInfo.replace("Auslastung: 2","<font color='#ff3300'>\u2b24</font>");

                tv.setText(Html.fromHtml(connectionInfo));
                //accessing vehicle icons
                ImageSpan tramIcon = new ImageSpan(getContext(), R.drawable.ic_tram_black_24dp);
                ImageSpan busIcon = new ImageSpan(getContext(), R.drawable.ic_directions_bus_black_24dp);
                //spannablestring to instert vehicle icons to list elements
                SpannableString formattedText = new SpannableString(tv.getText());
                //place icon in place of @ symbol
                int start = connectionInfo.indexOf("@");
                //choose the right icon
                if(connection.Verkehrsmittel.equals("STRAB")){
                    formattedText.setSpan(tramIcon, start, start+1, 0);
                }else{
                    formattedText.setSpan(busIcon, start, start+1, 0);
                }
                //display the formatted text in the list view
                tv.setText(formattedText);

                return view;
            }
        };
        Stationsliste = findViewById(R.id.stationList);
        Stationsliste.setAdapter(BahnAdapter);
        Stationsliste.setOnItemClickListener(this);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
    }

    //list item listener
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int aPosition, long l) {
        //upon choosing suggestion from popup (under search field)
        if(adapterView.getAdapter() == Suchfeld.getAdapter()){
            String station = SuggestionsAdapter.getItem(aPosition);
            getConnections(station);
        //upon choosing an element from departures list
        }else if(adapterView.getAdapter() == Stationsliste.getAdapter()){
            getStatistics();
        }
    }

    //calls API for station names from rnv database
    public void getStations(){
        StationenAsyncTask cloud1 = new StationenAsyncTask(Stationen);
        //for handling callback function to pass values from AsyncTask to MainActivity
        cloud1.delegate = this;
        cloud1.execute("http://rnv.the-agent-factory.de:8080/easygo2/api/regions/rnv/modules/stations/packages/1");
    }

    //fills Stationen map and Stationsnamen array with station names from rnv database (callback function)
    @Override
    public void processFinish(HashMap<String, String> output, ArrayList<String> outputKeys){
        Stationen = output;
        Stationsnamen = outputKeys;
        //loads connections for last station in the search history
        getConnections(Historie.get(0));
    }

    //loads names of all connections departing from given station from RNV database
    public void getConnections(String station){
        label.setVisibility(View.VISIBLE);
        label.setText("Nächste Verbindungen:");
        MyAsyncTask cloud2 = new MyAsyncTask(Bahnen, BahnAdapter, Stationsliste);
        //all keys are lower case to enable case insensitivity in case of manual user input for station name
        //convert to lower case to match lower case keys
        String HaltestellenID = Stationen.get(station.toLowerCase());
        if(HaltestellenID != null) {
            //in case of valid station name: add station to search history
            addStationToHistorie(station);
            Stationsliste.setVisibility(View.VISIBLE);
            String urlEncodedHaltestellenID = null;
            try {
                urlEncodedHaltestellenID = URLEncoder.encode(HaltestellenID, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            cloud2.execute("http://rnv.the-agent-factory.de:8080/easygo2/api/regions/rnv/modules/stationmonitor/element?hafasID=" + urlEncodedHaltestellenID + "&time=null");
        }else{
            label.setVisibility(View.VISIBLE);
            label.setText("Keine Ergebnisse");
            Bahnen.clear();
            BahnAdapter.notifyDataSetChanged();
        }
        clearButton.setVisibility(View.VISIBLE);
        Suchfeld.setText(station);
        Suchfeld.clearFocus();
        hideKeyboard();
    }

    //add station name to search history upon successful retrieval from map
    public void addStationToHistorie(String station) {
        //check if station in contained in search history (toLowercase for case insensitivity)
        for (int k = 0; k < Historie.size(); k++) {
            if (Historie.get(k).toLowerCase().equals(station.toLowerCase())) {
                Historie.remove(k);
            }
        }
        //add to front of the list to display latest search first
        Historie.add(0, station);
    }

    //popup window containing statistics
    //TODO: statistics popup implementation here
    public void getStatistics(){
        Intent intent = new Intent(  MainActivity.this, Stats.class );

        //generate tourIDs to create Statistics
        String tourId = null;
        switch (new Random().nextInt(3)){
            case 0:
                tourId = "17942-10301-1";
                break;
            case 1:
                tourId = "18653-10301-1";
                break;
            case 2:
                tourId = "19137-10301-1";
                break;
            default:
                tourId = "19374-33501-1";
                break;
        }
        intent.putExtra("tourId", tourId); //handover tourIdx

        startActivity( intent );
    }

    //hide keyboard
    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    //delete button listener
    public void onDelete(View aView){
        //delete text from search field
        Suchfeld.setText("");
        //hide list of stations
        Stationsliste.setVisibility(View.GONE);
        //hide delete button (x from search field)
        label.setVisibility(View.GONE);
        //focus returns to search field after pressing the button
        Suchfeld.requestFocus();
        //keyboard shows up after focusing on search field
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(Suchfeld, InputMethodManager.SHOW_IMPLICIT);
    }

    public void onSearch(View aView){
        String station = Suchfeld.getText().toString();
        getConnections(station);
    }

}
