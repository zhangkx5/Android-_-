package com.example.kaixin.final_experiment;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    //main
    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private TextView tv_note, tv_reminder, tv_diary, tv_mine;
    //存储
    private myDB dbOpenHelper;
    private myDB dbHelper;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private SharedPreferences pref;
    private String lock;
    private Boolean hasSetLock, hasChanged;
    private static final String DATABASE_NAME = "final.db";
    private static final String NOTE_SQL_DELETE = "delete from notes where time = ?";
    private static final String NOTE_SQL_SELECTALL = "select * from notes";
    private static final String NOTE_SQL_SELECT = "select filename from notes where time = ?";
    private static final String DIARY_SQL_DELETE = "delete from diaries where time = ?";
    private static final String DIARY_SQL_SELECTALL = "select * from diaries";
    private static final String DIARY_SQL_SELECT = "select filename from diaries where time = ?";

    //note
    private RecyclerView note_recyclerView;
    private FloatingActionButton fab;
    private ArrayList<NoteItem> note_list;

    //diary
    private EditText et_diary_pass;
    private ImageButton ibtn_diary_ok;
    private LinearLayout ll_pass_gone;
    private ListView diary_listView;
    private FloatingActionButton diary_fab;
    private SearchView note_search_view;

    //reminder
    private FloatingActionButton btn_addReminder;
    private ListView lv_reminder;
    private ArrayList<HashMap<String, String>> list_reminder;
    private SimpleAdapter simpleAdapter;
    private static String bc = "com.example.kaixin.final_experiment.reminderreceiver";
    private AlarmManager alarmManager;

    //mine
    private TextView tv_username, tv_setPassword, tv_about, tv_city, tv_shark;
    private EditText et_oldPas, et_newPas, et_conPas;
    private ImageView iv_userimage;
    private Button btn_quit, btn_save, btn_abandon;
    private String username, cityname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       //检查权限
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {

                        } else {
                            Toast.makeText(MainActivity.this, "App will finish in 1 secs...", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

        dbHelper = new myDB(this, DATABASE_NAME, null, 3);

        tv_note = (TextView)findViewById(R.id.tv_note);
        tv_reminder = (TextView)findViewById(R.id.tv_reminder);
        tv_diary = (TextView)findViewById(R.id.tv_diary);
        tv_mine = (TextView)findViewById(R.id.tv_mine);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        LayoutInflater inflater = getLayoutInflater();
        View view_note = inflater.inflate(R.layout.view_note, null);
        View view_reminder = inflater.inflate(R.layout.view_reminder, null);
        View view_diary = inflater.inflate(R.layout.view_diary, null);
        View view_mine = inflater.inflate(R.layout.view_mine, null);
        pageview = new ArrayList<View>();
        pageview.add(view_note);
        pageview.add(view_reminder);
        pageview.add(view_diary);
        pageview.add(view_mine);
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return pageview.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            public void destroyItem(View view, int i, Object object) {
                ((ViewPager) view).removeView(pageview.get(i));
            }

            public Object instantiateItem(View view, int i) {
                ((ViewPager) view).addView(pageview.get(i));
                return pageview.get(i);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tv_note.setTextColor(getResources().getColor(R.color.myColorAccent));
                        tv_reminder.setTextColor(getResources().getColor(R.color.myColorText));
                        tv_diary.setTextColor(getResources().getColor(R.color.myColorText));
                        tv_mine.setTextColor(getResources().getColor(R.color.myColorText));
                        break;
                    case 1:
                        tv_note.setTextColor(getResources().getColor(R.color.myColorText));
                        tv_reminder.setTextColor(getResources().getColor(R.color.myColorAccent));
                        tv_diary.setTextColor(getResources().getColor(R.color.myColorText));
                        tv_mine.setTextColor(getResources().getColor(R.color.myColorText));
                        break;
                    case 2:
                        tv_note.setTextColor(getResources().getColor(R.color.myColorText));
                        tv_reminder.setTextColor(getResources().getColor(R.color.myColorText));
                        tv_diary.setTextColor(getResources().getColor(R.color.myColorAccent));
                        tv_mine.setTextColor(getResources().getColor(R.color.myColorText));
                        if (!pref.getString("lock", "").equals(lock)) {
                            ll_pass_gone.setVisibility(View.VISIBLE);
                            diary_listView.setVisibility(View.GONE);
                            diary_fab.setVisibility(View.GONE);
                        }
                        break;
                    case 3:
                        tv_note.setTextColor(getResources().getColor(R.color.myColorText));
                        tv_reminder.setTextColor(getResources().getColor(R.color.myColorText));
                        tv_diary.setTextColor(getResources().getColor(R.color.myColorText));
                        tv_mine.setTextColor(getResources().getColor(R.color.myColorAccent));
                        break;
                    default:
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tv_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        tv_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        tv_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });
        tv_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3);
            }
        });

        //password
        pref = getSharedPreferences("myLock", MODE_PRIVATE);
        lock = pref.getString("lock", "");
        hasSetLock = pref.getBoolean("hasSetLock", false);
        hasChanged = false;
        username = pref.getString("username", "User");
        cityname = pref.getString("cityname", "火星");

