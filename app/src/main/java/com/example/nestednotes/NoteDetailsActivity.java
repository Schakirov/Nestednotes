package com.example.nestednotes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailsActivity extends AppCompatActivity {

    private EditText noteHeadingEditText;
    private EditText noteDetailsEditText;
    private Button saveNoteButton;
    private long noteId;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        noteHeadingEditText = findViewById(R.id.noteHeadingEditText);
        noteDetailsEditText = findViewById(R.id.noteDetailsEditText);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        databaseHelper = new DatabaseHelper(this);

        // Get noteId from the intent
        Intent intent = getIntent();
        noteId = intent.getLongExtra("noteId", -1);

        if (noteId != -1) {
            // Load existing note details
            DatabaseHelper.Note note = databaseHelper.getNoteById(noteId);
            if (note != null) {
                noteHeadingEditText.setText(note.getHeading());
                noteDetailsEditText.setText(note.getDetails());
            }
        }

        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    private void saveNote() {
        String heading = noteHeadingEditText.getText().toString().trim();
        String details = noteDetailsEditText.getText().toString().trim();

        if (TextUtils.isEmpty(heading)) {
            noteHeadingEditText.setError("Heading is required");
            return;
        }

        if (noteId == -1) {
            // Add a new note
            databaseHelper.addNote(heading, details);
        } else {
            // Update the existing note
            databaseHelper.updateNote(noteId, heading, details);
        }

        // Return to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
