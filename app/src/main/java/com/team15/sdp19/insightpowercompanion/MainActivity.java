package com.team15.sdp19.insightpowercompanion;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayDeque;
import java.util.Queue;

/* Main
* TODO Real-time Graphing
* TODO Add Button Interaction
* TODO Update TextView Field*/
public class MainActivity extends AppCompatActivity {
    private LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        double time =-10;
        Queue<Double> y = new ArrayDeque<Double>();

        for(double i=0;i<100;i++){
            if(i>10){
                y.remove();
                y.offer(i);
            }
            else y.offer(i);
        }
        Object[] data = y.toArray();
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-10);
        graph.getViewport().setMaxX(0);
        series = new LineGraphSeries<>();
        for(int x=0; x<11; x++){
            double out = (double)data[x];
            series.appendData(new DataPoint(time,out),true
                    ,100);
            time++;
        }
        graph.addSeries(series);
    }


}
