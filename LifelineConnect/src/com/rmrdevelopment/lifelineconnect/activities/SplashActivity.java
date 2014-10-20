package com.rmrdevelopment.lifelineconnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

import com.crittercism.app.Crittercism;
import com.google.analytics.tracking.android.EasyTracker;
import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.SQLiteHelper;

public class SplashActivity extends Activity {

	private SQLiteHelper helper;
	public static SQLiteDatabase db = null;

	public static SharedPreferences sharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_splash);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		//boolean NotificationFlag = sharedPreferences.getBoolean("Notification_Flag", true);
		Crittercism.initialize(getApplicationContext(), "53cf65570729df7716000004");
		
		helper = new SQLiteHelper(this, "lifelineconnect.sqlite");
		helper.createDatabase();
		db = helper.openDatabase();

		new Thread() {
			public void run() {
				try {
					int waited = 0;
					while (waited < 2000) {
						sleep(100);
						waited += 100;
					}
				} catch (InterruptedException e) {
				} finally {
					Cursor crsr = db.rawQuery("select * from user",null);
					if (crsr != null) {
						if (crsr.getCount() > 0) {
							crsr.moveToFirst();
							LLCApplication.setUsername(""
									+ crsr.getString(crsr
											.getColumnIndex("username")));
							LLCApplication.setPassword(""
									+ crsr.getString(crsr
											.getColumnIndex("password")));

							LLCApplication.setUserId(""
									+ crsr.getString(crsr
											.getColumnIndex("User_ID")));
							LLCApplication.setCanSeeDownline(""
									+ crsr.getString(crsr
											.getColumnIndex("CanSeeDownline")));
							LLCApplication.setUplineName(""
									+ crsr.getString(crsr
											.getColumnIndex("UplineName")));
							LLCApplication.setUplineUserID(""
									+ crsr.getString(crsr
											.getColumnIndex("UplineUserID")));
							LLCApplication
									.setReceiveNotifications(""
											+ crsr.getString(crsr
													.getColumnIndex("ReceiveNotifications")));
							
							LLCApplication.setRemember(crsr.getInt(crsr
									.getColumnIndex("remember")));
							LLCApplication.setUserloggedin(crsr.getInt(crsr
									.getColumnIndex("userloggedin")));
							LLCApplication
									.setCountMessagesDownline(crsr.getString(crsr
											.getColumnIndex("countMessagesDownline")));
							LLCApplication
									.setCountMessagesUpline(crsr.getString(crsr
											.getColumnIndex("countMessagesUpline")));
							LLCApplication
							.setSpeaker(crsr.getString(crsr
									.getColumnIndex("speaker")));
							
							LLCApplication.setCanVoiceTag(sharedPreferences.getString("CanVoiceTag", "1"));
						}
					}

					if (LLCApplication.getUserloggedin() == 1) {
						Intent intent = new Intent(SplashActivity.this,
								HomeSlidingFragmentActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.enter_from_left,
								R.anim.hold_bottom);
						finish();
					} else {
						Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.enter_from_left,
								R.anim.hold_bottom);
						finish();
					}
				}

			}
		}.start();

	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); 
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
