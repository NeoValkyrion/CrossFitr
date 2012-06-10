/**
 * Calendar
 * 
 * @Process: In development 
 * TODO: 1. Make a list below the calendar (Ian)
 *       2. Access to the database (Ian)
 *       3. A little mark on the cell which has workout record (Darren)
 *          (got an idea. I will implement it soon)
 *       
 * Last update: May 29 12
 */

package com.vorsk.crossfitr;

import java.util.*;
import java.text.*;
import com.vorsk.crossfitr.models.WorkoutModel;
import com.vorsk.crossfitr.models.WorkoutRow;
import com.vorsk.crossfitr.models.WorkoutSessionModel;
import com.vorsk.crossfitr.models.WorkoutSessionRow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CalendarActivity extends Activity implements OnClickListener {
	private static final String tag = "CalendarActivity";
	private ImageView calJournalButton, preMonth, nextMonth;
	private Button currentMonth;
	private View calendar_bg;

	private ListView calendarList;
	private GridView calView;
	private GridAdapter gridAdapter; // inner class to handle adapter
	private Calendar derpCal;
	private int month, year;
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";
	

	private WorkoutSessionModel model_data = new WorkoutSessionModel(this);;
	
	// font type for days of the week
	private TextView daysOfWeekText1;
	private TextView daysOfWeekText2;
	private TextView daysOfWeekText3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_main);
		

		calendar_bg = findViewById(R.id.calendar_bg);
		calendar_bg.setOnClickListener(this);
		
		
		// text setting for the days of the week
		Typeface font = Typeface.createFromAsset(this.getAssets(),
				"fonts/Roboto-Thin.ttf");

		daysOfWeekText1 = (TextView) findViewById(R.id.calendarHeaderText1);
		daysOfWeekText1.setTypeface(font);

		daysOfWeekText2 = (TextView) findViewById(R.id.calendarHeaderText2);
		daysOfWeekText2.setTypeface(font);

		daysOfWeekText3 = (TextView) findViewById(R.id.calendarHeaderText3);
		daysOfWeekText3.setTypeface(font);

		// declare a new calendar object with built-in calendar
		derpCal = Calendar.getInstance(Locale.getDefault());
		month = derpCal.get(Calendar.MONTH) + 1;
		year = derpCal.get(Calendar.YEAR);

		preMonth = (ImageView) this.findViewById(R.id.preMonth);
		preMonth.setOnClickListener(this);

		currentMonth = (Button) this.findViewById(R.id.currentMonth);
		currentMonth.setText(DateFormat.format(dateTemplate, derpCal.getTime()));

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calView = (GridView) this.findViewById(R.id.calendargrid);
		gridAdapter = new GridAdapter(getApplicationContext(), month, year, model_data);
		gridAdapter.notifyDataSetChanged();
		calView.setAdapter(gridAdapter);

	}

	public void onClick(View view) {
		switch (view.getId()){
		
		case R.id.preMonth:
			if (month <= 1) {
				month = 12;
				year--;
			} else 
				month--;
			setGridAdapterToDate(month, year);
			break;
			
			case R.id.nextMonth:
				if (month > 11) {
					month = 1;
					year++;
				} else
					month++;
				setGridAdapterToDate(month, year);
				break;
			
			case R.id.calendar_bg:
				TextView defaultTextName = (TextView) this.findViewById(R.id.cal_workoutname);
				TextView defaultTextRecord = (TextView) this.findViewById(R.id.cal_record);
				try{
					defaultTextName.setText(" ");
					defaultTextRecord.setText(" ");					
				}catch(NullPointerException e){
					Log.d(tag, "e.toString()= " + e.toString());
					Log.d(tag, "e = " + e.toString());
				}
				
				break;	
		}
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Activity shut down
	}

	private void setGridAdapterToDate(int month, int year) {
		gridAdapter = new GridAdapter(getApplicationContext(), month, year, model_data);
		derpCal.set(year, month - 1, derpCal.get(Calendar.DAY_OF_MONTH));
		// Field number for get and set indicating the day of the month.
		// This is a synonym for DATE. The first day of the month has value 1.

		currentMonth.setText(DateFormat.format(dateTemplate, derpCal.getTime()));
		gridAdapter.notifyDataSetChanged();
		calView.setAdapter(gridAdapter);
	}	
	
	
	
	
	
	
	
	

	/**
	 * Name: GridAdapter Inner class to handle the calendar grid adapter
	 * 
	 */
	public class GridAdapter extends BaseAdapter implements OnClickListener {

		private static final String tag = "GridAdapter";

		private final Context cal_context;
		private List<String> list;
		private static final int DAY_OFFSET = 1;

		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		// Strings for day
		private final String[] months = { "January", "February", "March", "April",
				"May", "June", "July", "August", "September", "October", "November",
				"December" }; // Strings for month
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
				30, 31 }; // the number of days of each month
		private final int month, year;
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private int currentMonth_value, currentYear_value;
		private String buttonControl_color = null;
		private Button buttonControl = null;
		
		// For list
		private CalendarList listAdapter;
		private WorkoutSessionModel calendar_WSession;
		private ArrayList<WorkoutSessionRow> workoutList;
		private WorkoutSessionRow[] pulledData;
		private ListView derp_calList;

		public GridAdapter(Context context, int month, int year, WorkoutSessionModel model_data) {
			super();
			this.calendar_WSession = model_data;
			this.cal_context = context;
			this.list = new ArrayList<String>();
			this.month = month;
			this.year = year;			

			Calendar tempcal = Calendar.getInstance();
			setCurrentDayOfMonth(tempcal.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(tempcal.get(Calendar.DAY_OF_WEEK));
			currentMonth_value = tempcal.get(Calendar.MONTH) + 1;
			currentYear_value = tempcal.get(Calendar.YEAR);	

			createMonth(month, year);
			
		}

		private String getMonthAsString(int i) {
			return months[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int arg0) {
			return list.get(arg0);
		}

		public int getCount() {
			return list.size();
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		private void createMonth(int mon, int year) {

			int trailingSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mon - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			GregorianCalendar cal = new GregorianCalendar(year, currentMonth, 1);

			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = year;
				nextYear = year + 1;
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = year - 1;
				nextYear = year;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = year;
				prevYear = year;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
			}

			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			if (cal.isLeapYear(cal.get(Calendar.YEAR)) && month == 2) {
				daysInMonth++;
			}

			for (int i = 0; i < trailingSpaces; i++) {
				list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
						+ i)
						+ "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
			}

			int indexCount = trailingSpaces - 1;
			String tempString;

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				list.add(String.valueOf(i) + "-WHITE" + "-"
						+ getMonthAsString(currentMonth) + "-" + year);
				indexCount++;

				if (indexCount % 7 == 6) {
					tempString = String.valueOf(i) + "-BLUE" + "-"
							+ getMonthAsString(currentMonth) + "-" + year;
					list.set(indexCount, tempString);
				} else if (indexCount % 7 == 0) {
					tempString = String.valueOf(i) + "-RED" + "-"
							+ getMonthAsString(currentMonth) + "-" + year;
					list.set(indexCount, tempString);
				}

				Log.d("Today is : ", "day: " + getCurrentDayOfMonth() + " month: "
						+ currentMonth_value + " year : " + currentYear_value);

				if (i == getCurrentDayOfMonth() && month == currentMonth_value
						&& year == currentYear_value) {
					tempString = String.valueOf(i) + "-YELLOW" + "-"
							+ getMonthAsString(currentMonth) + "-" + year;
					list.set(indexCount, tempString);
					
					derp_calList = (ListView) findViewById(R.id.calendar_listView);					
					int numberofRecord = recordChecker(Integer.toString(getCurrentDayOfMonth()),
							getMonthAsString(currentMonth),Integer.toString(currentYear_value));
					
					if(numberofRecord == 0)
						listAdapter = new CalendarList(getApplicationContext());
					else{
						ArrayList<WorkoutSessionRow> workouts = new ArrayList<WorkoutSessionRow>();
						for(int z = 0; z < numberofRecord;z++){
							workouts.add(pulledData[z]);
						}
						listAdapter = new CalendarList(getApplicationContext(), workouts);
					}					
					listAdapter.notifyDataSetChanged();
					derp_calList.setAdapter(listAdapter);			
				}
			}	
			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ getMonthAsString(nextMonth) + "-" + nextYear);
			}

			for (int i = 0; i < list.size(); i++) {
				Log.d(tag, "Index [" + i + "] :" + list.get(i));
			}

		}

		public View getView(int index, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) cal_context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.calendar_gridcell, parent, false);
			}

			// Get a reference to the Day
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);

			String[] day_color = list.get(index).split("-");

			// set color for days
			gridcell.setText(day_color[0]);

			gridcell.setTag(day_color[1] + "-" + day_color[0] + "-" + day_color[2]
					+ "-" + day_color[3]);

			gridcell.setTextColor(colorChanger(day_color[1]));

