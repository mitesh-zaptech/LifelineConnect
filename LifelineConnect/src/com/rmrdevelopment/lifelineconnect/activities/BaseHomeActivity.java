package com.rmrdevelopment.lifelineconnect.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.fragments.HomeMenuListFragment;
import com.rmrdevelopment.lifelineconnect.slidingmenu.lib.SlidingMenu;
import com.rmrdevelopment.lifelineconnect.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseHomeActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	protected Fragment mFrag;

	public BaseHomeActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerBaseActivityReceiver();
		setTitle(mTitleRes);

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			mFrag = new HomeMenuListFragment();
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else {
			mFrag = (ListFragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		// getActionBar().setDisplayHomeAsUpEnabled(true);
		BaseActivityClass.baseAct1 = this;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}

		return super.onOptionsItemSelected(item);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}
