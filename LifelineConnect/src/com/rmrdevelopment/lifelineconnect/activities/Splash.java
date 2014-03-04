package com.rmrdevelopment.lifelineconnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;

import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.utils.DataBaseManager;

public class Splash extends Activity {

	public static DataBaseManager db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_splash);

		db = DataBaseManager.getDBAdapterInstance(getApplicationContext());

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
					Cursor crsr = db.rawQuery("select * from user");
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
