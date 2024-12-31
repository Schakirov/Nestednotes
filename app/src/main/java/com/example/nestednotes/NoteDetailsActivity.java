package com.example.nestednotes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailsActivity extends AppCompatActivity {

    private EditText noteHeadingEditText;
    private EditText noteDetailsEditText;
    private Button saveNoteButton;
    private Button insertSymbolButton;
    private long noteId;
    private DatabaseHelper databaseHelper;
    private NoteDetailsViewModel viewModel; // ViewModel instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        noteHeadingEditText = findViewById(R.id.noteHeadingEditText);
        noteDetailsEditText = findViewById(R.id.noteDetailsEditText);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        insertSymbolButton = findViewById(R.id.insertSymbolButton);
        databaseHelper = new DatabaseHelper(this);
        viewModel = new NoteDetailsViewModel(); // Initialize ViewModel

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

        insertSymbolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cursorPosition = noteDetailsEditText.getSelectionStart();
                insertSymbol("■");
                makeSubstringClickable(cursorPosition, cursorPosition + 1);

                // Inform ViewModel about the inserted clickable symbol
                viewModel.onSymbolInserted(cursorPosition, "■");
            }
        });

        // Attach TextWatcher to noteDetailsEditText
        noteDetailsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Inform ViewModel of the upcoming change
                viewModel.onBeforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Inform ViewModel of the change in progress
                viewModel.onTextChanged(s, start, before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Inform ViewModel of the completed change
                viewModel.onAfterTextChanged(s);
            }
        });

        // Observe ViewModel for updates
        viewModel.getSymbolUpdates().observe(this, updates -> {
            // Apply symbol updates to noteDetailsEditText
            Editable editable = noteDetailsEditText.getText();
            //int cursorPosition = noteDetailsEditText.getSelectionStart();

            for (NoteDetailsViewModel.SymbolUpdate update : updates) {
                int cursorPosition = update.getPosition();
                if (update.get_n_remove() > 0) {
                    int start = cursorPosition - update.get_n_remove();
                    if (start >= 0 && cursorPosition <= editable.length()) {
                        editable.delete(start, cursorPosition);
                        cursorPosition = start;
                    }
                }
                if (update.get_n_add() > 0) {
                    editable.insert(cursorPosition, update.getSymbol());
                    cursorPosition += update.getSymbol().length();
                }
                noteDetailsEditText.setSelection(cursorPosition);
            }

            // Set the cursor position after applying updates
            //noteDetailsEditText.setSelection(cursorPosition);
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

    private void insertSymbol(String symbol) {
        int cursorPosition = noteDetailsEditText.getSelectionStart();
        Editable editable = noteDetailsEditText.getText();
        editable.insert(cursorPosition, symbol);
        noteDetailsEditText.setSelection(cursorPosition + symbol.length());
    }

    public void makeSubstringClickable(int start, int end) {
        Spannable spannable = noteDetailsEditText.getText();
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String clickedSubstring = spannable.subSequence(start, end).toString();
                viewModel.onSubstringClicked(start, end, clickedSubstring);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // Optional: Disable underline for clickable text
            }
        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        noteDetailsEditText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
