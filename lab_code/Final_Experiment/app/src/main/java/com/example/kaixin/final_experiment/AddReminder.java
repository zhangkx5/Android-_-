package com.example.kaixin.final_experiment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kaixin on 2016/12/1.
 */

public class AddReminder extends AppCompatActivity{

    private EditText et_title;
    private TextView tv_date, tv_time;
    private ImageButton ib_back, ib_save;
    private myDB dbHelper;
    private AlarmManager alarmManager;
    private static String bc = "com.example.kaixin.final_experiment.reminderreceiver";
    private int id;

    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.add_reminder_toolbar);
        setSupportActionBar(myToolbar);

        dbHelper = new myDB(this, "final.db", null, 3);

        et_title = (EditText)findViewById(R.id.title);
        tv_date = (TextView)findViewById(R.id.date);
        tv_time = (TextView)findViewById(R.id.time);
        ib_back = (ImageButton) findViewById(R.id.add_reminder_back);
        ib_save = (ImageButton)findViewById(R.id.add_reminder_done);
        bundle = this.getIntent().getExtras();
        if (bundle != null) {
            et_title.setText(bundle.getString("title"));
            tv_date.setText(bundle.getString("date"));
            tv_time.setText(bundle.getString("time"));
            //Toast.makeText(AddReminder.this, "YYY", Toast.LENGTH_SHORT).show();
        }
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddReminder.this.finish();
            }
        });
        ib_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_title.getText().toString())) {
                    Toast.makeText(AddReminder.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tv_date.getText().toString())) {
                    Toast.makeText(AddReminder.this, "请选择日期", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tv_time.getText().toString())) {
                    Toast.makeText(AddReminder.this, "请选择时间", Toast.LENGTH_SHORT).show();
                } else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    String what = et_title.getText().toString();
                    String date = tv_date.getText().toString();
                    String time = tv_time.getText().toString();
                    String when =  date + " " + time;
                    value.put("title", what);
                    value.put("time", when);
                    if (bundle != null) {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Integer.parseInt(bundle.getString("date").split("-")[0]),
                                Integer.parseInt(bundle.getString("date").split("-")[1])-1,
                                Integer.parseInt(bundle.getString("date").split("-")[2]),
                                Integer.parseInt(bundle.getString("time").split(":")[0]),
                                Integer.parseInt(bundle.getString("time").split(":")[1]));
                        Cursor cursor = db.rawQuery("select id from Reminder where title = ? and time = ?", new String[] {
                                bundle.getString("title"), bundle.getString("date")+" " +bundle.getString("time")});
                        if (cursor.moveToFirst()) {
                            int id_ = cursor.getColumnIndex("id");
                            //Toast.makeText(AddReminder.this, id_ + "", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                            //intent.setAction("ACTION"+calendar1.getTimeInMillis());
                            PendingIntent pit = PendingIntent.getBroadcast(getApplicationContext(), id_, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            alarmManager = (AlarmManager)AddReminder.this.getSystemService(ALARM_SERVICE);
                            alarmManager.cancel(pit);
                        }

                        db.execSQL("delete from Reminder where title = ? and time = ?", new String[] {
                                bundle.getString("title"), bundle.getString("date")+" "+bundle.getString("time")});

                    }
                    db.insert("Reminder", null, value);
                    Cursor cursor = db.rawQuery("select id from Reminder where title = ? and time = ?", new String[] {
                            what, when});
                    if (cursor.moveToFirst()) {
                        id = cursor.getColumnIndex("id");
                        // Toast.makeText(AddReminder.this, id + "", Toast.LENGTH_SHORT).show();
                    }

                    db.close();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Integer.parseInt(date.split("-")[0]),
                            Integer.parseInt(date.split("-")[1])-1,
                            Integer.parseInt(date.split("-")[2]),
                            Integer.parseInt(time.split(":")[0]),
                            Integer.parseInt(time.split(":")[1]));

                    Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    intent.setAction("ACTION"+calendar.getTimeInMillis());
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("title", what);
                    bundle1.putString("date", date);
                    bundle1.putString("time", time);
                    intent.putExtras(bundle1);
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    alarmManager = (AlarmManager)AddReminder.this.getSystemService(ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pi);

                    Intent intent_bc = new Intent(bc);
                    //intent_bc.putExtras(bundle1);
                    sendBroadcast(intent_bc);
                    AddReminder.this.finish();
                }
            }
        });
    }
    private DatePickerDialog.OnDateSetListener dataSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            String YY, MM, DD;
            YY = year+"";
            if (month + 1 < 10) {
                MM = "0" + (month + 1);
            } else {
                MM = (month+1)+"";
            }
            if (day < 10) {
                DD = "0" + day;
            }else {
                DD = day+"";
            }
            tv_date.setText(YY+"-"+MM+"-"+DD);
        }
    };
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String HH, MM;
            if (hourOfDay < 10) {
                HH = "0"+hourOfDay;
            } else {
                HH = ""+hourOfDay;
            }
            if (minute < 10) {
                MM = "0"+minute;
            } else {
                MM = ""+minute;
            }
            tv_time.setText(HH+":"+MM);
        }
    };
    protected Dialog onCreateDialog(int id) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        switch (id) {
            case 0:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, dataSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setCancelable(true);
                datePickerDialog.setTitle("选择日期");
                datePickerDialog.show();
                break;
            case 1:
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.setCancelable(true);
                timePickerDialog.setTitle("选择时间");
                timePickerDialog.show();
                break;
            default:
                break;
        }
        return null;
    }
}
