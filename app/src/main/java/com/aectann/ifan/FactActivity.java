package com.aectann.ifan;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class FactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact);
        init();
    }

    private void init(){
        String extraFact = getIntent().getStringExtra("EXTRA_FACT");
        int index = extraFact.indexOf(" ");
        String number = extraFact.substring(0, index);
        String fact = extraFact.substring(index + 1);
        Toolbar toolbarBack = findViewById(R.id.toolbarBack);
        TextView TVNumber = findViewById(R.id.TVNumber);
        TextView TVFact = findViewById(R.id.TVFact);
        toolbarBack.setOnClickListener(v->onBackPressed());
        TVNumber.setText(number);
        TVFact.setText(fact);
    }

}