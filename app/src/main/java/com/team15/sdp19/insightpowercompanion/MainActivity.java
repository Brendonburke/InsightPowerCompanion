package com.team15.sdp19.insightpowercompanion;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
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
    Random out = new Random();
    Socket sock;
    InputStream in;
    //String out;
    private int time = 0;
    private LineGraphSeries<DataPoint> series;
    XmlRpcClient client = new XmlRpcClient();
    Object[] param = new Object[0];
    public Handler m = new Handler();
    public ArrayList comm = new ArrayList<String>();
    public MainActivity() throws IOException {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button on_off = findViewById(R.id.button);
        on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    client.execute("togglePower",param);
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }

            }
        });
        
        GraphView graph = findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(60);
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
                                if(comm.isEmpty() == false) {
                                    Double point = (Double) comm.get(0);
                                    addPoint(point);
                                    comm.remove(0);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {

                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                try {
                    URL url = new URL("http://192.168.1.5:10568");
                    config.setServerURL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                client.setConfig(config);
                while(true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Double in = null;
                    try {
                        in = (Double) client.execute("getPoint", param);
                    } catch (XmlRpcException e1) {
                        e1.printStackTrace();
                    }
                    if(in != -1.0 && in != null) {
                        comm.add(in);
                    }

                }
            }
        }).start();
    }

    private void addPoint(Double point) throws IOException {
        double nextOut = new Double(point);
        series.appendData(new DataPoint(time++, nextOut ), true, 100);
        DecimalFormat roundOut = new DecimalFormat("#.###"); //Limit decimal places
        TextView tv = (TextView) findViewById(R.id.text);
        tv.setText(roundOut.format(nextOut ) + "W");

    }


}

