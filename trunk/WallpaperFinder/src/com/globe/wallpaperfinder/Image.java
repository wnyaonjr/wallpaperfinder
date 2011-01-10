package com.globe.wallpaperfinder;

import android.graphics.Bitmap;

public class Image {
	private Bitmap bitmap;
	private String path;
	//private boolean flag;
	private boolean isSelected;
	private String fileName;
	private String extensionName;
	private String urlString;
	
	/**
	 * @return the bitmap
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}
	/**
	 * @param bitmap the bitmap to set
	 */
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}
	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the extensionName
	 */
	public String getExtensionName() {
		return extensionName;
	}
	/**
	 * @param extensionName the extensionName to set
	 */
	public void setExtensionName(String extensionName) {
		this.extensionName = extensionName;
	}
	/**
	 * @return the urlString
	 */
	public String getUrlString() {
		return urlString;
	}
	/**
	 * @param urlString the urlString to set
	 */
	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	
}
