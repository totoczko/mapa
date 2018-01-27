package com.example.martyna.mapa;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Martyna on 2018-01-27.
 */

public class ProximityIntentReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1000;

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        String notificationName = intent.getExtras().getString("name");
        String notificationContent;

        //check if we enter or exit the area
        Boolean entering = intent.getBooleanExtra(key, false);
        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
            notificationContent = "Znalazłeś się w obszarze miejsca \"" + notificationName + "\".";

        }else {
            Log.d(getClass().getSimpleName(), "exiting");
            notificationContent = "Opuściłeś obszar miejsca \"" + notificationName + "\".";
        }

        //display notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification.Builder notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_add_location_black_24dp)
                .setContentTitle("Proximity Alert")
                .setContentText(notificationContent)
                .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

}
