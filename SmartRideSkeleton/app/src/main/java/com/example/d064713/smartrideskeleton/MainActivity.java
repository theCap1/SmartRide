package com.example.d064713.smartrideskeleton;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    ConstraintLayout mainScreen;

    AutoCompleteTextView Suchfeld;
    String[] Stationen;
    ArrayAdapter<String> StationAdapter;
    ListView Stationsliste;
    ArrayList<Bahn> Bahnen;
    ArrayAdapter<Bahn> BahnAdapter;

    PopupWindow stats;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainScreen = findViewById(R.id.constraintLayout);

        Stationen = getStations();
        StationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Stationen);
        Suchfeld = findViewById(R.id.searchField);
        Suchfeld.setAdapter(StationAdapter);
        Suchfeld.setOnItemClickListener(this);
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
        Bahnen = new ArrayList<Bahn>();
        BahnAdapter = new ArrayAdapter<Bahn>(this, android.R.layout.simple_list_item_1, Bahnen);
        Stationsliste = findViewById(R.id.stationList);
        Stationsliste.setAdapter(BahnAdapter);
        Stationsliste.setOnItemClickListener(this);
        stats = new PopupWindow(this);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        stats.setContentView(layout);
    }

    //list item listener
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int aPosition, long l) {
        if(adapterView.getAdapter() == Suchfeld.getAdapter()){
            String station = StationAdapter.getItem(aPosition);
            getConnections(station);
        }else if(adapterView.getAdapter() == Stationsliste.getAdapter()){
            getStatistics();
        }
    }

    //loads names of all stations from RNV database
    //TODO: API call implementation here - load names of all stations
    public String[] getStations(){
        return new String[] {
                "Mannheim Hauptbahnhof", "Mannheim Hauptfriedhof", "Marktplatz", "Markuskirche", "Meistersingerstraße", "Freiheitsplatz"
        };
    }

    //loads names of all connections departing from given station from RNV database
    //TODO: API call implementation here - load connections from selected station
    public void getConnections(String station){
        //Examples
        Bahnen.clear();
        Bahnen.add(new Bahn("5 Heidelberg", Calendar.getInstance().getTime()));
        Bahnen.add(new Bahn("12A Oststadt", Calendar.getInstance().getTime()));
        Bahnen.add(new Bahn("35 Neckarau West", Calendar.getInstance().getTime()));
        BahnAdapter.notifyDataSetChanged();
        Suchfeld.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    //popup window containing statistics
    //TODO: statistics popup implementation here
    public void getStatistics(){
        stats.showAtLocation(layout, Gravity.CENTER, 10, 10);
        stats.update(0, 0, 800, 300);
    }

}
