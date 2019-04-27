package com.team15.sdp19.insightpowercompanion;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment implements View.OnClickListener {
    double reactiveMax=200.0;
    private LineGraphSeries<DataPoint> reactiveSeries;
    private LineGraphSeries<DataPoint> activeSeries;
    public ArrayList reactiveComm = new ArrayList<String>();
    public ArrayList activeComm = new ArrayList<String>();
    int time = 0;
    View v;
    String classification;
    String[] idParam = new String[1];
    String[] reactiveParam = new String[2];
    String[] activeParam = new String[2];
    private Activity activity;
    GraphView graph;
    int start =0;
    int end =60;

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_graph, container, false);
        Button on_off = v.findViewById(R.id.button);
        on_off.setOnClickListener(this);

        idParam[0] = (String) MainActivity.outletArray[MainActivity.outletId];

        reactiveParam[0] = idParam[0];
        reactiveParam[1] = "rea";

        activeParam[0] = idParam[0];
        activeParam[1] = "act";



        graph = v.findViewById(R.id.graph);
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time (Seconds)");
        gridLabel.setVerticalAxisTitle("Reactive Power (VAR)");


        //Reactive power series
        reactiveSeries = new LineGraphSeries<>();
        graph.addSeries(reactiveSeries);

        //Active power series
        activeSeries = new LineGraphSeries<>();
        graph.getSecondScale().addSeries(activeSeries);

        gridLabel.setVerticalLabelsColor(Color.BLUE);
        gridLabel.setVerticalAxisTitleColor(Color.BLUE);
        reactiveSeries.setColor(Color.BLUE);

        gridLabel.setVerticalLabelsSecondScaleColor(Color.RED);
        activeSeries.setColor(Color.RED);
        graph.getSecondScale().setVerticalAxisTitle("Active Power (W)");
        //left Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(200);

        //right Y bounds
        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(200);

        //X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(60);
        graph.getViewport().setScrollable(true);
        return v;
    }

    @Override
    public void onClick(final View v){
        Thread power = new Thread(new Runnable() {
            @Override
            public void run() {
                switch(v.getId()){
                    case R.id.button:
                        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                        try {
                            URL url = new URL("http://192.168.0.5:10568");
                            config.setServerURL(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        try {

                            MainActivity.client.execute("togglePower", idParam);
                        } catch (XmlRpcException e) {
                            e.printStackTrace();
                        }
                }
            }
        });
        power.start();
    }

    @Override
    public void onResume(){
        super.onResume();


        new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(getActivity()== null){
                        break;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(reactiveComm.isEmpty() == false && activeComm.isEmpty() == false ) {
                                    Double reactivePoint = (Double) reactiveComm.get(0);
                                    Double activePoint = (Double) activeComm.get(0);
                                    addPoint(reactivePoint,activePoint);
                                    reactiveComm.remove(0);
                                    activeComm.remove(0);
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
                    URL url = new URL("http://192.168.0.5:10568");
                    config.setServerURL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                MainActivity.client.setConfig(config);
                while(true) {
                    if(getActivity() == null){
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Double reactiveIn = 0.0;
                    Double activeIn = 0.0;

                    try {

                        reactiveIn = (Double)  MainActivity.client.execute("getPoint", reactiveParam);
                    } catch (XmlRpcException e1) {
                        e1.printStackTrace();
                    }
                    if(reactiveIn != -1.0 && reactiveIn != null) {
                        reactiveComm.add(reactiveIn);
                    }

                    try {

                        activeIn = (Double) MainActivity.client.execute("getPoint", activeParam);
                    } catch (XmlRpcException e1) {
                        e1.printStackTrace();
                    }

                    if(activeIn != -1.0 && activeIn != null) {
                        activeComm.add(activeIn);
                    }

                    try {
                        classification = (String) MainActivity.client.execute("getClassification",idParam);
                    } catch (XmlRpcException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    private void addPoint(Double reactive, Double active) throws IOException {
        double nextReactive = new Double(reactive);
        double nextActive = new Double(active);

        if(nextReactive>reactiveMax) {
            reactiveMax = (int) nextReactive;
            graph.getViewport().setMaxY(reactiveMax+20);
            graph.getSecondScale().setMaxY(reactiveMax+20);
        }

        reactiveSeries.appendData(new DataPoint(time, nextReactive ), false, 100);
        activeSeries.appendData(new DataPoint(time, nextActive ), false, 100);
        time++;
        if(time>= 60){
            graph.getViewport().setMinX(start++);
            graph.getViewport().setMaxX(end++);
        }
        DecimalFormat roundOut = new DecimalFormat("#.###"); //Limit decimal places
        TextView apttv =  getView().findViewById(R.id.GraphText2);
        apttv.setText(roundOut.format(nextReactive ) + "VAR");
        TextView acttv = getView().findViewById(R.id.GraphText4);
        acttv.setText(roundOut.format(nextActive) + "W");
        TextView type = getView().findViewById(R.id.Classification);
        type.setText(classification);

    }
}
