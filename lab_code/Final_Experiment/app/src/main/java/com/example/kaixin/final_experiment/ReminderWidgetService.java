package com.example.kaixin.final_experiment;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ReminderWidgetService extends RemoteViewsService {
    private static final boolean DB = true;
    private static final String TAG = "ReminderWidgetService";

    private ArrayList<HashMap<String, String>> list_reminder;
    private myDB dbHelper;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        log("onGetViewFactory, intent=" + intent);

        dbHelper = new myDB(this, "final.db", null, 3);//
        list_reminder = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Reminder", null, null, null, null, null, "id desc");
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("what", cursor.getString(cursor.getColumnIndex("title")));
                map.put("when", cursor.getString(cursor.getColumnIndex("time")));
                list_reminder.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return new MyWidgetFactory(getApplicationContext(), intent, list_reminder);
    }


    public static class MyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;
        private ArrayList<HashMap<String, String>> list;

        // 构造
        public MyWidgetFactory(Context context, Intent intent, ArrayList<HashMap<String, String>> list) {
            log("MyWidgetFactory");
            mContext = context;
            this.list = list;

        }

        @Override
        public int getCount() {
            log("getCount");
            return list.size();
            //return mFoods.length;
        }

        @Override
        public long getItemId(int position) {
            log("getItemId");
            return position;
        }

        // 在调用getViewAt的过程中，显示一个LoadingView。
        // 如果return null，那么将会有一个默认的loadingView
        @Override
        public RemoteViews getLoadingView() {
            log("getLoadingView");
            return null;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            log("getViewAt, position=" + position);
            if (position < 0 || position >= getCount()) {
                return null;
            }
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_reminder);
            views.setTextViewText(R.id.tv_what, list.get(position).get("what"));
            views.setTextViewText(R.id.tv_when, list.get(position).get("when"));

            Intent intentclick = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("title", list.get(position).get("what"));
            bundle.putString("date", list.get(position).get("when").substring(0, 10));
            bundle.putString("time", list.get(position).get("when").substring(11, list.get(position).get("when").length()));
            intentclick.putExtras(bundle);
            //PendingIntent pendingIntentclick = PendingIntent.getActivity(mContext, 0, intentclick, 0);
            views.setOnClickFillInIntent(R.id.list_layout, intentclick);

            //views.setTextViewText(R.id.tv_what, mFoods[position]);
            //views.setTextViewText(R.id.tv_when, mFoods[position]);
            return views;
        }

        @Override
        public int getViewTypeCount() {
            log("getViewTypeCount");
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            log("hasStableIds");
            return true;
        }

        @Override
        public void onCreate() {
            log("onCreate");
        }

        @Override
        public void onDataSetChanged() {
            log("onDataSetChanged");

        }

        @Override
        public void onDestroy() {
            log("onDestroy");
        }
    }

    private static void log(String log) {
        if (DB)
            Log.d(TAG, log);
    }
}
