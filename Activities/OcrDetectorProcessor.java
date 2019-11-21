package com.c.idscanner;

import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.Arrays;

public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {
    TextView textView;

   OcrDetectorProcessor(TextView tv) {
        textView = tv;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        for (int i = 0; i < detections.getDetectedItems().size(); ++i) {
            textView.append(detections.getDetectedItems().valueAt(i).getValue());
        }
        String textParse = textView.getText().toString();
        textView.setText("");

        int tempi=0;
        char[] jnum=new char[9];
        Log.i("errors","finding jnum");
        for(int i=0;i<textParse.length();i++){
            if(textParse.charAt(i)=='J' && textParse.charAt(i+1) >='0' && textParse.charAt(i+1) <='9'){
                for(int x=0;x<9;x++) {
                    jnum[x] = textParse.charAt(i+x);
                    Log.i("errors","jnum:"+jnum[x]);
                }
                tempi=i;
                break;
            }
        }
        Log.i("errors","jnum:"+new String(jnum));
        int count=0;
        for(int i=tempi;(textParse.charAt(i) >= 'a' && textParse.charAt(i) <= 'z') || (textParse.charAt(i) >= 'A' && textParse.charAt(i) <= 'Z' || (textParse.charAt(i)==' '));i--) {
                count++;
                Log.i("errors","in1");
        }
        char name[] = new char[count];
        for(int i=1;i<count;i++){
            Log.i("errors","in2");
            name[i] = textParse.charAt(tempi-count+i);
        }
        Log.i("errors","name:"+new String(name));
        Log.i("errors","textviewtxt:"+textParse);
        textView.append("Name:");
        char[] n = Arrays.copyOfRange(name, 1, name.length);
        String namestr = new String(n);
        Log.i("errors","namestrproc:"+namestr);
        textView.append(namestr);
        textView.append("\nJNumber:");
        String jnumstr = new String(jnum);
        textView.append(jnumstr);
        String IDData[] = new String[2];
        IDData[0] = namestr;
        IDData[1] = jnumstr;

    }
    @Override
    public void release() {
    }
}
