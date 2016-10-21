package com.example.matthustahli.radarexposimeter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class OverviewScanPlotActivity extends AppCompatActivity {

    //----------------------------------------------------------------------
    //setup variables
    Rectangle coord;
    Display display;
    Point size;
    ImageView imageView;
    float abstandZwischenBalken = 5; //5dp
    int anzahlBalken = 96;
    Bitmap bitmap;
    Paint paint;
    Canvas canvas;
    Integer activeBar=0;
    TextView textView;
    ArrayList<Integer> fixedBars = new ArrayList<Integer>();
    private static String myMode;
    private final String CHOOSENMODE = "my_mode";
    private final String CHOOSENFREQ = "my_freq";
    public int[] peak= {302, 203, 340, 196, 191, 305, 256, 385, 119, 403, 304, 252, 152, 243, 254, 276, 131, 312, 116, 337, 457, 251, 330, 314, 201, 107, 235, 280, 470, 460, 394, 418, 378, 437, 260, 130, 449, 446, 277, 182, 240, 147, 316, 184, 350, 466, 441, 328, 411, 166, 127, 471, 248, 112, 226, 426, 319, 358, 149, 115, 408, 172, 436, 476, 361, 266, 366, 202, 375, 151, 171, 207, 106, 103, 224, 110, 410, 258, 297, 307, 209, 211, 262, 292, 370, 405, 417, 170, 220, 444, 176, 331, 190, 406, 430, 416, 494, 387, 348, 431, 246, 117, 145, 393, 129, 100, 447, 490, 404, 175, 395, 125, 478, 198, 159, 354, 452, 360, 162, 114, 433, 272, 222, 264, 458, 349, 329, 270, 438, 309, 100};
    public int[] rms= {348, 435, 332, 368, 271, 404, 346, 320, 371, 217, 126, 201, 118, 121, 199, 316, 310, 115, 361, 213, 196, 173, 114, 152, 480, 300, 285, 146, 194, 278, 353, 102, 179, 296, 182, 192, 272, 347, 407, 161, 448, 207, 256, 240, 253, 472, 153, 424, 323, 266, 185, 344, 484, 423, 134, 349, 209, 321, 269, 198, 302, 414, 254, 120, 224, 379, 488, 168, 382, 497, 359, 381, 243, 128, 410, 125, 291, 212, 276, 445, 474, 260, 362, 181, 372, 341, 401, 438, 406, 340, 113, 117, 363, 210, 178, 354, 314, 318, 384, 108, 400, 338, 233, 251, 208, 467, 479, 328, 288, 148, 216, 297, 265, 337, 249, 145, 174, 206, 277, 230, 171, 373, 186, 351, 376, 188, 315, 279, 331, 232, 100};
    private ArrayList<LiveMeasure> measures = new ArrayList<LiveMeasure>();
    public int[] valueToShow;
    int fixedBarsSize=8;

//----------------------------------------------------------------------

    //this is like the MAIN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_scan_plot);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // this removes the status bar (the one showing time, batterie ect..)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //this is MAIN, where magic happens
        getModeFromIntent();
        PaintABar();  //Makes the plot and draws it.
        textView = (TextView) findViewById(R.id.selected_freq);
        ActivateTouchOnPlot();
        ActivateAddButton();
        Log.d("activeBar(onCreate): ",String.valueOf(activeBar));


    }

//TODO  when element deleted, update length of Value Bar's

//----------------------------------------------------------------------

    private void getModeFromIntent() {
        Intent intent =getIntent();
        myMode = intent.getStringExtra("my_mode");
        Toast.makeText(OverviewScanPlotActivity.this, "My Mode is "+myMode, Toast.LENGTH_SHORT).show();
    }


   /*private void saveToSharedPref(ArrayList<Integer> toSave){
        SharedPreferences sp = getSharedPreferences("storedFrequencies", MODE_PRIVATE); //PreferenceManager.getDefaultSharedPreferences(SaveMainActivity.this);
        SharedPreferences.Editor  edit= sp.edit();
       // edit.putString("ArraySize", String.valueOf(fixedBars.size()));
        for (int i=0; i < toSave.size();i++) {
                                //if(fixedBars.size()< fixedBarsSize){fixedBars.add(0);}
            edit.putInt(String.valueOf(i), toSave.get(i));
        }
        edit.commit();
        Toast.makeText(OverviewScanPlotActivity.this,"saved"+ toSave.toString(),Toast.LENGTH_SHORT).show();

    }

    private void loadFromSharedPref(){
        SharedPreferences sp = getSharedPreferences("myValue", MODE_PRIVATE); //PreferenceManager.getDefaultSharedPreferences(SaveMainActivity.this);
        //String size = sp.getString("ArraySize", "0");
        fixedBars.clear();

        for (int i=0; i<8 ;i++){
            fixedBars.add(sp.getInt(String.valueOf(i), 0));
            Log.d("ArraySaved "+String.valueOf(i)+" : ",String.valueOf(fixedBars.get(i)));
        }
        for(int i=8;i>0; i--){
            if(fixedBars.get(i-1) == 0){
                Log.d("ArraySavedremove "+String.valueOf(i-1)+" : ",String.valueOf(fixedBars.get(i-1)));
                fixedBars.remove(i-1);
            }
            else{Log.d("ArraySavedClear "+String.valueOf(i-1)+" : ",String.valueOf(fixedBars.get(i-1)));}
        }
    }*/