//			Log.d(tag, "gridcell.getText : " + gridcell.getText());

			int numberofRecords = recordChecker(day_color[0], day_color[2],
					day_color[3]);
			Log.d(tag, "numberofRecords = " + numberofRecords);

			if (numberofRecords == 0) {
				gridcell.setBackgroundResource(R.drawable.calendar_cellfiller);
			} else if (numberofRecords == 1) {
				gridcell.setBackgroundResource(R.drawable.calendar_has_one_record);
			} else if (numberofRecords == 2) {
				gridcell.setBackgroundResource(R.drawable.calendar_has_two_records);
			} else if (numberofRecords == 3) {
				gridcell.setBackgroundResource(R.drawable.calendar_has_three_records);
			} else if (numberofRecords == 4) {
				gridcell.setBackgroundResource(R.drawable.calendar_has_four_records);
			} else if (numberofRecords >= 5) {
				gridcell.setBackgroundResource(R.drawable.calendar_has_five_records);
			}
			return row;
		}

		private int recordChecker(String day, String month, String year) {

			// Get the date range
			int nextday = Integer.parseInt(day) + 1;
			String startDate = day + "-" + month + "-" + year;
			String endDate = nextday + "-" + month + "-" + year;

			Log.d(tag, "startDate : " + startDate);
			Log.d(tag, "endDate : " + endDate);	

			calendar_WSession.open();
			
			// Fetch data within the range
			try {
				pulledData = calendar_WSession.getByTime(
					stampTime(startDate), stampTime(endDate));
			} catch (Exception e) {
				return 0;
			}

			Log.d(tag, "pulledData.length : " + pulledData.length);

			calendar_WSession.close();
			
			return pulledData.length;
		}

		private int colorChanger(String sColor) {

			if (sColor.equals("GREY")) {
				return Color.GRAY;
			}
			if (sColor.equals("BLUE")) {
				return getResources().getColor(R.color.static_text_blue);
			}
			if (sColor.equals("YELLOW")) {
				return getResources().getColor(R.color.static_text_yellow);
			}
			if (sColor.equals("RED")) {
				return getResources().getColor(R.color.static_text_red);
			} else {
				return Color.WHITE;
			}

		}

		
		public int getCurrentDayOfMonth() {
			Calendar tempcal = Calendar.getInstance();
			return tempcal.getTime().getDate();
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}

		public long stampTime(String _sDate) {
	  		Log.d("stampTime", "_sDate = " + _sDate);
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
				Date date = formatter.parse(_sDate);

				long milliTime = date.getTime();
				Log.d("stampTime", "milliTime = " + milliTime);
				return milliTime;
				
			} catch (ParseException e) {
				return 0;
			}
		}

		public void onClick(View view) {
			Button clickedButton = (Button) view;
			if (buttonControl != null && buttonControl_color != null) {
				buttonControl.setTextColor(colorChanger(buttonControl_color));
				// buttonControl.setBackgroundResource(R.drawable.calendar_cellfiller);
			}

			clickedButton.setOnClickListener(this);
			clickedButton.setTextColor(getResources().getColor(
					R.color.static_text_green));
			// clickedButton.setBackgroundResource(R.drawable.calendar_bg_frame);

			buttonControl = clickedButton;
			String tempHelper = (String) buttonControl.getTag();
			String[] colorHelper = tempHelper.split("-");
			buttonControl_color = colorHelper[0];

			
		//	derp_calList = (ListView) findViewById(R.id.calendar_listView);
			
			String[] noColor = ((String) clickedButton.getTag()).split("-");
			int numberofRecord = recordChecker(noColor[1],noColor[2],noColor[3]);
			
			if(numberofRecord == 0)
				listAdapter = new CalendarList(getApplicationContext());
			else{
				Log.d(tag, "Count: " + numberofRecord);
				// Construct a new one so further calls do not destroy previous references
				ArrayList<WorkoutSessionRow> workouts = new ArrayList<WorkoutSessionRow>();
				for(int i = 0; i < numberofRecord;i++){
					workouts.add(pulledData[i]);
				}
				listAdapter = new CalendarList(getApplicationContext(), workouts);
			}
			
			listAdapter.notifyDataSetChanged();
			derp_calList.setAdapter(listAdapter);			
		}
	}
	
	
	
	
	/**
	 * Name: CalendarListActivity Inner class to handle the calendar list adapter
	 */
	public class CalendarList extends BaseAdapter {

		private static final String tag = "CalendarList";
		private final Context listContext;
		private ArrayList<WorkoutSessionRow> arrayList;
		private boolean havenoRecord;
		private LayoutInflater inflater;
		private int numberofRecord;
		
		private String stringSecond;
		private String stringMinutes;
		private String finalResult;
		
		private TextView itemWorkout;
		private TextView itemRecord;
		
		public CalendarList(Context _context){
			this.listContext = _context;
			this.havenoRecord = true;
			inflater = (LayoutInflater) _context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
			numberofRecord = 1;
		}

		public CalendarList(Context _context, ArrayList<WorkoutSessionRow> _data){
			this.listContext = _context;
			this.arrayList = _data;
			this.havenoRecord = false;
			inflater = (LayoutInflater) _context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			numberofRecord = _data.size();
		}
		
		
		public View getView(int position, View convertView, ViewGroup parent) {
		//	Log.d(tag,"It works");
			if(convertView == null)
				convertView = inflater
				.inflate(R.layout.calendar_list_item, parent, false);
			
			itemWorkout = (TextView) convertView.findViewById(R.id.cal_workoutname);
			itemRecord = (TextView) convertView.findViewById(R.id.cal_record);			
			
			if(havenoRecord == true){
				itemWorkout.setText("No data existing on this day");
				itemRecord.setText("No data existing on this day");
			}else{
				WorkoutModel tempModel = new WorkoutModel(listContext);
				tempModel.open();
				
				WorkoutRow tempRowName = tempModel.getByID(arrayList.get(position).workout_id);
				tempModel.close();
				WorkoutSessionModel tempSession = new WorkoutSessionModel(listContext);
				tempSession.open();
				
				double tempdouble = arrayList.get(position).score / 1000;
		
				String score = Double.toString(tempdouble);
				Log.d(tag, "score : " + arrayList.get(position).score);
				Log.d(tag, "score_type " + arrayList.get(position).score_type_id);
				Log.d(tag, "score_type with getByID " + tempSession.getByID(arrayList.get(position).score_type_id).toString());

				if(arrayList.get(position).score_type_id == 1){
					int seconds = (arrayList.get(position).score / 1000) % 60 ;
					int minutes = ((arrayList.get(position).score / (1000*60)) % 60);
						
					if(seconds < 10)
						stringSecond = new String("0" + Integer.toString(seconds));
					else
						stringSecond = new String(Integer.toString(seconds));
					
					if(minutes < 10)
						stringMinutes = new String("0" + Integer.toString(minutes));
					else
						stringMinutes = new String(Integer.toString(minutes));
					
					finalResult = new String (stringMinutes + ":" + stringSecond);
					
				}
				
				
				itemWorkout.setText(tempRowName.name);
				itemRecord.setText(finalResult);
				tempSession.close();
								
			}	
			
			return convertView;
		}

		public int getCount() {
			return numberofRecord;
		}

		public String  getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}		
	}
}
