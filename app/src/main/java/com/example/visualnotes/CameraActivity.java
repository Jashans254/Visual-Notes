package com.example.visualnotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView imagePreview;
    Button btnCapture, btnAddMore, btnNext;

    ArrayList<Bitmap> imageBitmaps = new ArrayList<>(); // Store images

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imagePreview = findViewById(R.id.imagePreview);
        btnCapture = findViewById(R.id.btnCapture);
        btnAddMore = findViewById(R.id.btnAddMore);
        btnNext = findViewById(R.id.btnNext);

        btnCapture.setOnClickListener(v -> openCamera());
        btnAddMore.setOnClickListener(v -> openCamera());

        btnNext.setOnClickListener(v -> {
            if (imageBitmaps.isEmpty()) {
                Toast.makeText(this, "Please capture at least one image", Toast.LENGTH_SHORT).show();
            } else {
                // Send bitmaps to NoteEditorActivity
                NoteEditorActivity.bitmaps = imageBitmaps;
                Intent intent = new Intent(CameraActivity.this, NoteEditorActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                imageBitmaps.add(imageBitmap);
                imagePreview.setImageBitmap(imageBitmap); // Show latest image
            }
        }
    }
}
