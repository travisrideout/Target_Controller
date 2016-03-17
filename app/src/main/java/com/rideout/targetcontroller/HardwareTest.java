package com.rideout.targetcontroller;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ToggleButton;

public class HardwareTest extends AppCompatActivity {

	// Target LED Button declarations
	ToggleButton Target1LED;
	ToggleButton Target2LED;
	ToggleButton Target3LED;
	ToggleButton Target4LED;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hardware_test);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Keep screen awake

		// assign io's to buttons
		Target1LED = (ToggleButton) findViewById(R.id.tbTarget1LED);
		Target2LED = (ToggleButton) findViewById(R.id.tbTarget2LED);
		Target3LED = (ToggleButton) findViewById(R.id.tbTarget3LED);
		Target4LED = (ToggleButton) findViewById(R.id.tbTarget4LED);
	}
}

