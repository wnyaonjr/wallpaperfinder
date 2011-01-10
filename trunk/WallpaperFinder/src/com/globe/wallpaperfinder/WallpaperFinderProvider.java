package com.globe.wallpaperfinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class WallpaperFinderProvider {
	public android.os.ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException{
		URI tempUri = URI.create("file:///data/data/com.techjini/files/myImage.jpeg");
		File file = new File(tempUri);
		ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		return parcel;

	}
}
