package com.example.firebasedb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
EditText txtfirst, txtlast;
Button save;
DatabaseReference reff;
Student student = new Student();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtfirst=(EditText)findViewById(R.id.txtfirst);
        txtlast=(EditText)findViewById(R.id.txtlast);
        save=(Button)findViewById(R.id.save);
        reff= FirebaseDatabase.getInstance().getReference().child("Student");
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                student.setNamefirst(txtfirst.getText().toString().trim());
                student.setNamelast(txtlast.getText().toString().trim());
                reff.push().setValue(student);
                Toast.makeText(MainActivity.this,"data inserted",Toast.LENGTH_LONG).show();
            }
        });
    }
}
