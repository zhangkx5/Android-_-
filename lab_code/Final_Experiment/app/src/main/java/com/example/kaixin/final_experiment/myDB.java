package com.example.kaixin.final_experiment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by kaixin on 2016/12/1.
 */

public class myDB extends SQLiteOpenHelper {
    public static final String CREATE_TABLE = "create table Reminder"
            +"(id INTEGER PRIMARY KEY autoincrement, title TEXT, time TEXT)";
    private Context mContext;


    private static final String DB_NAME = "finaldb";
    private static final String NOTE_TABLE_NAME = "notes";
    private static final String DIARY_TABLE_NAME = "diaries";
    private static final int DB_VERSION = 1;

    public myDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);

        String NOTE_CREATE_TABLE = "create table if not exists "
                + NOTE_TABLE_NAME
                + " (id integer primary key,filename TEXT,time TEXT)";
        sqLiteDatabase.execSQL(NOTE_CREATE_TABLE);

        String DIARY_CREATE_TABLE = "create table if not exists "
                + DIARY_TABLE_NAME
                + " (id integer primary key,filename TEXT,time TEXT,city TEXT,weather TEXT)";
        sqLiteDatabase.execSQL(DIARY_CREATE_TABLE);

        String Shark_TABLE = "create table if not exists "
                + "Shark_TABLE"
                + " (id integer primary key autoincrement,joke TEXT)";
        sqLiteDatabase.execSQL(Shark_TABLE);
        sqLiteDatabase.execSQL("insert into Shark_TABLE(joke) values(?)",
                new String[] {"只有经历过地狱般的折磨，才有征服天堂的力量。只有流过血的手指才能弹出世间的绝唱。"});
        sqLiteDatabase.execSQL("insert into Shark_TABLE(joke) values(?)",
                new String[] {"既然人生的幕布已经拉开，就一定要积极的演出；既然脚步已经跨出，风雨坎坷也不能退步；" +
                        "既然我已把希望播在这里，就一定要坚持到胜利的谢幕……"});
        sqLiteDatabase.execSQL("insert into Shark_TABLE(joke) values(?)",
                new String[] {"不要轻易用过去来衡量生活的幸与不幸！每个人的生命都是可以绽放美丽的——只要你珍惜。"});
        sqLiteDatabase.execSQL("insert into Shark_TABLE(joke) values(?)",
                new String[] {"没有一种不通过蔑视、忍受和奋斗就可以征服的命运。"});
        sqLiteDatabase.execSQL("insert into Shark_TABLE(joke) values(?)",
                new String[] {"前有阻碍，奋力把它冲开，运用炙热的激情，转动心中的期待，血在澎湃，吃苦流汗算什么。"});
        sqLiteDatabase.execSQL("insert into Shark_TABLE(joke) values(?)",
                new String[] {"朋友是路，家是树。别迷路，靠靠树。"});
        sqLiteDatabase.execSQL("insert into Shark_TABLE(joke) values(?)",
                new String[] {"一份耕耘一份收获，未必；九份耕耘一份收获，一定。"});
        sqLiteDatabase.execSQL("insert into Shark_TABLE(joke) values(?)",
                new String[] {"老虎不发威他就一只病猫！发威了他就是王者！所以人人都可以是王者但同时也可能是病猫，关键在于你自己的选折！"});
        sqLiteDatabase.execSQL("insert into Shark_TABLE(joke) values(?)",
                new String[] {"如果要后退，上帝就会在我们的后脑长双眼睛了"});
        sqLiteDatabase.execSQL("insert into Shark_TABLE(joke) values(?)",
                new String[] {"人既不是天使，又不是禽兽；但不幸就在于想表现为天使的人却表现为禽兽。"});
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {
        sqLiteDatabase.execSQL("drop table if exists Reminder");
        sqLiteDatabase.execSQL("drop table if exists notes");
        sqLiteDatabase.execSQL("drop table if exists diaries");
        sqLiteDatabase.execSQL("drop table if exists Shark_TABLE");
        onCreate(sqLiteDatabase);
    }
}
