package com.example.kaixin.final_experiment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by baoanj on 2016/12/4.
 */
public class UpdateDiary extends AppCompatActivity {

    private EditText editText;
    private ImageButton imageButton1;
    private ImageButton imageButton2;

    private SQLiteDatabase dbRead;

    private static final String DATABASE_NAME = "final.db";
    private static final String SQL_SELECT = "select filename from diaries where time = ?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_diary_item);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.update_diary_toolbar);
        setSupportActionBar(myToolbar);

        editText = (EditText) findViewById(R.id.update_diary_content);
        imageButton1 = (ImageButton) findViewById(R.id.update_diary_back);
        imageButton2 = (ImageButton) findViewById(R.id.update_diary_done);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateDiary.this.finish();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        final String content = bundle.getString("content");
        final String time = bundle.getString("time");

        editText.setText(content);

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(UpdateDiary.this, "不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    myDB dbOpenHelper = new myDB(UpdateDiary.this, DATABASE_NAME, null, 3);

                    dbRead = dbOpenHelper.getReadableDatabase();
                    Cursor cursor = dbRead.rawQuery(SQL_SELECT, new String[] {time});
                    String filename = null;
                    while (cursor.moveToNext()) {
                        filename = cursor.getString(cursor.getColumnIndex("filename"));
                        break;
                    }
                    cursor.close();
                    dbRead.close();

                    try (FileOutputStream fileOutputStream = openFileOutput(filename, MODE_PRIVATE)) {
                        String content = editText.getText().toString();
                        fileOutputStream.write(content.getBytes());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    UpdateDiary.this.finish();
                }
            }
        });
    }
}
