package com.vorsk.crossfitr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;

public class TimerActivity extends Activity 
{	
	private static String TAG = "TimerActivity";
    static final int NUMBER_DIALOG_ID = 0; // Dialog variable
    TextView mTimeDisplay, mElapsedTime;
    private int mHour, mMin, mSec;
    NumberPicker mNumberPicker;
    Button mStart, mStop, mReset, mSetTimer;
    Time timer = new Time();
    
    
	// Timer to update the elapsedTime display
    private final long mFrequency = 100;    // milliseconds
    private final int TICK_WHAT = 2; 
	private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
        	updateElapsedTime();
        	sendMessageDelayed(Message.obtain(this, TICK_WHAT), mFrequency);
        }
    };
    
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timer_tab);
		
		mElapsedTime = (TextView)findViewById(R.id.ElapsedTime);
        
        mStart = (Button)findViewById(R.id.StartButton);
        mStop = (Button)findViewById(R.id.StopButton);
        mSetTimer = (Button)findViewById(R.id.SetTimer);
        
        mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), mFrequency);
    
		// Opens Dialog on click
		mSetTimer.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) 
			{
				showDialog(NUMBER_DIALOG_ID);
	        }
	    });
	}
	
	private NumberPickerDialog.OnNumberSetListener mNumberSetListener =
			new NumberPickerDialog.OnNumberSetListener() {
				public void onNumberSet(int selectedHour, int selectedMin, int selectedSec) {
					mHour = selectedHour;
					mMin = selectedMin;
					mSec = selectedSec;
					mElapsedTime.setText(new StringBuilder().append(mHour)
					    	.append(":").append(formatDigits(mMin)).append(":").append(formatDigits(mSec)));
					showStartButton();
				}

		    };

	
	private void updateElapsedTime() {
		mElapsedTime.setText(getFormattedElapsedTime());
	}
	 
	public long getStartTime(){
		long startTime = (mHour * 3600000) + (mMin * 60000) + (mSec * 1000);
 	    return startTime;
    }

	private String formatElapsedTime(long start) {
		long hours = 0;
		long minutes = 0;
		long seconds = 0;
		long tenths = 0;
		StringBuilder sb = new StringBuilder();
		if(start <= 0){
			timer.stop();
		}
		
		if (start < 1000) {
			tenths = start / 100;
		} 
		
		else if (start < 60000) 
		{
			seconds = start / 1000;
			start -= seconds * 1000;
			tenths = start / 100;
		}
		
		else if (start < 3600000) 
		{
			hours = start / 3600000;
			start -= hours * 3600000;
			minutes = start / 60000;
			start -= minutes * 60000;
			seconds = start / 1000;
			start -= seconds * 1000;
			tenths = start / 100;
		}
			
		sb.append(hours).append(":")
			.append(formatDigits(minutes)).append(":")
			.append(formatDigits(seconds)).append(".")
			.append(tenths);
	
		return sb.toString();
	}
	
	public String getFormattedElapsedTime() {
		return formatElapsedTime(getStartTime() - (getElapsedTime() % 30000));
	}
	
	public long getElapsedTime() {
		return timer.getElapsedTime();
	}
	
	public void onStartClicked(View V) {
		Log.d(TAG, "start button clicked");
		timer.start();
		showPauseButton();
	}

	public void onStopClicked(View v) {
		Log.d(TAG, "pause button clicked");
		timer.stop();
		showStartButton();
	}
	
	private void showPauseButton() {
		Log.d(TAG, "showPauseLapButtons");

		mStart.setVisibility(View.GONE);
		mStop.setVisibility(View.VISIBLE);
	}

	private void showStartButton() {
		Log.d(TAG, "showStartResetButtons");

		mStart.setVisibility(View.VISIBLE);
		mStop.setVisibility(View.GONE);
	}

	
	private String formatDigits(long num) {
		return (num < 10) ? "0" + num : new Long(num).toString();
	}
	
	
	// Override creating the Dialog
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		return new NumberPickerDialog(this, mNumberSetListener, 2, 0);
	}
}
