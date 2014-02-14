package com.example.lifelineconnect.activities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifelineconnect.LLCApplication;
import com.example.lifelineconnect.utils.Constant;

public class PassdownMessage extends BaseActivityClass {

	Context mContext = this;
	TextView title;
	RelativeLayout relativeBack;
	Typeface type;
	Button btnPass;
	EditText editMessage;
	int position;
	int msgType = 0;
	String msgStr = "";

	RelativeLayout relativeList;
	ListView listview;
	Button btnCancel;
	TextView txtTag;
	String strtags = "";
	String strIDs = "0";

	RelativeLayout relativeInfo;
	TextView txtInfo;
	Animation fade_in, fade_out;
	Button btnInfo, btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passdownmessage);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			position = extras.getInt("pos");
			msgType = extras.getInt("msgType");
		}

		type = Typeface.createFromAsset(getAssets(), "font.ttf");

		init();
		clickEvents();
	}

	private void init() {
		// TODO Auto-generated method stub
		relativeBack = (RelativeLayout) findViewById(R.id.relback);
		title = (TextView) findViewById(R.id.title);
		editMessage = (EditText) findViewById(R.id.msgtag);
		btnPass = (Button) findViewById(R.id.btnpass);
		title.setTypeface(type);

		relativeList = (RelativeLayout) findViewById(R.id.listlayout);
		listview = (ListView) findViewById(R.id.lst);
		btnCancel = (Button) findViewById(R.id.btncancel);
		txtTag = (TextView) findViewById(R.id.txttag);

		relativeInfo = (RelativeLayout) findViewById(R.id.infolayout);
		btnInfo = (Button) findViewById(R.id.btninfo);
		btnClose = (Button) findViewById(R.id.btnclose);
		txtInfo = (TextView) findViewById(R.id.txtinfo);

		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(200);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(200);

		if (msgType == 0) {
			txtInfo.setText("" + Constant.passdownMessageInfo);
			title.setText("Passdown Message");
			msgStr = "Are you sure you want to passdown this Message?";
			txtTag.setVisibility(View.VISIBLE);

			strtags = "";
			strIDs = "";
			for (int i = 0; i < LLCApplication.getDistroLists().size(); i++) {
				if (LLCApplication.getDistroLists().get(i).get("checked")
						.equals("1")) {

					if (strtags.length() == 0) {
						strtags += LLCApplication.getDistroLists().get(i)
								.get("Description");
						strIDs += LLCApplication.getDistroLists().get(i)
								.get("ID");
					} else {
						strtags += ", "
								+ LLCApplication.getDistroLists().get(i)
										.get("Description");
						strIDs += ";"
								+ LLCApplication.getDistroLists().get(i)
										.get("ID");
					}
				}
			}
			txtTag.setText("" + strtags);
		} else if (msgType == 1) {
			txtInfo.setText("" + Constant.passupMessageInfo);
			title.setText("Passup Message");
			msgStr = "Are you sure you want to passup this Message?";
			txtTag.setVisibility(View.GONE);
		} else if (msgType == 2) {
			txtInfo.setText("" + Constant.IndividualPassInfo);
			title.setText("Individual pass Message");
			msgStr = "Are you sure you want to pass individual this Message?";
			txtTag.setVisibility(View.GONE);
		}

		listview.setAdapter(new CustomAdapter(LLCApplication.getDistroLists()));

	}

	private void clickEvents() {
		// TODO Auto-generated method stub
		txtTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				relativeList.setVisibility(View.VISIBLE);
				relativeList.startAnimation(fade_in);
				disableComponents();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				strtags = "";
				strIDs = "";
				for (int i = 0; i < LLCApplication.getDistroLists().size(); i++) {
					if (LLCApplication.getDistroLists().get(i).get("checked")
							.equals("1")) {

						if (strtags.length() == 0) {
							strtags += LLCApplication.getDistroLists().get(i)
									.get("Description");
							strIDs += LLCApplication.getDistroLists().get(i)
									.get("ID");
						} else {
							strtags += ", "
									+ LLCApplication.getDistroLists().get(i)
											.get("Description");
							strIDs += ";"
									+ LLCApplication.getDistroLists().get(i)
											.get("ID");
						}
					}
				}
				txtTag.setText("" + strtags);
				relativeList.setVisibility(View.GONE);
				relativeList.startAnimation(fade_out);
				enableComponents();
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

		btnPass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle(Constant.Alert_Name);
				alert.setMessage("" + msgStr);
				alert.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

								String msg = editMessage.getText().toString();
								if (msg.length() > 0) {
									if (isOnline()) {
										if (msgType == 0) {
											PassDown(msg, position);
										} else if (msgType == 1) {
											PassUp(msg, position);
										} else if (msgType == 2) {
											PassToTeamNumber(msg, position);
										}
									} else {
										Toast.makeText(getApplicationContext(),
												"" + Constant.network_error,
												Toast.LENGTH_SHORT).show();
									}
								} else {
									Toast.makeText(getApplicationContext(),
											"please enter message",
											Toast.LENGTH_SHORT).show();
								}

							}
						});
				alert.setNegativeButton("NO",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});
				alert.create();
				alert.show();
			}
		});
	}

	protected void PassDown(final String message, final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(PassdownMessage.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "PassDown");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("MessageID",
						""
								+ LLCApplication.getVoicemailList()
										.get(position).get("ID"));
				map.put("tag", "" + message);
				map.put("PassTo", "" + strIDs);

				Log.i("strIDs", "" + strIDs);
				response = callAPI(map);
				Log.i("response", "" + response);
				UpdatePassDown();
			}
		});
		t.start();
	}

	private void UpdatePassDown() {
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

	protected void PassUp(final String message, final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(PassdownMessage.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "PassUp");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("MessageID",
						""
								+ LLCApplication.getVoicemailList()
										.get(position).get("ID"));
				map.put("tag", "" + message);

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdatePassUp();
			}
		});
		t.start();
	}

	private void UpdatePassUp() {
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

	protected void PassToTeamNumber(final String message, final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(PassdownMessage.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "PassToTeamNumber");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("MessageID",
						""
								+ LLCApplication.getVoicemailList()
										.get(position).get("ID"));
				map.put("tag", "" + message);
				map.put("PassTo",
						""
								+ LLCApplication.getVoicemailList()
										.get(position).get("UserID"));

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdatePassToTeamNumber();
			}
		});
		t.start();
	}

	private void UpdatePassToTeamNumber() {
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

	protected void enableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(true);
		relativeBack.setEnabled(true);
		editMessage.setEnabled(true);
		btnPass.setEnabled(true);
		txtTag.setEnabled(true);
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(false);
		relativeBack.setEnabled(false);
		editMessage.setEnabled(false);
		btnPass.setEnabled(false);
		txtTag.setEnabled(false);
	}

	class CustomAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> list;

		public CustomAdapter(ArrayList<HashMap<String, String>> list) {
			// TODO Auto-generated constructor stub
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int pos, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View row = null;
			if (convertView == null) {
				row = getLayoutInflater().inflate(R.layout.contactslist_row,
						null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.name = (TextView) row.findViewById(R.id.name);
				viewHolder.tick = (CheckBox) row.findViewById(R.id.tick);
				row.setTag(viewHolder);

			} else {
				row = convertView;
			}

			final ViewHolder holder = (ViewHolder) row.getTag();
			holder.name.setText("" + list.get(pos).get("Description"));

			if (list.get(pos).get("checked").equals("0")) {
				holder.tick.setChecked(false);
			} else {
				holder.tick.setChecked(true);
			}

			holder.tick.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (((CheckBox) v).isChecked()) {
						LLCApplication.getDistroLists().get(pos)
								.put("checked", "1");
					} else {
						LLCApplication.getDistroLists().get(pos)
								.put("checked", "0");
					}
				}
			});

			return row;
		}
	}

	static class ViewHolder {
		protected TextView name;
		protected CheckBox tick;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}

}
