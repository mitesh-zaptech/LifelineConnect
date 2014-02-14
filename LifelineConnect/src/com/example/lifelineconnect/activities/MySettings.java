package com.example.lifelineconnect.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifelineconnect.LLCApplication;
import com.example.lifelineconnect.utils.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MySettings extends BaseActivityClass {

	Typeface type;
	TextView title;
	RelativeLayout relativeBack;
	Button btnCapture, btnSaveSettings;
	ImageView img;

	MySettings CameraActivity = null;
	Uri imageUri = null;
	static Cursor cursor = null;
	static String camera_pathname;
	Bitmap bmp;

	RelativeLayout relativeInfo;
	TextView txtInfo;
	Animation fade_in, fade_out;
	Button btnInfo, btnClose;

	ProgressDialog progressDialog;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mysettings);

		camera_pathname = "";
		CameraActivity = this;
		type = Typeface.createFromAsset(getAssets(), "font.ttf");

		init();
		clickEvents();
	}

	private void init() {
		// TODO Auto-generated method stub
		relativeBack = (RelativeLayout) findViewById(R.id.relback);
		title = (TextView) findViewById(R.id.title);
		btnCapture = (Button) findViewById(R.id.btncapture);
		btnSaveSettings = (Button) findViewById(R.id.btnsavesettings);
		img = (ImageView) findViewById(R.id.img);

		relativeInfo = (RelativeLayout) findViewById(R.id.infolayout);
		btnInfo = (Button) findViewById(R.id.btninfo);
		btnClose = (Button) findViewById(R.id.btnclose);
		txtInfo = (TextView) findViewById(R.id.txtinfo);
		txtInfo.setText(Constant.MysettingsInfo);

		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(1000);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(1000);

		title.setTypeface(type);

		String fileName = "Camera_Example.jpg";
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		imageUri = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Log.i("path:",
				"" + Constant.sSiteUrl + "DesktopModules/TeamTalkApp/Images/"
						+ LLCApplication.getUserId() + ".thumb.jpg");
		options = new DisplayImageOptions.Builder().showStubImage(0)
				.showImageForEmptyUri(0).bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		imageLoader.displayImage(
				Constant.sSiteUrl + "DesktopModules/TeamTalkApp/Images/"
						+ LLCApplication.getUserId() + ".thumb.jpg", img,
				options);
	}

	private void clickEvents() {
		// TODO Auto-generated method stub
		btnCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				registerForContextMenu(v);
				v.showContextMenu();
			}
		});

		btnSaveSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isOnline()) {
					if (camera_pathname.length() > 0) {
						Log.i("camera_pathname", "" + camera_pathname);
						uploadImage(camera_pathname);
					} else {
						Toast.makeText(getApplicationContext(),
								"please capture an image", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"" + Constant.network_error, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		btnInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				relativeInfo.setVisibility(View.VISIBLE);
				relativeInfo.startAnimation(fade_in);
				disableComponents();
			}
		});

		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				relativeInfo.setVisibility(View.GONE);
				relativeInfo.startAnimation(fade_out);
				enableComponents();
			}
		});

		relativeBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		String[] menuItems = new String[] { "Photo Library", "Camera", "Cancel" };

		menu.add(Menu.NONE, 0, 0, menuItems[0]);
		menu.add(Menu.NONE, 1, 1, menuItems[1]);
		menu.add(Menu.NONE, 2, 2, menuItems[2]);

	}

	public boolean onContextItemSelected(MenuItem item) {

		int menuItemIndex = item.getItemId();
		switch (menuItemIndex) {
		case 0: {
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, 2);
			break;
		}
		case 1: {
			if (imageUri != null) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
				startActivityForResult(intent, 1);
			}
		}
			break;
		case 2:
			break;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {

				String imageId = convertImageUriToFile(imageUri, CameraActivity);
				new LoadImagesFromSDCard().execute("" + imageId);

			}
		} else if (requestCode == 2) {
			camera_pathname = null;
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = null;
				if (data != null) {
					try {
						uri = data.getData();

						String imageId = convertImageUriToFile(uri,
								CameraActivity);
						new LoadImagesFromSDCard().execute("" + imageId);

					} catch (Exception e) {
						// TODO: handle exception
						Log.i("Exception", "" + e);
					}
				}
				Log.d("Gallery Path: ", camera_pathname + bmp);

			}
		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/************ Convert Image Uri path to physical path **************/

	public static String convertImageUriToFile(Uri imageUri, Activity activity) {

		int imageID = 0;

		try {

			/*********** Which columns values want to get *******/
			String[] proj = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID,
					MediaStore.Images.Thumbnails._ID,
					MediaStore.Images.ImageColumns.ORIENTATION };

			cursor = activity.managedQuery(

			imageUri, // Get data for specific image URI
					proj, // Which columns to return
					null, // WHERE clause; which rows to return (all rows)
					null, // WHERE clause selection arguments (none)
					null // Order-by clause (ascending by name)

					);

			// Get Query Data

			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
			int columnIndexThumb = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
			int file_ColumnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

			// int orientation_ColumnIndex = cursor.
			// getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

			int size = cursor.getCount();

			/******* If size is 0, there are no images on the SD Card. *****/

			if (size == 0) {

			} else {

				int thumbID = 0;
				if (cursor.moveToFirst()) {

					/**************** Captured image details ************/

					/***** Used to show image on view in LoadImagesFromSDCard class ******/
					imageID = cursor.getInt(columnIndex);

					thumbID = cursor.getInt(columnIndexThumb);

					String Path = cursor.getString(file_ColumnIndex);
					camera_pathname = Path;
					// String orientation =
					// cursor.getString(orientation_ColumnIndex);

					String CapturedImageDetails = " CapturedImageDetails : \n\n"
							+ " ImageID :"
							+ imageID
							+ "\n"
							+ " ThumbID :"
							+ thumbID + "\n" + " Path :" + Path + "\n";

					// Show Captured Image detail on activity
					// imageDetails.setText( CapturedImageDetails );

				}
			}
		} finally {
			/*
			 * if (cursor != null) { cursor.close(); }
			 */
		}

		// Return Captured Image ImageID ( By this ImageID Image will load from
		// sdcard )

		return "" + imageID;
	}

	/**
	 * Async task for loading the images from the SD card.
	 * 
	 * @author Android Example
	 * 
	 */

	// Class with extends AsyncTask class

	public class LoadImagesFromSDCard extends AsyncTask<String, Void, Void> {

		private ProgressDialog Dialog = new ProgressDialog(MySettings.this);

		protected void onPreExecute() {
			/****** NOTE: You can call UI Element here. *****/

			// Progress Dialog
			Dialog.setMessage(" Loading image from Sdcard..");
			Dialog.show();
		}

		// Call after onPreExecute method
		protected Void doInBackground(String... urls) {

			Bitmap bitmap = null;
			Bitmap newBitmap = null;
			Uri uri = null;

			try {

				/**
				 * Uri.withAppendedPath Method Description Parameters baseUri
				 * Uri to append path segment to pathSegment encoded path
				 * segment to append Returns a new Uri based on baseUri with the
				 * given segment appended to the path
				 */

				uri = Uri.withAppendedPath(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ""
								+ urls[0]);

				/************** Decode an input stream into a bitmap. *********/
				bitmap = BitmapFactory.decodeStream(getContentResolver()
						.openInputStream(uri));

				if (bitmap != null) {

					/********* Creates a new bitmap, scaled from an existing bitmap. ***********/

					newBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200,
							true);

					bitmap.recycle();

					if (newBitmap != null) {

						bmp = newBitmap;
					}
				}
			} catch (IOException e) {
				// Error fetching image, try to recover

				/********* Cancel execution of this task. **********/
				cancel(true);
			}

			return null;
		}

		protected void onPostExecute(Void unused) {

			// NOTE: You can call UI Element here.

			// Close progress dialog
			Dialog.dismiss();

			if (bmp != null) {
				Bitmap newbm = decodeFile(camera_pathname);
				newbm = Bitmap.createScaledBitmap(newbm, 200, 200, true);
				img.setVisibility(View.VISIBLE);
				img.setImageBitmap(newbm);
			}

		}

		public Bitmap decodeFile(String path) {// you can provide file path here
			int orientation;
			try {
				if (path == null) {
					return null;
				}
				// decode image size
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;
				// Find the correct scale value. It should be the power of 2.
				final int REQUIRED_SIZE = 70;
				int width_tmp = o.outWidth, height_tmp = o.outHeight;
				int scale = 0;
				while (true) {
					if (width_tmp / 2 < REQUIRED_SIZE
							|| height_tmp / 2 < REQUIRED_SIZE)
						break;
					width_tmp /= 2;
					height_tmp /= 2;
					scale++;
				}
				// decode with inSampleSize
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = scale;
				Bitmap bm = BitmapFactory.decodeFile(path, o2);
				Bitmap bitmap = bm;

				ExifInterface exif = new ExifInterface(path);

				orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION, 1);

				Log.e("ExifInteface .........", "rotation =" + orientation);

				// exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

				Log.e("orientation", "" + orientation);
				Matrix m = new Matrix();

				if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
					m.postRotate(180);
					// m.postScale((float) bm.getWidth(), (float)
					// bm.getHeight());
					// if(m.preRotate(90)){
					Log.e("in orientation", "" + orientation);
					bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
							bm.getHeight(), m, true);
					return bitmap;
				} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
					m.postRotate(90);
					Log.e("in orientation", "" + orientation);
					bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
							bm.getHeight(), m, true);
					return bitmap;
				} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
					m.postRotate(270);
					Log.e("in orientation", "" + orientation);
					bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
							bm.getHeight(), m, true);
					return bitmap;
				}
				return bitmap;
			} catch (Exception e) {
				return null;
			}

		}

	}

	public void uploadImage(final String filePath) {
		progressDialog = ProgressDialog.show(MySettings.this, null,
				"Loading...	", true, false);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String resp = "";

				try {

					HttpClient client = new DefaultHttpClient();
					HttpResponse response = null;

					HttpPost poster = new HttpPost(
							Constant.sSiteUrl
									+ "/desktopmodules/teamtalkapp/UploadImage.aspx?User_ID="
									+ LLCApplication.getUserId());

					FileBody fbody = null;
					MultipartEntity entity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);

					Log.i("filePath", "" + filePath);
					File image = new File(filePath);
					fbody = new FileBody(image, "image/jpeg");
					entity.addPart("file", fbody);

					poster.setEntity(entity);
					response = client.execute(poster);

					BufferedReader rd = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					String line = null;
					while ((line = rd.readLine()) != null) {
						resp += line;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.d("Resp Upload", "" + resp);
				UpdateuploadImage(resp);
			}
		});
		t.start();

	}

	protected void UpdateuploadImage(final String response) {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (response != null) {
					if (response.equals("200")) {
						SaveSettings();
					}
				}

			}
		});
	}

	protected void SaveSettings() {
		// TODO Auto-generated method stub

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String deviceToken = ""
						+ Secure.getString(context.getContentResolver(),
								Secure.ANDROID_ID);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "SaveSettings");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("ReceiveNotifications",
						"" + LLCApplication.getReceiveNotifications());
				map.put("token", "" + deviceToken);

				response = callAPI(map);
				Log.i("response", "" + response);
				Update();
			}
		});
		t.start();
	}

	private void Update() {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				if (response != null) {
					int status = 0;
					try {
						json_str = new JSONObject(response);
						status = json_str.getInt("Status");

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (status == 1) {
						finish();
						overridePendingTransition(R.anim.hold_top,
								R.anim.exit_in_left);
					}
				}

			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (cursor != null) {
			cursor.close();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}

	protected void enableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(true);
		relativeBack.setEnabled(true);
		btnCapture.setEnabled(true);
		btnSaveSettings.setEnabled(true);
		img.setEnabled(true);
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(false);
		relativeBack.setEnabled(false);
		btnCapture.setEnabled(false);
		btnSaveSettings.setEnabled(false);
		img.setEnabled(false);
	}

}