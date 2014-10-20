package com.rmrdevelopment.lifelineconnect.activities;

import static com.rmrdevelopment.lifelineconnect.utils.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.rmrdevelopment.lifelineconnect.utils.CommonUtilities.SENDER_ID;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gcm.GCMRegistrar;
import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.utils.CommonUtilities;
import com.rmrdevelopment.lifelineconnect.utils.Constant;

public class LoginActivity extends BaseActivityClass {

	private Button btnLogin;
	private Button btnInfo;
	private Button btnClose;
	
	private EditText editUsername;
	private EditText editPass;
	
	private ToggleButton btnRememberOnOff;
	
	private RelativeLayout relativeInfo;
	
	private TextView txtInfo;
	private TextView txtRememberMe;
	
	private Animation fade_in;
	private Animation fade_out;
	
	//GCM
	AsyncTask<Void, Void, Void> mRegisterTask;
	SharedPreferences preferences;
	
	String respStoreGCM = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		// GCM
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

		final String regId = GCMRegistrar.getRegistrationId(this);
		Log.e("", "regId==" + regId);
		if (regId.equals("")) {
			Log.d("Applied for register", "true");
			// Automatically registers application on startup.
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			Log.d("register id not null", "true");
			// Device is already registered on GCM, needs to check if it is
			// registered on our server as well.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.
						if (!CommonUtilities.register) {
							GCMRegistrar.unregister(context);
						}
						return null;
					}
					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}
				};
				mRegisterTask.execute(null, null, null);
			}
		}
		// GCM
		if(preferences.getString("GCM_APP_ID", "").equalsIgnoreCase("")){}		

		init();
		clickEvents();
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
	
	private void init() {
		// TODO Auto-generated method stub
		editUsername = (EditText) findViewById(R.id.edit_username);
		editPass = (EditText) findViewById(R.id.edit_password);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnRememberOnOff = (ToggleButton) findViewById(R.id.toggleButtonRememberButton);
		txtRememberMe= (TextView) findViewById(R.id.textViewRemember);
		relativeInfo = (RelativeLayout) findViewById(R.id.infolayout);
		btnInfo = (Button) findViewById(R.id.btninfo);
		btnClose = (Button) findViewById(R.id.btnclose);
		txtInfo = (TextView) findViewById(R.id.txtinfo);
		txtInfo.setText(Constant.loginInfo);

		Log.i("remember", "" + LLCApplication.getRemember());
		if (LLCApplication.getRemember() == 0) {
			btnRememberOnOff.setChecked(false);
			btnRememberOnOff.setBackgroundResource(R.drawable.checkbox_blank);
		} else {
			btnRememberOnOff.setBackgroundResource(R.drawable.checkbox);
			btnRememberOnOff.setChecked(true);
			editUsername.setText("" + LLCApplication.getUsername());
			editPass.setText("" + LLCApplication.getPassword());
		}

		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(1000);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(1000);

	}
	
	// GCM
	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}
	
	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
		}
	}
	
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		// message
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString("message");

			Toast.makeText(LoginActivity.this, newMessage, Toast.LENGTH_LONG)
			.show();
		}
	};

	private void clickEvents() {
		// TODO Auto-generated method stub

		btnInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editUsername.setFocusable(false);
				editPass.setFocusable(false);
				relativeInfo.setVisibility(View.VISIBLE);
				relativeInfo.startAnimation(fade_in);
				disableComponents();
			}
		});

		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editUsername.setFocusable(true);
				editUsername.setFocusableInTouchMode(true);
				editPass.setFocusable(true);
				editPass.setFocusableInTouchMode(true);
				relativeInfo.setVisibility(View.GONE);
				relativeInfo.startAnimation(fade_out);
				enableComponents();
			}
		});

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editUsername.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editPass.getWindowToken(), 0);

				String email_ = editUsername.getText().toString();
				String pass_ = editPass.getText().toString();

				if (isValidate(email_, pass_)) {

					if (isOnline()) {
						login(email_, pass_);
					} else {
						Toast.makeText(getApplicationContext(),
								"" + Constant.network_error, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});

		btnRememberOnOff
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						Log.e("btnRememberOnOff", "" + isChecked);
						if (isChecked) {
							btnRememberOnOff
									.setBackgroundResource(R.drawable.checkbox);
						} else {
							btnRememberOnOff
									.setBackgroundResource(R.drawable.checkbox_blank);
						}
					}
				});

		txtRememberMe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!btnRememberOnOff.isChecked()) {
					btnRememberOnOff.setChecked(true);
					btnRememberOnOff
							.setBackgroundResource(R.drawable.checkbox);
				} else {
					btnRememberOnOff.setChecked(false);
					btnRememberOnOff
							.setBackgroundResource(R.drawable.checkbox_blank);
				}
			}
		});
	}

	protected void login(final String em, final String pwd) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(LoginActivity.this, null, "Loading...	",
				true, false);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String deviceToken = ""
						+ Secure.getString(context.getContentResolver(),
								Secure.ANDROID_ID);
				Log.i("deviceToken", "" + deviceToken);
				Log.i("deviceToken", "" + deviceToken);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "CheckLogin");
				map.put("Username", "" + em);
				map.put("Password", "" + pwd);
				map.put("deviceToken", "" + deviceToken);

				/*try {
					map.put("Username", "" + URLEncoder.encode(em, "utf-8"));
					map.put("Password", "" + URLEncoder.encode(pwd, "utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
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
					try {
						json_str = new JSONObject(response);

						if (!json_str.getString("User_ID").equals("-1")) {
							LLCApplication.setUserId(""
									+ json_str.getString("User_ID"));
							LLCApplication.setCanSeeDownline(""
									+ json_str.getString("CanSeeDownline"));
							LLCApplication.setUplineName(""
									+ json_str.getString("UplineName"));
							LLCApplication.setUplineUserID(""
									+ json_str.getString("UplineUserID"));
							LLCApplication.setReceiveNotifications(""
									+ json_str
											.getString("ReceiveNotifications"));
							LLCApplication.setCanVoiceTag(""
									+ json_str
											.getString("CanVoiceTag"));
							LLCApplication.setUsername(editUsername.getText()
									.toString());
							LLCApplication.setPassword(editPass.getText()
									.toString());
							LLCApplication.setUserloggedin(1);

							if (btnRememberOnOff.isChecked()) {
								LLCApplication.setRemember(1);
							} else {
								LLCApplication.setRemember(0);
							}

							GetUnreadMessageCount();
						} else {
							Toast.makeText(
									getApplicationContext(),
									"please enter correct username and password",
									Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}

	protected void GetUnreadMessageCount() {
		// TODO Auto-generated method stub

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "GetUnreadMessageCount");
				map.put("User_ID", "" + LLCApplication.getUserId());

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateGetUnreadMessageCount();
			}
		});
		t.start();
	}

	private void UpdateGetUnreadMessageCount() {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (response != null) {
					try {
						json_str = new JSONObject(response);
						LLCApplication.setCountMessagesDownline(""
								+ json_str.getString("countMessagesDownline"));
						LLCApplication.setCountMessagesUpline(""
								+ json_str.getString("countMessagesUpline"));

						ContentValues values = new ContentValues();
						values.put("username",
								"" + LLCApplication.getUsername());
						values.put("password",
								"" + LLCApplication.getPassword());

						values.put("User_ID", "" + LLCApplication.getUserId());
						values.put("CanSeeDownline",
								"" + LLCApplication.getCanSeeDownline());
						values.put("UplineName",
								"" + LLCApplication.getUplineName());
						values.put("UplineUserID",
								"" + LLCApplication.getUplineUserID());
						values.put("ReceiveNotifications",
								"" + LLCApplication.getReceiveNotifications());
						
						values.put("remember",
								"" + LLCApplication.getRemember());
						values.put("userloggedin",
								"" + LLCApplication.getUserloggedin());
						values.put("countMessagesDownline",
								"" + LLCApplication.getCountMessagesDownline());
						values.put("countMessagesUpline",
								"" + LLCApplication.getCountMessagesUpline());
						
						SplashActivity.db.update("user", values, "pk=1",null);
						
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
						Editor edit = sharedPreferences.edit();
						edit.putString("CanVoiceTag", ""+LLCApplication.getCanVoiceTag());
						edit.commit();
						
						storeGCMId();

					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}

	public boolean isValidate(String email, String password) {

		if (email.trim().length() == 0) {
			Toast.makeText(getApplicationContext(), "Enter Username.",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (password.trim().length() == 0) {
			Toast.makeText(getApplicationContext(), "Enter Password.",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;

	}

	protected void enableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(true);
		editUsername.setEnabled(true);
		editPass.setEnabled(true);
		btnRememberOnOff.setEnabled(true);
		btnLogin.setEnabled(true);
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(false);
		editUsername.setEnabled(false);
		editPass.setEnabled(false);
		btnRememberOnOff.setEnabled(false);
		btnLogin.setEnabled(false);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		finish();
	}
	
	public void storeGCMId() {
		// TODO Auto-generated method stub
		
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "GCMStore");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("RegistrationID",""+preferences.getString("GCM_APP_ID", ""));
				map.put("Active", "1");
				Log.i("storeGCMId map", "" + map);
				respStoreGCM = callAPI(map);
				Log.i("respSaveSettings", "" + respStoreGCM);
				updateGCMInfo();
			}
		});
		t.start();
	}
	
	public void updateGCMInfo(){
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				
				if (respStoreGCM != null) {
					try {
						json_str = new JSONObject(respStoreGCM);
						
						Intent intent = new Intent(LoginActivity.this,
								HomeSlidingFragmentActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.enter_from_left,
								R.anim.hold_bottom);
						finish();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	
}
