package com.c.idscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.samples.vision.ocrreader.R;
import com.c.idscanner.camera.*;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public final class OcrCaptureActivity extends AppCompatActivity {
    private static final String TAG = "errors";

    private static final int RC_HANDLE_GMS = 9001;

    private static final int RC_HANDLE_CAMERA_PERM = 2;
    public static final String TextBlockObject = "String";
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    private CameraSource mCameraSource;
    TextView textview;
    Button parsecard;
    Button checkStudent;
    Button accept;
    CameraSourcePreview mPreview;
    String clakey,key,tempstukey;
    DatabaseReference reff;
    FirebaseAuth mFirebaseAuth;
    Student students = new Student();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.ocr_capture);

        FirebaseApp.initializeApp(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        textview = (TextView)findViewById(R.id.textView);
        parsecard = (Button)findViewById(R.id.btntakepic);
        checkStudent = (Button)findViewById(R.id.btnacceptid);
        mPreview = (CameraSourcePreview)findViewById(R.id.preview);
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
        boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);

        reff= FirebaseDatabase.getInstance().getReference();
        final String user_id = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        clakey = getIntent().getStringExtra("class_key");
        Log.i("errors","clakeyocr:"+clakey);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus,useFlash);
        } else {
            requestCameraPermission();
        }
        parsecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textview.setText("");
                Log.i(TAG,"click");
                startCameraSource();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPreview.stop();
                    }
                },250);

            }
        });
        checkStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OcrCaptureActivity.this, MainActivity.class);
                intent.putExtra("class_key",clakey);
                String tempuser = textview.getText().toString();
                if(tempuser.equals("")) {

                }
                else {
                    String[] info = tempuser.split("\n");
                    String[] nametoken=info[0].split(":");
                    String[] jnumtoken=info[1].split(":");
                    String name=nametoken[1];

                    String jnum=jnumtoken[1];
                    name=name.replaceAll("&#65533;","");
                    students.setNames(name);
                    students.setJnum(jnum);
                    key = getIntent().getStringExtra("class_key");
                    Log.i("errors","inOCR"+key);
                    tempstukey = reff.child(user_id).child("Classes").child(key).child("Student").push().getKey();
                    //reff.child(user_id).child("Classes").child(key).child("Student").child(tempstukey).setValue(students);
                    intent.putExtra("name",name);
                    intent.putExtra("jnum",jnum);

                }

                String name = getIntent().getStringExtra("class_name");
                Log.i("errors","nameocr:"+name);
                String time = getIntent().getStringExtra("class_time");
                Log.i("errors","timeocr:"+time);
                intent.putExtra("class_name",name);
                intent.putExtra("class_time",time);
                Log.i(TAG,"past settings");

                startActivity(intent);
            }
        });

    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

    }

    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(textview));
        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "LOW STORAGE", Toast.LENGTH_LONG).show();
                Log.w(TAG, "LOW STORAGE");
            }
        }

        mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .build();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus,useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length + " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
    }


    @Override
    protected void onResume() {
        super.onResume();
        //startCameraSource();
        Log.i(TAG,"RESUME");
    }
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }
        try {
            mPreview.start(mCameraSource);
            Log.i(TAG,"started");
        }catch(IOException ioe) {
            Log.i(TAG,"ioe:"+ioe);
        }
    }
}