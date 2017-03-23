package com.example.kaixin.final_experiment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle bundle = intent.getExtras();
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        Intent i = new Intent(context, AddReminder.class);
        Bundle bundle1 = new Bundle();
        bundle1.putString("title", bundle.getString("title"));
        bundle1.putString("date", bundle.getString("date"));
        bundle1.putString("time", bundle.getString("time"));
        //Toast.makeText(context, bundle.getString("title")+bundle.getString("date")+bundle.getString("time"), Toast.LENGTH_SHORT).show();
        i.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("备忘")
                .setContentText(bundle.getString("title"))
                .setTicker("备忘")
                .setLargeIcon(bm)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notificationManager.notify(0, notification);
    }
}
