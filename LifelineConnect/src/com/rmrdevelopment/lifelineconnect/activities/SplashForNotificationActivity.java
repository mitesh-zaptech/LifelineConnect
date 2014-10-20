package com.rmrdevelopment.lifelineconnect.activities;

import android.content.Intent;
import android.os.Bundle;

import com.rmrdevelopment.lifelineconnect.AppBaseActivity;

public class SplashForNotificationActivity extends AppBaseActivity{

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		registerBaseActivityReceiver();
		
		Intent notificationIntent = new Intent(this, SplashActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
				Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(notificationIntent);
		
		closeAllActivities(); 
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
