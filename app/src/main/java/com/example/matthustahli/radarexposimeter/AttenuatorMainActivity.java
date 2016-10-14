package com.example.matthustahli.radarexposimeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;

public class AttenuatorMainActivity extends AppCompatActivity {

    Button b_modeNormal;
    Button b_mode21dB;
    Button b_mode41dB;
    Button b_mode_accumulation;
    private static String CHOOSENMODE = "my_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attenuator_main);
        activateButtons();
    }


    //connect buttons to view and handle click events
    private void activateButtons() {
        b_modeNormal=(Button) findViewById(R.id.b_mode_normal);
        b_mode21dB =(Button) findViewById(R.id.b_mode_21db);
        b_mode41dB = (Button) findViewById(R.id.b_mode_42db);
        b_mode_accumulation = (Button) findViewById(R.id.b_mode_accumulator);

        b_modeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextActivityWithSpecifivMode("normal");
            }
        });
        b_mode21dB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextActivityWithSpecifivMode("21dB");
            }
        });
        b_mode41dB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextActivityWithSpecifivMode("41dB");
            }
        });
        b_mode_accumulation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextActivityWithSpecifivMode("accumulation");
            }
        });
    }


    private void goToNextActivityWithSpecifivMode(String mode){
        Intent intent = new Intent(AttenuatorMainActivity.this, OverviewScanPlotActivity.class);
        intent.putExtra(CHOOSENMODE, mode);
        startActivity(intent);
    }
}

