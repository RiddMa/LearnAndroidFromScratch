package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class TakePictureActivity extends AppCompatActivity {
	private ImageView imageView;
	private final static int REQUEST_IMAGE_CAPTURE = 1;
	private final static int REQUEST_CAMERA = 123;
	private static final int REQUEST_EXTERNAL_STORAGE = 101;
	private static final String FILE_PROVIDER_AUTHORITY = "com.bytedance.camera.demo.fileprovider";
	private Uri mImageUri;
	private File imagePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);

		imageView = findViewById(R.id.img);
		findViewById(R.id.btn_picture).setOnClickListener(v -> {
			if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
					|| ContextCompat.checkSelfPermission(TakePictureActivity.this,
					Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(TakePictureActivity.this, new String[]{Manifest.permission.CAMERA,
						Manifest.permission.WRITE_EXTERNAL_STORAGE}, TakePictureActivity.REQUEST_CAMERA);
				Toast.makeText(this, "申请权限", Toast.LENGTH_SHORT).show();
			} else {
				takePicture();
			}
		});

	}

	private void takePicture() {
		Toast.makeText(this, "启动相机", Toast.LENGTH_SHORT).show();
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (cameraIntent.resolveActivity(getPackageManager()) != null) {
			imagePath = createImageFile();
			if (imagePath != null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					mImageUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, imagePath);
				} else {
					mImageUri = Uri.fromFile(imagePath);
				}
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
				startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	}

	private File createImageFile() {
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "RIDD_" + timestamp + "_";
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File imageFile = null;
		try {
			imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageFile;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			setPic();
		}
	}

	private void setPic() {
		Toast.makeText(this, mImageUri.getScheme(), Toast.LENGTH_SHORT).show();

		// Get the dimensions of the View
		int targetW = imageView.getWidth();
		int targetH = imageView.getHeight();
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		if (imagePath != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath.getPath(), bmOptions);
			Utils.rotateImage(bitmap, imagePath.getPath());
			imageView.setImageBitmap(bitmap);
		} else {
			Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case REQUEST_EXTERNAL_STORAGE:
			case REQUEST_CAMERA: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "已经授权" + Arrays.toString(permissions), Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	}
}
