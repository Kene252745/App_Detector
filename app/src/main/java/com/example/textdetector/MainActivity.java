package com.example.textdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.firebase_ml.zzht;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity<FirebaseVisionTextRecognizer> extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private Button captureImageBtn, detectTextBtn;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap image, Bitmap;
    private android.graphics.Bitmap imageBitmap;
    private zzht FirebaseVisionTextRecognizer;
    private FirebaseVisionText firebaseVisionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureImageBtn = findViewById(R.id.capture_image);
        detectTextBtn = findViewById(R.id.detect_text_image);
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_display);
        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                textView.setText("");

            }
        });

        detectTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTextFromImage();
            }
        });
    }




    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void detectTextFromImage() {
        final FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
//       FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "\"Error: \" + e.getMessage()", Toast.LENGTH_SHORT).show();

                Log.d("Error: ", e.getMessage());

            }
        });
    }

//    Task<FirebaseVisionText> result =
//            detector.processImage(image)
//                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//                        @Override
//                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
//                            // here you type for example puttin text on the text view
//                        }
//                    })
//                    .addOnFailureListener(
//                            new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    // some loging or stuff
//                                }
//                            });

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        this.firebaseVisionText = firebaseVisionText;
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if (blockList.size()==0){
            Toast.makeText(this, "No Text in Image", Toast.LENGTH_SHORT).show();
        }
        else{
            for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks()){

                String text = block.getText();
                textView.setText(text);
            }

        }

    }
}
