package com.ukdc.notifyme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //daftar semua komponen
        EditText txt_nama_set = (EditText) findViewById(R.id.txt_name_set);
        Button btn_masuk = (Button) findViewById(R.id.btn_masuk);

        //membuat shared preferences dengan nama file session
        SharedPreferences msession = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);

        //toast
        Context context = getApplicationContext();
        String Text = "Input your name";
        String PanjangNama = "Maximum number of character is 8 ";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, Text, duration);
        Toast toast2 = Toast.makeText(context, PanjangNama, duration);

        //cek isi shared preferences
        String nama = msession.getString("Nama", "Noname");
        if(nama == "Noname"){
            toast.show();
        }else{
            Intent pindah = new Intent(MainActivity.this, beranda.class);
            startActivity(pindah);

        }

        //buat tombol
        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User = txt_nama_set.getText().toString();
                if (User.matches("")){
                    toast.show();
                }else{
                    try{
                        //buka akses sharedpreferences file session
                        SharedPreferences.Editor editor = msession.edit();
                        editor.putString("Nama", txt_nama_set.getText().toString());
                        if(txt_nama_set.length() > 9){
                            toast2.show();
                        }else{
                            //commit perubahan isi file
                            editor.apply();
                            Intent pindah = new Intent(MainActivity.this, beranda.class);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}