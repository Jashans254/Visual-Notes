// adapter/NotesAdapter.java
package com.example.visualnotes.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.visualnotes.NoteDetailActivity;
import com.example.visualnotes.R;
import com.example.visualnotes.model.Note;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> notesList;

    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    public void filterList(List<Note> filteredList) {
        this.notesList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = notesList.get(position);

        holder.noteText.setText(note.getText());
        holder.noteDate.setText(note.getDate());

        if (note.getImagePath() != null && !note.getImagePath().isEmpty()) {
            holder.noteImage.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteDetailActivity.class);
            intent.putExtra("notePath", note.getPath());  // ✅ Pass full note folder path
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView noteImage;
        TextView noteText, noteDate;

        public NoteViewHolder(View itemView) {
            super(itemView);
            noteImage = itemView.findViewById(R.id.noteImage);
            noteText = itemView.findViewById(R.id.noteText);
            noteDate = itemView.findViewById(R.id.noteDate);
        }
    }
}
