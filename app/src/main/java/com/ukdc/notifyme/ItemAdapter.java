package com.ukdc.notifyme;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ModelData>arrayList;

    public ItemAdapter(Context context, ArrayList<ModelData> arrayList){
        super();
        this.context = context;
        this.arrayList = arrayList;
    }


    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, final ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        convertView = layoutInflater.inflate(R.layout.buatbaru, null);
        TextView id = convertView.findViewById(R.id.listID);
        TextView titleTextView = convertView.findViewById(R.id.title);
        TextView dateTextView = convertView.findViewById(R.id.dateTitle);
        TextView timeTextView = convertView.findViewById(R.id.timeTitle);

        ModelData modelData = arrayList.get(position);
        titleTextView.setText(modelData.getTitle());
        String id1 = String.valueOf(modelData.getId());
        id.setText(id1);
        dateTextView.setText(modelData.getDate());
        timeTextView.setText(modelData.getTime());
        return convertView;
    }
        //menghapus tugas dari listview
        private void deleteItem(int position){
        deleteItemFromdb(arrayList.get(position).getId());
        arrayList.remove(position);
        notifyDataSetChanged();
    }
        //menghapus tugas dari database
        private void deleteItemFromdb(int id){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        try{
            databaseHelper.deleteData(id);
            toastMsg("Activity Deleted");
        }catch (Exception e){
            e.printStackTrace();
            toastMsg("Oops, something gone wrong, contact developer");
        }
        }

        //membuat pesan toast
        private void toastMsg(String msg){
            Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0,0);
            t.show();
        }
}
