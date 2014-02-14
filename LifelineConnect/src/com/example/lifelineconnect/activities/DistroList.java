package com.example.lifelineconnect.activities;

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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifelineconnect.LLCApplication;
import com.example.lifelineconnect.utils.Constant;

public class DistroList extends BaseActivityClass {

	Context mContext = this;
	TextView title;
	RelativeLayout relativeBack;
	Typeface type;
	Button btnAdd;
	ListView listview;
	CustomAdapter adapter;
	String ListID, listName;
	int flagProgressbar = 0;

	RelativeLayout relativeInfo;
	TextView txtInfo;
	Animation fade_in, fade_out;
	Button btnInfo, btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.distrolist);

		type = Typeface.createFromAsset(getAssets(), "font.ttf");

		init();
		clickEvents();
	}

	private void init() {
		// TODO Auto-generated method stub
		relativeBack = (RelativeLayout) findViewById(R.id.relback);
		btnAdd = (Button) findViewById(R.id.btnadd);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(type);

		relativeInfo = (RelativeLayout) findViewById(R.id.infolayout);
		btnInfo = (Button) findViewById(R.id.btninfo);
		btnClose = (Button) findViewById(R.id.btnclose);
		txtInfo = (TextView) findViewById(R.id.txtinfo);
		txtInfo.setText(""+Constant.DistrolistInfo);
		
		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(1000);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(1000);

		listview = (ListView) findViewById(R.id.lst);

		Handler hn = new Handler();
		hn.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isOnline()) {
					flagProgressbar = 0;
					GetDistroLists();
				} else {
					Toast.makeText(getApplicationContext(),
							"" + Constant.network_error, Toast.LENGTH_SHORT)
							.show();
				}
			}
		}, 500);

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

		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setMessage("enter name");
				alert.setTitle("" + Constant.Alert_Name);

				// Set an EditText view to get user input
				final EditText input = new EditText(context);
				alert.setView(input);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (isOnline()) {
									listName = input.getText().toString();
									AddDistroList();
								} else {
									Toast.makeText(getApplicationContext(),
											"" + Constant.network_error,
											Toast.LENGTH_SHORT).show();
								}
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
								dialog.cancel();
							}

						});

				alert.create();
				alert.show();
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
				row = getLayoutInflater()
						.inflate(R.layout.distrolist_row, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.name = (TextView) row.findViewById(R.id.name);
				viewHolder.img = (ImageView) row.findViewById(R.id.img);
				row.setTag(viewHolder);

			} else {
				row = convertView;
			}

			final ViewHolder holder = (ViewHolder) row.getTag();

			if (pos == 0) {
				holder.img.setBackgroundResource(R.drawable.addbtn);
				Log.i("Description " + pos,
						"" + list.get(pos).get("Description"));
				holder.name.setText("" + list.get(pos).get("Description"));
			} else {
				holder.img.setBackgroundResource(R.drawable.editbtn);
				holder.name.setText("" + list.get(pos).get("Description"));
			}

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					ListID = "" + list.get(pos).get("ID");
					listName = "" + list.get(pos).get("Description");
					// view.showContextMenu();
					Intent intent = new Intent(DistroList.this,
							EditDistroList.class);
					intent.putExtra("pos", pos);
					intent.putExtra("ID", "" + list.get(pos).get("ID"));
					intent.putExtra("Description",
							"" + list.get(pos).get("Description"));
					startActivity(intent);
					overridePendingTransition(R.anim.enter_from_left,
							R.anim.hold_bottom);
				}
			});

			return row;
		}
	}

	static class ViewHolder {
		protected TextView name;
		protected ImageView img;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		String[] menuItems = new String[] { "Edit", "Delete" };
		menu.add(Menu.NONE, 0, 0, menuItems[0]);
		menu.add(Menu.NONE, 1, 1, menuItems[1]);
		menu.setHeaderTitle("Choose");

	}

	public boolean onContextItemSelected(MenuItem item) {

		int menuItemIndex = item.getItemId();
		switch (menuItemIndex) {
		case 0: {

			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setMessage("update name");
			alert.setTitle("" + Constant.Alert_Name);

			// Set an EditText view to get user input
			final EditText input = new EditText(context);
			input.setText("" + listName);
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (isOnline()) {
								listName = input.getText().toString();
								UpdateDistroName();
							} else {
								Toast.makeText(getApplicationContext(),
										"" + Constant.network_error,
										Toast.LENGTH_SHORT).show();
							}
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
							dialog.cancel();
						}

					});

			alert.create();
			alert.show();
			break;
		}
		case 1: {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage("Are you sure you want to delete ?");
			builder.setCancelable(false);
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
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
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();

						}
					});
			builder.show();
			break;
		}
		}
		return true;
	}

	protected void GetDistroLists() {
		// TODO Auto-generated method stub
		if (flagProgressbar == 0) {
			progressDialog = ProgressDialog.show(DistroList.this, null,
					"Loading...	", true, false);
		}

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "GetDistroLists");
				map.put("User_ID", "" + LLCApplication.getUserId());

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateGetDistroLists();
			}
		});
		t.start();
	}

	private void UpdateGetDistroLists() {
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
						data_array = json_str
								.getString("TeamGetVoicemailDistroList");
						array = new JSONArray(data_array);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					LLCApplication.getDistroLists().clear();

					HashMap<String, String> map1 = new HashMap<String, String>();
					map1.put("Description", "Create a List");
					map1.put("ID", "");
					map1.put("CreatedDate", "");
					map1.put("Number", "");
					map1.put("NumContacts", "");
					map1.put("checked", "");
					LLCApplication.getDistroLists().add(map1);

					for (int i = 0; i < array.length(); i++) {
						JSONObject obj;
						try {
							obj = array.getJSONObject(i);
							HashMap<String, String> map = new HashMap<String, String>();

							map.put("Number", "" + obj.getString("Number"));
							map.put("ID", "" + obj.getString("ID"));
							map.put("Description",
									"" + obj.getString("Description"));
							map.put("CreatedDate",
									"" + obj.getString("CreatedDate"));
							map.put("NumContacts",
									"" + obj.getString("NumContacts"));
							map.put("checked", "0");

							if (map.get("Description").length() > 0) {
								LLCApplication.getDistroLists().add(map);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					adapter = new CustomAdapter(LLCApplication.getDistroLists());
					listview.setAdapter(adapter);
				}
			}
		});
	}

	protected void UpdateDistroName() {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(DistroList.this, null,
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
								"DistroName updated successfully.",
								Toast.LENGTH_SHORT).show();
						flagProgressbar = 1;
						GetDistroLists();
					}
				}
			}
		});
	}

	protected void DeleteUserFromDistroList() {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(DistroList.this, null,
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
								"DistroName deleted successfully.",
								Toast.LENGTH_SHORT).show();
						flagProgressbar = 1;
						GetDistroLists();
					}
				}
			}
		});
	}

	protected void AddDistroList() {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(DistroList.this, null,
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

				if (response != null) {

					Toast.makeText(getApplicationContext(),
							"DistroName added successfully.",
							Toast.LENGTH_SHORT).show();
					flagProgressbar = 1;
					GetDistroLists();

				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (LLCApplication.isFlagRefresh()) {
			LLCApplication.setFlagRefresh(false);
			Handler hn = new Handler();
			hn.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (isOnline()) {
						flagProgressbar = 0;
						GetDistroLists();
					} else {
						Toast.makeText(getApplicationContext(),
								"" + Constant.network_error, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}, 200);
		}
	}

	protected void enableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(true);
		relativeBack.setEnabled(true);
		listview.setEnabled(true);
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(false);
		relativeBack.setEnabled(false);
		listview.setEnabled(false);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}

}
