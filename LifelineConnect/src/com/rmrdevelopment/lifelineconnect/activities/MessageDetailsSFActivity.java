package com.rmrdevelopment.lifelineconnect.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.fragments.MessageDetailsFragment;
import com.rmrdevelopment.lifelineconnect.fragments.MDMenuListFragment;
import com.rmrdevelopment.lifelineconnect.slidingmenu.lib.SlidingMenu;

public class MessageDetailsSFActivity extends BaseMDActivity {

	public MessageDetailsSFActivity() {
		super(R.string.hello_world);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSlidingMenu().setMode(SlidingMenu.RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		setContentView(R.layout.content_frame);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new MessageDetailsFragment()).commit();

		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame_two, new MDMenuListFragment())
				.commit();
	}

	public void switchContent(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
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

}
