package com.ukdc.notifyme;

import static java.util.Calendar.MINUTE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_beranda#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_beranda extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "fragment_beranda";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id ="default";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    DatabaseHelper databaseHelper;
    ListView itemsListView;
    FloatingActionButton fab;
    public fragment_beranda() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_beranda.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_beranda newInstance(String param1, String param2) {
        fragment_beranda fragment = new fragment_beranda();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_beranda, container, false);

        //databaseHelper = new DatabaseHelper(this);
        itemsListView = (ListView)v.findViewById(R.id.itemsList);
        itemsListView.setOnItemClickListener(this::onItemClick);
        databaseHelper = new DatabaseHelper(getContext());
        fab = v.findViewById(R.id.btn_add);
        //onFabClick();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.startAnimation(buttonClick);
                    showAddDialog();
            }
        });
        itemsListView.setEmptyView(v.findViewById(R.id.empty));
        populateListView();
        return v;
    }

    //fungsi untuk mereload data untuk ditampilkan ke dalam list view
    public void populateListView() {
        try {
            ArrayList<ModelData> items = databaseHelper.getAllData();
            ItemAdapter itemsAdopter = new ItemAdapter(getContext() , items);
            itemsListView.setAdapter(itemsAdopter);
            itemsAdopter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Mengatur notifikasi
    private void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(getContext(), Notifikasi.class);
        notificationIntent.putExtra(Notifikasi.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(Notifikasi.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getLayoutInflater().getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
        }
    }

    //Memasukkan data ke database
    private void insertDataToDb(String title, String date, String time) {
        boolean insertData = databaseHelper.insertData(title, date, time);
        if (insertData) {
            try {
                //refresh fragment
                toastMsg("New Activity created..");
                populateListView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            toastMsg("Opps.. something gone wrong, contact developer");
    }

        private Notification getNotification(String content) {

        //Saat notifikasi di klik di arahkan ke MainActivity
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getLayoutInflater().getContext(), default_notification_channel_id);
        builder.setContentTitle("NotifyMe!");
        builder.setContentText(content);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_baseline_add_alert_24);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();
    }

    @SuppressLint("SimpleDateFormat")
    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getLayoutInflater().getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText judul = dialogView.findViewById(R.id.edit_title);
        final TextView tanggal = dialogView.findViewById(R.id.date);
        final TextView waktu = dialogView.findViewById(R.id.time);

        final long date = System.currentTimeMillis();
        SimpleDateFormat dateSdf = new SimpleDateFormat("d MMMM yyyy");
        String dateString = dateSdf.format(date);
        tanggal.setText(dateString);

        SimpleDateFormat timeSdf = new SimpleDateFormat("hh : mm a");
        String timeString = timeSdf.format(date);
        waktu.setText(timeString);

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        //Set tanggal
        tanggal.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getLayoutInflater().getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String newMonth = getMonth(monthOfYear + 1);
                                tanggal.setText(dayOfMonth + " " + newMonth);
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, monthOfYear);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(date);
            }
        });

        //Set waktu
        waktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getLayoutInflater().getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time;
                                @SuppressLint("DefaultLocale") String minTime = String.format("%02d", minute);
                                if (hourOfDay >= 0 && hourOfDay < 12) {
                                    time = hourOfDay + " : " + minTime + " AM";
                                } else {
                                    if (hourOfDay != 12) {
                                        hourOfDay = hourOfDay - 12;
                                    }
                                    time = hourOfDay + " : " + minTime + " PM";
                                }
                                waktu.setText(time);
                                cal.set(Calendar.HOUR, hourOfDay);
                                cal.set(Calendar.MINUTE, minute);
                                cal.set(Calendar.SECOND, 0);
                                Log.d(TAG, "onTimeSet: Time has been set successfully");
                            }
                        }, cal.get(Calendar.HOUR), cal.get(MINUTE), false);
                timePickerDialog.show();
            }
        });

        dialogBuilder.setTitle("Create New Activity");
        dialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = judul.getText().toString();
                String date = tanggal.getText().toString();
                String time = waktu.getText().toString();
                if (title.length() != 0) {
                    try {
                        insertDataToDb(title, date, time);
                        scheduleNotification(getNotification(title), cal.getTimeInMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    toastMsg("Oops, activity cannot empty");
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    //Metode pesan toast
    private void toastMsg(String msg) {
        Toast t = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0,0);
        t.show();
    }


    //Mengkonversi bulan dari huruf menjadi angka
    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }


    //-------------------------------------------------------------------------------dialogbox hapus aktivitas
    public void onItemClick(AdapterView<?> parent, View view, int i, long x){
        TextView getId = (TextView)view.findViewById(R.id.listID);
        final int id =  Integer.parseInt(getId.getText().toString());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Menu: ");
        //menu list
        String[] options={"Edit Activity","Delete Activity"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch(which){
                    case 0:
                        //-----------------------------------------------edit aktivitas
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getLayoutInflater().getContext());
                        LayoutInflater inflater = getLayoutInflater();
                        @SuppressLint("InflateParams")
                        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                        dialogBuilder.setView(dialogView);

                        final EditText judul = dialogView.findViewById(R.id.edit_title);
                        final TextView tanggal = dialogView.findViewById(R.id.date);
                        final TextView waktu = dialogView.findViewById(R.id.time);

                        final long date = System.currentTimeMillis();
                        SimpleDateFormat dateSdf = new SimpleDateFormat("d MMMM yyyy");
                        String dateString = dateSdf.format(date);
                        tanggal.setText(dateString);

                        SimpleDateFormat timeSdf = new SimpleDateFormat("hh : mm a");
                        String timeString = timeSdf.format(date);
                        waktu.setText(timeString);

                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(System.currentTimeMillis());

                        //ambil data dari database
                        TextView getId = (TextView)view.findViewById(R.id.listID);
                        final int id =  Integer.parseInt(getId.getText().toString());
                        Cursor cursor = databaseHelper.getOneData(id);
                        if(cursor.moveToFirst()){
                            judul.setText(cursor.getString(1));
                            tanggal.setText(cursor.getString(2));
                            waktu.setText(cursor.getString(3));
                        }

                        //Set tanggal
                        tanggal.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View v) {
                                final DatePickerDialog datePickerDialog = new DatePickerDialog(getLayoutInflater().getContext(),
                                        new DatePickerDialog.OnDateSetListener() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                String newMonth = getMonth(monthOfYear + 1);
                                                tanggal.setText(dayOfMonth + " " + newMonth);
                                                cal.set(Calendar.YEAR, year);
                                                cal.set(Calendar.MONTH, monthOfYear);
                                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                            }
                                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                                datePickerDialog.show();
                                datePickerDialog.getDatePicker().setMinDate(date);
                            }
                        });

                        //Set waktu
                        waktu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TimePickerDialog timePickerDialog = new TimePickerDialog(getLayoutInflater().getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                String time;
                                                @SuppressLint("DefaultLocale") String minTime = String.format("%02d", minute);
                                                if (hourOfDay >= 0 && hourOfDay < 12) {
                                                    time = hourOfDay + " : " + minTime + " AM";
                                                } else {
                                                    if (hourOfDay != 12) {
                                                        hourOfDay = hourOfDay - 12;
                                                    }
                                                    time = hourOfDay + " : " + minTime + " PM";
                                                }
                                                waktu.setText(time);
                                                cal.set(Calendar.HOUR, hourOfDay);
                                                cal.set(Calendar.MINUTE, minute);
                                                cal.set(Calendar.SECOND, 0);
                                                Log.d(TAG, "onTimeSet: Time has been set successfully");
                                            }
                                        }, cal.get(Calendar.HOUR), cal.get(MINUTE), false);
                                timePickerDialog.show();
                            }
                        });
                        dialogBuilder.setTitle("Edit Activity");
                        dialogBuilder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String title = judul.getText().toString();
                                String date = tanggal.getText().toString();
                                String time = waktu.getText().toString();
                                if (title.length() != 0) {
                                    try {
                                        ContentValues values = new ContentValues();
                                        values.put(DatabaseHelper.COL2, String.valueOf(title));
                                        values.put(DatabaseHelper.COL3, String.valueOf(date));
                                        values.put(DatabaseHelper.COL4, String.valueOf(time));
                                        databaseHelper.updateData(values,id);
                                        scheduleNotification(getNotification(title), cal.getTimeInMillis());
                                        populateListView();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    toastMsg("Oops, activity cannot empty");
                                }
                            }
                        });
                        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog b = dialogBuilder.create();
                        b.show();

                }
                switch(which){
                    case 1:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                        builder1.setMessage("Delete this Activity?");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                databaseHelper.deleteData(id);

                                Toast.makeText(getContext(), "Activity deleted", Toast.LENGTH_SHORT).show();
                                populateListView();
                            }
                        });
                        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder1.create();
                        alertDialog.show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}