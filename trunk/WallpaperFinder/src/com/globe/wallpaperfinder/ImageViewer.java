package com.globe.wallpaperfinder;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageViewer extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.image_search_imageview);
		
		ImageView imageView = (ImageView) findViewById(R.id.image_view);
		
		Log.i("ImageViewer", getResources().getDisplayMetrics().widthPixels+":"+getResources().getDisplayMetrics().heightPixels);
		
		imageView.setImageBitmap(WallpaperFinder.decodeImage(getIntent().getExtras().getString(WallpaperFinder.PATH), getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels));
		
	}
}
