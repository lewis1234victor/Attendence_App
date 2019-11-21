package com.c.idscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    Button btnLogout,btnAddclass;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText editTxt;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference reff;
    String classtime,classname;
    int i=0;
    String[] keys = new String[100];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnLogout = findViewById(R.id.logout);
        btnAddclass = findViewById(R.id.addclass);
        FirebaseApp.initializeApp(this);
        FirebaseDatabase refftemp=FirebaseDatabase.getInstance();
        final String user_id = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        reff = refftemp.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        list = (ListView)findViewById(R.id.clalist);
        arrayList = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        list.setAdapter(adapter);

        //gets from database and detects changes. anytime something changes it will rerun onDataChange
        reff.child(user_id).child("Classes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("errors","dskey:"+dataSnapshot.getKey());
                Log.i("errors","children:"+dataSnapshot.getChildrenCount());
                adapter.clear();
                for (DataSnapshot cs : dataSnapshot.getChildren()) {
                    Log.i("errors","cskey:"+cs.getKey());
                    Log.i("errors","cschildren:"+cs.getChildrenCount());
                    //getting an array of each key directly under Classes.
                    keys[i]=cs.getKey();
                    //sets up the format for displaying the class in the listview. shows classname a space and classtime
                    String tempclass = cs.child("classname").getValue().toString() + " " + cs.child("classtime").getValue().toString();
                    classname = cs.child("classname").getValue().toString();
                    classtime = cs.child("classtime").getValue().toString();
                    arrayList.add(tempclass);
                    i++;
                    //basically refreshes the list, shows that the list has new data
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError dbe) {

            }
        });
        //addclass
        btnAddclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,add_class.class);
                startActivity(intent);
            }
        });
        //logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intToMain);
            }
        });
        //class list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapt, View view, int pos, long arg) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("class_key",keys[pos]);
                intent.putExtra("class_time",classtime);
                intent.putExtra("class_name",classname);
                Log.i("errors","sentkey:"+keys[pos]);
                startActivity(intent);
            }
        });
    }
}
