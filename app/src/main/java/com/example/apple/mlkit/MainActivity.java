package com.example.apple.mlkit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.apple.mlkit.Helper.GraphicOverlay;
import com.example.apple.mlkit.Helper.RectOverlay;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;


import java.net.URL;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    CameraView cameraView;
    GraphicOverlay graphicOverlay;
    Button btd;
    Button btnp;
    Button btns;
    static String emotion;
    static String moodinformation;
    static boolean detectresult = false;
    static boolean back = true;

    /**
     * Activate camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (CameraView)findViewById(R.id.camera_view);
        graphicOverlay = (GraphicOverlay)findViewById(R.id.graphicoverlay);
        btd = (Button) findViewById(R.id.btn_detect);
        btnp = (Button) findViewById(R.id.btn_play);
        btns = (Button) findViewById(R.id.btn_switch);

        btd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();

            }
        });

        btnp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detectresult) {
                    openActivity2();
                } else {
                    Toast.makeText(MainActivity.this, "oops, no face detected,please take picture first",Toast.LENGTH_LONG).show();
                }
            }
        });

        btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (back) {
                    cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
                    back = false;
                } else {
                    cameraView.setFacing(CameraKit.Constants.FACING_BACK);
                    back = true;
                }
            }
        });


        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            /**
             * transfer the imgae caputured to the bitmap
             * @param cameraKitImage captured image
             */

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();

                runFaceDetector(bitmap);

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

    }

    private void runFaceDetector(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetectorOptions opt = new FirebaseVisionFaceDetectorOptions.Builder()
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS).build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(opt);

        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                processFace(firebaseVisionFaces);
                detectresult = true;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                detectresult = false;
            }
        });

    }

    /**
     * processing the recognized face, add overlay and get the smiling probability.
     * @param firebaseVisionFaces a list of recognized face.
     */
    private void processFace(List<FirebaseVisionFace> firebaseVisionFaces) {
        String message;
        for (FirebaseVisionFace face : firebaseVisionFaces) {
            Rect bounds = face.getBoundingBox();
            RectOverlay rect = new RectOverlay(graphicOverlay,bounds);
            graphicOverlay.add(rect);
            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                float smilep = (float) face.getSmilingProbability();
                findmusic(smilep);
                //Toast.makeText(MainActivity.this, Float.toString(smilep),Toast.LENGTH_LONG).show();
                //Toast.makeText(this, moodinformation,Toast.LENGTH_LONG).show();
            }
        }
    }
    public static String getEmotion() {
        return emotion;
    }

    public static String getMoodinformation() {
        return moodinformation;
    }

    public void findmusic(float mood) {
        if (mood < (float) 0.25) {
            emotion = "PLbbWHERQAFRN8DuPjx1d8O6FPaZH5x0ep";
            moodinformation = "You look so sad :( You must wanna listen this...";
        }
        else if (mood > (float) 0.25 && mood < (float) 0.5) {
            emotion = "PL5D7fjEEs5yflZzSZAhxfgQmN6C_6UJ1W";
            moodinformation = "I guess you are in blue mood.";
        }
        else if (mood > (float) 0.5 && mood < (float) 0.75) {
            emotion = "PL4QNnZJr8sRNzSeygGocsBK9rVXhwy9W4";
            moodinformation = "It's time to relax and happy.";
        } else {
            emotion = "PLhGO2bt0EkwvRUioaJMLxrMNhU44lRWg8";
            moodinformation = "You look so happy:) Enjoy this wonderful day!";
        }
    }
    public void openActivity2() {
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("moodinfo",getMoodinformation());
        intent.putExtra("playlist",getEmotion());
        startActivity(intent);
    }
}
