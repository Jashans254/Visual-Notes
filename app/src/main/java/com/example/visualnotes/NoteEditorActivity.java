package com.example.visualnotes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class NoteEditorActivity extends AppCompatActivity {

    public static ArrayList<Bitmap> bitmaps = new ArrayList<>();

    private LinearLayout imageContainer;
    private EditText editNote;
    private Button btnSave, btnDiscard;

    private String dummyEmail = "user@example.com";  // Replace later with Google sign-in
    private static final int REQUEST_STORAGE_PERMISSIONS = 1001;

    private String existingNotePath = null;
    private File noteFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        imageContainer = findViewById(R.id.imageContainer);
        editNote = findViewById(R.id.editNote);
        btnSave = findViewById(R.id.btnSave);
        btnDiscard = findViewById(R.id.btnDiscard);

        requestStoragePermissions();

        // Check if editing existing note
        existingNotePath = getIntent().getStringExtra("notePath");
        if (existingNotePath != null) {
            noteFolder = new File(existingNotePath);
            loadExistingNote(noteFolder);
        }

        showImages();

        btnSave.setOnClickListener(v -> saveNote());
        btnDiscard.setOnClickListener(v -> {
            bitmaps.clear();
            finish();
        });
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSIONS);
        }
    }

    private void loadExistingNote(File folder) {
        try {
            // Load text
            File noteFile = new File(folder, "note.txt");
            if (noteFile.exists()) {
                FileInputStream fis = new FileInputStream(noteFile);
                byte[] buffer = new byte[(int) noteFile.length()];
                fis.read(buffer);
                fis.close();
                String text = new String(buffer);
                editNote.setText(text.trim());
            }

            // Load images
            File[] imageFiles = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
            if (imageFiles != null) {
                Arrays.sort(imageFiles, Comparator.comparing(File::getName)); // Sort for consistent order
                bitmaps.clear(); // Clear old ones if any
                for (File imgFile : imageFiles) {
                    Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (bmp != null) bitmaps.add(bmp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading note: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showImages() {
        imageContainer.removeAllViews();
        for (Bitmap bitmap : bitmaps) {
            if (bitmap != null) {
                ImageView img = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);
                params.setMargins(8, 8, 8, 8);
                img.setLayoutParams(params);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                img.setImageBitmap(bitmap);
                imageContainer.addView(img);
            }
        }
    }

    private void saveNote() {
        String noteText = editNote.getText().toString().trim();

        if (noteText.isEmpty() && bitmaps.isEmpty()) {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set note folder
        if (noteFolder == null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            noteFolder = new File(getExternalFilesDir(null), "VisualNotes/" + dummyEmail + "/Note_" + timestamp);
        }

        if (!noteFolder.exists()) noteFolder.mkdirs();

        // Clear old images if editing
        if (existingNotePath != null) {
            File[] oldImages = noteFolder.listFiles((dir, name) -> name.startsWith("image_"));
            if (oldImages != null) {
                for (File f : oldImages) f.delete();
            }
        }

        // Save each bitmap image
        int i = 1;
        for (Bitmap bmp : bitmaps) {
            if (bmp != null) {
                File imageFile = new File(noteFolder, "image_" + i + ".jpg");
                try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error saving image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                i++;
            }
        }

        // Save note text
        File noteFile = new File(noteFolder, "note.txt");
        try (FileOutputStream fos = new FileOutputStream(noteFile)) {
            fos.write(noteText.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving note: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Toast.makeText(this, "Note saved successfully!", Toast.LENGTH_LONG).show();
        bitmaps.clear();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSIONS) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission required to save notes.", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
