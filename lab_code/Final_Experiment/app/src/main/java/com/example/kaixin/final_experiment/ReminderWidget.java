package com.example.kaixin.final_experiment;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class ReminderWidget extends AppWidgetProvider {

    private static final String TAG = "ReminderWidget";
    private static String bc = "com.example.kaixin.final_experiment.reminderreceiver";

    /** package */
    static ComponentName getComponentName(Context context) {
        return new ComponentName(context, ReminderWidget.class);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reminder_widget);
            views.setTextViewText(R.id.appwidget_text, "备忘");
            Intent intentclick = new Intent(context, AddReminder.class);
            //intentclick.setAction();
            PendingIntent pendingIntentclick = PendingIntent.getActivity(context, 0, intentclick, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntentclick);

            Intent intent = new Intent(context, ReminderWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.listview1, intent);

            Intent intentitems = new Intent(context, AddReminder.class);
            PendingIntent pendingIntentitems = PendingIntent.getActivity(context, 0, intentitems, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.listview1, pendingIntentitems);

            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listview1);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(context);
        String action = intent.getAction();
        if (action.equals(bc)) {
            int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(context, ReminderWidget.class));
            for (int appWidgetId : appWidgetIds) {
                Log.d(TAG, "appWidgetId = " + appWidgetId);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reminder_widget);
                views.setTextViewText(R.id.appwidget_text, "备忘");
                Intent intentclick = new Intent(context, AddReminder.class);
                //intentclick.setAction();
                PendingIntent pendingIntentclick = PendingIntent.getActivity(context, 0, intentclick, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.widget_title, pendingIntentclick);

                Intent intentservice = new Intent(context, ReminderWidgetService.class);
                intentservice.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                intentservice.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                views.setRemoteAdapter(R.id.listview1, intentservice);

                Intent intentitems = new Intent(context, AddReminder.class);
                PendingIntent pendingIntentitems = PendingIntent.getActivity(context, 0, intentitems, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setPendingIntentTemplate(R.id.listview1, pendingIntentitems);

                appWidgetManger.updateAppWidget(appWidgetId, views);
                appWidgetManger.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listview1);
            }
        }
    }
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reminder_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

