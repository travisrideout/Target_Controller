package com.rideout.targetcontroller;

import java.util.Random;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ToggleButton;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.TextView;

//TO-DO
//Mulligans
//Stubborn Plate
//Draw time delay
//Force Reload - settable time delay
//More than one plate active at a time
//Different start types
//Finish icons
//Score board, group by game date, user added description
//Loadable game profiles

public class FallingPlate extends AppCompatActivity implements OnClickListener {    //extends IOIOActivity

	//Layout declarations
	ToggleButton Target1LED;
	ToggleButton Target2LED;
	ToggleButton Target3LED;
	ToggleButton Target4LED;
	Button startButton;
	TextView tvTimerValue;
	TextView HitTimes;
	TextView tvHitArrayTitleBar;
	SlidingDrawer sdSessions;
	TextView tvSessionsTitleBar;
	TextView tvSessionsArray;
	Typeface digital;
	ActionBar ab;

	// Timer declarations	
	long startTime = 0L;
	Handler customHandler = new Handler(); //handler for timer
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	//int countdown;
	boolean timerrunning = false;
	boolean gameStarted = false;
	String timerValue;

	//Media Declarations	
	SoundPool sp;
	int startBuzzer = 0;
	int stopBuzzer = 0;
	int shooterReady = 0;
	int standby = 0;

	//Hit time counter values
	int hitcounter = 0;
	long hittime;
	long lasthittime = 0;
	long hitdelta;
	String hitstring;
	StringBuilder hitstringarray = new StringBuilder();
	String hittimeprev;
	int hitdeltasum;
	String deltasum;

	//Session values
	int session = 1;
	StringBuilder sessionString = new StringBuilder();
	int sessionTimeRawSum;
	int sessionTimeRawAvg;
	String sessionTimeAvg;
	int sessionDeltaRawSum;
	int sessionDeltaRawAvg;
	String sessionDeltaAvg;
	int sessionHitSum;
	double sessionHitAvg;
	int sessionMissSum;
	double sessionMissAvg;
	String sessionAverages;
	String timeString;

	//Settings
	int game;
	int numPlates;
	int difficulty;
	int resets;
	int setupDelay;
	int shotTimerDelay;
	int autoStartDelay;
	boolean hitToStart;
	boolean ready;
	boolean buzzers;
	double platesense;
	boolean debug;

	//Count Down Timers
	CountDownTimer SetupCDT;
	CountDownTimer BuzzerCDT;
	CountDownTimer RoundCDT;

