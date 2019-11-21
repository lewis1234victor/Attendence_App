package com.c.idscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class add_student extends AppCompatActivity {
    String firstname,lastname,Jnumber,key;
    EditText name, jnumber;
    Button button;
    Student students = new Student();
    DatabaseReference reff;
    FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        name = findViewById(R.id.name);
        jnumber = findViewById(R.id.jnumber);
        button = findViewById(R.id.submitstu);

        FirebaseApp.initializeApp(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        final String user_id = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        reff= FirebaseDatabase.getInstance().getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname = name.getText().toString();
                Jnumber = jnumber.getText().toString();
                if(!(emptyCheck(firstname) && emptyCheck(Jnumber))){
                    Toast.makeText(add_student.this,"Fields Are Empty!", Toast.LENGTH_SHORT).show();
                }
                else {
                    students.setNames(name.getText().toString());
                    students.setJnum(jnumber.getText().toString());
                    String tempstukey;
                    key = getIntent().getStringExtra("class_key");
                    tempstukey = reff.child(user_id).child("Classes").child(key).child("Student").push().getKey();
                    String attendkey=reff.child(user_id).child("Classes").child(key).child("Student").child(tempstukey).push().getKey();
                    reff.child(user_id).child("Classes").child(key).child("Student").child(tempstukey).setValue(students);
                    reff.child(user_id).child("Classes").child(key).child("Student").child(tempstukey).child(attendkey).setValue(new attend());


                    Intent intent = new Intent(add_student.this, MainActivity.class);
                    intent.putExtra("name",students.getNamelast());
                    intent.putExtra("jnum",students.getJnum());
                    intent.putExtra("class_key",key);
                    String name = getIntent().getStringExtra("class_name");
                    Log.i("errors","nameocr:"+name);
                    String time = getIntent().getStringExtra("class_time");
                    Log.i("errors","timeocr:"+time);
                    intent.putExtra("class_name",name);
                    intent.putExtra("class_time",time);
                    startActivity(intent);
                }
            }
        });
    }
    public boolean emptyCheck(String et) {
        if(et.equals("")){
            return false;
        }else{
            return true;
        }
    }
}

