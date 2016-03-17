package com.rideout.targetcontroller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

public class GameSettings extends Activity {

	SeekBar sbNumPlates;
	TextView tvNumPlates;
	SeekBar sbDifficulty;
	TextView tvDifficulty;
	SeekBar sbResets;
	TextView tvResets;
	SeekBar sbSetupDelay;
	TextView tvSetupDelay;
	SeekBar sbShotTimerDelay;
	TextView tvShotTimerDelay;
	SeekBar sbAutoStart;
	TextView tvAutoStart;
	CheckBox cbHitToStart;
	CheckBox cbReady;
	CheckBox cbBuzzers;
	Button bPlay;
	
	int game;
	int numPlates;
	int difficulty;
	int resets;	
	int setupDelay;
	int shotTimerDelay;
	int autoStartDelay;
	int autoStartDelayTemp;
	boolean hitToStart;
	boolean ready;
	boolean buzzers;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_settings);
		
		// assign layout objects
		sbNumPlates = (SeekBar) findViewById(R.id.sbNumPlates);
		sbNumPlates.setMax(29);
		tvNumPlates = (TextView) findViewById(R.id.tvNumPlates);
		sbDifficulty = (SeekBar) findViewById(R.id.sbDifficulty);
		tvDifficulty = (TextView) findViewById(R.id.tvDifficulty);		
		sbResets = (SeekBar) findViewById(R.id.sbResets);
		sbResets.setMax(10);
		tvResets = (TextView) findViewById(R.id.tvResets);
		sbSetupDelay = (SeekBar) findViewById(R.id.sbSetupDelay);
		sbSetupDelay.setMax(9);
		tvSetupDelay = (TextView) findViewById(R.id.tvSetupDelay);
		sbShotTimerDelay = (SeekBar) findViewById(R.id.sbShotTimerDelay);
		sbShotTimerDelay.setMax(9);
		tvShotTimerDelay = (TextView) findViewById(R.id.tvShotTimerDelay);
		sbAutoStart = (SeekBar) findViewById(R.id.sbAutoStart);
		sbAutoStart.setMax(10);
		tvAutoStart = (TextView) findViewById(R.id.tvAutoStart);
		cbHitToStart = (CheckBox) findViewById(R.id.cbHitToStart);
		cbReady = (CheckBox) findViewById(R.id.cbReady);
		cbBuzzers = (CheckBox) findViewById(R.id.cbBuzzers);
		bPlay =(Button)findViewById(R.id.bPlay);
		
		//get stored preferences
		SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
		game = settings.getInt("Game", 1);
		numPlates = settings.getInt("NumPlates", 10);
		difficulty = settings.getInt("Difficulty", 2);
		resets = settings.getInt("Resets", 0);
		setupDelay = settings.getInt("SetupDelay", 2);
		shotTimerDelay = settings.getInt("ShotTimerDelay", 2);
		autoStartDelay = settings.getInt("AutoStartDelay", 0);
		hitToStart = settings.getBoolean("HitToStart", false);
		ready = settings.getBoolean("Ready", true);
		buzzers = settings.getBoolean("Buzzers", true);
		
		//Set UI to preference values
		sbNumPlates.setProgress(numPlates);
		sbDifficulty.setProgress(difficulty-1);
		sbResets.setProgress(resets);
		sbSetupDelay.setProgress(setupDelay-1);
		sbShotTimerDelay.setProgress(shotTimerDelay-1);
		sbAutoStart.setProgress(autoStartDelay);
		cbHitToStart.setChecked(hitToStart);
		cbReady.setChecked(ready);
		cbBuzzers.setChecked(buzzers);
		
		//Hide unneeded settings and set tv's
		tvNumPlates.setText("# Targets" + "\n" + numPlates);
		tvResets.setText("Resets Per" + "\n" + "Round " + resets);	
		tvSetupDelay.setText("Setup Delay:" + "\n" + setupDelay + " secs");
		tvShotTimerDelay.setText("Shot Timer" + "\n" + "Delay: " + shotTimerDelay + " secs");
		if(autoStartDelay==0){
			tvAutoStart.setText("Auto Start" + "\n" + "OFF");
		}else{
			tvAutoStart.setText("Auto Start" + "\n" + "Delay: " + autoStartDelay + " secs");
		}
		if (hitToStart){
			tvAutoStart.setText("Auto Start" + "\n" + "OFF");
			sbAutoStart.setEnabled(false);
			autoStartDelayTemp=autoStartDelay;
			autoStartDelay=0;
		}
		
		switch (game){
		//Falling Plates
        case 1: sbNumPlates.setVisibility(View.GONE);
				tvNumPlates.setVisibility(View.GONE);
				sbDifficulty.setVisibility(View.GONE);
				tvDifficulty.setVisibility(View.GONE);
        		break;
        //Random Plates
        case 2: sbDifficulty.setVisibility(View.GONE);
				tvDifficulty.setVisibility(View.GONE);
				tvResets.setVisibility(View.GONE);
				sbResets.setVisibility(View.GONE);
        		break;
        //Countdown
        case 3: tvResets.setVisibility(View.GONE);
				sbResets.setVisibility(View.GONE);
				tvDifficulty.setText("Timeout:" + "\n"+ String.format("%.2f", 6 - (Math.log10((difficulty*3)-.9)*2+3))+" secs");
        		break;
        //Stay Down
        case 4: sbNumPlates.setVisibility(View.GONE);
				tvNumPlates.setVisibility(View.GONE);
				tvDifficulty.setText("Knock Down:" + "\n" + String.format("%.0f",(1.0/(difficulty+1))*100) + "%");
        		break;
        //Whack-A-Mole
        case 5: tvResets.setVisibility(View.GONE);
				sbResets.setVisibility(View.GONE);
				tvNumPlates.setText("Round Time:" + "\n" + numPlates*2 + " secs");
				tvDifficulty.setText("Timeout:" + "\n"+ String.format("%.2f", 6 - (Math.log10((difficulty*3)-.9)*2+3))+" secs");
        		break;
        //Free Style
        case 6: tvResets.setVisibility(View.GONE);
				sbResets.setVisibility(View.GONE);
				sbDifficulty.setVisibility(View.GONE);
				tvDifficulty.setVisibility(View.GONE);
				sbAutoStart.setVisibility(View.GONE);
				tvAutoStart.setVisibility(View.GONE);
				cbHitToStart.setVisibility(View.GONE);
				tvNumPlates.setText("flash =" + "\n" + numPlates*5 + "ms");
        		break;
        }		
		
		//number of plates slider change listener
		sbNumPlates.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {								
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				numPlates = progress+1;
				if(game==5){
					tvNumPlates.setText("Round Time:" + "\n" + numPlates*2 + " secs");
				}else if(game==6){
					tvNumPlates.setText("flash =" + "\n" + (numPlates*5) + "ms");
				}else{
					tvNumPlates.setText("# Targets" + "\n" + numPlates);
				}
				SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("NumPlates", numPlates);
                editor.apply();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	
			}
		}); 
				
		//Difficulty slider change listener
		sbDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {								
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				difficulty = progress+1;
				if (game==3){
					tvDifficulty.setText("Timeout:" + "\n"+ String.format("%.2f", 6 - (Math.log10((difficulty*3)-.9)*2+3))+" secs");
				}else if(game==4){
					tvDifficulty.setText("Knock Down:" + "\n" + String.format("%.0f", (1.0/(difficulty+1))*100) + "%");
				}else if (game==5){
					tvDifficulty.setText("Timeout:" + "\n"+ String.format("%.2f", 6 - (Math.log10((difficulty*3)-.9)*2+3))+" secs");
				}
				SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("Difficulty", difficulty);
                editor.apply();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
				
		//resets slider change listener
		sbResets.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {								
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				resets = progress;
				tvResets.setText("Resets Per" + "\n" + "Round " + resets);
				SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("Resets", resets);
                editor.apply();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	
			}
		});
				
		//Setup Delay slider change listener
		sbSetupDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {								
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setupDelay = progress+1;
				tvSetupDelay.setText("Setup Delay:" + "\n" + setupDelay + " secs");
				SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("SetupDelay", setupDelay);
                editor.apply();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	
			}
		});
				
		//Shot Timer Delay slider change listener
		sbShotTimerDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {								
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				shotTimerDelay = progress+1;
				tvShotTimerDelay.setText("Shot Timer" + "\n" + "Delay: " + shotTimerDelay + " secs");
				SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("ShotTimerDelay", shotTimerDelay);
                editor.apply();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	
			}
		});
		
		//Auto Start Delay slider change listener
		sbAutoStart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				autoStartDelay = progress;
				if (autoStartDelay == 0) {
					tvAutoStart.setText("Auto Start" + "\n" + "OFF");
				} else {
					tvAutoStart.setText("Auto Start" + "\n" + "Delay: " + autoStartDelay + " secs");
				}
				SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("AutoStartDelay", autoStartDelay);
				editor.apply();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		
		//Start next round with hit on click listener
		cbHitToStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cbHitToStart.isChecked()) {
					hitToStart = true;
					tvAutoStart.setText("Auto Start" + "\n" + "OFF");
					sbAutoStart.setEnabled(false);
					autoStartDelayTemp = autoStartDelay;
					autoStartDelay = 0;
				} else {
					hitToStart = false;
					sbAutoStart.setEnabled(true);
					autoStartDelay = autoStartDelayTemp;
					if (autoStartDelay == 0) {
						tvAutoStart.setText("Auto Start" + "\n" + "OFF");
					} else {
						tvAutoStart.setText("Auto Start" + "\n" + "Delay: " + autoStartDelay + " secs");
					}
				}

				SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("HitToStart", hitToStart);
				editor.putInt("AutoStartDelay", autoStartDelay);
				editor.apply();
			}
		});

		//Ready on click listener
		cbReady.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ready = cbReady.isChecked();
				SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("Ready", ready);
				editor.apply();
			}
		});
		
		//Buzzers on click listener
		cbBuzzers.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buzzers = cbBuzzers.isChecked();

			      SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
	                SharedPreferences.Editor editor = settings.edit();
	                editor.putBoolean("Buzzers", buzzers);
	                editor.apply();
			}
		});
		
		bPlay.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent ourIntent = new Intent(GameSettings.this, FallingPlate.class);
        		startActivity(ourIntent);        			
        	}
        });
	}
}
