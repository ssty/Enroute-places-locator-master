package com.example.sunsun.shoppingrouteguide;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ListDataActivity extends AppCompatActivity {
    private ListView listView;
    private ListAdapter listAdapter;
    DatabaseHelper mDatabaseHelper;


    private ArrayList<Info> listData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        mDatabaseHelper = new DatabaseHelper(this);
        listView = (ListView) findViewById(R.id.listView);
        Cursor data = mDatabaseHelper.getAllData();
        while(data.moveToNext()){
            Info info = new Info();
            info.id = data.getString(0);
            info.description = data.getString(4);
            info.origin= data.getString(1);
            info.destination= data.getString(2);
            listData.add(info);

        }
        listAdapter = new ListAdapter(listData);
        listView.setAdapter(listAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    class Info{
        public String id="";
        public String description="";
        public String origin="";
        public String destination="";

    }
    class ViewHolder{
        public TextView description;
        public TextView origin;
        public TextView destination;
        public ImageButton open;
        public Button delete;
    }
    class ListAdapter extends BaseAdapter {
        private ArrayList<Info> listData;
        public ListAdapter(ArrayList<Info> listData){
            this.listData= (ArrayList<Info>) listData.clone();
        }
        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            if(convertView == null){
                convertView = View.inflate(getBaseContext(),R.layout.style_list,null);
                viewHolder.description = (TextView) convertView.findViewById(R.id.txtview_description);
                viewHolder.origin = (TextView) convertView.findViewById(R.id.txtview_origin);
                viewHolder.destination = (TextView) convertView.findViewById(R.id.txtview_destination);
                viewHolder.open = (ImageButton) convertView.findViewById(R.id.open);
                viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final String test= listData.get(position).id;
            viewHolder.description.setText(listData.get(position).description);
            viewHolder.origin.setText(listData.get(position).origin);
            viewHolder.destination.setText(listData.get(position).destination);

            final String urlDestination=listData.get(position).destination;
            final String urlOrigin= listData.get(position).origin;
           // final int id = position;
            viewHolder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+urlOrigin+"&daddr="+urlDestination+""));
                    startActivity(intent);
                }
            });
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabaseHelper.deleteData(test);
                    Intent i = new Intent(ListDataActivity.this,MainActivity.class);
                    startActivity(i);
                    Toast.makeText(ListDataActivity.this,"Route has been deleted",Toast.LENGTH_LONG).show();


                }
            });
            return convertView;
        }
    }
}

