package com.example.kaixin.final_experiment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by baoanj on 2016/12/4.
 */
public class AddDiary extends AppCompatActivity {

    private EditText editText;
    private ImageButton imageButton1;
    private ImageButton imageButton2;

    private SQLiteDatabase dbWrite;

    private static final String DATABASE_NAME = "final.db";
    private static final String SQL_INSERT = "insert into diaries (filename, time, city, weather) values (?, ?, ?, ?)";
    private static final String url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";

    private static final int UPDATE_CONTENT = 0;
    private static final int NOT_CONNECTED = 1;

    private String weather;
    private String city;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_CONTENT:
                    List<String> list = (List<String>) message.obj;
                    if (list.size() > 8) {
                        int index = list.get(7).indexOf("日");
                        weather = list.get(7).substring(index + 2);
                    } else {
                        weather = "天气走丢了";
                    }
                    break;
                case NOT_CONNECTED:
                    weather = "天气走丢了";
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_diary);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.add_diary_toolbar);
        setSupportActionBar(myToolbar);

        editText = (EditText) findViewById(R.id.new_diary_content);
        imageButton1 = (ImageButton) findViewById(R.id.add_diary_back);
        imageButton2 = (ImageButton) findViewById(R.id.add_diary_done);

        weather = "天气走丢了";
        city = "火星";

        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.example.kaixin.final_experiment"));
        if (permission) {
            LocationUtils.getCNBylocation(AddDiary.this);
            city = LocationUtils.cityName;
        }

        sendRequestWithHttpURLConnection();

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDiary.this.finish();
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(AddDiary.this, "不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());
                    String str = formatter.format(curDate);

                    try (FileOutputStream fileOutputStream = openFileOutput(str + "diary", MODE_PRIVATE)) {
                        String content = editText.getText().toString();
                        fileOutputStream.write(content.getBytes());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    myDB dbOpenHelper = new myDB(AddDiary.this, DATABASE_NAME, null, 3);
                    dbWrite = dbOpenHelper.getWritableDatabase();
                    dbWrite.execSQL(SQL_INSERT, new Object[]{str + "diary", str, city, weather});
                    dbWrite.close();
                    AddDiary.this.finish();
                }
            }
        });
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getSystemService(AddDiary.this.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    HttpURLConnection connection = null;
                    StringBuffer response = new StringBuffer();
                    try {
                        connection = (HttpURLConnection) ((new URL(url.toString()).openConnection()));
                        connection.setRequestMethod("POST");
                        connection.setReadTimeout(8000);
                        connection.setConnectTimeout(8000);

                        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                        String request = city;
                        request = URLEncoder.encode(request, "utf-8");
                        out.writeBytes("theCityCode=" + request + "&theUserID=");

                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        reader.close();
                        in.close();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(connection!=null) {
                            connection.disconnect();
                        }
                    }

                    Message message = new Message();
                    message.what = UPDATE_CONTENT;
                    message.obj = parseXMLWithPull(response.toString());
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = NOT_CONNECTED;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    private List<String> parseXMLWithPull(String xml) {
        List<String> list = new LinkedList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("string".equals(parser.getName())) {
                            String str = parser.nextText();
                            list.add(str);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
