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
    int apparentMax=60;
    private LineGraphSeries<DataPoint> apparentSeries;
    private LineGraphSeries<DataPoint> activeSeries;
    public ArrayList apparentComm = new ArrayList<String>();
    public ArrayList activeComm = new ArrayList<String>();
    int time = 0;
    View v;
    String classification;
    String[] idParam = new String[1];
    String[] apparentParam = new String[2];
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

        apparentParam[0] = idParam[0];
        apparentParam[1] = "apt";

        activeParam[0] = idParam[0];
        activeParam[1] = "act";



        graph = v.findViewById(R.id.graph);
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time (Seconds)");
        gridLabel.setVerticalAxisTitle("Apparent Power (VA)");


        //Apparent power series
        apparentSeries = new LineGraphSeries<>();
        graph.addSeries(apparentSeries);

        //Active power series
        activeSeries = new LineGraphSeries<>();
        graph.getSecondScale().addSeries(activeSeries);

        gridLabel.setVerticalLabelsColor(Color.BLUE);
        gridLabel.setVerticalAxisTitleColor(Color.BLUE);
        apparentSeries.setColor(Color.BLUE);

        gridLabel.setVerticalLabelsSecondScaleColor(Color.RED);
        activeSeries.setColor(Color.RED);
        graph.getSecondScale().setVerticalAxisTitle("Active Power (W)");
        //left Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(60);

        //right Y bounds
        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(60);

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
                            URL url = new URL("http://192.168.1.19:10568");
                            config.setServerURL(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        try {

                            MainActivity.client.execute("togglePower", apparentParam);
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
                                if(apparentComm.isEmpty() == false && activeComm.isEmpty() == false ) {
                                    Double apparentPoint = (Double) apparentComm.get(0);
                                    Double activePoint = (Double) activeComm.get(0);
                                    addPoint(apparentPoint,activePoint);
                                    apparentComm.remove(0);
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
                    URL url = new URL("http://192.168.1.19:10568");
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
                    Double apparentIn = 0.0;
                    Double activeIn = 0.0;

                    try {

                        apparentIn = (Double)MainActivity.client.execute("getPoint", apparentParam);
                    } catch (XmlRpcException e1) {
                        e1.printStackTrace();
                    }
                    if(apparentIn != -1.0 && apparentIn != null) {
                        apparentComm.add(apparentIn);
                    }

                    try {

                        activeIn = (Double)MainActivity.client.execute("getPoint", activeParam);
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

    private void addPoint(Double apparent, Double active) throws IOException {
        double nextApparent = new Double(apparent);
        double nextActive = new Double(active);

        if(nextApparent>apparentMax) {
            apparentMax = (int) nextApparent;
            graph.getViewport().setMaxY(apparentMax+20);
            graph.getSecondScale().setMaxY(apparentMax+20);
        }

        apparentSeries.appendData(new DataPoint(time++, nextApparent ), false, 100);
        activeSeries.appendData(new DataPoint(time, nextActive ), false, 100);
        if(time>= 60){
            graph.getViewport().setMinX(start++);
            graph.getViewport().setMaxX(end++);
        }
        DecimalFormat roundOut = new DecimalFormat("#.###"); //Limit decimal places
        TextView apttv =  getView().findViewById(R.id.GraphText2);
        apttv.setText(roundOut.format(nextApparent ) + "VA");
        TextView acttv = getView().findViewById(R.id.GraphText4);
        acttv.setText(roundOut.format(nextActive) + "W");
        TextView type = getView().findViewById(R.id.Classification);
        type.setText(classification);

    }
}
