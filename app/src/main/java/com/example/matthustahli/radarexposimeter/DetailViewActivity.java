package com.example.matthustahli.radarexposimeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class DetailViewActivity extends AppCompatActivity {

    private final String CHOOSENFREQ = "my_freq";
    ArrayList<Integer> fixedFreq= new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        getChoosenFreqFromIntent();
    }

// get choosen frequencies from OverViewPlot
    private void getChoosenFreqFromIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        fixedFreq = bundle.getIntegerArrayList(CHOOSENFREQ);
        String hello = fixedFreq.toString();
        Toast.makeText(DetailViewActivity.this, hello, Toast.LENGTH_SHORT).show();
    }
}
