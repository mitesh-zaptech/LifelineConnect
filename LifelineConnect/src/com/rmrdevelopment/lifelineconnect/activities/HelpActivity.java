package com.rmrdevelopment.lifelineconnect.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.rmrdevelopment.lifelineconnect.R;

public class HelpActivity extends BaseActivityClass {

	private WebView webView;
	private RelativeLayout relativeBack;
	private int pos;
	private Button btnHome;

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			pos = extras.getInt("pos");
		}

		btnHome = (Button) findViewById(R.id.btnhome);
		relativeBack = (RelativeLayout) findViewById(R.id.relback);
		webView = (WebView) findViewById(R.id.webview);
		webView.setBackgroundColor(0x00000000);
		//webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(true);

		switch (pos) {
		case 0:
			webView.loadUrl("file:///android_res/raw/help.html");
			break;
		case 1:
			webView.loadUrl("file:///android_res/raw/privacy.html");
			break;
		case 2:
			webView.loadUrl("file:///android_res/raw/terms.html");
			break;
		default:
			break;
		}

		btnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HelpActivity.this,
						HomeSlidingFragmentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}
}
