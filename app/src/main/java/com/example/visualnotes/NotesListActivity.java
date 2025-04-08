package com.example.visualnotes;

import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.visualnotes.adapter.NotesAdapter;
import com.example.visualnotes.model.Note;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class NotesListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<Note> notesList;
    private SearchView searchView;

    private final String dummyEmail = "user@example.com";  // Use same as NoteEditorActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        recyclerView = findViewById(R.id.notesRecyclerView);
        searchView = findViewById(R.id.searchView);

        notesList = loadNotesFromStorage();
        adapter = new NotesAdapter(this, notesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotes(newText);
                return true;
            }
        });
    }

    private void filterNotes(String query) {
        List<Note> filtered = new ArrayList<>();
        for (Note note : notesList) {
            if (note.getText().toLowerCase().contains(query.toLowerCase()) ||
                    note.getDate().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(note);
            }
        }
        adapter.filterList(filtered);
    }

    private List<Note> loadNotesFromStorage() {
        List<Note> notes = new ArrayList<>();

        File notesRootDir = new File(getExternalFilesDir(null), "VisualNotes/" + dummyEmail);
        if (notesRootDir.exists() && notesRootDir.isDirectory()) {
            File[] noteFolders = notesRootDir.listFiles();
            if (noteFolders != null) {
                for (File noteFolder : noteFolders) {
                    if (noteFolder.isDirectory()) {
                        String noteText = "";
                        String imagePath = "";
                        String date = getFormattedDate(noteFolder.lastModified());

                        // Read text
                        File noteTextFile = new File(noteFolder, "note.txt");
                        if (noteTextFile.exists()) {
                            noteText = readTextFromFile(noteTextFile);
                        }

                        // Use first image
                        File imageFile = new File(noteFolder, "image_1.jpg");
                        if (imageFile.exists()) {
                            imagePath = imageFile.getAbsolutePath();
                        }

                        notes.add(new Note(imagePath, noteText, date, noteFolder.getAbsolutePath()));
                    }
                }
            }
        }

        return notes;
    }

    private String readTextFromFile(File file) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getFormattedDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
