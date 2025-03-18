package com.sunmeat.room;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private WordViewModel mWordViewModel;

    private final ActivityResultLauncher<Intent> newWordActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String wordText = result.getData().getStringExtra(NewWordActivity.EXTRA_REPLY);
                    if (wordText != null && !wordText.isEmpty()) {
                        Word word = new Word(wordText);
                        mWordViewModel.insert(word);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.empty_not_saved, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewWordActivity.class);
            newWordActivityLauncher.launch(intent);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WordListAdapter adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mWordViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(WordViewModel.class);

        mWordViewModel.getAllWords().observe(this, adapter::setWords);

        TextInputEditText editWord = findViewById(R.id.edit_word);
        MaterialButton buttonSave = findViewById(R.id.button_save);

        buttonSave.setOnClickListener(view -> {
            String wordText = editWord.getText().toString().trim();
            if (!wordText.isEmpty()) {
                Word word = new Word(wordText);
                mWordViewModel.insert(word);
                editWord.setText("");
            } else {
                Toast.makeText(this, "Введите слово", Toast.LENGTH_SHORT).show();
            }
        });
    }
}