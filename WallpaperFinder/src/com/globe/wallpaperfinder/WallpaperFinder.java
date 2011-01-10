package com.globe.wallpaperfinder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

/**
 * ImageSearch is a small program intended to provide
 * service in searching images from google without showing the user
 * a web browser. Parsing and rendering of images is done in backend.
 * 
 *
 * @author      Winifredo N. Ya-on Jr.
 * @version     1.0, 08/02/10
 */

public class WallpaperFinder extends Activity{
	
	/**
	 * Information about the activity
	 */
	protected Context context;

	/**
	 * Use for google image search
	 */
	protected WebView browser;

	/**
	 * Constant for adding and retrieving URL data from bundle
	 */
	protected final static String URL = "URL";
	
	/**
	 * Constant for adding and retrieving image path data from bundle
	 */
	protected final static String PATH = "path";
	
	/**
	 * Constant for adding and retrieving image name from bundle
	 */
	protected final static String IMAGE_NAME = "image name";
	
	/**
	 * Constant for adding and retrieving extension name of image from bundle
	 */
	protected final static String EXTENSION_NAME = "extension name";
	
	/**
	 * Constant for adding and retrieving extension name of image from bundle
	 */
	protected final static String IMAGE_URL = "url";
	
	/**
	 * Constant which represents the waiting state of ImageSearch
	 */
	protected final static int WAITING = 0;
	
	/**
	 * Constant which represents the loading state of ImageSearch
	 */
	protected final static int LOADING = 1;
	
	/**
	 * Constant which represents the processing state of ImageSearch
	 */
	protected final static int PROCESSING = 2;
	
	/**
	 * Constant which represents the rendering state of ImageSearch
	 */
	protected final static int RENDERING = 3;
	
	/**
	 * Constant which represents the state of ImageSearch where error is encounterd
	 */
	protected final static int PROCESSING_ERROR = 4;
	
	/**
	 * Field for the state of the ImageSearch
	 */
	protected static int IMAGE_SEARCH_STATE = WAITING;
		
	/**
	 * Constant for saving image from the browser
	 */
	protected static int IMAGE_NAME_COUNTER = 0;
	
	/**
	 * Constant use in javascript injection
	 */
	protected static int IMAGE_ID = 0;
	
	/**
	 * Constant for unsuccessful loading of an image
	 */
	protected final static int LOADING_UNSUCCESSFUL = 0;
	
	/**
	 * Constant for successful loading of an image
	 */
	protected final static int LOADING_SUCCESSFUL = 1;

	/**
	 * Constant for maximum number of images to be displayed
	 */
	public static final int NUMBER_OF_IMAGES = 18;
	
	/**
	 * Constant for minimum size
	 */
	public static final int MINIMUM_SIZE = 50;

	public static final String TAG = "wallpaperFinder";
	
	
	
	/**
	 * Constant for the path the images will be saved
	 */
	protected final String cachePath = Environment.getExternalStorageDirectory().getPath()+"/"+TAG+"/.cache/";
	
	/**
	 * use for displaying thumbnails
	 */
	//protected GridView gridView;
	protected Gallery gallery;
	
	/**
	 * use for search string input
	 */
	protected EditText searchEditText;
	
	/**
	 * use for starting search
	 */
	protected Button searchButton;	
			
	/**
	 * Representation of the directory for the image cache
	 */
	protected File cacheDirectory;
	
	/**
	 * Storage of string used in searching
	 */
	protected String queryString = "";
	
	
	/**
	 * The field responsible for processing messages from browser, threads, and image rendering.
	 */
	protected Handler handler;
	
	/**
	 * The field for showing the android's default title progressbar.
	 */
	protected ProgressBar progressBar;
	
	/**
	 * The field for showing title for progress in search images
	 */
	//protected TextView progressText;

	private ImageAdapter imageAdapter;

	protected ImageView imageView;
	protected TextView imageName;
	
	/**
	 * Storage of path of saved images
	 */
	//protected ArrayList<Bitmap> bitmapSampleSize = new ArrayList<Bitmap>();
	//protected ArrayList<Boolean> downloadFlag = new ArrayList<Boolean>();
	
	/**
	 * Storage of path of saved images
	 */
	//protected ArrayList<String> bitmapPathList = new ArrayList<String>();
	protected ArrayList<Image> images = new ArrayList<Image>();
	
	private int width;
	private int height;

	protected boolean checkBoxIsVisible = false;
	
	public final String downloadsPath = Environment.getExternalStorageDirectory().getPath()+"/"+TAG+"/downloads/";
	protected File downloadsDirectory;
	protected ProgressDialog loadingDialog;
	