	//game variables
	int plateNumber;
	int selectedPlate;
	boolean gameOver = false;
	long timesup;
	int round = 1;
	int RoundTime;
	int score;
	int molenumber;
	int miss;
	int flashDuration;
	boolean go = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);  //set layout to game
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Keep screen awake
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ab = getSupportActionBar();

		// assign layout objects
		Target1LED = (ToggleButton) findViewById(R.id.tbTarget1LED);
		Target2LED = (ToggleButton) findViewById(R.id.tbTarget2LED);
		Target3LED = (ToggleButton) findViewById(R.id.tbTarget3LED);
		Target4LED = (ToggleButton) findViewById(R.id.tbTarget4LED);
		tvTimerValue = (TextView) findViewById(R.id.tvTimerValue);
		startButton = (Button) findViewById(R.id.startButton);
		HitTimes = (TextView) findViewById(R.id.tvHitArray);
		tvHitArrayTitleBar = (TextView) findViewById(R.id.tvHitArrayTitleBar);
		sdSessions = (SlidingDrawer) findViewById(R.id.sdSessions);
		tvSessionsTitleBar = (TextView) findViewById(R.id.tvSessionsTitleBar);
		tvSessionsArray = (TextView) findViewById(R.id.tvSessionsArray);

		//set on click listeners
		Target1LED.setOnClickListener(this);
		Target2LED.setOnClickListener(this);
		Target3LED.setOnClickListener(this);
		Target4LED.setOnClickListener(this);
		startButton.setOnClickListener(this);

		//assign font
		digital = Typeface.createFromAsset(getAssets(), "fonts/d7_mono.ttf");

		//assign media
		sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		startBuzzer = sp.load(this, R.raw.beep_2250hz_400ms, 1);
		stopBuzzer = sp.load(this, R.raw.beep_500hz_750ms, 1);
		shooterReady = sp.load(this, R.raw.shooter_ready, 1);
		standby = sp.load(this, R.raw.standby, 1);

		//import preferences
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		debug = getPrefs.getBoolean("cbDebug", true);
		platesense = (Integer.parseInt(getPrefs.getString("platesense", "50")));
		platesense = platesense / 100;

		//import game Settings
		SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
		game = settings.getInt("Game", 1);
		numPlates = settings.getInt("NumPlates", 10);
		resets = settings.getInt("Resets", 0);
		setupDelay = settings.getInt("SetupDelay", 2);
		shotTimerDelay = settings.getInt("ShotTimerDelay", 2);
		autoStartDelay = settings.getInt("AutoStartDelay", 0);
		hitToStart = settings.getBoolean("HitToStart", false);
		ready = settings.getBoolean("Ready", true);
		buzzers = settings.getBoolean("Buzzers", true);

		RoundTime = numPlates * 2;    //set RoundTime
		difficulty = ((settings.getInt("Difficulty", 2)) + 1) * 3;
		flashDuration = numPlates * 5;

		//setup layout	
		tvTimerValue.setTextSize(80);
		tvTimerValue.setTypeface(digital);
		HitTimes.setTypeface(digital);
		tvSessionsArray.setTypeface(digital);
		if (!debug) {
			Target1LED.setVisibility(View.GONE);
			Target2LED.setVisibility(View.GONE);
			Target3LED.setVisibility(View.GONE);
			Target4LED.setVisibility(View.GONE);
		}

		//Set game specific UI items
		switch (game) {
			case 1:
				ab.setTitle(R.string.title_activity_falling_plate);
				break;
			case 2:
				ab.setTitle(R.string.title_activity_random_plate);
				break;
			case 3:
				ab.setTitle(R.string.title_activity_countdown);
				break;
			case 4:
				ab.setTitle(R.string.title_activity_stay_down);
				difficulty = (settings.getInt("Difficulty", 2));
				break;
			case 5:
				ab.setTitle(R.string.title_activity_whack_amole);
				tvTimerValue.setTextSize(60);
				tvTimerValue.setText(R.string.whack_amole_time_score);	//00:0 HITS:00
				tvSessionsTitleBar.setText(R.string.whack_amole_lables);//"ROUND" + "    " + "HITS" + "   " + "MISSES"
				break;
			case 6:
				ab.setTitle(R.string.title_activity_free_style);
				break;
		}

		//Session Times drawer scroll handler. Hides hit times array when open/opening
		sdSessions.setOnDrawerScrollListener(new OnDrawerScrollListener() {
			private Runnable mRunnable = new Runnable() {
				@Override
				public void run() {
					while (sdSessions.isMoving()) {
						Thread.yield();
					}
					mHandler.sendEmptyMessage(0);
				}
			};
			private Handler mHandler = new Handler(new Handler.Callback() {
				public boolean handleMessage(Message msg) {
					if (sdSessions.isOpened()) {
						HitTimes.setVisibility(View.GONE);
						tvHitArrayTitleBar.setVisibility(View.GONE);
					} else {
						HitTimes.setVisibility(View.VISIBLE);
						tvHitArrayTitleBar.setVisibility(View.VISIBLE);
					}
					return false;
				}
			});

			@Override
			public void onScrollStarted() {
				if (!sdSessions.isOpened()) {
					HitTimes.setVisibility(View.GONE);
					tvHitArrayTitleBar.setVisibility(View.GONE);
				}
			}

			@Override
			public void onScrollEnded() {
				new Thread(mRunnable).start();
			}
		});
	} //end of onCreate

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		customHandler.removeCallbacks(autoStartThread);
		Intent myIntent = new Intent(FallingPlate.this, MainMenu.class);
		startActivity(myIntent);
	}

	;

	//onClick actions for GUI buttons
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.startButton) {
			if (!gameStarted) {
				customHandler.removeCallbacks(autoStartThread);
				startRound();
			} else {
				if (game != 6) {
					gameOver = true;
				} else if (go) {
					gameOver = true;
				}
				reset();
			}
		} else if (game == 6 && (timerrunning || go)) {
			updateHitCounter();
			customHandler.postDelayed(flash, flashDuration);
		} else {
			switch (v.getId()) {
				case R.id.tbTarget1LED:
					if (!Target1LED.isChecked()) {
						plateNumber = 1;
						customHandler.post(hitHandler);
					}
					break;
				case R.id.tbTarget2LED:
					if (!Target2LED.isChecked()) {
						plateNumber = 2;
						customHandler.post(hitHandler);
					}
					break;
				case R.id.tbTarget3LED:
					if (!Target3LED.isChecked()) {
						plateNumber = 3;
						customHandler.post(hitHandler);
					}
					break;
				case R.id.tbTarget4LED:
					if (!Target4LED.isChecked()) {
						plateNumber = 4;
						customHandler.post(hitHandler);
					}
					break;
			}
		}
	}

	;

	public void startRound() {
		turnOffAllPlates();
		disableAllPlates();
		SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
		resets = settings.getInt("Resets", 0);
		plateNumber = 0; //set plate number to 0 so getNextPlate can choose last hit plate as first plate
		startButton.setText(getString(R.string.bStop));
		gameStarted = true;
		gameOver = false;
		score = 0;
		miss = 0;
		timerValue = ("00:00:000");
		molenumber = 0;
		if (ready && (shooterReady != 0)) {
			sp.play(shooterReady, 1, 1, 0, 0, 1);
		}
		hitstringarray.setLength(0);
		HitTimes.setText(hitstringarray); //clear hit times
		lasthittime = 0;                    //clear last hit time so that delta reads correct on second run
		SetupCDT = new CountDownTimer(setupDelay * 1000, 100) {  //Setup delay, random, tick every 100ms
			public void onTick(long millisUntilFinished) {
				tvTimerValue.setTypeface(Typeface.SANS_SERIF);
				tvTimerValue.setTextSize(70);
				tvTimerValue.setText("Start in: " + ((millisUntilFinished / 1000) + 1));    //show countdown until start
			}

			public void onFinish() {  //after countdown execute
				if (ready && (standby != 0)) {
					sp.play(standby, 1, 1, 0, 0, 1);
				}
				Random startdelay = new Random();    //random number generator for start delay
				BuzzerCDT = new CountDownTimer(startdelay.nextInt(shotTimerDelay) * 1000 + 750, 100) {
					public void onTick(long millisUntilFinished) {
						tvTimerValue.setText("Standby");
					}

					public void onFinish() {
						if (buzzers && (startBuzzer != 0)) {
							sp.play(startBuzzer, 1, 1, 0, 0, 1);
						}
						updatedTime = 0L;
						timeSwapBuff = 0L;
						startTime = SystemClock.uptimeMillis();    //get start time for timer
						if (game != 6) {
							tvTimerValue.setText("");
							tvTimerValue.setTypeface(digital);
							tvTimerValue.setTextSize(80);
							customHandler.postDelayed(updateTimerThread, 0);
							timerrunning = true;
						} else {
							tvTimerValue.setText("GO!");
							go = true;
						}
						hitcounter = 0;
						gameSetup();
					}
				}.start();
			}
		}.start();
	}

	;

	public void reset() {
		startButton.setText(getString(R.string.bStart));
		if (buzzers && (stopBuzzer != 0)) {
			sp.play(stopBuzzer, 1, 1, 0, 0, 1);
		}
		gameStarted = false;
		go = false;
		customHandler.removeCallbacks(updateTimerThread);
		customHandler.removeCallbacks(timeout);
		SetupCDT.cancel();
		if (BuzzerCDT != null) {
			BuzzerCDT.cancel();
		}
		if (RoundCDT != null) {
			RoundCDT.cancel();
		}
		if (!timerrunning) {
			tvTimerValue.setTypeface(digital);
			tvTimerValue.setTextSize(80);
			tvTimerValue.setText(getString(R.string.timerVal));
		}
		timerrunning = false;
		if (hitcounter != 0) {
			avgDelta();
		}
		sessionTimes();
		hitdeltasum = 0;
		hitcounter = 0;
		lasthittime = 0;
		turnOffAllPlates();
		enableAllPlates();
		round++;
		difficulty++;
		if (gameOver) {
			customHandler.removeCallbacks(autoStartThread);
			tvTimerValue.setTypeface(digital);
			tvTimerValue.setTextSize(80);
			tvTimerValue.setText("Game Over");
			SharedPreferences settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
			difficulty = settings.getInt("Difficulty", 2) * 3;
			round = 1;
		} else if (game == 3) {
			tvTimerValue.setTextSize(25);
			tvTimerValue.setText("Round:" + round + "  Timeout = "
					+ String.format("%.2f", 6 - (Math.log10(difficulty - .9) * 2 + 3)) + " sec"
					+ "\n" + "Press START for next round");
		} else if (game == 5) {
			tvTimerValue.setTextSize(25);
			tvTimerValue.setText("You Hit " + String.format("%2d", score) + " out of " + (molenumber - 1) + " Targets" +
					"\n" + "Round: " + round + "  Timeout = "
					+ String.format("%.2f", 6 - (Math.log10(difficulty - .9) * 2 + 3)) + " sec");
		}
	}

	;

	// Timer thread for clock, hit times
	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			updatedTime = timeSwapBuff + timeInMilliseconds;
			int secs = (int) (updatedTime / 1000);
			int mins = secs / 60;
			secs %= 60;
			int milliseconds = (int) (updatedTime % 1000);
			timerValue = (String.format("%02d", mins) + ":" + String.format("%02d", secs)
					+ ":" + String.format("%03d", milliseconds));
			if (game != 5) {
				tvTimerValue.setText(timerValue);
			}
			customHandler.post(this);
		}
	};

	//post time of hit 
	public void updateHitCounter() {
		hitcounter++;
		score++;
		hittime = updatedTime;
		hitdelta = hittime - lasthittime;
		hitdeltasum += hitdelta;
		timeToString(hitdelta, false);
		hitstringarray.append((" " + String.format("%02d", hitcounter)
				+ "  " + timerValue + "  " + timeString + "\n"));
		HitTimes.setText(hitstringarray);
		lasthittime = hittime;
		if (game == 4) {
			plateLife();
		} else if (game == 5) {
			getNextPlate();
		} else if (game == 6 && hitcounter == 1) {
			timerrunning = true;
			go = false;
			tvTimerValue.setTypeface(digital);
			tvTimerValue.setTextSize(80);
			customHandler.postDelayed(updateTimerThread, 0);
		} else {
			roundEndCheck();
		}
	}

	;

	//post average delta time
	public void avgDelta() {
		timeSwapBuff += timeInMilliseconds;
		hitdeltasum /= hitcounter;
		timeToString(hitdeltasum, false);
		deltasum = timeString;
		hitstringarray.append("\n" + "Average Delta = " + deltasum);
		HitTimes.setText(hitstringarray);
	}

	;

	//post session time
	public void sessionTimes() {
		if (gameOver) {
			sessionString.append("--------DNF--------" + "\n");
		} else {
			sessionTimeRawSum += updatedTime;
			sessionTimeRawAvg = sessionTimeRawSum / session;
			timeToString(sessionTimeRawAvg, true);
			sessionTimeAvg = timeString;
			sessionDeltaRawSum += hitdeltasum;
			sessionDeltaRawAvg = sessionDeltaRawSum / session;
			timeToString(sessionDeltaRawAvg, false);
			sessionDeltaAvg = timeString;
			sessionHitSum += score;
			sessionHitAvg = ((float) sessionHitSum) / session;
			sessionMissSum += miss;
			sessionMissAvg = ((float) sessionMissSum) / session;
			if (game == 5) {
				sessionString.append(String.format("%02d", round)
						+ "     " + String.format("%02d", score) + "     "
						+ String.format("%02d", miss) + "\n");
			} else {
				sessionString.append(String.format("%02d", session)
						+ "  " + timerValue + "  " + deltasum + "\n");
			}
			session++;
		}
		if (game == 5) {
			sessionAverages = ("AVG:   " + String.format("%.1f", sessionHitAvg) + "    "
					+ String.format("%.1f", sessionMissAvg));
		} else {
			sessionAverages = ("AVG: " + sessionTimeAvg + "  " + sessionDeltaAvg + " ");
		}
		if (session <= 2) {
			tvSessionsArray.setText(sessionString);
		} else {
			tvSessionsArray.setText(sessionString + "\n" + sessionAverages);
		}
	}

	;

	//convert raw time values to string in format 00:00:000, with/without minutes
	public void timeToString(long time, boolean includeMins) {
		int secs = (int) (time / 1000);
		int mins = secs / 60;
		secs %= 60;
		int milliseconds = (int) (time % 1000);
		if (includeMins) {
			timeString = (String.format("%02d", mins) + ":" + String.format("%02d", secs)
					+ ":" + String.format("%03d", milliseconds));
		} else {
			timeString = (String.format("%02d", secs)
					+ ":" + String.format("%03d", milliseconds));
		}
	}

	;

	//Handles what happens after a "hit", either GUI button or plate sensor
	private Runnable hitHandler = new Runnable() {
		public void run() {
			if (((game == 6) && timerrunning) || ((game == 6) && go)) {
				updateHitCounter();
				customHandler.postDelayed(flash, flashDuration);
			} else {
				if (timerrunning) {
					switch (plateNumber) {
						case 1:
							Target1LED.setChecked(false);
							Target1LED.setEnabled(false);
							break;
						case 2:
							Target2LED.setChecked(false);
							Target2LED.setEnabled(false);
							break;
						case 3:
							Target3LED.setChecked(false);
							Target3LED.setEnabled(false);
							break;
						case 4:
							Target4LED.setChecked(false);
							Target4LED.setEnabled(false);
							break;
					}
					updateHitCounter();
				} else {
					if (hitToStart) {
						startRound();
					}
					switch (plateNumber) {
						case 1:
							Target1LED.setChecked(false);
							break;
						case 2:
							Target2LED.setChecked(false);
							break;
						case 3:
							Target3LED.setChecked(false);
							break;
						case 4:
							Target4LED.setChecked(false);
							break;
					}
				}
			}
		}
	};

	//starts games
	public void gameSetup() {
		switch (game) {
			case 1:    //Falling Plates
				enableAllPlates();
				turnOnAllPlates();
				break;
			case 2: //Random Plates
				getNextPlate();
				break;
			case 3: //Countdown
				getNextPlate();
				timesup = (long) (updatedTime + ((6 - (Math.log10(difficulty - .9) * 2 + 3)) * 1000));
				customHandler.post(timeout);
				break;
			case 4: //Staydown
				enableAllPlates();
				turnOnAllPlates();
				break;
			case 5: //Whack-A-Mole
				getNextPlate();
				customHandler.post(timeout);
				customHandler.post(RoundTimer);
				break;
			case 6: //Free Style
				enableAllPlates();
				break;
		}
	}

	;

	//check to see if rounds/games have ended, then call reset, else reset round	
	public void roundEndCheck() {
		switch (game) {
			case 1:    //Falling Plates
				if (hitcounter % 4 == 0 && resets == 0) {
					reset();
					if (autoStartDelay != 0) {
						customHandler.postDelayed(autoStartThread, (autoStartDelay * 1000));
					}
				} else if (hitcounter % 4 == 0) {
					resets--;
					gameSetup();
				}
				break;
			case 2:    //Random Plates
				if (numPlates == hitcounter) {
					reset();
					if (autoStartDelay != 0) {
						customHandler.postDelayed(autoStartThread, (autoStartDelay * 1000));
					}
				} else {
					getNextPlate();
				}
				break;
			case 3: //Countdown
				if (numPlates == hitcounter) {
					reset();
					if (autoStartDelay != 0) {
						customHandler.postDelayed(autoStartThread, (autoStartDelay * 1000));
					}
				} else {
					getNextPlate();
				}
				break;
			case 4: //Staydown
				if (!Target1LED.isChecked() && !Target2LED.isChecked()
						&& !Target3LED.isChecked() && !Target4LED.isChecked()) {
					if (resets == 0) {
						reset();
						if (autoStartDelay != 0) {
							customHandler.postDelayed(autoStartThread, (autoStartDelay * 1000));
						}
					} else {
						resets--;
						gameSetup();
					}
				}
				break;
			case 5: //Whack-A-Mole
				if (score == 0) {
					gameOver = true;
				}
				break;
			case 6: //Free Style
				break;
		}
	}

	//Pick next random plate
	public void getNextPlate() {
		Random NextPlate = new Random();
		selectedPlate = NextPlate.nextInt(4) + 1;
		if (selectedPlate == plateNumber) {
			getNextPlate();
		} else {
			molenumber++;
			timesup = (long) (updatedTime + ((6 - (Math.log10(difficulty - .9) * 2 + 3)) * 1000));
			customHandler.post(timeout);
			switch (selectedPlate) {
				case 1:
					Target1LED.setChecked(true);
					Target1LED.setEnabled(true);
					break;
				case 2:
					Target2LED.setChecked(true);
					Target2LED.setEnabled(true);
					break;
				case 3:
					Target3LED.setChecked(true);
					Target3LED.setEnabled(true);
					break;
				case 4:
					Target4LED.setChecked(true);
					Target4LED.setEnabled(true);
					break;
			}
		}
	}

	// time out thread used for countdown and whack-a-mole
	private Runnable timeout = new Runnable() {
		public void run() {
			if (updatedTime >= timesup) {
				if (game == 3) {
					gameOver = true;
					reset();
				} else if (game == 5) {
					miss++;
					hitstringarray.append(("MISS" + " " + timerValue + "  " + " MISS " + "\n"));
					HitTimes.setText(hitstringarray);
					turnOffAllPlates();
					disableAllPlates();
					lasthittime = updatedTime;
					getNextPlate();
				}
			} else {
				customHandler.post(this);
			}
		}
	};

	//Randomly reset plate based on difficulty setting
	public void plateLife() {
		Random toughness = new Random();
		if (toughness.nextInt(difficulty + 1) == 0) {
			roundEndCheck();
		} else {
			switch (plateNumber) {
				case 1:
					Target1LED.setChecked(true);
					Target1LED.setEnabled(true);
					break;
				case 2:
					Target2LED.setChecked(true);
					Target2LED.setEnabled(true);
					break;
				case 3:
					Target3LED.setChecked(true);
					Target3LED.setEnabled(true);
					break;
				case 4:
					Target4LED.setChecked(true);
					Target4LED.setEnabled(true);
					break;
			}
		}
	}

	//counting down round timer used whack-a-mole
	private Runnable RoundTimer = new Runnable() {
		public void run() {
			tvTimerValue.setTextSize(60);
			RoundCDT = new CountDownTimer((RoundTime * 1000) + 200, 100) {
				int secs = RoundTime;
				int tenths = 0;

				public void onTick(long millisUntilFinished) {
					tvTimerValue.setText(String.format("%02d", secs) + ":" + tenths
							+ " HITS:" + String.format("%02d", score));
					if (tenths == 0) {
						secs--;
						tenths = 9;
					} else {
						tenths--;
					}
				}

				public void onFinish() {
					roundEndCheck();
					reset();
					if (autoStartDelay != 0) {
						customHandler.postDelayed(autoStartThread, (autoStartDelay * 1000));
					}
				}
			}.start();
		}
	};

	private Runnable autoStartThread = new Runnable() {
		public void run() {
			startRound();
		}
	};

	private Runnable flash = new Runnable() {
		public void run() {
			turnOffAllPlates();
		}
	};

	public void turnOffAllPlates() {
		Target1LED.setChecked(false);    //turn off all plate LED's
		Target2LED.setChecked(false);
		Target3LED.setChecked(false);
		Target4LED.setChecked(false);
	}

	public void turnOnAllPlates() {
		Target1LED.setChecked(true);    //turn on all plate LED's
		Target2LED.setChecked(true);
		Target3LED.setChecked(true);
		Target4LED.setChecked(true);
	}

	public void disableAllPlates() {
		Target1LED.setEnabled(false);    //disable all plates
		Target2LED.setEnabled(false);
		Target3LED.setEnabled(false);
		Target4LED.setEnabled(false);
	}

	public void enableAllPlates() {
		Target1LED.setEnabled(true);    //enable all plates
		Target2LED.setEnabled(true);
		Target3LED.setEnabled(true);
		Target4LED.setEnabled(true);
	}
}
	
	/*class IOIOThread extends BaseIOIOLooper {

		private DigitalOutput out1;
		private DigitalOutput out3;
		private DigitalOutput out5;
		private DigitalOutput out7;
		private AnalogInput in40;
		private AnalogInput in41;
		private AnalogInput in42;
		private AnalogInput in43;
		
		private int debounce;
		private int debounceValue = 10;

		@Override
		protected void setup() throws ConnectionLostException {
			// assign ioio pins
			out1 = ioio_.openDigitalOutput(1, true);
			out3 = ioio_.openDigitalOutput(3, true);
			out5 = ioio_.openDigitalOutput(5, true);
			out7 = ioio_.openDigitalOutput(7, true);
			in40 = ioio_.openAnalogInput(40);
			in41 = ioio_.openAnalogInput(41);
			in42 = ioio_.openAnalogInput(42);
			in43 = ioio_.openAnalogInput(43);			
		}

		 public void loop() throws ConnectionLostException, InterruptedException {
			 
			 try {
				out1.write(Target1LED.isChecked());
				out3.write(Target2LED.isChecked());
				out5.write(Target3LED.isChecked());
				out7.write(Target4LED.isChecked());
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}				
			
			if (debounce>0){ //buffer so that next plate and updatehitcounter only get called once
				debounce--;							
			}
							 
			try {								
				if (in40.read()>platesense&&Target1LED.isChecked()&&debounce==0) {  // Turn off target 1 LED if target 1 switch is triggered 
					plateNumber=1;
					customHandler.post(hitHandler);
					debounce = debounceValue;					
				}else if (in41.read()>platesense&&Target2LED.isChecked()&&debounce==0) { // Turn off target 2 LED if target 2 switch is triggered
					plateNumber=2;
					customHandler.post(hitHandler);
					debounce = debounceValue;
				}else if (in42.read()>platesense&&Target3LED.isChecked()&&debounce==0) { // Turn off target 3 LED if target 3 switch is triggered
					plateNumber=3;
					customHandler.post(hitHandler);
					debounce = debounceValue;
				}else if (in43.read()>platesense&&Target4LED.isChecked()&&debounce==0) { // Turn off target 4 LED if target 4 switch is triggered
					plateNumber=4;
					customHandler.post(hitHandler);
					debounce = debounceValue;
				}else if ((hitToStart==true||game==6)&&timerrunning==false&&debounce==0&&
					(in40.read()>platesense||in41.read()>platesense||in42.read()>platesense||in43.read()>platesense)){
					debounce = debounceValue;
					customHandler.post(hitHandler);
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new IOIOThread();
	}
}
*/