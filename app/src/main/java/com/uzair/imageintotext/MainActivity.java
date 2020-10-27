package com.uzair.imageintotext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int requestPermissionID = 12 ;
    EditText textView;
    SurfaceView mCameraView;
    CameraSource cameraSource;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.resultText);
        mCameraView = findViewById(R.id.surfaceView);

        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();




        if (!textRecognizer.isOperational()) {
            Log.w("error", "Detector dependencies not loaded yet");
        }
        else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        cameraSource.start(mCameraView.getHolder());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                /**
                 * Release resources for cameraSource
                 */
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>()
            {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                        final SparseArray<TextBlock> items = detections.getDetectedItems();
                        if (items.size() != 0) {

                            textView.post(new Runnable() {
                                @Override
                                public void run() {

                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < items.size(); i++) {
                                        TextBlock item = items.valueAt(i);
                                        stringBuilder.append(item.getValue());
                                        stringBuilder.append("\n");
                                    }
                                    textView.setText(stringBuilder.toString());
                                }
                            });
                        }
                    }

            });
        }










    }


//    public void convertToText(View view)
//    {
//        Bitmap bitmap = BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.prot);
//
//        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
//
//        if(!textRecognizer.isOperational())
//        {
//            Toast.makeText(this, "can't convert to text", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//
//            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);
//
//            StringBuilder stringBuilder = new StringBuilder();
//
//            for(int i=0; i<textBlockSparseArray.size(); i++)
//            {
//                TextBlock myItem = textBlockSparseArray.valueAt(i);
//                stringBuilder.append(myItem.getValue());
//                stringBuilder.append("\n");
//            }
//
//
//            textView.setText(stringBuilder.toString());
//
//
//        }
//
//    }


}