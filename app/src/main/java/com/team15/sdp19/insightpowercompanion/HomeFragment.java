package com.team15.sdp19.insightpowercompanion;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;




/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    String[] idParam = new String[1];
    String[] idParam2 = new String[1];
    String classified;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView t = v.findViewById(R.id.text_view_id3);
        Button lightSwitch = (Button) v.findViewById(R.id.button1);
        lightSwitch.setOnClickListener(this);
        t.setText(Integer.toString(MainActivity.numOutlets));

        return v;
    }
    public void onResume(){
        super.onResume();

        Thread start = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainActivity.scanOutlet();
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }
            }
        });
        start.start();

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
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    try {
                        URL url = new URL("http://192.168.0.101:10568");
                        config.setServerURL(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    MainActivity.client.setConfig(config);
                    if(idParam.length!=0) {
                        for (int i = 0; i < MainActivity.light.length; i++) {
                            idParam[0] = (String) MainActivity.outletArray[i];
                            try {
                                classified = (String) MainActivity.client.execute("getClassification", idParam);
                            } catch (XmlRpcException e) {
                                e.printStackTrace();
                            }
                            if (classified .equals("Resistive load")) {
                                MainActivity.light[i] = true;
                            }
                            if (classified.equals("Inductive load") || classified.equals("Non-linear load")) {
                                MainActivity.light[i] = false;
                            } else {
                            }

                        }
                    }
                }
            }
        }).start();

    }


    @Override
    public void onClick(final View v){
       Thread lights = new Thread(new Runnable() {
           @Override
           public void run() {
               switch(v.getId()){
                   case R.id.button1:
                       XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                       try {
                           URL url = new URL("http://192.168.0.101:10568");
                           config.setServerURL(url);
                       } catch (MalformedURLException e) {
                           e.printStackTrace();
                       }
                       MainActivity.client.setConfig(config);
                       if(idParam2.length!=0) {
                           for (int i = 0; i < MainActivity.light.length; i++) {
                               idParam2[0] = (String) MainActivity.outletArray[i];
                               if (MainActivity.light[i] == true) {
                                   try {
                                       MainActivity.client.execute("togglePower", idParam2);
                                       MainActivity.client.execute("togglePower", idParam2);
                                   } catch (XmlRpcException e) {
                                       e.printStackTrace();
                                   }
                               } else {
                               }
                           }
                       }
               }
           }
       });
       lights.start();
    }


}
