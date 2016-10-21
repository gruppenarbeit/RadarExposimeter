package com.example.matthustahli.radarexposimeter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.PersistableBundle;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetailViewActivity extends AppCompatActivity {

    private final String CHOOSENFREQ = "my_freq";
    ArrayList<Integer> fixedFreq= new ArrayList<Integer>();
    private ArrayAdapter<LiveMeasure> adapter;
    Integer peak[]= {302, 2, 340, 100, 191, 305, 256, 385, 119, 403, 304, 252, 152, 243, 254, 276, 131, 312, 116, 337, 457, 251, 330, 314, 201, 107, 235, 280, 470, 460, 394, 418, 378, 437, 260, 130, 449, 446, 277, 182, 240, 147, 316, 184, 350, 466, 441, 328, 411, 166, 127, 471, 248, 112, 226, 426, 319, 358, 149, 115, 408, 172, 436, 476, 361, 266, 366, 202, 375, 151, 171, 207, 106, 103, 224, 110, 410, 258, 297, 307, 209, 211, 262, 292, 370, 405, 417, 170, 220, 444, 176, 331, 190, 406, 430, 416, 494, 387, 348, 431, 246, 117, 145, 393, 129, 100, 447, 490, 404, 175, 395, 125, 478, 198, 159, 354, 452, 360, 162, 114, 433, 272, 222, 264, 458, 349, 329, 270, 438, 309, 100};
    Integer rms[]= {250, 435, 500, 368, 271, 404, 346, 320, 371, 217, 126, 201, 118, 121, 199, 316, 310, 115, 361, 213, 196, 173, 114, 152, 480, 300, 285, 146, 194, 278, 353, 102, 179, 296, 182, 192, 272, 347, 407, 161, 448, 207, 256, 240, 253, 472, 153, 424, 323, 266, 185, 344, 484, 423, 134, 349, 209, 321, 269, 198, 302, 414, 254, 120, 224, 379, 488, 168, 382, 497, 359, 381, 243, 128, 410, 125, 291, 212, 276, 445, 474, 260, 362, 181, 372, 341, 401, 438, 406, 340, 113, 117, 363, 210, 178, 354, 314, 318, 384, 108, 400, 338, 233, 251, 208, 467, 479, 328, 288, 148, 216, 297, 265, 337, 249, 145, 174, 206, 277, 230, 171, 373, 186, 351, 376, 188, 315, 279, 331, 232, 100};
    private ArrayList<LiveMeasure> measures = new ArrayList<LiveMeasure>();
    Float maxPeak = 0f;
    Float maxRMS = 0f;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); //let it turn again

        //load data
        //loadFromSharedPref();
        //setup and initialize

        getChoosenFreqFromIntent();
        populateMeasurements();
        findMaxValuesOfRmsAndPeak();
        adapter=new MyListAdapter();
        //activate Buttons
        activateEditText();
        activateAddButton();
        //setup list
        populateListView();             // this plots the data to the layout
        //activate clicks on list
        handleClicksOnList();
    }



//ArrayList<ObjectName> arraylist  = extras.getParcelableArrayList("arraylist");

//TODO save instance when leaving and comming back.
/*
  private void saveToSharedPref(ArrayList<Integer> toSave){
        SharedPreferences sp = getSharedPreferences("storedFrequencies", MODE_PRIVATE); //PreferenceManager.getDefaultSharedPreferences(SaveMainActivity.this);
        SharedPreferences.Editor  edit= sp.edit();
        //edit.putString("ArraySize", String.valueOf(fixedFreq.size()));
        for (int i=0; i < toSave.size();i++) {
            edit.putInt(String.valueOf(i), toSave.get(i));
        }
        edit.commit();
    }

    private void loadFromSharedPref(){
        SharedPreferences sp = getSharedPreferences("myValue", MODE_PRIVATE); //PreferenceManager.getDefaultSharedPreferences(SaveMainActivity.this);
        //String size = sp.getString("ArraySize", "0");
        fixedFreq.clear();

        for (int i=0; i<8 ;i++){
            fixedFreq.add(sp.getInt(String.valueOf(i), 0));
            Log.d("ArraySavedShow "+String.valueOf(i)+" : ",String.valueOf(fixedFreq.get(i)));
        }
        for(int i=8;i>0; i--){
            if(fixedFreq.get(i-1) == 0){
                Log.d("ArraySavedShowremove "+String.valueOf(i-1)+" : ",String.valueOf(fixedFreq.get(i-1)));
                fixedFreq.remove(i-1);
            }
            else{Log.d("ArraySavedshowClear "+String.valueOf(i-1)+" : ",String.valueOf(fixedFreq.get(i-1)));}
        }
    }

*/

