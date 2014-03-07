package com.rmrdevelopment.lifelineconnect.activities;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.utils.Constant;

public class Login extends BaseActivityClass {

	Button btnLogin, btnInfo, btnClose;
	EditText editUsername, editPass;
	ToggleButton btnRememberOnOff;
	RelativeLayout relativeInfo;
	TextView txtInfo;
	Animation fade_in, fade_out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		init();
		clickEvents();
	}

	private void init() {
		// TODO Auto-generated method stub
		editUsername = (EditText) findViewById(R.id.edit_username);
		editPass = (EditText) findViewById(R.id.edit_password);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnRememberOnOff = (ToggleButton) findViewById(R.id.toggleButtonRememberButton);
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

	private void clickEvents() {
		// TODO Auto-generated method stub

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

	}

	protected void login(final String em, final String pwd) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(Login.this, null, "Loading...	",
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

				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

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
						
						Splash.db.update("user", values, "pk=1");

						Intent intent = new Intent(Login.this,
								HomeSlidingFragment.class);
						startActivity(intent);
						overridePendingTransition(R.anim.enter_from_left,
								R.anim.hold_bottom);
						finish();

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
}
