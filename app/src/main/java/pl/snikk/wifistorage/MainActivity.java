package pl.snikk.wifistorage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private boolean isRunning = false;
    private int port = 8080;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isServiceRunning(ServerService.class))
        {
            Intent i = new Intent(this, ServerService.class);
            this.startService(i);
        }

        RelativeLayout bConnect = findViewById(R.id.relativeStart);
        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void connect(){
        RelativeLayout bConnect = findViewById(R.id.relativeStart);
        TextView txtStart = findViewById(R.id.startDesc);
        if (!isRunning) {
            Intent intent = new Intent("StartStopService");
            intent.putExtra("action", "start");
            intent.putExtra("port", port);

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            isRunning = true;
            txtStart.setText(R.string.stop);
            new CheckPublicIp().execute();
            TextView txtLocalIp = (TextView) findViewById(R.id.tvLocalIp);
            txtLocalIp.setText(getLocalIpAddress()+":"+String.valueOf(port));

        } else {
            Intent intent = new Intent("StartStopService");
            intent.putExtra("action", "stop");

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            isRunning = false;
            txtStart.setText(R.string.server_status_launch);
        }
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private class CheckPublicIp extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://bot.whatismyipaddress.com");
                URLConnection urlconnection = url.openConnection();
                InputStream in = new BufferedInputStream(urlconnection.getInputStream());
                try {
                    String str = "";
                    int numByte = in.available();
                    byte[] buf = new byte[numByte];
                    in.read(buf);
                    for (byte b:buf) {
                        str += (char)b;
                    }
                    return str;
                }
                finally {
                    in.close();
                }

            }
            catch (Exception e){
            }
            return "Error";
        }

        @Override
        protected void onPostExecute(String ip) {
            //TextView txtIp = (TextView) findViewById(R.id.tvPublicIp);
            //txtIp.setText(ip+":"+String.valueOf(port));
        }
    }
}
