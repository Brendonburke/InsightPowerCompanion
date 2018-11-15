package com.team15.sdp19.insightpowercompanion;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/* Main
* TODO Real-time Graphing
* TODO Add Button Interaction
* TODO Update TextView Field*/
public class MainActivity extends AppCompatActivity {
    private int time = 0;
    private LineGraphSeries<DataPoint> series;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GraphView graph = findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setScrollable(true);
    }

    @Override
    protected void onResume(){
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addPoint();

                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void addPoint() {
        series.appendData(new DataPoint(time++, new Random().nextDouble() * 10d),true,100);
    }
    private double powerVal() {
        return 0.0;
    }
}
    /*double time =-10;
        Queue<Double> y = new ArrayDeque<Double>();

        for(double i=0;i<100;i++){
            if(i>10){
                y.remove();
                y.offer(i);
            }
            else y.offer(i);
        }
        Object[] data = y.toArray();



        for(int x=0; x<11; x++){
            double out = (double)data[x];
            series.appendData(new DataPoint(time,out),true
                    ,100);
            time++;

        }
        graph.addSeries(series);
    }


}*/
