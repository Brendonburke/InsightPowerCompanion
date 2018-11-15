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

        double time = 0;
        Queue<Double> y = new ArrayDeque<Double>();
        Object[] data = y.toArray();
        for(double i=0;i<12
                ;i++){
            y.offer(i);
        }
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-10);
        graph.getViewport().setMaxX(0);
        series = new LineGraphSeries<>();
        for(time=-11; time<1; time++){
            double out = y.poll();
            series.appendData(new DataPoint(time,out),true
                    ,100);

        }
        graph.addSeries(series);
    }


}
