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

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        Button lightSwitch = (Button) v.findViewById(R.id.button1);
        lightSwitch.setOnClickListener(this);


        return v;
    }

    @Override
    public void onResume() {
        final TextView t = getView().findViewById(R.id.text_view_id3);
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                t.setText(Integer.toString(MainActivity.numOutlets));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                           URL url = new URL("http://192.168.1.5:10568");
                           config.setServerURL(url);
                       } catch (MalformedURLException e) {
                           e.printStackTrace();
                       }
                       MainActivity.client.setConfig(config);
                       try {
                           MainActivity.client.execute("toggleLights", MainActivity.noParam);
                       } catch (XmlRpcException e) {
                           e.printStackTrace();
                       }
               }
           }
       });
       lights.start();

    }
}
