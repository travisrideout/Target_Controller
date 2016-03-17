package com.rideout.targetcontroller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    private Gallery gallery;
    TextView tvGameName;
    Button bGameInfo;
    Button bGameSettings;
    Button bPlay;
    int selectedImagePosition = 0;
    int game;
    private List<Drawable> drawables;
    private GalleryImageAdapter galImageAdapter;    
    Button btnClosePopup;
    TextView tvPopup;
    private PopupWindow pwindo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getDrawablesList();
        tvGameName = (TextView) findViewById(R.id.tvGameName);        
        bPlay =(Button)findViewById(R.id.bPlay);
        gallery = (Gallery) findViewById(R.id.gallery);
        
        SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
		game = settings.getInt("Game", 1);
        
        setupUI();        
   
    	bGameInfo = (Button) findViewById(R.id.bGameInfo); 
    	bGameSettings = (Button)findViewById(R.id.bGameSettings);
    	
    	bGameInfo.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    		initiatePopupWindow();    		
    		}
    		});
    	
    	bGameSettings.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {      		
        		Intent myIntent = new Intent(MainMenu.this, GameSettings.class);
        		startActivity(myIntent);
        	}
        });
    }
    
    @Override
    public void onBackPressed() {
       finish();
    }
    
  private void initiatePopupWindow() {
    	try {
    	// We need to get the instance of the LayoutInflater
    	LayoutInflater inflater = (LayoutInflater) MainMenu.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.popup,
    	(ViewGroup) findViewById(R.id.popup_element));
    	pwindo = new PopupWindow(layout, 500, 600, true);
    	pwindo.setBackgroundDrawable(new BitmapDrawable(null,""));
    	pwindo.setOutsideTouchable(true);
    	pwindo.setFocusable(true);
    	pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
    	btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
    	btnClosePopup.setOnClickListener(cancel_button_click_listener);
    	tvPopup = ((TextView)pwindo.getContentView().findViewById(R.id.tvPopup));
    	    	
    	switch (selectedImagePosition + 1){
        case 1: tvPopup.setText(getString(R.string.GameDescriptionFallingPlates));
        		break;
        case 2: tvPopup.setText(getString(R.string.GameDescriptionRandomPlates));
        		break;
        case 3: tvPopup.setText(getString(R.string.GameDescriptionCountdown));
        		break;
        case 4: tvPopup.setText(getString(R.string.GameDescriptionStayDown));
        		break;
        case 5: tvPopup.setText(getString(R.string.GameDescriptionWhackAMole));
        		break;
        case 6: tvPopup.setText(getString(R.string.GameDescriptionFreeStyle));
        		break;
        }

    	} catch (Exception e) {
    	e.printStackTrace();
    	}    	
   }
  
    private OnClickListener cancel_button_click_listener = new OnClickListener() {
    	public void onClick(View v) {
    		pwindo.dismiss();
    	}
    };
    
    private void setupUI() { 
        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                
            	if(pos >= drawables.size()) {
            		selectedImagePosition = pos % drawables.size();
            		}else{
            			selectedImagePosition = pos;
            		} 
            	
            	SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("Game", selectedImagePosition+1);
                editor.apply();
                
            	galImageAdapter.setShowImageBiggerAtPosition(pos);
            	galImageAdapter.notifyDataSetChanged();
            	 
                switch (selectedImagePosition + 1){
                case 1: tvGameName.setText(getString(R.string.title_activity_falling_plate));                		
                		break;
                case 2: tvGameName.setText(getString(R.string.title_activity_random_plate));
                		break;
                case 3: tvGameName.setText(getString(R.string.title_activity_countdown));
                		break;
                case 4: tvGameName.setText(getString(R.string.title_activity_stay_down));
                		break;
                case 5: tvGameName.setText(getString(R.string.title_activity_whack_amole));
                		break;
                case 6: tvGameName.setText(getString(R.string.title_activity_free_style));
                		break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }              
            
        });

        bPlay.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent ourIntent = new Intent(MainMenu.this, FallingPlate.class);
        		startActivity(ourIntent);
        	}
        });
      
        galImageAdapter = new GalleryImageAdapter(this, drawables);
        gallery.setAdapter(galImageAdapter);
        gallery.setSelection(((Integer.MAX_VALUE/2) - (Integer.MAX_VALUE/2) % drawables.size())+(game-1), false);
        
    }

    private void getDrawablesList() {
        drawables = new ArrayList<Drawable>();
        drawables.add(getResources().getDrawable(R.drawable.fallingplate2));
        drawables.add(getResources().getDrawable(R.drawable.randomplate2));
        drawables.add(getResources().getDrawable(R.drawable.countdown2));
        drawables.add(getResources().getDrawable(R.drawable.ic_launcher));
        drawables.add(getResources().getDrawable(R.drawable.whackamole2));
        drawables.add(getResources().getDrawable(R.drawable.ic_launcher));       
    }

    @Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater blowUp = getMenuInflater();
		blowUp.inflate(R.menu.root_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {		
		case R.id.preferences:
			Intent p = new Intent("com.rideout.targetcontroller.PREFS");
			startActivity(p);
			break;
		case R.id.hardwaretest:
			Intent h = new Intent("com.rideout.targetcontroller.HARDWARETEST");
			startActivity(h);
			break;	
		case R.id.exit:
			finish();
			break;
		}
		return false;
	}    
}



