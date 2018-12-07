package com.team15.sdp19.insightpowercompanion;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/* Main
* TODO Real-time Graphing: DONE
* TODO Add Button Interaction
* TODO Update TextView Field: DONE
* TODO Raspberry PI communication*/
public class MainActivity extends AppCompatActivity {
    //Random out = new Random();
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
                            try {
                                addPoint();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


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
    private void addPoint() throws IOException {
        Socket sock = new Socket("192.168.1.3", 10568);
        InputStream in = sock.getInputStream();
        String out = streamToString(in);
        sock.close();
        double nextOut = Double.parseDouble(out);
        series.appendData(new DataPoint(time++, nextOut * 10d), true, 100);
        DecimalFormat roundOut = new DecimalFormat("#.###"); //Limit decimal places
        TextView tv = (TextView) findViewById(R.id.text);
        tv.setText(roundOut.format(nextOut * 10d) + "W");

    }

   /* public static void main(String args[]) throws IOException {
        Socket sock = new Socket("192.168.1.3", 10568);
        System.out.println("Connected");
        while (true) {
            InputStream in = sock.getInputStream();
            String out = streamToString(in);
            System.out.println(out);

        }*/

    private static String streamToString(InputStream is){
        Scanner s = new Scanner(is,"UTF-8").useDelimiter("\n");
        return s.hasNext() ? s.next() : "0";
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
