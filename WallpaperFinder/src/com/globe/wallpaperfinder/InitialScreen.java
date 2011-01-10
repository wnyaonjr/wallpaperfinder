package com.globe.wallpaperfinder;

import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class InitialScreen extends Activity implements OnClickListener{
	
	private EditText searchEditText;
	private Button searchButton;
	
	public final static String SETTINGS_FILENAME = "InitialScreenSettings";
	public final static String EULA = "eula";
	protected SharedPreferences settings;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.initial_screen);
		
		settings = this.getSharedPreferences(SETTINGS_FILENAME, MODE_WORLD_WRITEABLE);
		
		if(!settings.getBoolean(EULA, false))getAlertDialog().show();
		
		Button downloadsButton = (Button) findViewById(R.id.download_button);
		downloadsButton.setOnClickListener(this);
		
		
		searchEditText = (EditText) findViewById(R.id.search_box);
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
				
				
				String searchString = searchEditText.getText().toString();				
				if (!searchString.equals("")){

					Intent intent = new Intent(InitialScreen.this, WallpaperFinder.class);
					intent.putExtra(WallpaperFinder.SEARCH_STRING, searchString);
					startActivity(intent);
				}
				return false;
			}
		});
		
		searchButton = (Button) findViewById(R.id.search_button);
		searchButton.setOnClickListener(this);
		
		Button aboutButton = (Button) findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
	}
	
	
	public AlertDialog getAlertDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(InitialScreen.this);
		builder.setInverseBackgroundForced(true);
		builder.setCancelable(false);
		
		builder.setTitle("EULA");
		builder.setMessage(R.string.license);
		builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences.Editor settingsEditor = settings.edit();
				settingsEditor.putBoolean(EULA, true);
				settingsEditor.commit();
				dialog.dismiss();
			}
			
		});
		builder.setNegativeButton("Disagree", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
			
		});
		
		
		AlertDialog alert = builder.create();
		return alert;
	}


	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.download_button:
				intent = new Intent(InitialScreen.this, Downloads.class);
				startActivity(intent);
				break;
			case R.id.search_button:
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
				String searchString = searchEditText.getText().toString();				
				if (!searchString.equals("")){

					intent = new Intent(InitialScreen.this, WallpaperFinder.class);
					intent.putExtra(WallpaperFinder.SEARCH_STRING, searchString);
					startActivity(intent);
				}
				break;
			
			case R.id.about_button:
				AlertDialog.Builder builder = new AlertDialog.Builder(InitialScreen.this);
				builder.setInverseBackgroundForced(true);
				builder.setCancelable(true);
				
				builder.setTitle("About");
				builder.setIcon(R.drawable.icon22);
				builder.setMessage("Wallpaper Finder - v1.0\n\n\u00A9 2010 - Team Underground");
				builder.setPositiveButton("Show Eula", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						AlertDialog.Builder builder = new AlertDialog.Builder(InitialScreen.this);
						builder.setInverseBackgroundForced(true);
						builder.setCancelable(true);
						builder.setIcon(R.drawable.icon22);
						builder.setTitle("EULA");
						builder.setMessage(R.string.license);
						
						((AlertDialog)builder.create()).show();
						
					}
					
				});
				
				((AlertDialog)builder.create()).show();
				break;
		default:
			break;
		}
	}
}