/*
    //TODO restore the state from when i left   do i need resume here???
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //get my values back..

        super.onRestoreInstanceState(savedInstanceState);
    }

    //TODO SAVE MY ACTIVITY STATE HERE--save our array here, is this enougth or do i need to save to inner memory..
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        saveToSharedPref();
        super.onSaveInstanceState(outState, outPersistentState);
    }
    //TODO get my values

*/
    @Override
    protected void onPause() {
    //here i need to save
        super.onPause();
        //saveToSharedPref(fixedBars);
    }

    @Override
    protected void onResume() {
       // loadFromSharedPref();
        super.onResume();
       }




    private void ActivateAddButton() {
        //this button adds freq to the list
        ImageButton addButton = (ImageButton) findViewById(R.id.add_freq_button);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Toast.makeText(XY_dataplot_MainActivity.this,String.valueOf(activeBar),Toast.LENGTH_SHORT).show();
                //add frequency to list.. so we can open it in other activity
                fixedBars.add(activeBar);
                chandeBarColorToFixed();
            }
        });
        //this button saves the frequencies, and sets up new intent.
        ImageButton nextButton = (ImageButton) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Toast.makeText(OverviewScanPlotActivity.this,fixedBars.toString(),Toast.LENGTH_SHORT).show();
                //saveToSharedPref(fixedBars);
                OpenDetailViewActivity();

            }
        });
        //this button clears list of fixed bars
        ImageButton clearButton = (ImageButton) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFixedBars();
                fixedBars.clear();
                //saveToSharedPref(fixedBars);
                Log.d("cleared: ","true");
            }
        });

        final Button b_rms = (Button) findViewById(R.id.switch_to_rms);
        final Button b_peak =(Button)findViewById(R.id.switch_to_peak);

        b_rms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coord.height= rms;
                b_peak.setBackgroundColor(OverviewScanPlotActivity.this.getResources().getColor(R.color.background_listview));
                b_rms.setBackgroundColor(OverviewScanPlotActivity.this.getResources().getColor(R.color.activeBar));
                int color =OverviewScanPlotActivity.this.getResources().getColor(R.color.normalBar);
                paint.setColor(color);
                for (int i = 0; i <= anzahlBalken; i++) {
                    canvas.drawRect(coord.getLeft(i), coord.getTop(i), coord.getRight(i), coord.getBottom(i), paint);      //somehow i get bottom wrong!
                }
                imageView.setImageBitmap(bitmap);
                chandeBarColorToFixed();

            }
        });

        b_peak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coord.height=peak;
                b_rms.setBackgroundColor(OverviewScanPlotActivity.this.getResources().getColor(R.color.background_listview));
                b_peak.setBackgroundColor(OverviewScanPlotActivity.this.getResources().getColor(R.color.activeBar));

                int color =OverviewScanPlotActivity.this.getResources().getColor(R.color.normalBar);
                paint.setColor(color);
                for (int i = 0; i <= anzahlBalken; i++) {
                    canvas.drawRect(coord.getLeft(i), coord.getTop(i), coord.getRight(i), coord.getBottom(i), paint);      //somehow i get bottom wrong!
                }
                imageView.setImageBitmap(bitmap);
                chandeBarColorToFixed();
            }
        });

    }

    //this lets us go to the next activity
    private void OpenDetailViewActivity(){
        Intent intent = new Intent(OverviewScanPlotActivity.this, DetailViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(CHOOSENFREQ,fixedBars);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void ActivateTouchOnPlot(){
        final TextView xCoord = (TextView)findViewById(R.id.coord_x);
        final TextView yCoord = (TextView)findViewById(R.id.coord_y);

        View touchView = findViewById(R.id.activity_overview_scan_plot);
        touchView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN: {
                        xCoord.setText(String.valueOf((int) event.getX()));
                        yCoord.setText(String.valueOf((int) event.getY()));
                        int position= returnPosition((int) event.getX());
                        changeBarColorToActiv(position);
                        break;
                    }

                    case MotionEvent.ACTION_MOVE:{

                        xCoord.setText("x: "+String.valueOf((int) event.getX()));
                        yCoord.setText("y: "+String.valueOf((int) event.getY()));
                        int position= returnPosition((int) event.getX());
                        changeBarColorToActiv(position);
                        break;
                    }
                }
                return true; //true= we handled the event!!
            }
        });
    }

    //gets position from coordinates
    public int returnPosition(int x){
        int i=0;
        int fromLeftToRight = (int) coord.getLeft(i);
        while (fromLeftToRight<x){
            if(i==anzahlBalken){        //boundary condition on right edge
                return i;
            }
            i++;
            fromLeftToRight = (int) coord.getLeft(i);
        }
        if(i<=0){       //boundary condition on left edge
            return 0;
        }
        return i-1;
    }

    //change color of only one bar and sets up textview
    public void changeBarColorToActiv(int position){
        //desactivate last visited bar
        changeBarCororToNOTactiv(activeBar);
        activeBar = position;       //with this position we can also use the add button to put it in a list!
        //update textview
        textView.setText(String.valueOf(activeBar)+ " MHz"); //sets selected freq into textVie
        //set color to active
        int color =OverviewScanPlotActivity.this.getResources().getColor(R.color.activeBar);
        paint.setColor(color);
        canvas.drawRect(coord.getLeft(position), coord.getTop(position), coord.getRight(position), coord.getBottom(position), paint);
        imageView.setImageBitmap(bitmap);
        chandeBarColorToFixed();
       // Log.d("activeBar_colorActive: ",String.valueOf(activeBar));
    }

    //changes Bar back to gray color
    private void changeBarCororToNOTactiv(Integer position) {
        paint.setColor(Color.parseColor("#CCCCCC"));
        canvas.drawRect(coord.getLeft(position), coord.getTop(position), coord.getRight(position), coord.getBottom(position), paint);
        imageView.setImageBitmap(bitmap);
        chandeBarColorToFixed();
    }

    //fix added Frequencies and change theire color!
    public void chandeBarColorToFixed(){

        for(int i = 0; i< fixedBars.size(); i++){
            int color =OverviewScanPlotActivity.this.getResources().getColor(R.color.fixedBar);
            paint.setColor(color);
            canvas.drawRect(coord.getLeft(fixedBars.get(i)), coord.getTop(fixedBars.get(i)), coord.getRight(fixedBars.get(i)), coord.getBottom(fixedBars.get(i)), paint);
            imageView.setImageBitmap(bitmap);
        }
    }

    //clears all fixed bars and removes them from list
    public void clearAllFixedBars(){
        for(int i =0; i< fixedBars.size();i++){
            int color =OverviewScanPlotActivity.this.getResources().getColor(R.color.normalBar);
            paint.setColor(color);
            canvas.drawRect(coord.getLeft(fixedBars.get(i)), coord.getTop(fixedBars.get(i)), coord.getRight(fixedBars.get(i)), coord.getBottom(fixedBars.get(i)), paint);
            imageView.setImageBitmap(bitmap);
        }
        fixedBars.clear();
    }


    //Makes the plot and draws it.
    public void PaintABar() {
        // get size of display
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        imageView = (ImageView) findViewById(R.id.image_bitmap);
        float height = 100;
        float breiteBalken = size.x / anzahlBalken + ((1 - anzahlBalken) * abstandZwischenBalken) / anzahlBalken;
        //initialize bitmap to draw on, create paint to set how we want our drawing element..
        //paint sets colors ect. canvas then draws it on the bitmap.
        bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        paint = new Paint();
        canvas = new Canvas(bitmap);  //needs to be conected to bitmap
        //haw should our element look like?
        int color =OverviewScanPlotActivity.this.getResources().getColor(R.color.normalBar);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        //canvas.drawBitmapMesh();
        // canvas.drawPoint(x,y,paint);
        //Toast.makeText(XY_dataplot_MainActivity.this, "X: " + size.x + "Y: " + size.y, Toast.LENGTH_SHORT).show();
        coord = new Rectangle(breiteBalken, anzahlBalken, abstandZwischenBalken, size.y, size.x);
        for (int i = 0; i <= anzahlBalken; i++) {
            canvas.drawRect(coord.getLeft(i), coord.getTop(i), coord.getRight(i), coord.getBottom(i), paint);      //somehow i get bottom wrong!
        }
        imageView.setImageBitmap(bitmap);
    }
}


//----------------------------------------------------------------------




