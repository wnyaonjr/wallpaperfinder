package com.globe.wallpaperfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Sharing extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		
		startActivity(shareIntent);

	}

}
