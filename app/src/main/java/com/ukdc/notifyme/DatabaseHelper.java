package com.ukdc.notifyme;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHelper";
    public static final String database_name = "DB_todoTable";
    public static final String TABLE_NAME = "Todo_table";
    public static final String COL1 = "ID";
    public static final String COL2 = "Name";
    public static final String COL3 = "Date";
    public static final String COL4 = "Time";

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 2);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +  COL1  + " integer primary key autoincrement, " +  COL2  + " TEXT, " +  COL3  + " DATE, " +  COL4  +  " TIME" + ")";
        Log.d(TAG, "Creating table" + createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    //memasukkan data ke dalam database
    public boolean insertData(String item, String date, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item);
        contentValues.put(COL3, date);
        contentValues.put(COL4, time);
        Log.d(TAG, "insertData: inserting"+ item+"to "+ TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    //menghapus data dari database
    void deleteData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL1 + "="+ id, null);
    }

    //memuat semua data ke list view
    public ArrayList<ModelData> getAllData(){
        ArrayList<ModelData> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+ TABLE_NAME;
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String date = cursor.getString(2);
            String time = cursor.getString(3);
            ModelData modelData = new ModelData(id, title, date, time);
            arrayList.add(modelData);
        }
//        db.close();
        return arrayList;
    }

    public Cursor getOneData(int id) {
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 + "=" + id, null);
        return cur;
    }

    public void updateData(ContentValues values,int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NAME, values,COL1 + "="+ id, null);
    }
}