/*

    @Override
    protected void onPause() {
        super.onPause();
        saveToSharedPref(fixedFreq);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFromSharedPref();
    }
    */

    //------------------------calculate the size of the value bar ------------------------
    private void findMaxValuesOfRmsAndPeak() {
        //set maxPeak
        for(int i=0;i<measures.size();i++){
            if((float)measures.get(i).getPeak() > maxPeak){
                maxPeak= (float) measures.get(i).getPeak();
            }
        }
        //set maxRMS
        for(int i=0; i<measures.size();i++){
            if((float)measures.get(i).getRMS()> maxRMS){
                maxRMS= (float)measures.get(i).getRMS();
            }
        }
    }

    public float getMySizeComparedToMax(Float maxValue, Integer myValue){
        float returnSize;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float barWidth=size.x/4;
        returnSize = (myValue/maxValue)*barWidth;
        Log.d("returnSize", String.valueOf(returnSize));
        return returnSize;
    }



    //------------------------------------------------------------------------------------------------


    private void handleClicksOnList() {
        final ListView listView = (ListView) findViewById(R.id.list_live_data);

        //short click on item- go to specific plot
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override   //display choosen frequency in toast..
            //from here, go to other activity which shows smaller plot..
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LiveMeasure chosenFrequency = measures.get(position);
                Toast.makeText(DetailViewActivity.this, String.valueOf(chosenFrequency.getFrequency()), Toast.LENGTH_SHORT).show();
                //go to new activity
                Intent intent = new Intent(DetailViewActivity.this, TimeLineActivity.class);
                intent.putExtra("frequency" ,chosenFrequency.getFrequency());
                startActivity(intent);
            }
        });


        //todo, update bar size
        //long click on iten- mark to be deleted
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id) {

                //option 1. relode list activity
                measures.remove(position);          //take from list
                //option 2. remove from data and from adapter
                adapter.notifyDataSetChanged();     //update list
                return true;        //true means, i  have handled the event and it should stop here.. if i put false, it will trigger a normal click when removing my finger.
            }
        });
    }


    //   ------------------------add new freq to list section----------------------------
//lets me add frequency to my listview
    private void activateEditText(){
        EditText editText = (EditText) findViewById(R.id.edittext_new_freq);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ImageButton b_add_freq = (ImageButton) findViewById(R.id.b_add_freq_to_list);
                b_add_freq.setVisibility(ImageButton.VISIBLE);
            }
        });

    }

    //adds the new freq to my list
    private void activateAddButton() {

        final ImageButton b_add_freq = (ImageButton) findViewById(R.id.b_add_freq_to_list);
        b_add_freq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.edittext_new_freq);
                if(editText.length()!=0){
                    int freq = Integer.parseInt(editText.getText().toString());
                 //close if textview is empty or not in range
                    if(0<= freq && freq<=130){
                       measures.add(new LiveMeasure(freq,0,rms[freq],peak[freq]));
                       adapter.notifyDataSetChanged();
                     }else{
                    Toast.makeText(DetailViewActivity.this,"OUT OF RANGE", Toast.LENGTH_SHORT).show();}
                }
                editText.getText().clear();
                editText.clearFocus();
                InputMethodManager inputManager = (InputMethodManager) DetailViewActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(DetailViewActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                b_add_freq.setVisibility(ImageButton.GONE);
            }
        });
    }

    //   ----------------------------------------------------------------------------------------------------


    private void populateListView() {
        ListView list = (ListView) findViewById(R.id.list_live_data);
        list.setAdapter(adapter);
    }

    //fill arrayList with values
    private void populateMeasurements() {
        for(int i = 0; i< fixedFreq.size();i++){
            int freq = fixedFreq.get(i);
            measures.add(new LiveMeasure( freq ,0,rms[freq], peak[freq]));
        }
    }

    public void barLengtInRelationToMaxLength(int max, int value){

    }

    // get choosen frequencies from OverViewPlot
    private void getChoosenFreqFromIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        fixedFreq = bundle.getIntegerArrayList(CHOOSENFREQ);
        String hello = fixedFreq.toString();
        Toast.makeText(DetailViewActivity.this, hello, Toast.LENGTH_SHORT).show();
    }


    public class MyListAdapter extends ArrayAdapter<LiveMeasure> {

        public MyListAdapter() {
            //super-because i need to call the base class constructor..general constructor to be an instance of this class, and then, we need to tell in witch view we are in..
            //.this gives me the pointer to the class..  then tell how each thing should look like (done with item_view.) and then give the values..
            super(DetailViewActivity.this, R.layout.one_list_item, measures); //as i am in inner class, i have a reverence to my outer class... so i dont need to populate the list...
        }
        @Override   //position in array,
        public View getView(int position, View convertView, ViewGroup parent) {
            //make shure there is a view to work with
            View itemView = convertView;
            if(convertView == null){
                itemView = getLayoutInflater().inflate(R.layout.one_list_item, parent, false);          //??????????
            }

            //HERE I POPULATE THE UI OF THE LIST
            //find measurement to work with. the object at certain position
            LiveMeasure currentMeasure= measures.get(position);

            //Fill the view, connect elements to layout item_view..
            // ImageView listImage = (ImageView) itemView.findViewById(R.id.list_icon);    //findview on this specific itemView..is new for every new list layer..
            // listImage.setImageResource(R.drawable.radar);

            //befor filling list, update biggest value
            findMaxValuesOfRmsAndPeak();

            //Fill the text views
            //set frequency
            TextView freqText= (TextView) itemView.findViewById(R.id.textview_freq);
            freqText.setText(String.valueOf(currentMeasure.getFrequency())+ " MHz");

            //set median
            TextView rmsBar = (TextView) itemView.findViewById(R.id.textview_rms);
            TextView rmsText = (TextView) itemView.findViewById(R.id.show_rms);
            rmsText.setText(String.valueOf(currentMeasure.getRMS())+" v/m");
            rmsBar.setWidth((int) getMySizeComparedToMax(maxRMS, currentMeasure.getRMS()));

            //set peak
            TextView peakBar = (TextView) itemView.findViewById(R.id.textview_peak);
            TextView peakText = (TextView) itemView.findViewById(R.id.show_peak);
            peakText.setText(String.valueOf(currentMeasure.getPeak())+" v/m");
            peakBar.setWidth((int) getMySizeComparedToMax(maxPeak, currentMeasure.getPeak()));

            return itemView;
        }
    }
}
