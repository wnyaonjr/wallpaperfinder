package com.globe.wallpaperfinder;

import java.io.File;
import java.util.Comparator;

public class FileOrder implements Comparator<File>{

	@Override
	public int compare(File object1, File object2) {
		
		String image1FullName = object1.getName();
		String image2FullName = object2.getName();
		return image1FullName.compareToIgnoreCase(image2FullName);
	}
	

}
