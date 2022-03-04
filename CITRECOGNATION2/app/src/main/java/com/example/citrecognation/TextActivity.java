package com.example.citrecognation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.*;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.VolumeShaper;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class TextActivity extends AppCompatActivity {
    private ImageButton edit_btn,done_btn;
    private String resultText;
    private EditText recognizeText;
    private TextView text;
    private Editable editable;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        toolbar.setTitle("Detected Text");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




        recognizeText=findViewById(R.id.recognize_text);
        text=findViewById(R.id.textView4);
        resultText= getIntent().getStringExtra("text");
        text.setText(resultText);


        edit_btn=findViewById(R.id.edit_btn);

        edit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                text.setVisibility(View.GONE);
                recognizeText.setVisibility(View.VISIBLE);
                done_btn.setVisibility(View.VISIBLE);
                resultText= getIntent().getStringExtra("text");
                recognizeText.setText(resultText);
                editable=recognizeText.getText();

            }
        });
        done_btn=findViewById(R.id.done_btn);

        done_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                recognizeText.setVisibility(View.GONE);

                edit_btn.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                done_btn.setVisibility(View.GONE);
                text.setText(editable);

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_to_text, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                MyDatabaseHelper myDB = new MyDatabaseHelper(TextActivity.this);
                myDB.addRecord(text.getText().toString().trim());
                break;
            case R.id.action_copy:
                copy(text.getText().toString());
                break;
            case R.id.action_share:
                share(text.getText().toString());
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(TextActivity.this, "copied", Toast.LENGTH_SHORT).show();


    }
    public void share(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Text Recognized");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share..."));
    }
}