package com.vorsk.crossfitr;

import java.io.File;
import java.sql.Date;

import com.vorsk.crossfitr.models.WorkoutSessionModel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CrossFitrActivity extends Activity implements OnClickListener {
	private TextView workoutsText;
	private TextView calendarText;
	private TextView profileText;
	private TextView numOfWorkouts;
	private TextView lastWorkouts;
	private TextView numOfAchievments;
	
	private TextView statusDisplay1;
	private TextView statusDisplay2;
	private TextView statusDisplay3;
	
	private ImageView userPic;
	
	private File file;
	private Typeface font;
	
	WorkoutSessionModel sessionModel = new WorkoutSessionModel(this);


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		

		font = Typeface.createFromAsset(this.getAssets(),
				"fonts/Roboto-Thin.ttf");
		
		// User photo
		file = new File(Environment.getExternalStorageDirectory(), "profile.png");
		userPic = (ImageView) this.findViewById(R.id.main_button_userpic);
		Bitmap bMap = BitmapFactory.decodeFile(file.toString());
		if(bMap != null){
			userPic.setImageBitmap(bMap);
		}

		// workouts button
		View workoutButton = findViewById(R.id.main_button_workouts);
		workoutButton.setOnClickListener(this);
		workoutsText = (TextView) findViewById(R.id.main_button_workouts);
		workoutsText.setTypeface(font);

		// calendar button
		View calendarButton = findViewById(R.id.main_button_calendar);
		calendarButton.setOnClickListener(this);
		calendarText = (TextView) findViewById(R.id.main_button_calendar);
		calendarText.setTypeface(font);

		// profile button
		View profileButton = findViewById(R.id.main_button_profile);
		profileButton.setOnClickListener(this);
		profileText = (TextView) findViewById(R.id.main_button_profile);
		profileText.setTypeface(font);
		
		// status displays
		statusDisplay1 = (TextView) findViewById(R.id.status_display1);
		statusDisplay1.setTypeface(font);
		
		statusDisplay2 = (TextView) findViewById(R.id.status_display2);
		statusDisplay2.setTypeface(font);
		
		statusDisplay3 = (TextView) findViewById(R.id.status_display3);
		statusDisplay3.setTypeface(font);

		/** user status dialog **/
		
		numOfWorkouts = (TextView) findViewById(R.id.main_num_of_workouts);
		sessionModel.open();
		numOfWorkouts.setText(" " + sessionModel.getTotal());
		numOfWorkouts.setTypeface(font);

		Date date;
		
		try{
			date = new Date((sessionModel.getMostRecent(null).date_created));
		}
		catch(Exception e){
			date = new Date(0);
		}
		
		lastWorkouts = (TextView) findViewById(R.id.main_last_workout);
		lastWorkouts.setText(" " + date.toString());
		lastWorkouts.setTypeface(font);

		
		sessionModel.close();
		numOfAchievments = (TextView) findViewById(R.id.main_num_of_achievments);
		numOfAchievments.setText("0");
		numOfAchievments.setTypeface(font);
	}
	
	public void onResume()
	{
		super.onResume();
		
		// User photo
		file = new File(Environment.getExternalStorageDirectory(), "profile.png");
		userPic = (ImageView) this.findViewById(R.id.main_button_userpic);
		Bitmap bMap = BitmapFactory.decodeFile(file.toString());
		if(bMap != null){
			userPic.setImageBitmap(bMap);
		}
	}

	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.main_button_workouts:
			Intent i = new Intent(this, WorkoutsActivity.class);
			startActivity(i);
			break;

		case R.id.main_button_calendar:
			Intent c = new Intent(this, CalendarActivity.class);
			startActivity(c);
			break;

		case R.id.main_button_profile:
			Intent p = new Intent(this, UserProfileActivity.class);
			startActivity(p);
			break;
		}
	}
}