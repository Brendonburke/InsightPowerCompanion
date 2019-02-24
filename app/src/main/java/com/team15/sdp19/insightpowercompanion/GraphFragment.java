package com.team15.sdp19.insightpowercompanion;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {
    private LineGraphSeries<DataPoint> series;
    public ArrayList comm = new ArrayList<String>();
    int time = 0;
    String classifcation;
    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Button on_off = getView().findViewById(R.id.button);
        on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.client.execute("togglePower", (List) MainActivity.outletArray[MainActivity.outletId]);
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }

            }
        });

        GraphView graph = getView().findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(60);
        graph.getViewport().setScrollable(true);


       return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();


        new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    getActivity().runOnUiThread(new Runnable() {
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
                MainActivity.client.setConfig(config);
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Double in = null;
                    try {
                        in = (Double) MainActivity.client.execute("getPoint", (Object[]) MainActivity.outletArray[MainActivity.outletId]);
                    } catch (XmlRpcException e1) {
                        e1.printStackTrace();
                    }
                    if(in != -1.0 && in != null) {
                        comm.add(in);
                    }
                    try {
                        classifcation = (String) MainActivity.client.execute("getClassification",(Object[]) MainActivity.outletArray[MainActivity.outletId]);
                    } catch (XmlRpcException e) {
                        e.printStackTrace();
                    }
                    TextView type = getView().findViewById(R.id.Classification);
                    type.setText(classifcation);
                }
            }
        }).start();
    }

    private void addPoint(Double point) throws IOException {
        double nextOut = new Double(point);
        series.appendData(new DataPoint(time++, nextOut ), true, 100);
        DecimalFormat roundOut = new DecimalFormat("#.###"); //Limit decimal places
        TextView tv = (TextView) getView().findViewById(R.id.GraphText2);
        tv.setText(roundOut.format(nextOut ) + "W");

    }
}
