package com.globe.wallpaperfinder;

import java.util.Comparator;

public class ImageOrder implements Comparator<Image>{

	@Override
	public int compare(Image object1, Image object2) {
		
		String image1FullName = object1.getFileName()+""+object1.getExtensionName();
		String image2FullName = object2.getFileName()+""+object2.getExtensionName();
		return image1FullName.compareToIgnoreCase(image2FullName);
	}
	

}
