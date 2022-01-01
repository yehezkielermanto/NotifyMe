package com.ukdc.notifyme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class menu_profile extends AppCompatActivity {

    //komponen
    ImageView backmenu;
   private TextView profilename;
    Button daftaronline;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_profile);

        //daftar komponen
        backmenu = findViewById(R.id.iconBack);
        profilename = (TextView) findViewById(R.id.Namaorang);
        daftaronline = findViewById(R.id.buttonDaftar);
        //buat kelas sharedprefrences dengan nama file yang sama yaitu session
        SharedPreferences msession = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String nama = msession.getString("Nama", "Noname");
        profilename.setText(nama);

        //button back to beranda
        backmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(menu_profile.this, beranda.class);
                startActivity(back);
            }
        });

        daftaronline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambahnamaOnline();
                daftaronline.setVisibility(View.GONE); // hide it
                daftaronline.setClickable(false); // disable the ability to click it
                //buka akses sharedpreferences file session
                SharedPreferences.Editor editor = msession.edit();
                status = nama;
                editor.putString("Status", status);
                editor.apply();
            }
        });

        //check status shared preferences
        String status_click = msession.getString("Status", "Noname");
        if(status_click.equals("Noname")){
        }else{
            daftaronline.setVisibility(View.GONE); // hide it
            daftaronline.setClickable(false); // disable the ability to click it
        }
    }

    //buat nama ke database
    private void tambahnamaOnline(){
        final String nama = profilename.getText().toString().trim();
        if(nama.equals("")){
            Toast.makeText(menu_profile.this,"Isi Kosong", Toast.LENGTH_SHORT).show();
        }else{
            class tambahnama extends AsyncTask<Void, Void, String>{
                ProgressDialog loading;
                @Override
                protected void onPreExecute(){
                    super.onPreExecute();
                    loading = ProgressDialog.show(menu_profile.this, "Make Online...","Please Waiting...", false, false);
                }
                @Override
                protected void onPostExecute(String s){
                    super.onPostExecute(s);
                    loading.dismiss();
                    Toast.makeText(menu_profile.this, s,Toast.LENGTH_LONG).show();
                }
                @Override
                protected String doInBackground(Void... v){
                    HashMap<String, String> params = new HashMap<>();
                    params.put(konfigurasi.KEY_PENGGUNA_NAMA, nama);

                    RequestHandler rh = new RequestHandler();
                    String res = rh.sendPostRequest(konfigurasi.URL_ADD,params);
                    return res;
                }
            }
            tambahnama to = new tambahnama();
            to.execute();
        }
    }
}