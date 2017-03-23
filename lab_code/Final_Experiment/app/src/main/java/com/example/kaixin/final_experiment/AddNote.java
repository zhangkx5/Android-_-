package com.example.kaixin.final_experiment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
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
public class AddNote extends AppCompatActivity {

    private EditText editText;
    private ImageButton imageButton1;
    private ImageButton imageButton2;

    private SQLiteDatabase dbWrite;

    private static final String DATABASE_NAME = "final.db";
    private static final String SQL_INSERT = "insert into notes (filename, time) values (?, ?)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.add_note_toolbar);
        setSupportActionBar(myToolbar);

        editText = (EditText) findViewById(R.id.new_note_content);
        imageButton1 = (ImageButton) findViewById(R.id.add_note_back);
        imageButton2 = (ImageButton) findViewById(R.id.add_note_done);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNote.this.finish();
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(AddNote.this, "不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);

                    try {
                        File file = new File(getExternalFilesDir(null), str + "note.txt");
                        OutputStream os = new FileOutputStream(file);
                        String content = editText.getText().toString();
                        os.write(content.getBytes());
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    myDB dbOpenHelper = new myDB(AddNote.this, DATABASE_NAME, null, 3);
                    dbWrite = dbOpenHelper.getWritableDatabase();
                    dbWrite.execSQL(SQL_INSERT, new Object[]{str + "note.txt", str});
                    dbWrite.close();
                    AddNote.this.finish();
                }
            }
        });
    }
}
