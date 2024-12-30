package com.example.nestednotes;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<DatabaseHelper.Note> notes;
    private List<String> noteHeadings;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        notes = new ArrayList<>();
        noteHeadings = new ArrayList<>();

        // Load notes from the database
        loadNotesFromDatabase();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noteHeadings);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        EditText noteHeadingEditText = findViewById(R.id.noteHeadingEditText);
        Button addNoteButton = findViewById(R.id.addNoteButton);

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String heading = noteHeadingEditText.getText().toString().trim();
                if (!heading.isEmpty()) {
                    // Add to the database and the list
                    long newNoteId = databaseHelper.addNote(heading, "");
                    if (newNoteId != -1) {
                        DatabaseHelper.Note newNote = new DatabaseHelper.Note(newNoteId, heading, "");
                        notes.add(newNote);
                        noteHeadings.add(heading);
                        adapter.notifyDataSetChanged();
                        noteHeadingEditText.setText("");
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Show confirmation dialog before deleting
                showDeleteConfirmationDialog(position);
                return true;
            }
        });
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Get the note to delete
                    DatabaseHelper.Note noteToDelete = notes.get(position);

                    // Delete the note from the database
                    boolean isDeleted = databaseHelper.deleteNoteById(noteToDelete.getId());
                    if (isDeleted) {
                        // Remove the note from the list and update the adapter
                        notes.remove(position);
                        noteHeadings.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void loadNotesFromDatabase() {
        notes.clear();
        noteHeadings.clear();
        notes.addAll(databaseHelper.getAllNotes());
        for (DatabaseHelper.Note note : notes) {
            noteHeadings.add(note.getHeading());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
