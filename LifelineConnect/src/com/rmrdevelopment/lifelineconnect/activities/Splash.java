package com.rmrdevelopment.lifelineconnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;

import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.SQLiteHelper;

public class Splash extends Activity {

	SQLiteHelper helper;
	public static SQLiteDatabase db = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_splash);

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
						}
					}

					if (LLCApplication.getUserloggedin() == 1) {
						Intent intent = new Intent(Splash.this,
								HomeSlidingFragment.class);
						startActivity(intent);
						overridePendingTransition(R.anim.enter_from_left,
								R.anim.hold_bottom);
						finish();
					} else {
						Intent intent = new Intent(Splash.this, Login.class);
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
