package com.example.nestednotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

public class NoteDetailsViewModel {

    private final MutableLiveData<List<SymbolUpdate>> symbolUpdates = new MutableLiveData<>();

    public NoteDetailsViewModel() {
        symbolUpdates.setValue(new ArrayList<>());
    }

    public LiveData<List<SymbolUpdate>> getSymbolUpdates() {
        return symbolUpdates;
    }

    public void onSymbolInserted(int position, String symbol) {
        List<SymbolUpdate> updates = new ArrayList<>();
        updates.add(new SymbolUpdate(0, 0, position, ""));
        symbolUpdates.setValue(updates);
    }

    public void onSubstringClicked(int start, int end, String clickedSubstring) {
        List<SymbolUpdate> updates = new ArrayList<>();
        updates.add(new SymbolUpdate(0, 1, end, "!"));
        symbolUpdates.setValue(updates);
    }

    public void onBeforeTextChanged(CharSequence s, int start, int count, int after) {
        // Placeholder for handling text before change
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Placeholder for handling text during change
    }

    public void onAfterTextChanged(CharSequence s) {
        // Placeholder for handling text after change
    }

    public static class SymbolUpdate {
        private final int n_remove;
        private final int n_add;
        private final int position;
        private final String symbol;

        public SymbolUpdate(int n_remove, int n_add, int position, String symbol) {
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
}
