package com.ukdc.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static java.util.Calendar.MINUTE;

import com.google.android.material.tabs.TabLayout;

public class beranda extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    private static final String TAG = "beranda";
    private fragment_beranda fragmentBeranda;

    DatabaseHelper databaseHelper;
    ListView itemsListView;
    ImageView menu;
    //ListView itemsListView;
    private FloatingActionButton fab;
    //private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.3F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);

        //database
        databaseHelper = new DatabaseHelper(this);

        //itemsListView = findViewById(R.id.itemsList);

        //hideFab();

        //buat kelas sharedprefrences dengan nama file yang sama yaitu session
        SharedPreferences msession = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);

        //daftar semua komponen
        TextView txt_nama_get = (TextView) findViewById(R.id.txt_name_get);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        menu = findViewById(R.id.menu);
        String nama = msession.getString("Nama", "Noname");
        txt_nama_get.setText(nama);

        //aktivitas pager
        tabLayout.addTab(tabLayout.newTab().setText("Activities"));
        tabLayout.addTab(tabLayout.newTab().setText("Friends"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final MyAdapter adapter = new MyAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menu = new Intent(beranda.this, menu_profile.class);
                startActivity(menu);
            }
        });

    }
    private Boolean exit = false;
    @Override
    public void onBackPressed(){
        finishAffinity();
    }

}