	public final static String SEARCH_STRING = "search string";
	
	
/*	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureScanner;*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//set the activity content
        //setContentView(R.layout.image_search);
		setContentView(R.layout.main);
		
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.image_search_title);
		
		width = getResources().getDisplayMetrics().widthPixels/2;
		height = getResources().getDisplayMetrics().heightPixels/2;
		//+":"+getResources().getDisplayMetrics().heightPixels
		
		progressBar = (ProgressBar) findViewById(android.R.id.progress);
		progressBar.setVisibility(View.GONE);
		//progressText = (TextView) findViewById(R.id.image_searching_txt);
		//setProgressTitleVisibility(false);
		
        handler = new ImageSearchHandler();
        //initialization of layout components
        //gridView = getGridView();
        
        imageName = (TextView) findViewById(R.id.image_filename);
        imageName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = imageName.getText().toString();
				
				if (!url.equals("")) {
					Intent browse_intent = new Intent(Intent.ACTION_VIEW);
					browse_intent.setData(android.net.Uri.parse(url));
					startActivity(browse_intent);
				}

			}
		});
        
        imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        gallery = getGalleryView();
		searchEditText = (EditText) findViewById(R.id.search_box);
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
				
				
				String searchString = searchEditText.getText().toString();				
				if (!searchString.equals("") && !queryString.equals(searchString)){
//					bitmapPathList.clear();
//					bitmapSampleSize.clear();
					//gridView.invalidateViews();
					//gallery.invalidate();
					images.clear();
					imageView.setImageBitmap(null);
					imageAdapter.notifyDataSetChanged();
					imageName.setText("");
					
					queryString = searchString;
					browser.stopLoading();
					IMAGE_SEARCH_STATE = WAITING;
					//clearOrCreateDirectory(cacheDirectory);

					progressBar.setVisibility(View.GONE);
					new Thread(new BrowserThread(searchString)).start();
				}
				return false;
			}
		});
		searchButton = getSearchButton();
		
		browser = getWebView();
		
		//initialization of the directory representation for the storage of images
		cacheDirectory = new File(cachePath);
		clearOrCreateDirectory(cacheDirectory);

		
		
		downloadsDirectory = new File(downloadsPath);
		createDirectory(downloadsDirectory);
	
		//TODO
		//getDownloads();
		
		String searchString = getIntent().getExtras().getString(SEARCH_STRING);
		searchEditText.setText(searchString);
		
		progressBar.setVisibility(View.GONE);
		new Thread(new BrowserThread(searchString)).start();
		
		
	}
	
	private void getDownloads() {
			//bitmapPathList
		//	bitmapSampleSize
		//downloadFlag = new ArrayList<Boolean>();
		
		if (downloadsDirectory.exists()) {
			File[] files = downloadsDirectory.listFiles();
			
			Arrays.sort(files);
			
			for (File file : files) {
				String fileNameArray[] = file.getName().split("[.]");
				
				String fileName = "";
				for(int i=0; i<fileNameArray.length-1; i++)
					fileName += ""+fileNameArray[i];
				
				String extensionName = "."+fileNameArray[fileNameArray.length-1];
				
				Image image = new Image();
				image.setFileName(fileName);
				image.setExtensionName(extensionName);
				image.setPath(downloadsPath+""+file.getName());
				image.setSelected(false);
				image.setBitmap(decodeImage(downloadsPath+""+file.getName(), width, height));
				
				images.add(image);
			}
			//Collections.sort(images, new ImageOrder());
			
			imageAdapter.notifyDataSetChanged();
		}
			
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				IMAGE_SEARCH_STATE = WAITING;
				break;
	
			default:
				break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	protected void onPause() {
		//TODO clear memory allocated for bitmap
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO decoding of bitmap
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		//clearOrCreateDirectory(cacheDirectory);
		/*for (String path : bitmapPathList) {
			Log.i("onDestroy()", "onDestroy(): "+path);
			File file = new File(path);
			file.delete();
		}
		
		bitmapSampleSize.clear();*/
		for (Image image: images) {
			try {
				File file = new File(image.getPath());
				file.delete();
				Bitmap bitmap = image.getBitmap();
				bitmap.recycle();
				bitmap = null;
				java.lang.System.gc();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		images.clear(); 
		imageAdapter.notifyDataSetChanged();
		imageView.setImageBitmap(null);
		//java.lang.System.exit(0);
		super.onDestroy();
	}
	
	/**
	 * convenience method for getting a new instance of the search button of this activity
	 * 
	 * @return	the new instance of the search button 
	 *  
	 */
	private Button getSearchButton() {
		Button button = (Button) findViewById(R.id.search_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				/*final Intent pickWallpaper = new Intent(android.content.Intent.ACTION_SEND);
				//pickWallpaper.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				pickWallpaper.setType("image/*");
				
				File downloadedPic =  new File("/sdcard/DCIM/11.jpg");
				pickWallpaper.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(downloadedPic));
				
				pickWallpaper.putExtra(Intent.EXTRA_SUBJECT, "test");
				//pickWallpaper.setDataAndType(Uri.parse("http://www.desktoprating.com/wallpapers/landscape-wallpapers-pictures/snow-landscape-wallpaper.jpg"), "image/jpeg");
				
		        startActivity(Intent.createChooser(pickWallpaper,("Share")));*/
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
				String searchString = searchEditText.getText().toString();				
				if (!searchString.equals("") && !queryString.equals(searchString)){
//					bitmapPathList.clear();
//					bitmapSampleSize.clear();
					//gridView.invalidateViews();
					//gallery.invalidate();
					
					images.clear();
					imageView.setImageBitmap(null);
					imageName.setText("");
					imageAdapter.notifyDataSetChanged();
					
					
					queryString = searchString;
					browser.stopLoading();
					IMAGE_SEARCH_STATE = WAITING;
					//clearOrCreateDirectory(cacheDirectory);
					progressBar.setVisibility(View.GONE);
					new Thread(new BrowserThread(searchString)).start();
				}
					
			}
		});
		
