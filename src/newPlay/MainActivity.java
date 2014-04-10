package com.example.newPlay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	// YOU CAN EDIT THIS TO WHATEVER YOU WANT
	private static final int SELECT_PICTURE = 1;

	private String filemanagerstring;
	public static String selectedImagePath;
	// ADDED

	File tempFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.e("Application", "started!!!");
		((Button) findViewById(R.id.bImage))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						// in onCreate or any event where your want the user to
						// select a file
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						try {
							startActivityForResult(Intent.createChooser(intent,
									"Select Picture"), SELECT_PICTURE);
						} catch (Exception e) {
							Log.e("MainACtivity", "" + e);
						}
					}
				});
	}

	// UPDATED

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				filemanagerstring = selectedImageUri.getPath();
				selectedImagePath = getPath(selectedImageUri);
				if (selectedImagePath == null) {
					selectedImagePath = filemanagerstring;
					System.out.println(selectedImagePath
							+ " is the right one for you!");
				}

				String path = Environment.getExternalStorageDirectory()
						.toString();
				File myDir = new File(path + "/MagicInfo_Images");
				myDir.mkdirs();
				File tempFile = new File(myDir, "tempPhoto.jpeg");
				File selectedFile = new File(selectedImagePath);
				FileOutputStream out;
				try {
					out = new FileOutputStream(tempFile);
					FileInputStream in = new FileInputStream(selectedFile);
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
					selectedImagePath = tempFile.getAbsolutePath();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent iImageEdit = new Intent(this.getBaseContext(),ViewPagerActivity.class);
			     startActivity(iImageEdit);
			}
		}
	}

	// UPDATED!
	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

}