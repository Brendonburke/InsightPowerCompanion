package com.team15.sdp19.insightpowercompanion;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import java.net.MalformedURLException;
import java.net.URL;

/* Main
* TODO Real-time Graphing: DONE
* TODO Add Button Interaction
* TODO Update TextView Field: DONE
* TODO Raspberry PI communication*/
public class MainActivity extends AppCompatActivity {
    public static int numOutlets = 0;
    public static Object[] noParam;
    public static int outletId;
    public static Object[] outletArray;
    public static XmlRpcClient client = new XmlRpcClient();
    static Toolbar toolbar;
   // static DrawerLayout drawerLayout;
    FragmentTransaction fragmentTransaction;
    static NavigationView navigationView;
    static int homeId = R.id.home_id;
    static int scanId = R.id.scan_id;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new HomeFragment());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Home");
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.home_id:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new HomeFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Home");
                        menuItem.setChecked(true);
                        break;

                        case R.id.scan_id :
                            Thread scan = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        scanOutlet();
                                    } catch (XmlRpcException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            scan.start();
                        break;
                        default:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new GraphFragment());
                        fragmentTransaction.commit();
                        outletId = menuItem.getItemId();
                        getSupportActionBar().setTitle("Outlet " + Integer.toString(outletId+1));
                        menuItem.setChecked(true);
                        break;

                }


                return false;
            }
        });


    }


    public static void scanOutlet() throws XmlRpcException {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = new URL("http://192.168.1.5:10568");
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        client.setConfig(config);
        outletArray = (Object[]) client.execute("getOutlets", noParam);
        numOutlets = outletArray.length;
        newMenuOptions(numOutlets);
    }

    public static void newMenuOptions(int x){
       Menu menu = navigationView.getMenu();
       menu.clear();
       menu.add(0,homeId,0,"Home");
        menu.add(0,scanId,0,"Scan for Outlets");
        for(int i=0; i<x; i++){
        menu.add(0,i,0, "Outlet "+Integer.toString(i+1));
        }
    }

}