		return button;
	}

	/**
	 * convenience method for getting a new instance of the gridview of this activity
	 * for displaying thumbnails
	 * 
	 * @return	the new instance of the gridview 
	 *  
	 */
	/*private GridView getGridView() {
		GridView gridView = (GridView) findViewById(R.id.thumbnails_view);
		gridView.setAdapter(imageAdapter = new ImageAdapter());
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				for(int counter=0; counter<bitmapPathList.size(); counter++){
					Log.i("Content:"+counter, bitmapPathList.get(counter));
				}
				
				Intent intent =  new Intent(context,ImageViewer.class);
				Bundle b = new Bundle();
		    	b.putString(PATH, bitmapPathList.get(position));
		    	intent.putExtras(b);
		    	context.startActivity(intent);
			}
			
		});
		
		return gridView;
	}*/
	
	private Gallery getGalleryView(){
		final Gallery gallery = (Gallery) findViewById(R.id.gallery);
		//gallery.setSelection(position, true);
		gallery.setAdapter(imageAdapter = new ImageAdapter());
		
		gallery.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (checkBoxIsVisible){
					checkBoxIsVisible = false;
					resetSelections();
				}
				else
					checkBoxIsVisible = true;
				
				imageAdapter.notifyDataSetChanged();
				return false;
			}
		});
		
