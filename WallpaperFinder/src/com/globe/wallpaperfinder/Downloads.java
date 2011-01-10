package com.globe.wallpaperfinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Downloads extends Activity implements OnClickListener{
	
	GridView gridView;
	protected Context context;
	protected ArrayList<Image> images = new ArrayList<Image>();
	
	public final String downloadsPath = Environment.getExternalStorageDirectory().getPath()+"/"+WallpaperFinder.TAG+"/downloads/";
	protected File downloadsDirectory;
	
	private int width;
	private int height;
	private ImageAdapter imageAdapter;
	
	protected boolean checkBoxIsVisible = false;
	
	protected final int START = 0;
	protected final int REFRESH_GRIDVIEW = 1;
	protected final int NO_IMAGES_FOUND = 2;
	protected static final int SHOW_INSTRUCTIONS = 3;
	
	protected boolean isRunning = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.download);
		
		context = this;
		
		width = 100;
		height = 100;
		
		Button aboutButton = (Button) findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		gridView = (GridView) findViewById(R.id.gridview);
		registerForContextMenu(gridView);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				final Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
				//pickWallpaper.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setType("image/*");
				
				File downloadedPic =  new File(images.get(position).getPath());
				intent.setDataAndType(Uri.fromFile(downloadedPic), "image/*"); 
				//pickWallpaper.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(downloadedPic));
				
				//pickWallpaper.putExtra(Intent.EXTRA_SUBJECT, "test");
				//pickWallpaper.setDataAndType(Uri.parse("http://www.desktoprating.com/wallpapers/landscape-wallpapers-pictures/snow-landscape-wallpaper.jpg"), "image/jpeg");
				
		        startActivity(Intent.createChooser(intent,("View using:")));
			}
		});
		
		gridView.setAdapter(imageAdapter = new ImageAdapter());
		
		
		
		downloadsDirectory = new File(downloadsPath);
		createDirectory(downloadsDirectory);
				
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				getDownloads();
			}
		}).start();
	}
	
	private void createDirectory(File directory) {
		if (directory.exists()) {
		}
		else {
			directory.mkdirs();
		}
	}
	
	private void getDownloads() {
		//bitmapPathList
	//	bitmapSampleSize
	//downloadFlag = new ArrayList<Boolean>();
	boolean flag = true;
	
	if (downloadsDirectory.exists()) {
		File[] files = downloadsDirectory.listFiles();
		Arrays.sort(files);
		
		if (files.length > 0)
			mainHandler.sendEmptyMessage(START);
		else
			mainHandler.sendEmptyMessage(NO_IMAGES_FOUND);
		
		for (File file : files) {
			Image image = new Image();
			image.setFileName(file.getName());
			image.setExtensionName("");
			image.setPath(downloadsPath+""+file.getName());
			image.setSelected((flag = !flag));
			image.setBitmap(WallpaperFinder.decodeImage(downloadsPath+""+file.getName(), width, height));
			
			if (!isRunning)
				break;
			images.add(image);
			
			//Collections.sort(images, new ImageOrder());
			mainHandler.sendEmptyMessage(REFRESH_GRIDVIEW);
			
		}
		
		
		if (isRunning && images.size() > 0)
			mainHandler.sendEmptyMessage(SHOW_INSTRUCTIONS);
		
		
		//imageAdapter.notifyDataSetChanged();
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
	
	protected Handler mainHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case START:
					Toast.makeText(Downloads.this, "Loading images...", Toast.LENGTH_SHORT).show();
					break;
				case REFRESH_GRIDVIEW:
					imageAdapter.notifyDataSetChanged();
					break;
				case NO_IMAGES_FOUND:
					Toast.makeText(Downloads.this, "No images.", Toast.LENGTH_SHORT).show();
					break;
				case SHOW_INSTRUCTIONS:
					Toast.makeText(Downloads.this, "Long press image to set as wallpaper, rename, delete, or share.", Toast.LENGTH_LONG).show();
					break;
				default:
					break;
			}
		};
	};
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.title_only, menu);
		
		menu.setHeaderTitle("Complete action using:");
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
		final int index = info.position;
		
		final Image image = (Image) imageAdapter.getItem(index);
		final String imageName = image.getFileName()+""+image.getExtensionName();
		
		AlertDialog.Builder builder;
		AlertDialog alert;
		//Toast.makeText(this, image.getPath(), Toast.LENGTH_SHORT).show();
		switch (item.getItemId()) {
			case R.id.delete:
				builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setCancelable(true);
				builder.setInverseBackgroundForced(true);
				builder.setTitle("Delete "+imageName+"?");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						File file = new File(image.getPath());
						file.delete();
						
						images.remove(index);
						imageAdapter.notifyDataSetChanged();
						
						Toast.makeText(Downloads.this, "File successfully deleted!", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});
				
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.i("Test", "No");
						dialog.dismiss();
					}
				});
				
				alert = builder.create();
				alert.show();
			break;

			case R.id.rename:
				final EditText input = new EditText(this);
				input.setText(image.getFileName());
				
				builder = new AlertDialog.Builder(this);
				builder.setCancelable(true);
				builder.setInverseBackgroundForced(true);
				builder.setTitle("Enter new name");
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						/*File file = new File(image.getPath());
						file.delete();
						
						images.remove(index);
						imageAdapter.notifyDataSetChanged();
						dialog.dismiss();*/
						
						String newName = input.getText().toString();
						final File file = new File(image.getPath());
						final File newFile = new File(downloadsPath+""+newName);
						
						if(newFile.exists()){
							AlertDialog.Builder builder = new AlertDialog.Builder(Downloads.this);
							builder.setIcon(android.R.drawable.ic_dialog_alert);
							builder.setTitle("Delete file");
							builder.setCancelable(true);
							builder.setInverseBackgroundForced(true);
							builder.setMessage("The file name "+newName+" already exists. Are you sure you to overwrite it?");
							builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
//									File file = new File(image.getPath());
//									file.delete();
//									
//									images.remove(index);
									int i;
									for (i = 0; i < images.size(); i++){
										if (images.get(i).getFileName().equals(newFile.getName()))
											break;
									}
										
									
									images.remove(i);
									newFile.delete();
									file.renameTo(newFile);
									imageAdapter.notifyDataSetChanged();
									
									Toast.makeText(getApplicationContext(), "File renamed successfully!", Toast.LENGTH_SHORT).show();
									
									dialog.dismiss();
								}
							});
							
							builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Log.i("Test", "No");
									dialog.dismiss();
								}
							});
							
							AlertDialog alert = builder.create();
							alert.show();
						}else{
							file.renameTo(newFile);
							Toast.makeText(getApplicationContext(), "File renamed successfully!", Toast.LENGTH_SHORT).show();
						}
						
						dialog.dismiss();
					}
				});
				
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				alert = builder.create();
				alert.setView(input);
				alert.show();
				
				break;
			
				case R.id.wallpaper:
					File source = new File(image.getPath());
			try {
				InputStream in = new FileInputStream(source);
				context.setWallpaper(in);
				Toast.makeText(getApplicationContext(), "Image set as wallpaper.", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Failed to set image as wallpaper.", Toast.LENGTH_SHORT).show();
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
					//context.setWallpaper(bitmap)
				
					break;
			case R.id.share:
				/*Intent picMessageIntent = new Intent("android.intent.action.SEND_MSG");//Intent(android.content.Intent.ACTION_SEND);  
				//picMessageIntent.setType("image/*");
				picMessageIntent.setType("vnd.android-dir/mms-sms"); 
				File downloadedPic =  new File(image.getPath());  
				picMessageIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(downloadedPic));
				startActivity(Intent.createChooser(picMessageIntent, "Send your picture using:"));*/
				//intent.putExtra("sms_body", "some text");
			    //intent.putExtra(Intent.EXTRA_SUBJECT, image.getFileName());
			    //intent.putExtra("msg_uri", Uri.parse(image.getPath()));
			    //intent.setType("i/*");
			    //intent.setType("*/*");
			  //Full Path to the attachment
			    //Intent sendIntent = new Intent(Intent.ACTION_SEND); 
			    /*sendIntent.putExtra("sms_body", "some text"); 
			    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
			    sendIntent.setType("image/png"); */

			   // File downloadedPic =  new File(image.getPath());
			    
				//intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(downloadedPic));
			    //intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+image.getPath()));
				
				//Log.i("Downloads", "Downloads --> "+Uri.parse("file://"+image.getPath()));
				
			    //startActivity(Intent.createChooser(intent, "Send your picture using:"));
			    
			    
			    Intent intent = new Intent("android.intent.action.SEND");
			    intent.setType("image/*"); 
			    intent.putExtra("sms_body", "Found this great wallpaper via WallpaperFinder!"); 

			    
			    // Add a new record without the bitmap, but with the values just set.
			    // insert() returns the URI of the new record.

			    // Now get a handle to the file for that record, and save the data into it.
			    // Here, sourceBitmap is a Bitmap object representing the file to save to the database.
			    try {
			    	
			    	ContentValues values = new ContentValues(3);
				    values.put(Media.DISPLAY_NAME, image.getFileName());
				    values.put(Media.MIME_TYPE, "image/png");
				    
				    Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
				    
				    File originalImage = new File(image.getPath());
				    InputStream is = new FileInputStream(originalImage);
			        OutputStream outStream = getContentResolver().openOutputStream(uri);
					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					while ((len = is.read(buf)) > 0)
						outStream.write(buf, 0, len);
					
					outStream.flush();
					outStream.close();
					is.close();
			        //sourceBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
					intent.putExtra(Intent.EXTRA_STREAM, uri);
			    } catch (Exception e) {
			    	e.printStackTrace();
			    }
			    
			   //startActivity(intent);
			    startActivity(Intent.createChooser(intent, "Send your picture using:"));
				//break;
			default:
				break;
		
		}
		
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.about_button:
				AlertDialog.Builder builder = new AlertDialog.Builder(Downloads.this);
				builder.setInverseBackgroundForced(true);
				builder.setCancelable(true);
				
				builder.setTitle("About");
				builder.setIcon(R.drawable.icon22);
				builder.setMessage("Wallpaper Finder - v1.0\n\n\u00A9 2010 - Team Underground");
				builder.setPositiveButton("Show Eula", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						AlertDialog.Builder builder = new AlertDialog.Builder(Downloads.this);
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
	
	@Override
	protected void onPause() {
		isRunning = false;
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		
		for (Image image : images) {
			try {
				getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, Media.DISPLAY_NAME+"=\'"+image.getFileName()+"\'", null);
				Bitmap bitmap = image.getBitmap();
				bitmap.recycle();
				bitmap = null;
				java.lang.System.gc();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		super.onDestroy();
	}

}
