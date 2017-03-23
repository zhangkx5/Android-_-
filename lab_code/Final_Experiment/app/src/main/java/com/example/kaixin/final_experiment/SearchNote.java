package com.example.kaixin.final_experiment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchNote extends AppCompatActivity {

    private TextView textView;
    private ListView listView;
    private ImageButton imageButton;

    private myDB dbOpenHelper;
    private SQLiteDatabase dbRead;

    private static final String DATABASE_NAME = "final.db";
    private static final String NOTE_SQL_SELECTALL = "select * from notes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_note_layout);

        textView = (TextView) findViewById(R.id.note_search_disc);
        listView = (ListView) findViewById(R.id.note_search_listView);
        imageButton = (ImageButton) findViewById(R.id.search_note_back);

        Bundle bundle = this.getIntent().getExtras();
        final String search_str = bundle.getString("search_str");

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchNote.this.finish();
            }
        });

        dbOpenHelper = new myDB(SearchNote.this, DATABASE_NAME, null, 3);
        dbRead = dbOpenHelper.getReadableDatabase();

        final List<Map<String, Object>> data = new ArrayList<>();

        Cursor cursor = dbRead.rawQuery(NOTE_SQL_SELECTALL, null);
        while (cursor.moveToNext()) {
            String filename = cursor.getString(cursor.getColumnIndex("filename"));
            String time = cursor.getString(cursor.getColumnIndex("time"));

            try {
                File file = new File(getExternalFilesDir(null), filename);
                FileInputStream fis = new FileInputStream(file);
                byte[] contents = new byte[fis.available()];
                fis.read(contents);

                String strr = new String(contents);
                if (strr.contains(search_str)) {
                    Map<String, Object> temp = new LinkedHashMap<>();
                    temp.put("time", time);
                    temp.put("content", new String(contents));
                    data.add(temp);
                }

                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        dbRead.close();

        Collections.reverse(data);

        if (data.isEmpty()) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("time", "o(≧v≦)o~~");
            temp.put("content", "未搜索到任何内容");
            data.add(temp);
        }

        textView.setText("以下是关于【" + search_str + "】的搜索结果");

        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.note_search_item,
                new String[] {"time", "content"}, new int[] {R.id.note_search_time, R.id.note_search_content});
        listView.setAdapter(simpleAdapter);
    }
}

