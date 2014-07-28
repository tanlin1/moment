package com.example.services;

import android.content.Context;

import java.io.*;

/**
 * Created by HP on 2014/7/20.
 */
public class FileService {

	private Context context;

	public FileService(Context context){
		this.context = context;
	}

	public void save(String fileName, String content) throws IOException {
		FileOutputStream outputStream = context.openFileOutput(fileName,Context.MODE_MULTI_PROCESS);
		outputStream.write(content.getBytes());
		outputStream.close();

	}
	public String open(String fileName) throws IOException {
		FileInputStream fileInputStream = context.openFileInput(fileName);
		InputStream inputStream = null;
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int length;
		while((length = fileInputStream.read(buffer)) != -1){
			byteArrayOutputStream.write(buffer,0,length);
		}
		byte[] data = byteArrayOutputStream.toByteArray();
		String tt = data.toString();
		return tt;
	}
}