/*		gallery.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (checkBoxIsVisible)
					checkBoxIsVisible = false;
				else
					checkBoxIsVisible = true;
				
				gallery.invalidate();
				return false;
			}
		});*/
		
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View v,
					int position, long id) {
				Image image = images.get(position);
				//imageView.setImageBitmap(bitmapSampleSize.get(position));
				imageView.setImageBitmap(image.getBitmap());
				//imageName.setText(image.getFileName());
				imageName.setText(image.getUrlString());
				
					
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				Image image = images.get(position);
				imageView.setImageBitmap(image.getBitmap());
				//imageName.setText(image.getFileName());
				imageName.setText(image.getUrlString());
				
				
				if(checkBoxIsVisible){
					image.setSelected(!image.isSelected());
					imageAdapter.notifyDataSetChanged();
				}
				/*for(int counter=0; counter<bitmapPathList.size(); counter++){
					Log.i("Content:"+counter, bitmapPathList.get(counter));
				}
				
				Intent intent =  new Intent(context,ImageViewer.class);
				Bundle b = new Bundle();
		    	b.putString(PATH, bitmapPathList.get(position));
		    	intent.putExtras(b);
		    	context.startActivity(intent);*/
			}
			
		});
		
		
		return gallery;
	}
	
	protected void resetSelections() {
		for (Image image : images) {
			image.setSelected(false);
		}
	}

	/**
	 * ImageAdapter is the class responsible for integrating the images
	 * into the gridview.
	 * 
	 * @author      Winifredo N. Ya-on Jr.
	 * @version     1.0, 08/02/10
	 */
	protected class ImageAdapter extends BaseAdapter{
		LayoutInflater layoutInflater;
		private int mGalleryItemBackground;		
		
		public ImageAdapter() {
			layoutInflater = LayoutInflater.from(context);
			TypedArray a = obtainStyledAttributes(R.styleable.WallpaperFinder);
	        mGalleryItemBackground = a.getResourceId(
	                R.styleable.WallpaperFinder_android_galleryItemBackground, 0);
	        a.recycle();

		}
		
		/*
		 * (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			//return bitmapSampleSize.size();
			return images.size();
		}
		
		/*
		 * (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			//return bitmapSampleSize.get(position);
			return images.get(position);
		}

		/*
		 * (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/*
		 * (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	        	convertView = layoutInflater.inflate(R.layout.image_search_item, null);
	        }
	        
	        Image image = images.get(position);
	        ImageView imageView = (ImageView)convertView.findViewById(R.id.image);
	        //imageView.setImageBitmap(bitmapSampleSize.get(position));
	        imageView.setImageBitmap(image.getBitmap());
	        imageView.setBackgroundResource(mGalleryItemBackground);
	        
	        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
	        if (checkBoxIsVisible){
	        	checkBox.setVisibility(View.VISIBLE);
	        	//checkBox.setChecked(downloadFlag.get(position));
	        	checkBox.setChecked(image.isSelected());
	        }
	        else
	        	checkBox.setVisibility(View.INVISIBLE);

	        
	        return convertView;
			
			/*ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new Gallery.LayoutParams(150, 100));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setPadding(1, 1, 1, 1);
            imageView.setImageBitmap(bitmapSampleSize.get(position));
            
            
            return imageView;*/

		}
	}
	
	
	/**
	 * convenience method for getting a new instance of the webview
	 * for this activity that will be used for searching images from
	 * google
	 * 
	 * @return	the new instance of the webview
	 *  
	 */
	protected WebView getWebView(){
		
		WebView browser = new WebView(context);
		WebSettings settings = browser.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setPluginsEnabled(true);
		
		/* Register a new JavaScript interface called HTMLOUT */  
		browser.addJavascriptInterface(new JavaScriptInterface(), "HTMLOUT");
		
//		browser.setWebChromeClient(new WebChromeClient(){
//			
//			@Override
//			public void onConsoleMessage(String message, int lineNumber,
//					String sourceID) {
//				Log.i("Console message: ", message);
//				handler.sendEmptyMessage(PROCESSING_ERROR);
//				super.onConsoleMessage(message, lineNumber, sourceID);
//			}
//		});
		
		browser.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				
				handler.sendEmptyMessage(PROCESSING);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.i("WebView --> Error", description);
				handler.sendEmptyMessage(PROCESSING_ERROR);
			}
			
			
		
		});
		
		return browser;
	}
	
	/**
	 * JavaScriptInterface is the class that will be registered as a JavaScript interface.
	 * 
	 * @author      Winifredo N. Ya-on Jr.
	 * @version     1.0, 08/02/10
	 */  
	protected class JavaScriptInterface{
		
		/**
		 * convenience method for obtaining raw html when inserting javascript
		 * 
		 * @param html	the raw html ouput from javascript injection 
		 *  
		 */
		public void getHTML(String html){
			Message msg = new Message();
			msg.what = RENDERING;
			Bundle bundle = new Bundle();
			bundle.putString(URL, parseImageURL(html));
			msg.setData(bundle);
			handler.sendMessage(msg);
			
	    }
		
		/**
		 * convenience method for parsing url of an image
		 * 
		 * @param input	the raw html ouput from javascript injection
		 * 
		 * @return	the parsed url of the image 
		 *  
		 */
		protected String parseImageURL(String input){
			int state = 0;
			String url = "";
			for(int index=0; (state!=7) && (index<input.length()); index++){
				switch(state){
					case 0:
						if(input.charAt(index) == '<')
							state = 1;
						break;
					case 1:
						if(input.charAt(index) == ' ')
							state = 2;
						break;
					case 2:
						if(input.charAt(index) == '=')
							state = 3;
						break;
					case 3:
						if(input.charAt(index) == '"')
							state = 4;
						break;
					case 4:
						if(input.charAt(index) == '?')
							state = 5;
						break;
					case 5:
						if(input.charAt(index) == '=')
							state = 6;
						break;
					case 6:
						if(input.charAt(index) == '&')
							state = 7;
						else
							url += input.charAt(index);
						break;
				}
			}		
				
			/*url = url.replace("%2520", "%20");
			url = url.replace("%2520", "%20");
			url = url.replace("%3D", "=");
			url = url.replace("%252"," %2");
			url = url.replace("%26", "&");
			url = url.replace("%3F","?");*/
			url = URLDecoder.decode(url);
			url = url.split("[?]")[0];
			
			
			
			return url;
		}
	}
		
	
	/**
	 * ImageLoader is the class responsible for loading image given the address 
	 * 
	 * @author      Winifredo N. Ya-on Jr.
	 * @version     1.0, 08/02/10
	 */  
	protected class ImageLoader implements Runnable{
		
		/**
		 * Constant size of buffer to read from input stream, currently 5KB
		 */
		private final static int BUFFER_SIZE = 1024*5;
		
		/**
		 * String to store the url that will refer to the image 
		 */
		private String urlString;
		
		/**
		 * Contructs and initializes an image loader with the given url string
		 * 
		 * @param urlString	the url string of the newly contructed ImageLoader
		 */
		public ImageLoader(String urlString){
			this.urlString = urlString;
		}
		
		
		/**
		 * Reads the image on the internet in a byte-wise manner and write on the path of the cache.
		 * 
		 * Also sends message to the handler for successful and unsuccessful loading of image.
		 */
		@Override
		public void run() {
			Message msg = new Message();
			String link = urlString;
			String linkArray[] = link.split("/");
			String imageName = linkArray[linkArray.length-1];
			String imageNameArray[] = imageName.split("[.]");
			
			String extension = "."+imageNameArray[imageNameArray.length-1];
			
			imageName = "";
			for(int i=0; i<imageNameArray.length-1; i++)
				imageName = imageName+""+imageNameArray[i];
			//imageName = 
			
			
			
			File fileChecking = new File(cachePath+""+imageName+""+extension);
			int counter = 0;
			while(fileChecking.exists()){
				counter++;
				//imageName = imageName+"("+counter+")";
				fileChecking = new File(cachePath+""+imageName+"("+counter+")"+extension);
			}
			
			if (counter > 0)
				imageName = imageName+"("+counter+")";
			
			String finalPath = cachePath+""+imageName+""+extension;
			
			
			Log.i("FileName", "FileName --> finalPath: "+finalPath);
			
			
			
			try {
				URL url = new URL(link);
				InputStream instream = url.openStream();
				//FileOutputStream fos = new FileOutputStream(cachePath+"image"+IMAGE_NAME_COUNTER);
				//TODO checking for duplicate file name
				FileOutputStream fos = new FileOutputStream(finalPath);
				OutputStream out = new BufferedOutputStream(fos);
				byte[] buffer = new byte[BUFFER_SIZE];
				
				int bytesLength;
				 while((IMAGE_SEARCH_STATE != WAITING) && ((bytesLength = instream.read(buffer)) != -1))
	                    out.write(buffer, 0, bytesLength);
				 
				out.flush();
				out.close();
	            instream.close();
				
	            java.lang.System.gc();
	            
	            switch (IMAGE_SEARCH_STATE){
					case WAITING:
						Log.i("ImageLoader", "Interrupted saving image");
						msg.what = LOADING_UNSUCCESSFUL;
						
//						File file = new File(cachePath+"image"+IMAGE_NAME_COUNTER);
						File file = new File(finalPath);
						file.delete();
						
					break;
					default:
						Log.i("ImageLoader", "Finish saving image");
			            msg.what = LOADING_SUCCESSFUL;
			            Bundle data = new Bundle();
			            //data.putString(PATH, cachePath+"image"+IMAGE_NAME_COUNTER);
			            data.putString(PATH, finalPath);
			            data.putString(IMAGE_NAME, imageName);
			            data.putString(EXTENSION_NAME, extension);
			            data.putString(IMAGE_URL, link);
			            msg.setData(data);
					break;
				}
	             
			} catch (Exception e) {
				Log.i("Handler error", e.getMessage());
				msg.what = LOADING_UNSUCCESSFUL;
			}
			
			handler.sendMessage(msg);
		}		
	}
	
	/**
	 * BrowserThread is the class responsible for starting the browser in loading a url string.
	 * 
	 * @author      Winifredo N. Ya-on Jr.
	 * @version     1.0, 08/02/10
	 */
	private class BrowserThread implements Runnable{
		/**
		 * The url to be loaded by the browser
		 */
		private String urlString;
		
		/**
		 * Constructs and initializes a browser thread with the specified search string
		 * and generate the url.
		 * 
		 * 
		 * @param searchString
		 */
		public BrowserThread(String searchString){
			urlString = generateURL(searchString, 0);
			Log.i("BrowserThread", "Started loading "+urlString);
		}
		
		
		/**
		 * Make the browser load the url string and send message to the handler with loading state as a parameter.
		 * 
		 * <p>Send message to the handler with waiting state as a parameter on error.</p>
		 */
		@Override
		public void run(){
			
			try {
				browser.loadUrl(urlString);
				handler.sendEmptyMessage(LOADING);
			} catch (Exception e) {
				Toast.makeText(context, "No results found", Toast.LENGTH_SHORT).show();
				handler.sendEmptyMessage(WAITING);
				Log.i("BrowserThread", e.getMessage());
			}
		}
		
		
	}
	
	/**
	 * Convinience method for constructing the url of google image search given search string
	 * and starting index of image
	 * 
	 * @param searchString	the keyword that will be use in searching images
	 * @param start			start index of image to be displayed
	 * @return	the generated url for google image search
	 */
	protected String generateURL(String searchString, int start){
		searchString+= " wallpaper";
		
		searchString = URLEncoder.encode(searchString);
		
		String url = "http://www.google.com.ph/images?q=";
		url += ""+searchString;
		url += " &hl=tl&gbv=2&tbs=isch:1&ei=OJFXTJvIJoKxccD0uL8M&sa=N&start=";
		url += ""+start;
		url += "&ndsp="+start+18;
		
		return url;
	}
	

	/**
	 * convenience method for clearing a directory if exists or creates the complete
	 * directory path 
	 * 
	 * @param cacheDirectory	the directory that will be cleard or created
	 * 
	 * @return	the new instance of the search button 
	 *  
	 */
	private void createDirectory(File directory) {
		if (directory.exists()) {
		}
		else {
			directory.mkdirs();
		}
	}
	
	private void clearOrCreateDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			for (File file : files) {
				file.delete();
			}
		}
		else {
			directory.mkdirs();
		}
	}
	
	/*public static Bitmap decodeFile(String path, int width, int height){
	    try {
	        //Decode image size
	        BitmapFactory.Options opts1 = new BitmapFactory.Options();
	        opts1.inJustDecodeBounds = true;
	        //BitmapFactory.decodeStream(new FileInputStream(f),null,o);
	        BitmapFactory.decodeFile(path, opts1);

	        //Log.i("decodeFile", opts1.outWidth+":"+opts1.outHeight);
	        
	        //Find the correct scale value, it should be the power of 2.
	        int width_tmp=opts1.outWidth, height_tmp=opts1.outHeight;
	        int scale=1;
	        while(true){
	            if(width_tmp/2<width || height_tmp/2<height)
	                break;
	            width_tmp/=2;
	            height_tmp/=2;
	            scale*=2;
	        }

	        //Decode with inSampleSize
	        BitmapFactory.Options opts2 = new BitmapFactory.Options();
	        opts2.inSampleSize=scale;
	        //Log.i("decodeFile", "SampleSize: "+scale);
	        
	        return BitmapFactory.decodeFile(path, opts2);
	    } catch (Exception e) {
	    	Log.e("decodeFile", "Error! "+e.getMessage());
	    }
	    return null;
	}*/
	/**
	 * convenience method for decoding image and scaling it to reduce memory consumption
	 * 
	 * @param path		the location of the image to be decoded
	 * @param width	 	desired width of the image
	 * @param height 	desired height of the image
	 * 
	 * @return	the decoded and scaled bitmap image 
	 *  
	 */
	public static Bitmap decodeImage(String path, int width, int height) {

      //Decode image size
        BitmapFactory.Options opts1 = new BitmapFactory.Options();
        opts1.inJustDecodeBounds = true;
        //BitmapFactory.decodeStream(new FileInputStream(f),null,o);
        BitmapFactory.decodeFile(path, opts1);

        //Log.i("decodeFile", opts1.outWidth+":"+opts1.outHeight);
        //Find the correct scale value, it should be the power of 2.
        int w = opts1.outWidth;
        int h = opts1.outHeight;
        
        int candidateW = w / width;
        int candidateH = h / height;
        int candidate = Math.max(candidateW, candidateH);

        if (candidate == 0)
            return BitmapFactory.decodeFile(path);

        if (candidate > 1) {
            if ((w > width) && (w / candidate) < width)
                candidate += 1;
        }

        if (candidate > 1) {
            if ((h > height) && (h / candidate) < height)
                candidate += 1;
        }
        	
        //return candidate;
      //Decode with inSampleSize
        BitmapFactory.Options opts2 = new BitmapFactory.Options();
        opts2.inSampleSize=candidate;
        //Log.i("decodeFile", "SampleSize: "+scale);
        
        return BitmapFactory.decodeFile(path, opts2);
    }
	