//note
        note_recyclerView = (RecyclerView) view_note.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        note_recyclerView.setLayoutManager(layoutManager);

        fab = (FloatingActionButton) view_note.findViewById(R.id.add_a_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNote.class);
                startActivity(intent);
            }
        });

        note_search_view = (SearchView) view_note.findViewById(R.id.note_search_view);
        note_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String str) {

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String str) {
                Intent intent = new Intent(MainActivity.this, SearchNote.class);
                Bundle bundle = new Bundle();
                bundle.putString("search_str", str);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }

        });


//reminder
        lv_reminder = (ListView)view_reminder.findViewById(R.id.lv_reminder);
        btn_addReminder = (FloatingActionButton)view_reminder.findViewById(R.id.btn_addReminder);
        btn_addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddReminder.class);
                startActivity(intent);
            }
        });
        lv_reminder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddReminder.class);
                Bundle bundle = new Bundle();
                //bundle.putString("id", );
                bundle.putString("title", list_reminder.get(position).get("what"));
                bundle.putString("date", list_reminder.get(position).get("when").substring(0, 10));
                bundle.putString("time", list_reminder.get(position).get("when").substring(11, list_reminder.get(position).get("when").length()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        lv_reminder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("是否删除？");
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        String date_ = list_reminder.get(position).get("when").substring(0, 10);
                        String time_ = list_reminder.get(position).get("when").substring(11, list_reminder.get(position).get("when").length());
                        //Toast.makeText(MainActivity.this, date_+time_, Toast.LENGTH_SHORT).show();
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Integer.parseInt(date_.split("-")[0]),
                                Integer.parseInt(date_.split("-")[1])-1,
                                Integer.parseInt(date_.split("-")[2]),
                                Integer.parseInt(time_.split(":")[0]),
                                Integer.parseInt(time_.split(":")[1]));

                        db.execSQL("delete from Reminder where title = ? and time = ?", new String[] {
                                list_reminder.get(position).get("what"), list_reminder.get(position).get("when")});
                        db.close();

                        list_reminder.remove(position);
                        simpleAdapter.notifyDataSetChanged();

                        Intent intent_bc = new Intent(bc);
                        sendBroadcast(intent_bc);

                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });

//diary
        et_diary_pass = (EditText) view_diary.findViewById(R.id.diary_pass);
        ibtn_diary_ok = (ImageButton) view_diary.findViewById(R.id.diary_ok);
        ll_pass_gone = (LinearLayout) view_diary.findViewById(R.id.pass_gone);
        diary_listView = (ListView) view_diary.findViewById(R.id.diary_listView);
        diary_fab = (FloatingActionButton) view_diary.findViewById(R.id.add_a_diary);

        ibtn_diary_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(lock)) {
                    Toast.makeText(MainActivity.this, "您还没有开启日记功能\n" +
                            "请前往“我——设置日记锁”中设置日记锁、开启日记功能", Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(et_diary_pass.getText().toString())) {
                        Toast.makeText(MainActivity.this, "请输入日记锁密码", Toast.LENGTH_SHORT).show();
                    } else if (et_diary_pass.getText().toString().equals(lock)) {
                        ll_pass_gone.setVisibility(View.GONE);
                        diary_listView.setVisibility(View.VISIBLE);
                        diary_fab.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(MainActivity.this, "密码无效，请重新输入", Toast.LENGTH_SHORT).show();
                        et_diary_pass.setText(null);
                    }
                }
            }
        });



