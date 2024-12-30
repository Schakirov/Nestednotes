package com.example.nestednotes;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> noteHeadings;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteHeadings = new ArrayList<>();
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
                    noteHeadings.add(heading);
                    adapter.notifyDataSetChanged();
                    noteHeadingEditText.setText("");
                }
            }
        });
    }
}
