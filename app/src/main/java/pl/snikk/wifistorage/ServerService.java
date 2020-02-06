package pl.snikk.wifistorage;

import java.io.IOException;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ServerService extends Service {

    private Server server;
    private Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("StartStopService"));
        context = this;
        return Service.START_NOT_STICKY;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (intent.hasExtra("extra"))
                server.stop();
            if (intent.getStringExtra("action").equals("start")) {
                server = new Server(intent.getIntExtra("port", 8080), context, intent.getStringExtra("password"));
                try {
                    server.start();
                    Intent notificationIntent = new Intent(context,
                            MainActivity.class);
                    Notification notification = new NotificationCompat.Builder(
                            context)
                            .setContentTitle(getString(R.string.app_name))
                            .setTicker(getString(R.string.app_name))
                            .setContentText(getString(R.string.server_start)+" "+getString(R.string.online))
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentIntent(
                                    PendingIntent.getActivity(context, 0,
                                            notificationIntent, 0))
                            .setOngoing(true).build();
                    startForeground(100, notification);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (intent.getStringExtra("action").equals("stop")) {
                server.stop();
                stopForeground(true);
            }
        }
    };
}