//mine
        iv_userimage = (ImageView)view_mine.findViewById(R.id.userimage);
        tv_username = (TextView)view_mine.findViewById(R.id.username);
        tv_username.setText(username);
        tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et_user =new EditText(MainActivity.this);
                et_user.setText(username);
                new AlertDialog.Builder(MainActivity.this).setTitle("修改昵称")
                        .setView(et_user)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tv_username.setText(et_user.getText().toString());
                                SharedPreferences.Editor editor = getSharedPreferences("myLock", MODE_PRIVATE).edit();
                                editor.putString("username", et_user.getText().toString());
                                editor.commit();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        tv_city = (TextView)view_mine.findViewById(R.id.city);
        tv_city.setText(cityname);
        tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et_city = new EditText(MainActivity.this);
                et_city.setText(cityname);
                new AlertDialog.Builder(MainActivity.this).setTitle("修改所在地")
                        .setView(et_city)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tv_city.setText(et_city.getText().toString());
                                SharedPreferences.Editor editor = getSharedPreferences("myLock", MODE_PRIVATE).edit();
                                editor.putString("cityname", et_city.getText().toString());
                                editor.commit();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        btn_quit = (Button)view_mine.findViewById(R.id.quit);
        String head = pref.getString("head", "");
        if ("".equals(head)) {
            Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher);
            iv_userimage.setImageBitmap(bm);
        } else {
            Bitmap bm = BitmapFactory.decodeFile(head);
            bm = ImageCrop(bm);
            iv_userimage.setImageBitmap(bm);
        }
        iv_userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                boolean permission_read = (PackageManager.PERMISSION_GRANTED ==
                        pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE", "com.example.kaixin.final_experiment"));
                boolean permission_write = (PackageManager.PERMISSION_GRANTED ==
                        pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.example.kaixin.final_experiment"));
                if (permission_read && permission_write) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 100);
                } else {
                    Toast.makeText(MainActivity.this, "请前往设置中开启相关权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
        tv_about = (TextView)view_mine.findViewById(R.id.about);
        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
        tv_setPassword = (TextView)view_mine.findViewById(R.id.setPassword);
        tv_setPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View vi = factory.inflate(R.layout.dialog_setpassword, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(vi);
                final Dialog dialog = builder.create();
                dialog.show();
                final TextInputLayout oldPasText = (TextInputLayout) vi.findViewById(R.id.pas_old);
                final TextInputLayout newPasText = (TextInputLayout) vi.findViewById(R.id.pas_new);
                final TextInputLayout conPasText = (TextInputLayout) vi.findViewById(R.id.pas_confirm);
                et_oldPas = oldPasText.getEditText();
                et_newPas = newPasText.getEditText();
                et_conPas = conPasText.getEditText();
                btn_save = (Button)vi.findViewById(R.id.save);
                if (!hasSetLock) {
                    et_oldPas.setVisibility(View.GONE);
                    et_newPas.setVisibility(View.VISIBLE);
                    et_conPas.setVisibility(View.VISIBLE);
                } else {
                    et_oldPas.setVisibility(View.VISIBLE);
                    et_newPas.setVisibility(View.VISIBLE);
                    et_conPas.setVisibility(View.VISIBLE);
                    et_oldPas.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (TextUtils.isEmpty(et_oldPas.getText().toString())) {
                                oldPasText.setErrorEnabled(true);
                                oldPasText.setError("请输入原密码");
                            } else if (!et_oldPas.getText().toString().equals(lock)) {
                                oldPasText.setErrorEnabled(true);
                                oldPasText.setError("原密码不正确，请重新输入");
                            } else {
                                oldPasText.setError(null);
                            }
                        }
                    });
                }
                et_newPas.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(et_newPas.getText().toString())) {
                            newPasText.setErrorEnabled(true);
                            newPasText.setError("请输入新密码");
                        } else {
                            newPasText.setError(null);
                        }
                    }
                });
                et_conPas.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(et_conPas.getText().toString())) {
                            conPasText.setErrorEnabled(true);
                            conPasText.setError("请确认密码");
                        } else if (!et_conPas.getText().toString().equals(et_newPas.getText().toString())) {
                            newPasText.setErrorEnabled(true);
                            conPasText.setError("密码不一致，请重新输入");
                        } else {
                            conPasText.setError(null);
                        }
                    }
                });
                btn_abandon = (Button)vi.findViewById(R.id.abandon);
                btn_abandon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!hasSetLock) {
                            if (et_conPas.getText().toString().equals(et_newPas.getText().toString())) {
                                Toast.makeText(MainActivity.this, "开启日记功能，日记锁设置成功", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = getSharedPreferences("myLock", MODE_PRIVATE).edit();
                                editor.putString("lock", et_conPas.getText().toString());
                                editor.putBoolean("hasSetLock", true);
                                editor.commit();
                                lock = et_conPas.getText().toString();
                                hasSetLock = true;
                                hasChanged = true;
                            }
                        } else if (hasSetLock){
                            if (et_conPas.getText().toString().equals(et_newPas.getText().toString())
                                    && et_oldPas.getText().toString().equals(lock)) {
                                Toast.makeText(MainActivity.this, "日记锁密码修改成功", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = getSharedPreferences("myLock", MODE_PRIVATE).edit();
                                editor.putString("lock", et_conPas.getText().toString());
                                editor.putBoolean("hasSetLock", true);
                                editor.commit();
                                lock = et_conPas.getText().toString();
                                hasSetLock = true;
                                hasChanged = true;
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        tv_shark = (TextView)view_mine.findViewById(R.id.shark);
        tv_shark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SharkActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            SharedPreferences.Editor editor = getSharedPreferences("myLock", MODE_PRIVATE).edit();
            editor.putString("head", imagePath);
            editor.commit();
            Bitmap bm = BitmapFactory.decodeFile(imagePath);
            bm = ImageCrop(bm);
            iv_userimage.setImageBitmap(bm);
            iv_userimage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

        @Override
    protected void onResume() {
        super.onResume();
        showReminderDB();
    }

    public void showReminderDB() {
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
        simpleAdapter = new SimpleAdapter(this, list_reminder, R.layout.list_reminder,
                new String[] {"what", "when"}, new int[] {R.id.tv_what, R.id.tv_when});
        lv_reminder.setAdapter(simpleAdapter);
    }



    @Override
    protected void onStart() {
        super.onStart();

        // note recyclerview
        dbOpenHelper = new myDB(MainActivity.this, DATABASE_NAME, null, 3);
        dbRead = dbOpenHelper.getReadableDatabase();

        note_list = new ArrayList<>();

        Cursor cursor = dbRead.rawQuery(NOTE_SQL_SELECTALL, null);
        while (cursor.moveToNext()) {
            String filename = cursor.getString(cursor.getColumnIndex("filename"));
            String time = cursor.getString(cursor.getColumnIndex("time"));

            try {
                File file = new File(getExternalFilesDir(null), filename);
                FileInputStream fis = new FileInputStream(file);
                byte[] contents = new byte[fis.available()];
                fis.read(contents);
                note_list.add(new NoteItem(new String(contents), time));
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        dbRead.close();

        Collections.reverse(note_list);

        final NoteAdapter adapter = new NoteAdapter(MainActivity.this, note_list);

        adapter.setOnItemClickLitener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, NoteItem item) {
                Intent intent =new Intent(MainActivity.this, UpdateNote.class);
                Bundle bundle=new Bundle();
                bundle.putString("content", item.getContent());
                bundle.putString("time", item.getTime());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        adapter.setOnItemLongClickListener(new NoteAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position, final NoteItem item) {
                View popupView = LayoutInflater.from(MainActivity.this).inflate(R.layout.pop_window, null);
                final PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable());
                popupWindow.setAnimationStyle(R.style.AnimationPreview);

                ImageButton delete = (ImageButton) popupView.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        dbRead = dbOpenHelper.getReadableDatabase();
                        Cursor cursor = dbRead.rawQuery(NOTE_SQL_SELECT, new String[] {item.getTime()});
                        String filename = null;
                        while (cursor.moveToNext()) {
                            filename = cursor.getString(cursor.getColumnIndex("filename"));
                            break;
                        }
                        cursor.close();
                        dbRead.close();

                        dbWrite = dbOpenHelper.getWritableDatabase();
                        dbWrite.execSQL(NOTE_SQL_DELETE, new Object[] {item.getTime()});
                        dbWrite.close();

                        //MainActivity.this.deleteFile(filename);
                        File file = new File(getExternalFilesDir(null), filename);
                        file.delete();

                        note_list.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });

                popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int popupWidth = popupView.getMeasuredWidth();
                int popupHeight =  popupView.getMeasuredHeight();
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                popupWindow.showAtLocation(view, Gravity.NO_GRAVITY,
                        (location[0] + view.getWidth() / 2) - popupWidth / 2,
                        location[1] - view.getHeight() * 3 / 5);
            }
        });

        note_recyclerView.setAdapter(adapter);

        // diary listview
        dbRead = dbOpenHelper.getReadableDatabase();

        final List<Map<String, Object>> data = new ArrayList<>();

        Cursor cursor1 = dbRead.rawQuery(DIARY_SQL_SELECTALL, null);
        while (cursor1.moveToNext()) {
            String filename = cursor1.getString(cursor1.getColumnIndex("filename"));
            String time = cursor1.getString(cursor1.getColumnIndex("time"));
            String city = cursor1.getString(cursor1.getColumnIndex("city"));
            String weather = cursor1.getString(cursor1.getColumnIndex("weather"));

            try (FileInputStream fileInputStream = openFileInput(filename)) {
                byte[] contents = new byte[fileInputStream.available()];
                fileInputStream.read(contents);

                Map<String, Object> temp = new LinkedHashMap<>();
                temp.put("diary_time", time);
                temp.put("diary_city", city);
                temp.put("diary_weather", weather);
                temp.put("diary_content", new String(contents));
                data.add(temp);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        cursor1.close();
        dbRead.close();

        Collections.reverse(data);

        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.diary_item,
                new String[] {"diary_time", "diary_city", "diary_weather", "diary_content"},
                new int[] {R.id.diary_time, R.id.diary_city, R.id.diary_weather, R.id.diary_content});
        diary_listView.setAdapter(simpleAdapter);

        diary_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView diary_content = (TextView) view.findViewById(R.id.diary_content);
                TextView diary_time = (TextView) view.findViewById(R.id.diary_time);

                Intent intent = new Intent(MainActivity.this, UpdateDiary.class);
                Bundle bundle = new Bundle();
                bundle.putString("content", diary_content.getText().toString());
                bundle.putString("time", diary_time.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        diary_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final TextView diary_time = (TextView) view.findViewById(R.id.diary_time);

                final NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(MainActivity.this);
                dialogBuilder
                        .withTitle("删除日记")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor("#11000000")
                        .withMessage("想好了吗，是否真的要删除这篇日记？")
                        .withMessageColor("#FFFFFFFF")
                        .withDialogColor("#009A61")
                        .withIcon(getResources().getDrawable(R.mipmap.delete))
                        .withDuration(700)
                        .withEffect(Effectstype.Slidetop)
                        .withButton1Text("取消")
                        .withButton2Text("删除")
                        .isCancelableOnTouchOutside(true)
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dbRead = dbOpenHelper.getReadableDatabase();
                                Cursor cursor = dbRead.rawQuery(DIARY_SQL_SELECT, new String[] {diary_time.getText().toString()});
                                String filename = null;
                                while (cursor.moveToNext()) {
                                    filename = cursor.getString(cursor.getColumnIndex("filename"));
                                    break;
                                }
                                cursor.close();
                                dbRead.close();

                                dbWrite = dbOpenHelper.getWritableDatabase();
                                dbWrite.execSQL(DIARY_SQL_DELETE, new Object[] {diary_time.getText().toString()});
                                dbWrite.close();

                                MainActivity.this.deleteFile(filename);

                                data.remove(i);
                                simpleAdapter.notifyDataSetChanged();
                                dialogBuilder.dismiss();
                            }
                        })
                        .show();

                return true;
            }
        });

        diary_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddDiary.class);
                startActivity(intent);
            }
        });
    }
    /**     * 按正方形裁切图片
     */
    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        //下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }
}
