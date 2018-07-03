package com.example.d064713.smartrideskeleton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Calendar;

/**
 * Created by Sven on 11.06.18.
 */

public class  Stats extends Activity {

    GraphView stats;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setTitle( "Statistische Auslastung dieser Verbindung" );

        setContentView( R.layout.statspopup );

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( dm );

        //fit Statistics to Screensize
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        stats = findViewById( R.id.stats );
        getWindow().setLayout( (int)(displayWidth*.95) , (int)(displayHeight*.32) );

        //get TourId from MainActivity and convert it to statistics
        Intent intent = getIntent();
        String tourId = intent.getStringExtra( "tourId" );
        String stationName = intent.getStringExtra( "stationName" );
        int[] values = getStatistics( tourId );

        //generate GraphData

        BarGraphSeries<DataPoint> series = null;

        //System.out.println("station = " + stationName );
        if (stationName.equals( "Paradeplatz" ) || stationName.equals( "Duale Hochschule (MA)" )) {
            //System.out.println("Wir befinden uns im ersten Teil des If-Statements");
            series = new BarGraphSeries<>( new DataPoint[]{
                    new DataPoint( 0, 3 ),
                    new DataPoint( 1, 2 ),
                    new DataPoint( 2, 1 ),
                    new DataPoint( 3, 1 ),
                    new DataPoint( 4, 2 ),
                    new DataPoint( 5, 3 ),
                    new DataPoint( 6, 5 ),
                    new DataPoint( 7, 6 ),
                    new DataPoint( 8, 8 ),
                    new DataPoint( 9, 5 ),
                    new DataPoint( 10, 7 ),
                    new DataPoint( 11, 3 ),
                    new DataPoint( 12, 2 ),
                    new DataPoint( 13, 3 ),
                    new DataPoint( 14, 2 ),
                    new DataPoint( 15, 4 ),
                    new DataPoint( 16, 5 ),
                    new DataPoint( 17, 7 ),
                    new DataPoint( 18, 9 ),
                    new DataPoint( 19, 8 ),
                    new DataPoint( 20, 5 ),
                    new DataPoint( 21, 4 ),
                    new DataPoint( 22, 3 ),
                    new DataPoint( 23, 3 ),
            } );
        } else{
            //System.out.println("Wir befinden uns im zweiten Teil des If-Statements");
            series = new BarGraphSeries<>( new DataPoint[]{
                    new DataPoint( 0, values[0] ),
                    new DataPoint( 1, values[1] ),
                    new DataPoint( 2, values[2] ),
                    new DataPoint( 3, values[3] ),
                    new DataPoint( 4, values[4] ),
                    new DataPoint( 5, values[5] ),
                    new DataPoint( 6, values[6] ),
                    new DataPoint( 7, values[7] ),
                    new DataPoint( 8, values[8] ),
                    new DataPoint( 9, values[9] ),
                    new DataPoint( 10, values[10] ),
                    new DataPoint( 11, values[11] ),
                    new DataPoint( 12, values[12] ),
                    new DataPoint( 13, values[13] ),
                    new DataPoint( 14, values[14] ),
                    new DataPoint( 15, values[15] ),
                    new DataPoint( 16, values[16] ),
                    new DataPoint( 17, values[17] ),
                    new DataPoint( 18, values[18] ),
                    new DataPoint( 19, values[19] ),
                    new DataPoint( 20, values[20] ),
                    new DataPoint( 21, values[21] ),
                    new DataPoint( 22, values[22] ),
                    new DataPoint( 23, values[23] ),
            } );
        }

        stats.addSeries(series);

        //adjust bar size
        series.setSpacing( 50 );

        // set manual Y bounds
        stats.getViewport().setYAxisBoundsManual(true);
        stats.getViewport().setMinY(0);
        stats.getViewport().setMaxY(10);

        //set X-Axis to only see the next 10h based on device time
        Calendar calendar = Calendar.getInstance();
        String now = calendar.getTime().toString();
        //System.out.println( now );
        String[] date = now.split( " " );
        String time = date[3];
        int hour = Integer.parseInt( time.split( ":" )[0] );
        if (hour <= 13) {
            stats.getViewport().setMinX( hour );
            stats.getViewport().setMaxX( hour + 10 );
        }
        else {
            stats.getViewport().setMinX( 13 );
            stats.getViewport().setMaxX( 23 );
        }
        //make statistic scroll- and scalable
        stats.getViewport().setScalable(true); // enables horizontal zooming and scrolling

        // custom label formatter to show " Uhr" on X-Axis values
        stats.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show x values with " Uhr"
                    return super.formatLabel(value, isValueX) + " Uhr";
                } else {
                    // show no y values
                    return "";//super.formatLabel(value, isValueX);
                }
            }
        });
/*
        // use static labels for vertical labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(stats);
        staticLabelsFormatter.setVerticalLabels(new String[] {"gering", "mittel", "hoch"});
        stats.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
*/
        // styling Ampelsystem
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                int color;
                if ((int) data.getY() <= 3){
                    color=Color.rgb(0,255,0);
                }else if ((int) data.getY() <= 6){
                    color=Color.rgb(255,230,0);
                }else{
                    color=Color.rgb(255,0,0);
                }
                return color;
            }
        });

        //add legend to the statistic
        TextView legend = findViewById( R.id.legend );
        legend.setText( Html.fromHtml("<font color='#00cc00'>\u2b24</font>" + " geringe Auslastung " +
                "<font color='#ffe600'>\u2b24</font>" + " mittlere Auslastung " +
                "<font color='#ff0000'>\u2b24</font>" + " hohe Auslastung "));
    }

    //get an partly random Array with 24h statistics for a connection
    public int[] getStatistics(String tourId){
        String[] parts = tourId.split( "-" );
        String[] chars = (tourId.replace("-", "")).split("(?!^)");
        String id = null;
        //System.out.println("Parts[] hat die Länge " + parts.length);
        if (parts.length > 1)
            id = parts[1] + parts[2] + chars[7] + chars[1] + chars[7] + parts[1] + chars[9] + chars[5] + chars[7] + chars[1] + chars[1] + chars[7] + parts[1];
        else
            id = "321123568573232457985433";

        //System.out.println("TourID in getStatistics() = " + id);

        String[] idArray = id.split("(?!^)");
        int[] values = new int[idArray.length];
        for (int i = 0; i < idArray.length; i++) {
            values[i] = Integer.parseInt(idArray[i]);
        }
        values[17] = values[17]-2;
        values[18] = values[18]-1;

        return values;
    }
}
