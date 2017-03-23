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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by baoanj on 2016/12/3.
 */
public class UpdateNote extends AppCompatActivity {

    private EditText editText;
    private ImageButton imageButton1;
    private ImageButton imageButton2;

    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;

    private static final String DATABASE_NAME = "final.db";
    private static final String SQL_SELECT = "select filename from notes where time = ?";
    private static final String SQL_DELETE = "delete from notes where time = ?";
    private static final String SQL_INSERT = "insert into notes (filename, time) values (?, ?)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note_item);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.update_note_toolbar);
        setSupportActionBar(myToolbar);

        editText = (EditText) findViewById(R.id.update_note_content);
        imageButton1 = (ImageButton) findViewById(R.id.update_note_back);
        imageButton2 = (ImageButton) findViewById(R.id.update_note_done);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateNote.this.finish();
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
                    Toast.makeText(UpdateNote.this, "不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);

                    myDB dbOpenHelper = new myDB(UpdateNote.this, DATABASE_NAME, null, 3);

                    dbRead = dbOpenHelper.getReadableDatabase();
                    Cursor cursor = dbRead.rawQuery(SQL_SELECT, new String[] {time});
                    String filename = null;
                    while (cursor.moveToNext()) {
                        filename = cursor.getString(cursor.getColumnIndex("filename"));
                        break;
                    }
                    cursor.close();
                    dbRead.close();

                    dbWrite = dbOpenHelper.getWritableDatabase();
                    dbWrite.execSQL(SQL_DELETE, new Object[] {time});
                    dbWrite.close();

                    dbWrite = dbOpenHelper.getWritableDatabase();
                    dbWrite.execSQL(SQL_INSERT, new Object[] {filename, str});
                    dbWrite.close();

//                    try (FileOutputStream fileOutputStream = openFileOutput(filename, MODE_PRIVATE)) {
//                        String content = editText.getText().toString();
//                        fileOutputStream.write(content.getBytes());
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }

                    try {
                        File file = new File(getExternalFilesDir(null), filename);
                        OutputStream os = new FileOutputStream(file);
                        String content = editText.getText().toString();
                        os.write(content.getBytes());
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    UpdateNote.this.finish();
                }
            }
        });
    }
}
