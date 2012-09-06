package com.freescale.iastate.hvac;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.freescale.iastate.hvac.weather.JSONParser;

public class WeatherActivity extends Activity implements MenuInterface {
	final char degree = 0x00B0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		//this sets the help string for the current activity.
		//copy paste 
		rootIntent.setHelpText(getText(R.string.weather_help));

		ForecastWeatherTask task = new ForecastWeatherTask();
		task.execute();

	}

	private class ForecastWeatherTask extends AsyncTask<Void, Void, String> {
		ImageView dayOneImageView = (ImageView) findViewById(R.id.day1image);
		ImageView dayTwoImageView = (ImageView) findViewById(R.id.day2image);
		ImageView dayThreeImageView = (ImageView) findViewById(R.id.day3image);
		TextView dayOneTextView = (TextView) findViewById(R.id.day1text);
		TextView dayTwoTextView = (TextView) findViewById(R.id.day2text);
		TextView dayThreeTextView = (TextView) findViewById(R.id.day3text);

		String day1Text, day2Text, day3Text;
		Drawable day1Image, day2Image, day3Image;

		protected String doInBackground(Void... params) {
			try {
				
				SharedPreferences settings = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				String zip = settings.getString("zip_code_key", "50014");

				JSONParser parser = new JSONParser();
				JSONParser.zip = zip;
				JSONObject json = parser
						.getJSONFromUrl(JSONParser.threedayURL);
				String readable = json.toString(5);
				
				JSONObject forecast = json.getJSONObject("forecast");
				JSONObject txt_forecast = forecast.getJSONObject("txt_forecast");
				JSONArray forecastDay = null;
				forecastDay = txt_forecast.getJSONArray("forecastday");
				
				String[] dayArray = new String[12];
				for (int i=0; i<forecastDay.length(); i++){
					JSONObject day = forecastDay.getJSONObject(i);
					dayArray[i] = day.getString("title")+": "+day.getString("fcttext")+"\n";
				}
				
				
				day1Text = dayArray[0]+dayArray[1];
				day1Image = weatherImage(day1Text);

				day2Text = dayArray[2]+dayArray[3];
				day2Image = weatherImage(day2Text);

				day3Text = dayArray[4]+dayArray[5];;
				day3Image = weatherImage(day3Text);
				return null;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		}

		protected void onPostExecute(String result) {
			dayOneTextView.setText(day1Text);
			dayOneImageView.setImageDrawable(day1Image);
			dayTwoTextView.setText(day2Text);
			dayTwoImageView.setImageDrawable(day2Image);
			dayThreeTextView.setText(day3Text);
			dayThreeImageView.setImageDrawable(day3Image);
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
			return rootIntent.onOptionsItemSelected(this, item);
	}

	public Drawable weatherImage(String condition) {
		Resources res = getResources();
		Drawable conditionDrawable = null;
		if (condition.contains("rain") || condition.contains("Rain")) {
			conditionDrawable = res.getDrawable(R.drawable.weather_rain);
		}
		if (condition.contains("sun") || condition.contains("Sun")
				|| condition.contains("clear") || condition.contains("Clear")) {
			conditionDrawable = res.getDrawable(R.drawable.weather_clear);
		}
		if (condition.contains("storm") || condition.contains("Storm")) {
			conditionDrawable = res.getDrawable(R.drawable.weather_storm);
		}
		if (condition.contains("cloudy") || condition.contains("Cloudy")
				|| condition.contains("Overcast")) {
			conditionDrawable = res.getDrawable(R.drawable.weather_cloudy);
		}
		if (condition.contains("snow") || condition.contains("Snow")) {
			conditionDrawable = res.getDrawable(R.drawable.weather_snow);
		}
		return conditionDrawable;

	}


}
