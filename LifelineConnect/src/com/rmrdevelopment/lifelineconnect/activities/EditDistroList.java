package com.rmrdevelopment.lifelineconnect.activities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.utils.Constant;

public class EditDistroList extends BaseActivityClass {

	Context mContext = this;
	TextView title;
	RelativeLayout relativeBack;
	Typeface type;
	String ListID = "", listName = "", memberUserID = "";
	int position;
	Button btnSave, btnDelete, btnSearch, btnCancel, btnAddMember,btnHome;
	EditText editName, txtName;
	ToggleButton toggleButtonAdd;
	ListView listview, searchedListview;
	CustomAdapter adapter;
	RelativeLayout relativeList;
	boolean isListCreated = false;

	RelativeLayout relativeInfo;
	TextView txtInfo;
	Animation fade_in, fade_out;
	Button btnInfo, btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editdistrolist);

		type = Typeface.createFromAsset(getAssets(), "font.ttf");
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			position = extras.getInt("pos");
			ListID = extras.getString("ID");
			listName = extras.getString("Description");
		}

		init();
		clickEvents();
	}

	private void init() {
		// TODO Auto-generated method stub
		relativeBack = (RelativeLayout) findViewById(R.id.relback);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(type);
		txtName = (EditText) findViewById(R.id.txtname);
		btnAddMember = (Button) findViewById(R.id.btnaddmember);
		btnSave = (Button) findViewById(R.id.btnsave);
		btnInfo = (Button) findViewById(R.id.btninfo);
		btnCancel = (Button) findViewById(R.id.btncancel);
		btnDelete = (Button) findViewById(R.id.btndelete);
		btnSearch = (Button) findViewById(R.id.btnsearch);
		editName = (EditText) findViewById(R.id.editname);
		toggleButtonAdd = (ToggleButton) findViewById(R.id.toggleButtonAdd);
		listview = (ListView) findViewById(R.id.lst);
		searchedListview = (ListView) findViewById(R.id.searchlist);
		relativeList = (RelativeLayout) findViewById(R.id.listlayout);
		
		btnHome = (Button) findViewById(R.id.btnhome);

		relativeInfo = (RelativeLayout) findViewById(R.id.infolayout);
		btnInfo = (Button) findViewById(R.id.btninfo);
		btnClose = (Button) findViewById(R.id.btnclose);
		txtInfo = (TextView) findViewById(R.id.txtinfo);
		txtInfo.setText("" + Constant.EditDistrolistInfo);

		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(200);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(200);

		if (!listName.equals("Create a List")) {
			txtName.setText("" + listName);
		}

		if (position != 0) {
			Handler hn = new Handler();
			hn.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (isOnline()) {
						GetUsersInDistroList();
					} else {
						Toast.makeText(getApplicationContext(),
								"" + Constant.network_error, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}, 500);
		}
	}

	private void clickEvents() {
		// TODO Auto-generated method stub

		btnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(EditDistroList.this,
						HomeSlidingFragment.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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

		btnAddMember.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (ListID.length() > 0) {
					if (editName.getText().toString().length() > 0
							&& memberUserID.length() > 0) {
						AddUserToDistroList(memberUserID);
					} else {
						/*
						 * Toast.makeText(getApplicationContext(),
						 * "please enter member name.", Toast.LENGTH_SHORT)
						 * .show();
						 */
					}
				} else {
					if (editName.getText().toString().length() > 0
							&& memberUserID.length() > 0) {

						listName = txtName.getText().toString();
						if (listName.length() > 0) {
							if (isOnline()) {
								isListCreated = true;
								AddDistroList();
							} else {
								Toast.makeText(getApplicationContext(),
										"" + Constant.network_error,
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(
									getApplicationContext(),
									"Distributionlist name should not be empty.",
									Toast.LENGTH_SHORT).show();
						}
					}

				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				relativeList.setVisibility(View.GONE);
				relativeList.startAnimation(fade_out);
				enableComponents();
			}
		});

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String sreachedString = editName.getText().toString()
						.trim();
				if (sreachedString.length() > 2) {
					if (isOnline()) {
						SearchUsers(sreachedString);
					} else {
						Toast.makeText(getApplicationContext(),
								"" + Constant.network_error, Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Enter atleast 3 character to search",
							Toast.LENGTH_SHORT).show();
				}
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

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				listName = txtName.getText().toString();
				if (listName.length() > 0) {
					if (isOnline()) {
						if (position == 0) {
							if (isListCreated == false) {
								AddDistroList();
							} else {
								UpdateDistroName();
							}
						} else {
							UpdateDistroName();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"" + Constant.network_error, Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Distributionlist name should not be empty.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (ListID.length() > 0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage("Are you sure you want to delete this list?");
					builder.setCancelable(false);
					builder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									if (isOnline()) {
										DeleteUserFromDistroList();
									} else {
										Toast.makeText(getApplicationContext(),
												"" + Constant.network_error,
												Toast.LENGTH_SHORT).show();
									}
								}
							});

					builder.setNegativeButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.cancel();

								}
							});
					builder.show();
				}
			}
		});

		toggleButtonAdd
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						Log.e("btnRememberOnOff", "" + isChecked);
						if (isChecked) {
							toggleButtonAdd
									.setBackgroundResource(R.drawable.checkbox);
						} else {
							toggleButtonAdd
									.setBackgroundResource(R.drawable.checkbox_blank);
						}
					}
				});
	}

	protected void GetUsersInDistroList() {
		// TODO Auto-generated method stub

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "GetUsersInDistroList");
				map.put("ListID", "" + ListID);

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateGetUsersInDistroList();
			}
		});
		t.start();
	}

	private void UpdateGetUsersInDistroList() {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (progressDialog != null) {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
				}

				if (response != null) {
					try {
						json_str = new JSONObject(response);
						data_array = json_str
								.getString("VoiceMailDistroListMembersWithName");
						array = new JSONArray(data_array);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					LLCApplication.getDistroMembersLists().clear();

					for (int i = 0; i < array.length(); i++) {
						JSONObject obj;
						try {
							obj = array.getJSONObject(i);
							HashMap<String, String> map = new HashMap<String, String>();

							map.put("ListID", "" + obj.getString("ListID"));
							map.put("UserID", "" + obj.getString("UserID"));
							map.put("IncludeDownline",
									"" + obj.getString("IncludeDownline"));
							map.put("Stamp", "" + obj.getString("Stamp"));
							map.put("DisplayName",
									"" + obj.getString("DisplayName"));

							if (map.get("DisplayName").length() > 0) {
								LLCApplication.getDistroMembersLists().add(map);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					adapter = new CustomAdapter(LLCApplication
							.getDistroMembersLists());
					listview.setAdapter(adapter);
				}
			}
		});
	}

	protected void SearchUsers(final String searchString) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(EditDistroList.this, null,
				"Loading...	", true, false);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "SearchUsers");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("SearchString", "" + searchString);

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateSearchUsers();
			}
		});
		t.start();
	}

	private void UpdateSearchUsers() {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (progressDialog != null) {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
				}

				if (response != null) {
					try {
						json_str = new JSONObject(response);
						data_array = json_str
								.getString("TeamGetVoiceMailPeopleSearch");
						array = new JSONArray(data_array);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					LLCApplication.getWireSearchUsers().clear();

					for (int i = 0; i < array.length(); i++) {
						JSONObject obj;
						try {
							obj = array.getJSONObject(i);
							HashMap<String, String> map = new HashMap<String, String>();

							map.put("FullName", "" + obj.getString("FullName"));
							map.put("UserID", "" + obj.getString("UserID"));
							map.put("IsDown", "" + obj.getString("IsDown"));
							map.put("CanEntireDownline",
									"" + obj.getString("CanEntireDownline"));

							if (map.get("FullName").length() > 0) {
								LLCApplication.getWireSearchUsers().add(map);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					searchedListview.setAdapter(new SearchedCustomAdapter(
							LLCApplication.getWireSearchUsers()));
					relativeList.setVisibility(View.VISIBLE);
					relativeList.startAnimation(fade_in);
					disableComponents();
				}
			}
		});
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
				row = getLayoutInflater().inflate(
						R.layout.distromemberlist_row, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.name = (TextView) row.findViewById(R.id.name);
				viewHolder.isdownline = (TextView) row
						.findViewById(R.id.isinclude);
				viewHolder.img = (ImageView) row.findViewById(R.id.img);
				row.setTag(viewHolder);

			} else {
				row = convertView;
			}

			final ViewHolder holder = (ViewHolder) row.getTag();

			holder.name.setText("" + list.get(pos).get("DisplayName"));
			if (list.get(pos).get("IncludeDownline").equals("true")) {
				holder.isdownline.setVisibility(View.VISIBLE);
			} else {
				holder.isdownline.setVisibility(View.GONE);
			}

			holder.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage("Are you sure you want to delete this Member?");
					builder.setCancelable(false);
					builder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									if (isOnline()) {
										DeleteUserFromDistroList(list.get(pos)
												.get("UserID"));
									} else {
										Toast.makeText(getApplicationContext(),
												"" + Constant.network_error,
												Toast.LENGTH_SHORT).show();
									}
								}
							});

					builder.setNegativeButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.cancel();

								}
							});
					builder.show();
				}
			});

			return row;
		}
	}

	static class ViewHolder {
		protected TextView name, isdownline;
		protected ImageView img;
	}

	class SearchedCustomAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> list;

		public SearchedCustomAdapter(ArrayList<HashMap<String, String>> list) {
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
				row = getLayoutInflater().inflate(
						R.layout.distromemberlist_row, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.name = (TextView) row.findViewById(R.id.name);
				viewHolder.isdownline = (TextView) row
						.findViewById(R.id.isinclude);
				viewHolder.img = (ImageView) row.findViewById(R.id.img);
				row.setTag(viewHolder);

			} else {
				row = convertView;
			}

			final ViewHolder holder = (ViewHolder) row.getTag();

			holder.img.setVisibility(View.GONE);
			holder.name.setText("" + list.get(pos).get("FullName"));
			holder.isdownline.setVisibility(View.GONE);

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					memberUserID = list.get(pos).get("UserID");
					relativeList.setVisibility(View.GONE);
					relativeList.startAnimation(fade_out);
					enableComponents();
					editName.setText("" + list.get(pos).get("FullName"));
				}
			});

			return row;
		}
	}

	protected void DeleteUserFromDistroList(final String UserID) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(EditDistroList.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "DeleteUserFromDistroList");
				map.put("ListID", "" + ListID);
				map.put("User_ID", "" + UserID);

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateDeleteUserFromDistroList();
			}
		});
		t.start();
	}

	private void UpdateDeleteUserFromDistroList() {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

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
						Toast.makeText(getApplicationContext(),
								"Member deleted successfully.",
								Toast.LENGTH_SHORT).show();

						GetUsersInDistroList();
					}
				}
			}
		});
	}

	protected void AddUserToDistroList(final String UserID) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(EditDistroList.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String IsIncludeDownline = "";
				if (toggleButtonAdd.isChecked()) {
					IsIncludeDownline = "true";
				} else {
					IsIncludeDownline = "false";
				}

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "AddUserToDistroList");
				map.put("ListID", "" + ListID);
				map.put("User_ID", "" + UserID);
				map.put("IsIncludeDownline", "" + IsIncludeDownline);

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateAddUserToDistroList();
			}
		});
		t.start();
	}

	private void UpdateAddUserToDistroList() {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

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
						editName.setText("");
						memberUserID = "";
						Toast.makeText(getApplicationContext(),
								"Member added successfully.",
								Toast.LENGTH_SHORT).show();

						GetUsersInDistroList();
					} else {
						if (progressDialog != null) {
							if (progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
						}

						Toast.makeText(getApplicationContext(),
								"Member already added.", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});
	}

	protected void UpdateDistroName() {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(EditDistroList.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "UpdateDistroName");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("listName", "" + listName);
				map.put("ListID", "" + ListID);

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateDistroNamee();
			}
		});
		t.start();
	}

	private void UpdateDistroNamee() {
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
						LLCApplication.setFlagRefresh(true);
						Toast.makeText(getApplicationContext(),
								"Distrolist updated successfully.",
								Toast.LENGTH_SHORT).show();
						finish();
						overridePendingTransition(R.anim.hold_top,
								R.anim.exit_in_left);
					}
				}
			}
		});
	}

	protected void DeleteUserFromDistroList() {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(EditDistroList.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "DeleteDistroList");
				map.put("ListID", "" + ListID);

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateDeleteUserFromDistroList1();
			}
		});
		t.start();
	}

	private void UpdateDeleteUserFromDistroList1() {
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
						LLCApplication.setFlagRefresh(true);
						Toast.makeText(getApplicationContext(),
								"DistroName deleted successfully.",
								Toast.LENGTH_SHORT).show();
						finish();
						overridePendingTransition(R.anim.hold_top,
								R.anim.exit_in_left);
					}
				}
			}
		});
	}

	protected void AddDistroList() {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(EditDistroList.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "AddDistroList");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("DistroName", "" + listName);

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateAddDistroList();
			}
		});
		t.start();
	}

	private void UpdateAddDistroList() {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				if (response != null) {

					LLCApplication.setFlagRefresh(true);

					if (isListCreated == false) {
						Toast.makeText(getApplicationContext(),
								"DistroName added successfully.",
								Toast.LENGTH_SHORT).show();
						finish();
						overridePendingTransition(R.anim.hold_top,
								R.anim.exit_in_left);
					} else {
						try {
							json_str = new JSONObject(response);
							ListID = json_str.getString("ListID");
							AddUserToDistroList(memberUserID);
						} catch (Exception e) {
							// TODO: handle exception
						}

					}

				}
			}
		});
	}

	protected void enableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(true);
		relativeBack.setEnabled(true);
		btnSave.setEnabled(true);
		btnDelete.setEnabled(true);
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(false);
		relativeBack.setEnabled(false);
		btnDelete.setEnabled(false);
		btnSave.setEnabled(false);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}

}