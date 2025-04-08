package com.example.visualnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class NoteDetailActivity extends AppCompatActivity {

    private LinearLayout imageContainer;
    private TextView noteTextView;
    private ImageView editBtn, deleteBtn;

    private String notePath;
    private File noteFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        imageContainer = findViewById(R.id.detailImageContainer);
        noteTextView = findViewById(R.id.detailNoteText);
        editBtn = findViewById(R.id.editNote);
        deleteBtn = findViewById(R.id.deleteNote);

        notePath = getIntent().getStringExtra("notePath");

        if (notePath == null) {
            Toast.makeText(this, "Note path not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        noteFolder = new File(notePath);
        if (!noteFolder.exists() || !noteFolder.isDirectory()) {
            Toast.makeText(this, "Note folder not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadNoteText(new File(noteFolder, "note.txt"));
        loadAllImages(noteFolder);

        // ✏️ Edit note button
//        editBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(this, NoteEditorActivity.class);
//            intent.putExtra("notePath", notePath);
//            startActivity(intent);
//        });
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(NoteDetailActivity.this, NoteEditorActivity.class);
            intent.putExtra("notePath", notePath);  // pass existing note folder path
            startActivity(intent);
        });


        // 🗑️ Delete note button
        deleteBtn.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (deleteNoteRecursively(noteFolder)) {
                        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean deleteNoteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteNoteRecursively(child);
            }
        }
        return file.delete();
    }

    private void loadNoteText(File noteTextFile) {
        if (!noteTextFile.exists()) return;

        try {
            FileInputStream fis = new FileInputStream(noteTextFile);
            byte[] buffer = new byte[(int) noteTextFile.length()];
            fis.read(buffer);
            fis.close();
            String text = new String(buffer);
            noteTextView.setText(text.trim());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading note text.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAllImages(File noteFolder) {
        File[] files = noteFolder.listFiles();
        if (files == null) return;

        Arrays.sort(files);

        for (File file : files) {
            if (file.getName().startsWith("image_") && file.getName().endsWith(".jpg")) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    ImageView imgView = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(600, 600);
                    params.setMargins(16, 16, 16, 16);
                    imgView.setLayoutParams(params);
                    imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imgView.setImageBitmap(bitmap);

                    // 🖼️ Fullscreen image viewer
                    imgView.setOnClickListener(view -> showImageFullScreen(bitmap));

                    imageContainer.addView(imgView);
                }
            }
        }
    }

    private void showImageFullScreen(Bitmap bitmap) {
        ImageView fullImage = new ImageView(this);
        fullImage.setImageBitmap(bitmap);
        fullImage.setAdjustViewBounds(true);
        fullImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(fullImage)
                .setCancelable(true)
                .create();

        dialog.show();

        // Optional: Set full screen size
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
