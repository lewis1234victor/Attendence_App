package com.c.idscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * recognizes text.
 */
public class MainActivity extends AppCompatActivity {

    // Use a compound button so either checkbox or switch widgets work.
    private TextView statusMessage;
    DatabaseReference reff;
    Button addstu,attend;
    FirebaseAuth mFirebaseAuth;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    String clakey;
    int i=0;
    String[] keys = new String[100];
    String stukey;
    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "hope";
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        reff= FirebaseDatabase.getInstance().getReference();
        statusMessage = (TextView)findViewById(R.id.status_message);
        addstu = (Button)findViewById(R.id.addstu);
        list = (ListView)findViewById(R.id.stulist);
        attend = findViewById(R.id.attend);
        statusMessage.setText(getIntent().getStringExtra("class_name")+"!"+getIntent().getStringExtra("class_time"));

        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        list.setAdapter(adapter);
        clakey = getIntent().getStringExtra("class_key");
        Log.i("errors","null??:"+clakey);
        final String user_id = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        reff.child(user_id).child("Classes").child(clakey).child("Student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("errors","inondata");
                String tempjnum,templname,tempinfo;
                adapter.clear();
                for (DataSnapshot cs : dataSnapshot.getChildren()) {
                    Log.i("errors","infirstfordata");
                    tempjnum = cs.child("jnum").getValue().toString();
                    templname = cs.child("namelast").getValue().toString();
                    keys[i]=cs.getKey();
                    stukey=keys[i];

                    if(tempjnum.equals(getIntent().getStringExtra("jnum"))&&templname.equals(getIntent().getStringExtra("name"))){
                        String t=statusMessage.getText().toString();
                        String[] tk=t.split("!");
                        String time=tk[1];
                        Log.i("errors","time:"+time);
                        time = time.substring(0,5);
                        if(LocalTime.now().isAfter(LocalTime.parse(time))){
                            Log.i("errors","inif");
                            for (DataSnapshot c : cs.getChildren()) {
                                Log.i("errors","infor");
                                int tardy = Integer.parseInt(c.child("t").getValue().toString());
                                tardy++;
                                reff.child(user_id).child("Classes").child(clakey).child("Student").child(keys[i]).child(c.getKey()).child("t").setValue(tardy);
                                break;
                            }
                        }
                        else{
                            Log.i("errors","inifelse");
                            for (DataSnapshot c : cs.getChildren()) {
                                Log.i("errors","infor");
                                int ontime = Integer.parseInt(c.child("ot").getValue().toString());
                                ontime++;
                                reff.child(user_id).child("Classes").child(clakey).child("Student").child(keys[i]).child(c.getKey()).child("ot").setValue(ontime );
                                break;
                            }

                        }
                    }
                    Log.i("errors",getIntent().getStringExtra("jnum")+" "+tempjnum+getIntent().getStringExtra("name")+templname);
                    Log.i("errors","i:"+i);
                    Log.i("errors","children:"+dataSnapshot.getChildrenCount());
                    tempinfo = "Name:" + templname + "\n" + "JNumber:" + tempjnum;
                    i++;
                    arrayList.add(tempinfo);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError dbe) {

            }
        });

        addstu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,add_student.class);
                intent.putExtra("class_key",clakey);
                String t=statusMessage.getText().toString();
                String[] tk=t.split("|");
                Log.i("errors","tk0:"+tk[0]);
                Log.i("errors","tk1:"+tk[1]);
                intent.putExtra("class_name",tk[0]);
                intent.putExtra("class_time",tk[1]);
                startActivity(intent);

            }
        });
        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, OcrCaptureActivity.class);
                Log.i("errors","clakeyocr:"+clakey);
                intent.putExtra("class_key",clakey);
                String t=statusMessage.getText().toString();
                String[] tk=t.split("!");
                Log.i("errors","tk0:"+tk[0]);
                Log.i("errors","tk1:"+tk[1]);
                intent.putExtra("class_name",tk[0]);
                intent.putExtra("class_time",tk[1]);
                startActivity(intent);
            }
        });
    }
}