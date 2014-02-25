package com.example.lifelineconnect.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifelineconnect.LLCApplication;
import com.example.lifelineconnect.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.example.lifelineconnect.pulltorefresh.PullToRefreshView;
import com.example.lifelineconnect.utils.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Downline extends BaseActivityClass {

	Context mContext = this;
	TextView title;
	RelativeLayout relativeBack;
	private PullToRefreshView mPullRefreshListView;
	ListView listview;
	CustomAdapter adapter;
	MediaPlayer mediaPlayer;
	int currentMediaPlayerPos = -1;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	Typeface type;
	int flag_refresh = 0;
	String tabValue;

	RelativeLayout relativeInfo;
	TextView txtInfo;
	Animation fade_in, fade_out;
	Button btnInfo, btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downline);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			tabValue = extras.getString("tab");
		}

		type = Typeface.createFromAsset(getAssets(), "font.ttf");

		init();
		clickEvents();

		Handler hn = new Handler();
		hn.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isOnline()) {
					flag_refresh = 0;
					GetVoicemailList();
				} else {
					Toast.makeText(getApplicationContext(),
							"" + Constant.network_error, Toast.LENGTH_SHORT)
							.show();
				}
			}
		}, 500);
	}

	@SuppressWarnings("deprecation")
	private void init() {
		// TODO Auto-generated method stub
		relativeBack = (RelativeLayout) findViewById(R.id.relback);
		title = (TextView) findViewById(R.id.title);
		mPullRefreshListView = (PullToRefreshView) findViewById(R.id.pull_refresh_list);
		listview = mPullRefreshListView.getRefreshableView();
		listview.setDivider(null);
		listview.setDividerHeight(10);

		relativeInfo = (RelativeLayout) findViewById(R.id.infolayout);
		btnInfo = (Button) findViewById(R.id.btninfo);
		btnClose = (Button) findViewById(R.id.btnclose);
		txtInfo = (TextView) findViewById(R.id.txtinfo);

		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(1000);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(1000);

		options = new DisplayImageOptions.Builder().showStubImage(0)
				.showImageForEmptyUri(0).cacheInMemory().cacheOnDisc()
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		title.setTypeface(type);

		if (tabValue.equals("0")) {
			title.setText("Downline...");
			txtInfo.setText(Constant.downlineInfo);
		} else if (tabValue.equals("1")) {
			title.setText("Upline...");
			txtInfo.setText(Constant.uplineInfo);
		} else if (tabValue.equals("2")) {
			title.setText("Archive...");
			txtInfo.setText(Constant.archiveInfo);
		}
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

		relativeBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
			}
		});

		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(DateUtils
						.formatDateTime(getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL));

				// Do work to refresh the list here.
				if (isOnline()) {
					flag_refresh = 1;
					GetVoicemailList();
				} else {
					Toast.makeText(getApplicationContext(),
							"" + Constant.network_error, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
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

	protected void GetVoicemailList() {
		// TODO Auto-generated method stub
		if (flag_refresh == 0) {
			progressDialog = ProgressDialog.show(Downline.this, null,
					"Loading...	", true, false);
		}

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "GetVoicemailList");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("tab", "" + tabValue);

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

				if (response != null) {
					try {
						json_str = new JSONObject(response);
						data_array = json_str.getString("TeamTalk");
						array = new JSONArray(data_array);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					LLCApplication.getVoicemailList().clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj;
						try {
							obj = array.getJSONObject(i);
							HashMap<String, String> map = new HashMap<String, String>();

							map.put("ID", "" + obj.getString("ID"));
							map.put("SubmittedBy",
									"" + obj.getString("SubmittedBy"));
							map.put("Summary", "" + obj.getString("Summary"));
							map.put("ExtendedDescription",
									"" + obj.getString("ExtendedDescription"));
							map.put("DateSubmitted",
									"" + obj.getString("DateSubmitted"));
							map.put("RunToDate",
									"" + obj.getString("RunToDate"));
							map.put("Length", "" + obj.getString("Length"));
							map.put("StoredFile",
									"" + obj.getString("StoredFile"));
							map.put("FileName", "" + obj.getString("FileName"));
							map.put("VoicemailFromName",
									"" + obj.getString("VoicemailFromName"));
							map.put("Active", "" + obj.getString("Active"));
							map.put("Permission",
									"" + obj.getString("Permission"));
							map.put("Seconds", "" + obj.getString("Seconds"));
							map.put("LastName", "" + obj.getString("LastName"));
							map.put("FirstName",
									"" + obj.getString("FirstName"));
							map.put("CanPassDown",
									"" + obj.getString("CanPassDown"));
							map.put("ListenedTo",
									"" + obj.getString("ListenedTo"));
							map.put("CanReply", "" + obj.getString("CanReply"));
							map.put("Stamp", "" + obj.getString("Stamp"));
							map.put("Tags", "" + obj.getString("Tags"));
							map.put("expanded", "0");
							map.put("UserID", "" + obj.getString("UserID"));

							LLCApplication.getVoicemailList().add(map);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					GetDistroLists();

				}
			}
		});
	}

	protected void GetDistroLists() {
		// TODO Auto-generated method stub

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

				if (flag_refresh == 0) {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
				} else {
					mPullRefreshListView.onRefreshComplete();
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

					// For All Tags:
					HashMap<String, String> mapp = new HashMap<String, String>();
					mapp.put("ID", "0");
					mapp.put("Description", "All");
					mapp.put("checked", "1");
					LLCApplication.getDistroLists().add(mapp);

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

							if (map.get("Description").length() > 0)
								LLCApplication.getDistroLists().add(map);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					adapter = new CustomAdapter(LLCApplication
							.getVoicemailList());
					listview.setAdapter(adapter);
				}
			}
		});
	}

	protected void DeleteMessage(final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(Downline.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "DeleteMessage");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("MessageID",
						""
								+ LLCApplication.getVoicemailList()
										.get(position).get("ID"));

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateDeleteMessage(position);
			}
		});
		t.start();
	}

	private void UpdateDeleteMessage(final int position) {
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
						adapter.remove(position);
					}
				}
			}
		});
	}

	protected void ArchiveMessage(final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(Downline.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "ArchiveMessage");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("MessageID",
						""
								+ LLCApplication.getVoicemailList()
										.get(position).get("ID"));

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateArchiveMessage(position);
			}
		});
		t.start();
	}

	private void UpdateArchiveMessage(final int position) {
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
						adapter.remove(position);
					}
				}

			}
		});
	}

	protected void PassDown(final String message, final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(Downline.this, null,
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
				map.put("PassTo", "0");

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
						adapter.notifyDataSetChanged();
					}
				}

			}
		});
	}

	protected void PassUp(final String message, final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(Downline.this, null,
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
						adapter.notifyDataSetChanged();
					}
				}

			}
		});
	}

	class CustomAdapter extends BaseAdapter {

		ArrayList<HashMap<String, String>> locallist;

		public CustomAdapter(ArrayList<HashMap<String, String>> VoicemailList) {
			// TODO Auto-generated constructor stub
			locallist = VoicemailList;
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return locallist.size();
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

		public void remove(int pos) {
			// TODO Auto-generated method stub
			locallist.remove(pos);
			notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertview, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View row = convertview;
			if (row == null) {
				row = getLayoutInflater().inflate(R.layout.voicemaillist_row,
						null);
			}

			final RelativeLayout relativePart1 = (RelativeLayout) row
					.findViewById(R.id.relativepart1);
			final RelativeLayout relativePart2 = (RelativeLayout) row
					.findViewById(R.id.relativepart2);
			final RelativeLayout bottomArchive = (RelativeLayout) row
					.findViewById(R.id.bottomarchive);
			final LinearLayout bottom = (LinearLayout) row
					.findViewById(R.id.bottom);
			ImageView imgnew = (ImageView) row.findViewById(R.id.imgnew);
			ImageView img = (ImageView) row.findViewById(R.id.img);
			TextView name = (TextView) row.findViewById(R.id.name_);
			TextView stamp = (TextView) row.findViewById(R.id.stamp);
			TextView length = (TextView) row.findViewById(R.id.length);

			final RelativeLayout layoutPlus = (RelativeLayout) row
					.findViewById(R.id.layoutplus);
			final Button btnplay = (Button) row.findViewById(R.id.btnplay_);
			final Button btnPlus = (Button) row.findViewById(R.id.btnplus);

			TextView txtDelete = (TextView) row.findViewById(R.id.txtdelete);
			TextView txtArchive = (TextView) row.findViewById(R.id.txtarchive);

			LinearLayout layoutPassdown = (LinearLayout) row
					.findViewById(R.id.layoutpassdown);
			LinearLayout layoutPassup = (LinearLayout) row
					.findViewById(R.id.layoutpassup);

			final TextView editMessage = (TextView) row
					.findViewById(R.id.editmessage);

			layoutPlus.setVisibility(View.GONE);

			if (tabValue.equals("2")) {
				bottomArchive.setVisibility(View.VISIBLE);
				bottom.setVisibility(View.GONE);
			} else {
				bottomArchive.setVisibility(View.GONE);
				bottom.setVisibility(View.VISIBLE);
			}

			if (position % 2 == 0) {
				relativePart1
						.setBackgroundResource(R.drawable.commentbox_1_shadow_left);
				relativePart2
						.setBackgroundResource(R.drawable.commentbox_1shadow_left);
			} else {
				relativePart1
						.setBackgroundResource(R.drawable.commentbox_1_shadow_right);
				relativePart2
						.setBackgroundResource(R.drawable.commentbox_1shadow_right);
			}

			if (currentMediaPlayerPos != position) {
				btnplay.setBackgroundResource(R.drawable.play_icon);
			}

			name.setText(locallist.get(position).get("FirstName") + " "
					+ locallist.get(position).get("LastName"));
			stamp.setText(locallist.get(position).get("Stamp"));
			length.setText(locallist.get(position).get("Length"));
			editMessage.setText(locallist.get(position).get("Summary"));

			imageLoader.displayImage(
					locallist.get(position).get("SubmittedBy"), img, options);

			if (locallist.get(position).get("expanded").equals("0")) {
				relativePart2.setVisibility(View.GONE);
				btnPlus.setBackgroundResource(R.drawable.plus);
			} else {
				relativePart2.setVisibility(View.VISIBLE);
				btnPlus.setBackgroundResource(R.drawable.minus);
			}

			if (locallist.get(position).get("ListenedTo").equals("0")) {
				imgnew.setVisibility(View.VISIBLE);
			} else {
				imgnew.setVisibility(View.GONE);
			}

			btnplay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (btnInfo.isEnabled()) {
						if (isOnline()) {
							if (currentMediaPlayerPos == position) {
								if (mediaPlayer != null) {
									if (mediaPlayer.isPlaying()) {
										mediaPlayer.pause();
										btnplay.setBackgroundResource(R.drawable.play_icon);
									} else {
										btnplay.setBackgroundResource(R.drawable.pause);
										mediaPlayer.start();
									}
								}
							} else {
								for (int i = 0; i < locallist.size(); i++) {
									int wantedPosition = i;
									int firstPosition = listview
											.getFirstVisiblePosition()
											- listview.getHeaderViewsCount();
									int wantedChild = wantedPosition
											- firstPosition;

									if (wantedChild < 0
											|| wantedChild >= listview
													.getChildCount()) {
										// return;
									} else {
										View wantedView = listview
												.getChildAt(wantedChild);
										Button bPlay = (Button) wantedView
												.findViewById(R.id.btnplay_);
										bPlay.setBackgroundResource(R.drawable.play_icon);

									}
								}

								if (mediaPlayer != null) {
									mediaPlayer.reset();
								}

								Thread t = new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										mediaPlayer = new MediaPlayer();
										String streamPath = ""
												+ Constant.messagePath
												+ LLCApplication
														.getVoicemailList()
														.get(position)
														.get("FileName");
										try {
											mediaPlayer
													.setDataSource(streamPath);
											mediaPlayer
													.setAudioStreamType(AudioManager.STREAM_MUSIC);
											mediaPlayer.prepare();
										} catch (IllegalArgumentException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (SecurityException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IllegalStateException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										if (mediaPlayer != null) {
											mediaPlayer
													.setOnPreparedListener(new OnPreparedListener() {

														@Override
														public void onPrepared(
																final MediaPlayer mediaPlayer) {
															// TODO
															// Auto-generated
															// method
															// stub
															btnplay.setBackgroundResource(R.drawable.pause);
															mediaPlayer.start();
														}
													});

											mediaPlayer
													.setOnCompletionListener(new OnCompletionListener() {

														@Override
														public void onCompletion(
																MediaPlayer mp) {
															// TODO
															// Auto-generated
															// method
															// stub
															mediaPlayer
																	.seekTo(0);
															btnplay.setBackgroundResource(R.drawable.play_icon);
														}
													});
										}
									}
								});
								t.start();

							}

							currentMediaPlayerPos = position;
							SetMessageAsListened();
						}
					}
				}
			});

			layoutPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (btnInfo.isEnabled()) {
						Log.e("locallist.get(position).get(expanded)", ""
								+ locallist.get(position).get("expanded"));
						if (locallist.get(position).get("expanded").equals("0")) {
							relativePart2.setVisibility(View.VISIBLE);
							locallist.get(position).put("expanded", "1");
							btnPlus.setBackgroundResource(R.drawable.minus);
						} else {
							relativePart2.setVisibility(View.GONE);
							locallist.get(position).put("expanded", "0");
							btnPlus.setBackgroundResource(R.drawable.plus);
						}
					}
				}
			});

			txtDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					AlertDialog.Builder alert = new AlertDialog.Builder(
							mContext);
					alert.setTitle(Constant.Alert_Name);
					alert.setMessage("Are you sure you want to delete this Message?");
					alert.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();

									if (isOnline()) {
										DeleteMessage(position);
									} else {
										Toast.makeText(getApplicationContext(),
												"" + Constant.network_error,
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

			txtArchive.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					AlertDialog.Builder alert = new AlertDialog.Builder(
							mContext);
					alert.setTitle(Constant.Alert_Name);
					alert.setMessage("Are you sure you want to archive this Message?");
					alert.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();

									if (isOnline()) {
										ArchiveMessage(position);
									} else {
										Toast.makeText(getApplicationContext(),
												"" + Constant.network_error,
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

			layoutPassdown.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Downline.this,
							PassdownMessage.class);
					intent.putExtra("pos", position);
					intent.putExtra("msgType", 0);
					startActivity(intent);
					overridePendingTransition(R.anim.enter_from_left,
							R.anim.hold_bottom);

				}
			});

			layoutPassup.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Downline.this,
							PassdownMessage.class);
					intent.putExtra("pos", position);
					intent.putExtra("msgType", 1);
					startActivity(intent);
					overridePendingTransition(R.anim.enter_from_left,
							R.anim.hold_bottom);
				}
			});

			bottomArchive.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					AlertDialog.Builder alert = new AlertDialog.Builder(
							mContext);
					alert.setTitle(Constant.Alert_Name);
					alert.setMessage("Are you sure you want to delete?");
					alert.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();

									if (isOnline()) {
										DeleteMessage(position);
									} else {
										Toast.makeText(getApplicationContext(),
												"" + Constant.network_error,
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

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (btnInfo.isEnabled()) {
						Intent intent = new Intent(Downline.this,
								MessageDetailsSlidingFragment.class);
						intent.putExtra("pos", position);
						startActivity(intent);
						overridePendingTransition(R.anim.enter_from_left,
								R.anim.hold_bottom);
					}
				}
			});

			return row;
		}
	}

	@SuppressWarnings("unused")
	private void playAudio(int pos) {
		// TODO Auto-generated method stub
		if (isOnline()) {

			if (mediaPlayer != null) {
				mediaPlayer.reset();
			}

			mediaPlayer = new MediaPlayer();
			String streamPath = ""
					+ Constant.messagePath
					+ LLCApplication.getVoicemailList().get(pos)
							.get("FileName");
			try {
				mediaPlayer.setDataSource(streamPath);
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.prepare();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mediaPlayer.seekTo(0);
				}
			});

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Handler hn = new Handler();
		hn.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (LLCApplication.isFlagRefresh()) {
					LLCApplication.setFlagRefresh(false);
					if (isOnline()) {
						flag_refresh = 0;
						GetVoicemailList();
					} else {
						Toast.makeText(getApplicationContext(),
								"" + Constant.network_error, Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					if (adapter != null)
						adapter.notifyDataSetChanged();
				}
			}
		}, 100);
	}

	protected void SetMessageAsListened() {
		// TODO Auto-generated method stub

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "SetMessageAsListened");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("MessageID",
						""
								+ LLCApplication.getVoicemailList()
										.get(currentMediaPlayerPos).get("ID"));

				response = callAPI(map);
				Log.i("response", "" + response);
				UpdateListened();
			}
		});
		t.start();
	}

	@SuppressLint("NewApi")
	private void UpdateListened() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

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
						LLCApplication.getVoicemailList()
								.get(currentMediaPlayerPos)
								.put("ListenedTo", "1");
						// ((CustomAdapter)listview.getAdapter()).notifyDataSetChanged();
						if (adapter != null)
							adapter.notifyDataSetChanged();
					}
				}
			}
		});
	}

}
