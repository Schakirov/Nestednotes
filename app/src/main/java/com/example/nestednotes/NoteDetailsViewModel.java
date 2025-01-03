package com.example.nestednotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import androidx.core.util.Pair;
import com.example.nestednotes.NoteTreeMarkupConverter;

public class NoteDetailsViewModel {

    private final MutableLiveData<List<SymbolUpdate>> symbolUpdates = new MutableLiveData<>();

    private final MutableLiveData<List<ClickableUpdate>> clickableUpdates = new MutableLiveData<>();

    private DatabaseHelper databaseHelper;

    public String noteDetails;
    public String noteHeading;
    public NoteTreeHandler tree = new NoteTreeHandler();

    public NoteDetailsViewModel(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        symbolUpdates.setValue(new ArrayList<>());
        clickableUpdates.setValue(new ArrayList<>());
    }

    public LiveData<List<SymbolUpdate>> getSymbolUpdates() {
        return symbolUpdates;
    }

    public LiveData<List<ClickableUpdate>> getClickableUpdates() {
        return clickableUpdates;
    }

    public Pair<String, String>  onNoteLoading(long noteId) {
        if (noteId != -1) {
            // Load existing note details
            DatabaseHelper.Note note = databaseHelper.getNoteById(noteId);
            if (note != null) {
                noteHeading = note.getHeading();
                String markup = note.getDetails();
                tree = NoteTreeMarkupConverter.buildTreeFromMarkup(markup);
                //noteDetails = note.getDetails();
                //tree.addSubstring(0, noteDetails);
                noteDetails = tree.calculateSymbolsFromTree();
                return new Pair<>(noteHeading, noteDetails);
            }
        }
        return null;
    }

    public void onNoteLoadingMakeSymbolsClickable(String noteDetails) {
        List<ClickableUpdate> updates_clickable = new ArrayList<>();
        int offset = 0;
        for (int i = 0; i < noteDetails.length(); i++) {
            char c = noteDetails.charAt(i);
            if (c == '■' || c == '◆') {
                updates_clickable.add(new ClickableUpdate(offset + i, offset + i + 1));
            }
        }
        clickableUpdates.setValue(updates_clickable);
    }

    public void onSymbolInserted(int position, String symbol) {
        tree.addChildAtPosition(position);
        List<SymbolUpdate> updates = new ArrayList<>();
        updates.add(new SymbolUpdate(0, 0, position, "\n... \n"));
        symbolUpdates.setValue(updates);
        position = position;
    }

    public void onSubstringClicked(int start, int end, String clickedSubstring) {
        List<SymbolUpdate> updates = new ArrayList<>();
        List<ClickableUpdate> updates_clickable = new ArrayList<>();
        if (Objects.equals(clickedSubstring, "■")) {
            //for Tree
            tree.expandNode(start);
            // for View
            String to_add = String.join("", tree.calculateSymbolsToAddForExpansion(start));
            updates.add(new SymbolUpdate(1, to_add.length(), end, to_add));

            // Find and make symbols in to_add clickable
            int offset = end - 1;
            for (int i = 0; i < to_add.length(); i++) {
                char c = to_add.charAt(i);
                if (c == '■' || c == '◆') {
                    updates_clickable.add(new ClickableUpdate(offset + i, offset + i + 1));
                }
            }
            //final updates for View
            symbolUpdates.setValue(updates);
            clickableUpdates.setValue(updates_clickable);
        } else if (Objects.equals(clickedSubstring, "◆")) {
            // for Tree
            tree.minimizeNode(start);
            // for View (NoteDetailsActivity)
            String to_remove = String.join("", tree.calculateSymbolsToDeleteForMinimization(start));
            updates.add(new SymbolUpdate(- to_remove.length(), 0, start, ""));
            updates.add(new SymbolUpdate(0, 1, start, "■"));
            updates_clickable.add(new ClickableUpdate(start, start + 1));
            // final updates for View
            symbolUpdates.setValue(updates);
            clickableUpdates.setValue(updates_clickable);
        }
        end = end;
    }

    public void onBeforeTextChanged(CharSequence s, int start, int count, int after) {
        // Placeholder for handling text before change
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Update the noteDetails string with the correct part of the changed text
        /*noteDetails = noteDetails.substring(0, start)
                + s.subSequence(start, start + count)
                + noteDetails.substring(start + before);*/
        tree.addSubstring(start, s.subSequence(start, start + count).toString());
        noteDetails = noteDetails;
    }

    public void onAfterTextChanged(CharSequence s) {
        // Placeholder for handling text after change
    }

    public void saveNote(long noteId, String heading, String details) {
        String markup = NoteTreeMarkupConverter.buildMarkupFromTree(tree.getRoot());
        if (noteId == -1) {
            // Add a new note
            databaseHelper.addNote(heading, markup);
        } else {
            // Update the existing note
            databaseHelper.updateNote(noteId, heading, markup);
        }
    }

    public static class SymbolUpdate {
        private final int n_remove;
        private final int n_add;
        private final int position;
        private final String symbol;

        public SymbolUpdate(int n_remove, int n_add, int position, String symbol) {
            // n_remove is deleted to the left of position if positive
            // otherwise to the right of position
            this.n_remove = n_remove;
            this.n_add = n_add;
            this.position = position;
            this.symbol = symbol;
        }

        public int get_n_add() {
            return n_add;
        }

        public int get_n_remove() {
            return n_remove;
        }

        public int getPosition() {
            return position;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public static class ClickableUpdate {
        private final int start;
        private final int end;

        public ClickableUpdate(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}
