package com.vm.uploadfilesample;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private Button mButtonUpload, mButtonCamera;
	private ImageView imgPreview;
	private HttpClientUpload clientUpload;
	private String Tag = MainActivity.class.getName();
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
	private int mImageHeight, mImageWidth;
	private float mScaleFactor = .4f;
	private Matrix mMatrix = new Matrix();
	private float mFocusX = 0.f;
	private float mFocusY = 0.f;
	private static File capturedImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mButtonUpload = (Button) findViewById(R.id.btn);
		mButtonCamera = (Button) findViewById(R.id.btnCamera);
		imgPreview = (ImageView) findViewById(R.id.imageView);
		mButtonUpload.setOnClickListener(this);
		mButtonCamera.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn:
			// File file = new File(Environment.getExternalStorageDirectory()
			// + "/sampletext.txt");
			if (capturedImage.exists()) {
				clientUpload = new HttpClientUpload(MainActivity.this) {
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						Log.i(Tag, "Started to Upload");
					}

					@Override
					protected void onPostExecute(String result) {
						super.onPostExecute(result);
						Log.i("Result --", result);
					}
				};
				clientUpload.execute(capturedImage);
			}

			break;
		case R.id.btnCamera:
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

			// start the image capture Intent
			startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
			break;

		}

	}

	/*
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/*
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
			capturedImage = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// display it in image view
				previewCapturedImage();
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(getApplicationContext(),
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();
			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(),
						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/*
	 * Display image from a path to ImageView
	 */
	private void previewCapturedImage() {
		try {

			imgPreview.setVisibility(View.VISIBLE);

			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 8;

			final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
					options);

			imgPreview.setImageBitmap(bitmap);

			// Bitmap bitmap =
			// BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			// BitmapDrawable drawable = new BitmapDrawable(this.getResources(),
			// bitmap);
			/*
			 * // Determine dimensions of 'earth' image Drawable d =
			 * this.getResources().getDrawable(R.drawable.image1); mImageHeight
			 * = d.getIntrinsicHeight(); mImageWidth = d.getIntrinsicWidth();
			 */

			mImageHeight = imgPreview.getLayoutParams().height;
			mImageWidth = imgPreview.getLayoutParams().width;
			/*
			 * Toast.makeText(getApplicationContext(),
			 * String.valueOf(mImageHeight), Toast.LENGTH_SHORT).show();
			 * Toast.makeText(getApplicationContext(),
			 * String.valueOf(mImageWidth), Toast.LENGTH_SHORT).show();
			 */
			// View is scaled and translated by matrix, so scale and translate
			// initially
			float scaledImageCenterX = (mImageWidth * mScaleFactor) / 2;
			float scaledImageCenterY = (mImageHeight * mScaleFactor) / 2;

			mMatrix.postScale(mScaleFactor, mScaleFactor);
			mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY
					- scaledImageCenterY);
			imgPreview.setImageMatrix(mMatrix);

		} catch (NullPointerException e) {
			Log.e(Tag, e.getMessage());
		}

	}
}
