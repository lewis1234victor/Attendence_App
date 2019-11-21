package com.c.idscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class add_class extends AppCompatActivity {
    EditText classname;
    EditText hour,minute;
    RadioButton am,pm;
    Button submit;
    String time;
    String aorp;
    String key;
    DatabaseReference reff;
    FirebaseAuth mFirebaseAuth;
    Classes classes = new Classes();
    Student students = new Student();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        FirebaseApp.initializeApp(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        final String user_id = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        reff= FirebaseDatabase.getInstance().getReference();
        classname = findViewById(R.id.classname);
        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.minute);
        am = findViewById(R.id.am);
        pm = findViewById(R.id.pm);

        students.setNames("tl");
        students.setJnum("J01234567");

        submit = findViewById(R.id.submitclass);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("errors","classname:"+emptyCheck(classname)+"hour:"+emptyCheck(hour)+"minute:"+emptyCheck(minute)+"am and pm:"+am.isChecked()+pm.isChecked())
;                if(!(emptyCheck(classname) && emptyCheck(hour) && emptyCheck(minute) && (am.isChecked() || pm.isChecked())) ){
                    Toast.makeText(add_class.this,"Fields Are Empty!", Toast.LENGTH_SHORT).show();
                }else {
                    classes.setClassname(classname.getText().toString());
                    classes.setClasstime(hour.getText().toString() + ":" + minute.getText().toString() + aorp);
                    time = hour.getText().toString() + minute.getText();
                    String tempstukey;
                    key = reff.child(user_id).child("Classes").push().getKey();
                    tempstukey = reff.child(user_id).child("Classes").child(key).child("Student").push().getKey();
                    reff.child(user_id).child("Classes").child(key).setValue(classes);
                    reff.child(user_id).child("Classes").child(key).child("Student").child(tempstukey).setValue(students);


                    Intent intent = new Intent(add_class.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.am:
                if (checked)
                    aorp="am";
                    break;
            case R.id.pm:
                if (checked)
                    aorp="pm";
                    break;
        }
    }
    public boolean emptyCheck(EditText et) {
        if(et.getText().toString().equals("")){
            return false;
        }else{
            return true;
        }
    }
}
