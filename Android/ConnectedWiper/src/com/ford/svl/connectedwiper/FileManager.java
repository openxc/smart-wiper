package com.ford.svl.connectedwiper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileManager {
	private static final String TAG = FileManager.class.getSimpleName();
	
	@SuppressWarnings("unused")
	private String fileName = null;
	private File fileLocation;
	
	public FileManager(String fileName){
		this.fileName = fileName;
		this.fileLocation = getFileDirectory(fileName);
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	
	public File getFileDirectory(String fileName) {
		
		if(isExternalStorageWritable() && isExternalStorageReadable()){
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
			file.getParentFile().mkdirs();
			return file;
	    }else{
	    	Log.e(TAG, "Error creating file");
	    	return null;
	    }
	}
	
	public void writeToFile(String strToSave){

		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(fileLocation, true);
			outputStream.write(strToSave.getBytes());
			outputStream.close();
		} catch (FileNotFoundException e) {
            Log.e(TAG, ""+e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG,""+e.getLocalizedMessage());
			e.printStackTrace();
		}

	}
}