package com.islavdroid.firechat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

//  https://www.youtube.com/watch?v=wVCz1a3ogqk&index=7&list=WL
public class MainActivity extends AppCompatActivity {
    private Button addRoom;
    private EditText room_name;
    private ListView listView;
    private ArrayAdapter<String>arrayAdapter;
    private ArrayList<String>list_of_rooms = new ArrayList<>();
    private String name;
    //DatabaseReference представляет собой ссылку на наша базу и позволяет читать и писать в неё
    //getRoot () - ссылка на корневой каталог этой бд

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addRoom =(Button)findViewById(R.id.btn_add_room);
        room_name =(EditText)findViewById(R.id.room_name_edittext);
        listView=(ListView)findViewById(R.id.listView);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_rooms);
        listView.setAdapter(arrayAdapter);
        requestUserName();
        //добавляем комнату
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object>map =new HashMap<String, Object>();
                //указываем имя комнаты
                map.put(room_name.getText().toString(),"");
                //обновляем наш root в котором будет ключ с именем комнаты
                root.updateChildren(map);

            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            //Слушатель получает DataSnapshot, который представляет собой снимок данных
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set =new HashSet<String>();
                Iterator i =dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());

                }
                list_of_rooms.clear();
                list_of_rooms.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //обработчик нажатия на комнату
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(getApplicationContext(),ChatRoom.class);
                intent.putExtra("room_name",(((TextView)view)).getText().toString());
                intent.putExtra("user_name",name);
                startActivity(intent);
            }
        });


    }

    private void requestUserName() {
        //запрашиваем имя пользователя
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name:");
        final EditText input_field =new EditText(this);
        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               name =input_field.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                requestUserName();
            }
        });
builder.show();

    }
}