/*	protected String getJavaScriptParser(){
		return "var state = 0;" +
				"var url = \"\";" +
				"for(index=0; (state != 7) && (index<input.length); index++){" +
					"switch(state){" +
						"case 0:" +
							"if(input.charAt(index) == '<')" +
							"state = 1;" +
							"break;" +
						"case 1:" +
							"if(input.charAt(index) == ' ')" +
								"state = 2;" +
								"break;" +
						"case 2:" +
							"if(input.charAt(index) == '=')" +
								"state = 3;" +
								"break;" +
						"case 3:" +
							"if(input.charAt(index) == '\"')" +
								"state = 4;" +
								"break;" +
						"case 4:" +
							"state = 5;" +
							"break;" +
						"case 5:" +
							"if(input.charAt(index) == '=')" +
								"state = 6;" +
								"break;" +
						"case 6:" +
							"if(input.charAt(index) == '&')" +
								"state = 7;" +
							"else" +
								"url += input.charAt(index);" +
								"break;"+
					"}" +
				"}" +
			"}";
	}*/
	
	/**
	 * ImageSearchHandler is a class responsible for processing messages from browser, threads, and image rendering.
	 * It also handles forking of thread for inserting javascript and rendering an image.
	 * 
	 * <p>
	 * The implementation of the automata for the different states which
	 * uses the messages as interrupts to perform an operation and/or change current state
	 * (interrupt-driven like architecture). 
	 * </p>
	 * 
	 * <p>
	 * The state of the activity is determined by the variable IMAGE_SEARCH_STATE which is initially
	 * at <b>waiting</b> state.
	 * </p>
	 * 
	 * <p>
	 * The <b>waiting</b> state means that there is <b>no loading page</b>,
	 * <b>no processing of image</b> is done. It is only waiting to transfer to its next state which
	 * is the <b>loading</b> state. Upon receiving a message containing the next state as <b>loading</b>,
	 * it starts the progress bar in the header part to notify the user that a process has started,
	 * clears the list of paths, reset variable for naming the saved images, reset variable
	 * for accessing images through id in javascript, invalidate views of gridview,
	 * invoke the method for clearing or creating the directory, and finally changing the current state
	 * to <b>loading</b> state.
	 * </p>
	 * 
	 * <p>
	 * In <b>loading</b> state, it waits for message of changing to <b>waiting</b> or <b>processing</b> state.
	 * Receiving waiting state will stop the progress bar and change the current state to <b>waiting</b>.
	 * On the other hand, message containing <b>processing</b> state will change the current state to processing
	 * and <b>trigger sending of message</b> containing processing state. 
	 * </p>
	 * 
	 * <p>
	 * In <b>processing</b> state, it waits for message of changing to <b>waiting</b>, <b>processing</b>,
	 * or <b>rendering</b> state, and also handles error from javascript injection.
	 * Receiving waiting state will stop the progress bar and change the current state to <b>waiting</b>.
	 * A message containing <b>processing</b> state or an <b>error</b> will insert javascript if the counter
	 * for accessing the images in a loaded page is less than the desired number of images else change
	 * the current state to waiting and stop the progress bar.
	 * On the other hand, a message containing <b>rendering</b> state will <b>fork new thread</b> for requesting
	 * and saving the image from the given URL. It will change the current state to rendering. 
	 * </p>
	 * 
	 * <p>
	 * In <b>rendering</b> state, it <b>waits for a message</b> of <b>successful or unsuccessful loading of image</b>
	 * from the <b>thread</b>. If loading is successful, it adds the path of the newly created image to the list and
	 * invalidate the views in the gridview. Finally changing the current state to <b>processing</b> whether loading
	 * image is successful or not, manually sending message containing <b>processing</b> as the next state.  
	 * </p>
	 * 
	 * 
	 */
	protected class ImageSearchHandler extends Handler{
		
		/**
		 * receive and process messages, handle forking of thread for inserting javascript and rendering an image, and
		 * changing states of the ImageSearch class. 
		 */
		@Override
		public void handleMessage(Message msg) {
			switch (IMAGE_SEARCH_STATE) {
				case WAITING:
					Log.i("IMAGE_SEARCH_STATE", "WAITING");
					if (msg.what == LOADING){
						
						loadingDialog = ProgressDialog.show(WallpaperFinder.this, "", 
		                        "Loading. Please wait...", true, true);
						Log.i("IMAGE_SEARCH_STATE", "WAITING --> LOADING");
						//setProgressBarIndeterminateVisibility(true);
						//setProgressTitleVisibility(true);
//						bitmapPathList.clear();
//						bitmapSampleSize.clear();
						IMAGE_NAME_COUNTER = 0;
						IMAGE_ID = 0;
						
						//gridView.invalidateViews();
						//gallery.invalidate();
						imageAdapter.notifyDataSetChanged();
						//clearOrCreateDirectory(cacheDirectory);
						
						IMAGE_SEARCH_STATE = LOADING;
						
					}
					break;
					
				case LOADING:
					Log.i("IMAGE_SEARCH_STATE", "LOADING");
					switch (msg.what) {
						case WAITING:
							loadingDialog.cancel();
							progressBar.setVisibility(View.INVISIBLE);
							Toast.makeText(WallpaperFinder.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
							Log.i("IMAGE_SEARCH_STATE", "LOADING --> WAITING");
							//setProgressBarIndeterminateVisibility(false);
							//setProgressTitleVisibility(false);
							finish();
							IMAGE_SEARCH_STATE = WAITING;
							break;
						case PROCESSING:
							Log.i("IMAGE_SEARCH_STATE", "LOADING --> PROCESSING");
							IMAGE_SEARCH_STATE = PROCESSING;
							handler.sendEmptyMessage(PROCESSING);
							break;
						case PROCESSING_ERROR:
							loadingDialog.cancel();
							progressBar.setVisibility(View.INVISIBLE);
							Toast.makeText(WallpaperFinder.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
							Log.i("IMAGE_SEARCH_STATE", "LOADING --> PROCESSING_ERROR");
							IMAGE_SEARCH_STATE = WAITING;
							finish();
							break;
						default:
							break;
					}
					
					break;
					
				case PROCESSING:
					Log.i("IMAGE_SEARCH_STATE", "PROCESSING");
					switch (msg.what) {
						case WAITING:
							Log.i("IMAGE_SEARCH_STATE", "PROCESSING --> WAITING");
							IMAGE_SEARCH_STATE = WAITING;
							progressBar.setVisibility(View.GONE);
							//setProgressBarIndeterminateVisibility(false);
							//setProgressTitleVisibility(false);
							break;
							
						case PROCESSING:
							if (!progressBar.isShown())
								progressBar.setVisibility(View.VISIBLE);
							Log.i("IMAGE_SEARCH_STATE", "PROCESSING --> PROCESSING");
							if(IMAGE_ID < NUMBER_OF_IMAGES)
								browser.loadUrl("javascript:window.HTMLOUT.getHTML(document.getElementById('tDataImage"+(IMAGE_ID++)+"').innerHTML);");
							else{
								//TODO
								Toast.makeText(context, "Finish loading images...", Toast.LENGTH_SHORT).show();
								progressBar.setVisibility(View.GONE);
								IMAGE_SEARCH_STATE = WAITING;
								//setProgressBarIndeterminateVisibility(false);
								//setProgressTitleVisibility(false);
							}
							break;
						
						case PROCESSING_ERROR:
							Log.i("IMAGE_SEARCH_STATE", "PROCESSING --> PROCESSING_ERROR");
							if(IMAGE_ID < NUMBER_OF_IMAGES)
								browser.loadUrl("javascript:window.HTMLOUT.getHTML(document.getElementById('tDataImage"+(IMAGE_ID++)+"').innerHTML);");
							else{
								IMAGE_SEARCH_STATE = WAITING;
								//setProgressBarIndeterminateVisibility(false);
								//setProgressTitleVisibility(false);
							}
							break;
						case RENDERING:
							Log.i("IMAGE_SEARCH_STATE", "PROCESSING --> RENDERING");
							IMAGE_SEARCH_STATE = RENDERING;
							new Thread(new ImageLoader(msg.getData().getString(URL))).start();
							break;
						default:
							break;
					}
					break;
					
				case RENDERING:
					Log.i("IMAGE_SEARCH_STATE", "RENDERING");
					switch (msg.what) {
						case LOADING_SUCCESSFUL:
							
							if (loadingDialog.isShowing())
								loadingDialog.cancel();
							
							Bundle data = msg.getData();
							String path = data.getString(PATH);
							String imageName = data.getString(IMAGE_NAME);
							String extensionName = data.getString(EXTENSION_NAME);
							String imageUrl = data.getString(IMAGE_URL);
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inJustDecodeBounds = true;
					        //BitmapFactory.decodeStream(new FileInputStream(f),null,o);
							try {
								BitmapFactory.decodeFile(path, options);
						        //Log.i("decodeFile", opts1.outWidth+":"+opts1.outHeight);
						        //Find the correct scale value, it should be the power of 2.
						        if((options.outWidth > MINIMUM_SIZE) || (options.outHeight > MINIMUM_SIZE)){
						        	Log.i("IMAGE_SEARCH_STATE", "RENDERING --> LOADING_SUCCESFUL --> add to list: "+path);
						        	
						        	Image image = new Image();
						        	image.setPath(path);
						        	image.setBitmap(decodeImage(path, width, height));
						        	image.setFileName(imageName);
						        	image.setExtensionName(extensionName);
						        	image.setSelected(false);
						        	image.setUrlString(imageUrl);
						        	
						        	images.add(image);
						        	
						        	//bitmapPathList.add(path);
						        	//bitmapSampleSize.add(decodeImage(path, width, height));
						        	//downloadFlag.add(false);
									//gridView.invalidateViews();
									//gallery.invalidate();
									imageAdapter.notifyDataSetChanged();
									IMAGE_NAME_COUNTER++;
						        }
						        else{
						        	//IMAGE_NAME_COUNTER--;
						        	Log.i("IMAGE_SEARCH_STATE", "RENDERING --> LOADING_SUCCESFUL --> Rendering failed!");
						        }
							} catch (Exception e) {
								//IMAGE_NAME_COUNTER--;
					        	Log.i("IMAGE_SEARCH_STATE", "RENDERING --> LOADING_SUCCESFUL --> Rendering failed:"+e.getMessage()+"!");
							}
					        	
							
							break;
						case LOADING_UNSUCCESSFUL:
							break;
						default:
							break;
					}
					IMAGE_SEARCH_STATE = PROCESSING;
					handler.sendEmptyMessage(PROCESSING);
					break;
					
				default:
					break;
			}
		}
	};
	
	
	/*public void setProgressTitleVisibility(Boolean bool){
		progressBar.setVisibility(bool?View.VISIBLE:View.GONE);
		progressText.setVisibility(bool?View.VISIBLE:View.GONE);
		
	}*/
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		MenuItem menuItemDownload = menu.findItem(R.id.main_menu_download);
		
		if (checkBoxIsVisible) {
			int counter=0;
			for (Image image: images)
				if (image.isSelected())counter++;
			
			menuItemDownload.setEnabled((counter > 0)?true:false);
		}else
			menuItemDownload.setEnabled(false);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.main_menu_download) {
			downloadSelected();
		}
		return super.onOptionsItemSelected(item);
	}

	private void downloadSelected() {
		//TODO show percent dialog for downloading and download image
		for (Image image : images) {
			if (image.isSelected()) {
				
				String imageName = image.getFileName();
				String extension = image.getExtensionName();
				
				File source = new File(image.getPath());
				File destination = new File(downloadsPath+imageName+""+extension);
				
				try {
					InputStream in = new FileInputStream(source);
					
					int counter = 0;
					while(destination.exists()){
						counter++;
						destination = new File(downloadsPath+imageName+"("+counter+")"+extension);
					}
					
					if (counter > 0)
						imageName = imageName+"("+counter+")";
					
					
					OutputStream out = new FileOutputStream(destination);
					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0)
						out.write(buf, 0, len);
					
					out.flush();
					out.close();
					in.close();  
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
		
		resetSelections();
		checkBoxIsVisible = false;
		imageAdapter.notifyDataSetChanged();
		
		Toast.makeText(WallpaperFinder.this, "Images saved.", Toast.LENGTH_LONG).show();
	}

}
