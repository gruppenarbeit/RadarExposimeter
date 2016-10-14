package com.example.matthustahli.radarexposimeter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OverviewScanPlotActivity extends AppCompatActivity {

    //----------------------------------------------------------------------
    //setup variables
    Rectangle coord;
    Display display;
    Point size;
    ImageView imageView;
    float abstandZwischenBalken = 5; //5dp
    int anzahlBalken = 130;
    Bitmap bitmap;
    Paint paint;
    Canvas canvas;
    Integer activeBar=0;
    TextView textView;
    ArrayList<Integer> fixedBars = new ArrayList<Integer>();
    private static String myMode;
    private final String CHOOSENMODE = "my_mode";
    private final String CHOOSENFREQ = "my_freq";

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



//----------------------------------------------------------------------

    private void getModeFromIntent() {
        Intent intent =getIntent();
        myMode = intent.getStringExtra("my_mode");
        Toast.makeText(OverviewScanPlotActivity.this, "My Mode is "+myMode, Toast.LENGTH_SHORT).show();
    }


    //TODO restore the state from when i left   do i need resume here???
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //get my values back..

        super.onRestoreInstanceState(savedInstanceState);
    }

    //TODO SAVE MY ACTIVITY STATE HERE--save our array here, is this enougth or do i need to save to inner memory..
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    //TODO get my values





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
               // Toast.makeText(OverviewScanPlotActivity.this,"well done Einstein!",Toast.LENGTH_SHORT).show();
                OpenDetailViewActivity();

            }
        });
        //this button clears list of fixed bars
        ImageButton clearButton = (ImageButton) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFixedBars();
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
        textView.setText(String.valueOf(activeBar)+ " GHz"); //sets selected freq into textVie
        //set color to active
        int color =OverviewScanPlotActivity.this.getResources().getColor(R.color.activeBar);
        paint.setColor(color);
        canvas.drawRect(coord.getLeft(position), coord.getTop(position), coord.getRight(position), coord.getBottom(position), paint);
        imageView.setImageBitmap(bitmap);
        chandeBarColorToFixed();
        Log.d("activeBar_colorActive: ",String.valueOf(activeBar));
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
        for(int i =0; i< fixedBars.size(); i++){
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
        coord = new Rectangle(breiteBalken, height, anzahlBalken, abstandZwischenBalken, size.y, size.x);
        for (int i = 0; i <= anzahlBalken; i++) {
            canvas.drawRect(coord.getLeft(i), coord.getTop(i), coord.getRight(i), coord.getBottom(i), paint);      //somehow i get bottom wrong!
        }
        imageView.setImageBitmap(bitmap);
    }
}


//----------------------------------------------------------------------




