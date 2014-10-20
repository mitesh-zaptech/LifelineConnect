package com.rmrdevelopment.lifelineconnect.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.activities.BaseActivityClass;
import com.rmrdevelopment.lifelineconnect.activities.ReplyMessageActivity;
import com.rmrdevelopment.lifelineconnect.activities.ScreenReceiver;
import com.rmrdevelopment.lifelineconnect.activities.SplashActivity;
import com.rmrdevelopment.lifelineconnect.utils.Constant;
import com.rmrdevelopment.lifelineconnect.utils.RestClient;

@SuppressLint("NewApi")
public class MessageDetailsFragment extends Fragment {

	private FragmentManager fm;
	
	private Button btnreply;
	private Button btnplay;
	private Button btnDelete;
	private Button btnTasks;
	private Button btnSpeaker;
	private Button btnInfo;
	private Button btnClose;
	private Button btnAddFav;
	private Button btnRemoveFav;
	
	private TextView btnprev;
	private TextView btnnext;
	private TextView txtFrom;
	private TextView txtSent;
	private TextView title;
	private TextView txtInfo;
	
	private RelativeLayout relativeBack;
	private RelativeLayout relativeMiddle;
	private RelativeLayout relativeInfo;
	
	private ProgressBar progressBar;
	private ListView listview;
	
	private int selectionAudioIdx =-1;
	private int position;
	
	private Animation animFromLeft;
	private Animation animFromRight;
	
	public static MediaPlayer mediaPlayer;
	public static boolean updateProgress = false;
	private Handler hn;
	
	public static Typeface type;
	
	private Animation fade_in;
	private Animation fade_out;

	// BaseActivity:
	private Context context;
	
	private String response="";
	private String responseFavorite="";
	private String strStreamPath= "";
	
	private JSONObject json_str;
	private JSONArray array = null;
	private ProgressDialog progressDialog;

	private AudioManager audioManager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Left", "onCreateView()");
		return inflater.inflate(R.layout.messagedetails, container, false);
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		context = getActivity();
		Bundle extras = getActivity().getIntent().getExtras();
		if (extras != null) {
			position = extras.getInt("pos");
		}

		fm = getFragmentManager();
		fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {

			@Override
			public void onBackStackChanged() {
				Log.i("getBackStackEntryCount", ""
						+ getFragmentManager().getBackStackEntryCount());
				if (getFragmentManager().getBackStackEntryCount() == 0) {

					if (mediaPlayer != null && mediaPlayer.isPlaying()) {
						mediaPlayer.pause();
						updateProgress = false;
						btnplay.setText("Play");
						//btnplay.setBackgroundResource(R.drawable.play_icon);
					}

					getActivity().finish();
					getActivity().overridePendingTransition(R.anim.hold_top,
							R.anim.exit_in_left);
				}
			}
		});

		init();
		clickEvents();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		relativeBack = (RelativeLayout) getView().findViewById(R.id.relback);
		btnreply = (Button) getView().findViewById(R.id.btnreply);
		txtFrom = (TextView) getView().findViewById(R.id.txtfrom);
		txtSent = (TextView) getView().findViewById(R.id.txtsent);
		btnplay = (Button) getView().findViewById(R.id.btnplay);
		btnDelete = (Button) getView().findViewById(R.id.btnDeleteMsg);
		btnSpeaker = (Button) getView().findViewById(R.id.btnSpeaker);
		progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
		btnprev = (TextView) getView().findViewById(R.id.btnprev);
		btnnext = (TextView) getView().findViewById(R.id.btnnext);
		relativeMiddle = (RelativeLayout) getView().findViewById(R.id.middle);
		listview = (ListView) getView().findViewById(R.id.lst);

		title = (TextView) getView().findViewById(R.id.title);
		title.setTypeface(HomeFragment.type);

		relativeInfo = (RelativeLayout) getView().findViewById(R.id.infolayout);
		btnInfo = (Button) getView().findViewById(R.id.btninfo);
		btnTasks = (Button) getView().findViewById(R.id.btnTasks);
		btnClose = (Button) getView().findViewById(R.id.btnclose);
		txtInfo = (TextView) getView().findViewById(R.id.txtinfo);
		txtInfo.setText(Constant.messageDetailsInfo);
		
		btnAddFav= (Button) getView().findViewById(R.id.buttonAddFav);
		btnRemoveFav= (Button) getView().findViewById(R.id.buttonRemoveFav);
		
		type = Typeface.createFromAsset(getActivity().getAssets(), "font.ttf");
		btnreply.setTypeface(type);
		btnplay.setTypeface(type);
		btnSpeaker.setTypeface(type);
		btnDelete.setTypeface(type);

		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(1000);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(1000);

		animFromRight = AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), R.anim.enter_from_right);
		animFromLeft = AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), R.anim.enter_from_left);
		
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (LLCApplication.getVoicemailList()
				.get(LLCApplication.getCurrentDownlinePosition())
				.get("CanReply").equals("false")
				|| LLCApplication.getVoicemailList()
						.get(LLCApplication.getCurrentDownlinePosition())
						.get("CanReply").equals("0")) {
			btnreply.setVisibility(View.GONE);
			/*
			 * btnreply.setEnabled(false); btnreply.setAlpha(0.5f);
			 */
		} else {
			btnreply.setVisibility(View.VISIBLE);
			/*
			 * btnreply.setEnabled(true); btnreply.setAlpha(1.0f);
			 */
		}
		
		if(LLCApplication.getVoicemailList().get(position).get("IsFavorite").equals("true")){
			btnAddFav.setVisibility(View.GONE);
			btnRemoveFav.setVisibility(View.VISIBLE);
		}else{
			btnAddFav.setVisibility(View.VISIBLE);
			btnRemoveFav.setVisibility(View.GONE);
		}

		if (position == 0) {
			btnprev.setEnabled(false);
			btnprev.setTextColor(Color.GRAY);
		}

		if (position == LLCApplication.getVoicemailList().size() - 1) {
			btnnext.setEnabled(false);
			btnnext.setTextColor(Color.GRAY);
		}

		if (LLCApplication.getSpeaker().equals("1")) {
			
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			audioManager.setWiredHeadsetOn(false);
			audioManager.setSpeakerphoneOn(true);
			btnSpeaker.setText("Headphone");
			audioManager.setStreamVolume(AudioManager.MODE_IN_CALL, 1, 0);
			//btnSpeaker.setBackgroundResource(R.drawable.headphone);
		} else {
			
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			audioManager.setWiredHeadsetOn(true);
			audioManager.setSpeakerphoneOn(false);
			btnSpeaker.setText("Speaker");
			audioManager.setStreamVolume(AudioManager.MODE_IN_CALL, 1, 0);
			//btnSpeaker.setBackgroundResource(R.drawable.speaker);
		}
		
		hn = new Handler();
		hn.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				setData(position);
				hn.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (isOnline()) {
							SetMessageAsListened();
							try {
								if(mediaPlayer!=null){
									if(mediaPlayer.isPlaying()){
										mediaPlayer.stop();
									}
								}
								playAudio(position);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}

						} else {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"" + Constant.network_error,
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}, 500);

	}

	private void clickEvents() {
		// TODO Auto-generated method stub

		btnSpeaker.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (LLCApplication.getSpeaker().equals("1")) {
					LLCApplication.setSpeaker("0");
					btnSpeaker.setText("Speaker");
					//btnSpeaker.setBackgroundResource(R.drawable.speaker);

					audioManager.setMode(AudioManager.MODE_IN_CALL);
					audioManager.setWiredHeadsetOn(true);
					audioManager.setSpeakerphoneOn(false);
					audioManager.setStreamVolume(AudioManager.MODE_IN_CALL, 1, 0);
				} else {
					LLCApplication.setSpeaker("1");
					btnSpeaker.setText("Headphone");
					//btnSpeaker.setBackgroundResource(R.drawable.headphone);

					/*@SuppressWarnings("rawtypes")
					Class audioSystemClass;
					try {
						audioSystemClass = Class.forName("android.media.AudioSystem");
						@SuppressWarnings("unchecked")
						Method setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);
						// First 1 == FOR_MEDIA, second 1 == FORCE_SPEAKER. To go back to the default
						// behavior, use FORCE_NONE (0).
						setForceUse.invoke(null, 1, 1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					
					audioManager.setMode(AudioManager.MODE_IN_CALL);
					audioManager.setWiredHeadsetOn(false);
					audioManager.setSpeakerphoneOn(true);
					audioManager.setStreamVolume(AudioManager.MODE_IN_CALL, 1, 0);
				}

				ContentValues values = new ContentValues();
				values.put("speaker", "" + LLCApplication.getSpeaker());
				SplashActivity.db.update("user", values, "pk=1",null);
			}
		});

		btnTasks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				BaseActivityClass.baseAct.toggle();
			}
		});

		btnreply.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mediaPlayer != null
						&& mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					updateProgress = false;
				}
				Intent intent = new Intent(getActivity(), ReplyMessageActivity.class);
				intent.putExtra("pos", position);
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);

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

		btnplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.pause();
						updateProgress = false;
						btnplay.setText("Play");
						//btnplay.setBackgroundResource(R.drawable.play_icon);
					} else {
						btnplay.setText("Pause");
						if(selectionAudioIdx >= ((CustomAdapter) listview.getAdapter()).getAudioFileList().size()){
							selectionAudioIdx=0;
							if(mediaPlayer!=null){
								if(mediaPlayer.isPlaying()){
									mediaPlayer.stop();
								}
							}
							playAudio(selectionAudioIdx);
						}else{
							mediaPlayer.start();
							updateProgress = true;
							new Thread(new Runnable() {

								public void run() {
									try {
										while (mediaPlayer != null
												&& mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration()
												&& updateProgress) {
											progressBar.setProgress(mediaPlayer.getCurrentPosition());
											Thread.sleep(1);
										}
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
									}
									
								}
							}).start();
						}
						
					}
				}
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/*if (mediaPlayer != null) {
					mediaPlayer.pause();
					mediaPlayer.seekTo(0);
					progressBar.setProgress(0);
					updateProgress = false;
					btnplay.setText("Play");
					//btnplay.setBackgroundResource(R.drawable.play_icon);
				}*/
				
				AlertDialog.Builder alert11 = new AlertDialog.Builder(
						getActivity());
				alert11.setTitle(Constant.Alert_Name);
				alert11.setMessage("Are you sure you want to delete this Message?");
				alert11.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

								if (isOnline()) {
									DeleteMessage(LLCApplication
											.getPosition());
								} else {
									Toast.makeText(
											getActivity(),
											"" + Constant.network_error,
											Toast.LENGTH_SHORT).show();
								}

							}
						});
				alert11.setNegativeButton("NO",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});
				alert11.create();
				alert11.show();
			}
		});

		btnnext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (position != LLCApplication.getVoicemailList().size() - 1) {
					position = position + 1;

					if (position == LLCApplication.getVoicemailList().size() - 1) {
						btnnext.setEnabled(false);
						btnnext.setTextColor(Color.GRAY);
					} else {
						btnnext.setEnabled(true);
						btnprev.setEnabled(true);
						btnnext.setTextColor(Color.WHITE);
						btnprev.setTextColor(Color.WHITE);
					}
					if (position != 0) {
						btnprev.setEnabled(true);
						btnprev.setTextColor(Color.WHITE);
					}
				}

				setData(position);
				relativeMiddle.startAnimation(animFromLeft);
				animFromLeft.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						hn.post(new Runnable() {

							@SuppressLint("NewApi")
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (isOnline()) {
									if(mediaPlayer!=null){
										if(mediaPlayer.isPlaying()){
											mediaPlayer.stop();
										}
									}
									SetMessageAsListened();
									playAudio(position);
								} else {
									Toast.makeText(
											getActivity()
													.getApplicationContext(),
											"" + Constant.network_error,
											Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				});

			}
		});

		btnprev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (position != 0) {
					position = position - 1;
					if (position == 0) {
						btnprev.setEnabled(false);
						btnprev.setTextColor(Color.GRAY);
					} else {
						btnprev.setEnabled(true);
						btnprev.setTextColor(Color.WHITE);
					}

					if (position != LLCApplication.getVoicemailList().size() - 1) {
						btnnext.setEnabled(true);
						btnnext.setTextColor(Color.WHITE);
					}

					setData(position);
					relativeMiddle.startAnimation(animFromRight);
					animFromRight.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
							hn.post(new Runnable() {

								@SuppressLint("NewApi")
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (isOnline()) {
										if(mediaPlayer!=null){
											if(mediaPlayer.isPlaying()){
												mediaPlayer.stop();
											}
										}
										SetMessageAsListened();
										playAudio(position);
									} else {
										Toast.makeText(
												getActivity()
														.getApplicationContext(),
												"" + Constant.network_error,
												Toast.LENGTH_SHORT).show();
									}
								}
							});
						}
					});

				}
			}
		});

		relativeBack.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getActivity().finish();
				getActivity().overridePendingTransition(R.anim.hold_top,
						R.anim.exit_in_left);
			}
		});
		
		btnAddFav.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isOnline()) {
					addRemoveFavorite(LLCApplication
							.getPosition());
				} else {
					Toast.makeText(
							getActivity(),
							"" + Constant.network_error,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		btnRemoveFav.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isOnline()) {
					addRemoveFavorite(LLCApplication
							.getPosition());
				} else {
					Toast.makeText(
							getActivity(),
							"" + Constant.network_error,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void addRemoveFavorite(final int position){
		progressDialog = ProgressDialog.show(getActivity(), null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "FavoriteMessage");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("MessageID",
						""+ LLCApplication.getVoicemailList()
										.get(position).get("ID"));

				responseFavorite = callAPI(map);
				Log.i("responseFavorite", "" + responseFavorite);
				updateFavoriteMessage(position);
			}
		});
		t.start();
	}
	private void updateFavoriteMessage(final int position){
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if (responseFavorite != null) {
					int status = 0;
					try {
						json_str = new JSONObject(responseFavorite);
						status = json_str.getInt("Status");
						if(status==1){
							if(LLCApplication.getVoicemailList().get(position).get("IsFavorite").equals("true")){
								LLCApplication.getVoicemailList().get(position).put("IsFavorite", "false");
								btnAddFav.setVisibility(View.VISIBLE);
								btnRemoveFav.setVisibility(View.GONE);
							}else{
								LLCApplication.getVoicemailList().get(position).put("IsFavorite", "true");
								btnAddFav.setVisibility(View.GONE);
								btnRemoveFav.setVisibility(View.VISIBLE);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (status == 1) {
						LLCApplication.setFlagRefresh(true);
						
					}
				}
			}
		});
	}
	protected void DeleteMessage(final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(getActivity(), null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "DeleteMessage");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("MessageID",
						""+ LLCApplication.getVoicemailList()
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
		getActivity().runOnUiThread(new Runnable() {

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
						if (mediaPlayer != null
								&& mediaPlayer.isPlaying()) {
							mediaPlayer.pause();
							updateProgress = false;
						}

						getActivity().finish();
						getActivity().overridePendingTransition(
								R.anim.hold_top, R.anim.exit_in_left);
					}
				}
			}
		});
	}
	
	protected void enableComponents() {
		// TODO Auto-generated method stub
		relativeBack.setEnabled(true);
		btnplay.setEnabled(true);
		btnDelete.setEnabled(true);
		btnprev.setEnabled(true);
		btnnext.setEnabled(true);
		btnInfo.setEnabled(true);
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		relativeBack.setEnabled(false);
		btnplay.setEnabled(false);
		btnDelete.setEnabled(false);
		btnprev.setEnabled(false);
		btnnext.setEnabled(false);
		btnInfo.setEnabled(false);
	}

	private void setData(int pos) {
		// TODO Auto-generated method stub
		LLCApplication.setPosition(pos);
		HashMap<String, String> map = LLCApplication.getVoicemailList()
				.get(pos);

		txtFrom.setText("" + map.get("FirstName") + " " + map.get("LastName"));
		txtSent.setText("" + map.get("Stamp"));
		updateProgress = false;
		progressBar.setProgress(0);
		btnplay.setText("Play");
		//btnplay.setBackgroundResource(R.drawable.play_icon);
		
		if(LLCApplication.getVoicemailList().get(position).get("IsFavorite").equals("true")){
			btnAddFav.setVisibility(View.GONE);
			btnRemoveFav.setVisibility(View.VISIBLE);
		}else{
			btnAddFav.setVisibility(View.VISIBLE);
			btnRemoveFav.setVisibility(View.GONE);
		}
		loadTags(map);
	}

	private void loadTags(final HashMap<String, String> mapp) {
		// TODO Auto-generated method stub
		if (mapp.get("Tags") != null) {
			try {
				array = new JSONArray(mapp.get("Tags"));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			LLCApplication.getTagsList().clear();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj;
				try {
					obj = array.getJSONObject(i);
					HashMap<String, String> map = new HashMap<String, String>();

					map.put("ID", "" + obj.getString("ID"));
					map.put("UserID", "" + obj.getString("UserID"));
					map.put("FirstName", "" + obj.getString("FirstName"));
					map.put("LastName", "" + obj.getString("LastName"));
					map.put("Tag", "" + obj.getString("Tag"));
					map.put("TagType", "" + obj.getString("TagType"));

					LLCApplication.getTagsList().add(map);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			listview.setAdapter(new CustomAdapter(LLCApplication.getTagsList()));
		}
	}

	private void playAudio(final int pos) {
		// TODO Auto-generated method stub
		if (isOnline()) {
			
			if (mediaPlayer != null) {
				mediaPlayer.reset();
			}else{
				mediaPlayer= new MediaPlayer();
			}
			strStreamPath= "";
			//selectedAudioPos = pos;
			selectionAudioIdx =-1;
			if(progressDialog!=null ){
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
			}
			progressDialog = ProgressDialog.show(getActivity(), null, "Loading...	", true, false);
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					//mediaPlayer = new MediaPlayer();
					/*String streamPath = ""
							+ Constant.messagePath
							+ LLCApplication.getVoicemailList().get(pos)
									.get("FileName");
					//strStreamPath= streamPath;
					Log.d("streamPath ", ">>"+streamPath);
					for(int i=0;i<((CustomAdapter)listview.getAdapter()).getAudioFileList().size();i++){
						Log.d("ListItem "+i, ""+((CustomAdapter)listview.getAdapter()).getAudioFileList().get(i));
					}*/
					selectionAudioIdx = 0;
					
					Log.d("PlayAudio", ">>>>"+((CustomAdapter)listview.getAdapter()).getAudioFileList().size());
					Log.d("AudioFile at"+selectionAudioIdx, ">>>>"+((CustomAdapter)listview.getAdapter()).getAudioFileList().get(selectionAudioIdx));
					strStreamPath= ((CustomAdapter)listview.getAdapter()).getAudioFileList().get(selectionAudioIdx);
					Log.d("strStreamPath", ">>>>"+strStreamPath);
					try {
						mediaPlayer.setDataSource(strStreamPath);
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

					if (mediaPlayer != null) {
						
					}
				}
			});
			t.start();
			
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(
						final MediaPlayer mediaPlayer) {
					// TODO Auto-generated method stub
					btnplay.setText("Pause");
					//btnplay.setBackgroundResource(R.drawable.pause);
					mediaPlayer.start();
					progressBar.setMax(mediaPlayer.getDuration());
					progressBar.setProgress(0);
					if(progressDialog!=null ){
						if(progressDialog.isShowing()){
							progressDialog.dismiss();
						}
					}
					updateProgress = true;
					new Thread(new Runnable() {
						public void run() {
							try {
								while (mediaPlayer != null && 
										mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration()
										&& updateProgress) {
									progressBar.setProgress(mediaPlayer.getCurrentPosition());

									try {
										Thread.sleep(1);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								// TODO: handle exception
							}

						}
					}).start();
				}
			});

			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mediaPlayer.seekTo(0);
					progressBar.setProgress(0);
					updateProgress = false;
					btnplay.setText("Play");
					//btnplay.setBackgroundResource(R.drawable.play_icon);
					
					
					Log.e("Completed _idx="+selectionAudioIdx, ">>> "+((CustomAdapter)listview.getAdapter()).getAudioFileList().size());
					if(((CustomAdapter)listview.getAdapter()).getAudioFileList().size()>0){
						selectionAudioIdx++;
						Log.d("selectionAudioIdx: "+selectionAudioIdx, "Audio List size: "+((CustomAdapter)listview.getAdapter()).getAudioFileList().size());
						if(selectionAudioIdx< ((CustomAdapter)listview.getAdapter()).getAudioFileList().size()){
							if(progressDialog!=null ){
								if(progressDialog.isShowing()){
									progressDialog.dismiss();
								}
							}
							progressDialog = ProgressDialog.show(getActivity(), null, "Loading...	", true, false);
							playNextAudio();	
						}
					}
				}
			});

		}
	}
	
	private void playNextAudio(){
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//mediaPlayer = new MediaPlayer();
				if (mediaPlayer != null) {
					mediaPlayer.reset();
				}else{
					mediaPlayer= new MediaPlayer();
				}
				if(selectionAudioIdx < ((CustomAdapter)listview.getAdapter()).getAudioFileList().size()){
					String streamPath = ""+ ((CustomAdapter)listview.getAdapter()).getAudioFileList().get(selectionAudioIdx);
					try {
						mediaPlayer.setDataSource(streamPath);
						//mediaPlayer.setAudioStreamType(AudioManager.MODE_IN_CALL);
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
				}
			}
		});
		t.start();
	}

	@SuppressLint("DefaultLocale")
	class CustomAdapter extends BaseAdapter {

		ArrayList<HashMap<String, String>> locallist;
		ArrayList<String> audioFileList= new ArrayList<String>();

		public CustomAdapter(ArrayList<HashMap<String, String>> VoicemailList) {
			// TODO Auto-generated constructor stub
			locallist = VoicemailList;
			audioFileList.clear();
			ArrayList<String> tempList= new ArrayList<String>();
			for(int i=0;i<locallist.size();i++){
				if(locallist.get(i).get("TagType").equalsIgnoreCase("Audio")){
					tempList.add(Constant.messagePath+locallist.get(i).get("Tag"));
				}
			}
			if(tempList.size()>0){
				for(int i=(tempList.size()-1);i>=0;i--){
					audioFileList.add(tempList.get(i));
				}
			}
			
			String streamPath = ""
					+ Constant.messagePath
					+ LLCApplication.getVoicemailList().get(position)
							.get("FileName");
			audioFileList.add(streamPath);
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

		public void clearAudioFileList(){
			this.audioFileList.clear();
		}
		
		public ArrayList<String> getAudioFileList(){
			return audioFileList;
		}
		
		@SuppressLint({ "NewApi", "DefaultLocale", "InflateParams" })
		@Override
		public View getView(final int position, View convertview, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View row = convertview;
			if (row == null) {
				row = getActivity().getLayoutInflater().inflate(
						R.layout.tagslist_row, null);
			}

			LinearLayout main = (LinearLayout) row.findViewById(R.id.main);
			TextView name = (TextView) row.findViewById(R.id.name);
			TextView tag = (TextView) row.findViewById(R.id.tag);
			Log.e("localList", ""+locallist.get(position));
			if(locallist.get(position).get("TagType").equalsIgnoreCase("Audio")){
				name.setText("");
				tag.setText("");
			}else{
				name.setText("" + locallist.get(position).get("FirstName") + " "
						+ locallist.get(position).get("LastName"));
				tag.setText("" + locallist.get(position).get("Tag"));
			}
			/*name.setText("" + locallist.get(position).get("FirstName") + " "
					+ locallist.get(position).get("LastName"));
			tag.setText("" + locallist.get(position).get("Tag"));*/

			LayoutParams lp = (LayoutParams) main.getLayoutParams();

			if (name.getText().toString().toLowerCase()
					.equals(txtFrom.getText().toString().toLowerCase())) {
				lp.gravity = Gravity.RIGHT;
				name.setLayoutParams(lp);
				tag.setLayoutParams(lp);
			} else {
				lp.gravity = Gravity.LEFT;
				name.setLayoutParams(lp);
				tag.setLayoutParams(lp);
			}
			return row;
		}
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
										.get(position).get("ID"));

				response = callAPI(map);
				Log.i("response", "" + response);
				Update();
			}
		});
		t.start();
	}

	@SuppressLint("NewApi")
	private void Update() {
		// TODO Auto-generated method stub
		getActivity().runOnUiThread(new Runnable() {

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
						if(LLCApplication.getVoicemailList()!=null && LLCApplication.getVoicemailList().size() > position){
							LLCApplication.getVoicemailList().get(position).put("ListenedTo", "1");
						}
					}
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		
		if (audioManager != null) {
			/*@SuppressWarnings("rawtypes")
			Class audioSystemClass;
			try {
				audioSystemClass = Class.forName("android.media.AudioSystem");
				@SuppressWarnings("unchecked")
				Method setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);
				// First 1 == FOR_MEDIA, second 1 == FORCE_SPEAKER. To go back to the default
				// behavior, use FORCE_NONE (0).
				setForceUse.invoke(null, 0, 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setWiredHeadsetOn(false);
			audioManager.setSpeakerphoneOn(true);
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		// WHEN THE SCREEN IS ABOUT TO TURN OFF
		if (ScreenReceiver.wasScreenOn) {
			// THIS IS THE CASE WHEN ONPAUSE() IS CALLED BY THE SYSTEM DUE TO A
			// SCREEN STATE CHANGE
			System.out.println("SCREEN TURNED OFF");
		} else {
			// THIS IS WHEN ONPAUSE() IS CALLED WHEN THE SCREEN STATE HAS NOT
			// CHANGED
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				updateProgress = false;
				btnplay.setText("Play");
				//btnplay.setBackgroundResource(R.drawable.play_icon);
			}
		}
		super.onPause();
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public String callAPI(HashMap<String, String> map) {
		// TODO Auto-generated method stub
		String result = null;
		int ResponseCode;
		try {

			HttpParams httpParams = new BasicHttpParams();

			httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			httpParams.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
					HTTP.UTF_8);
			httpParams.setParameter(CoreProtocolPNames.USER_AGENT,
					"Apache-HttpClient/Android");
			// httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
			// 15000);
			httpParams.setParameter(
					CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));
			ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
					httpParams, schemeRegistry);

			HttpClient client = new DefaultHttpClient(cm, httpParams);

			String url = Constant.sActionUrl;
			HttpPost request = new HttpPost(url);
			request.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			// nameValuePairs.add(new BasicNameValuePair("json", params1[0]));

			for (String key : map.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(key, map.get(key)));
			}

			request.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

			HttpResponse response = client.execute(request);

			ResponseCode = response.getStatusLine().getStatusCode();

			if (ResponseCode == 200) {
				HttpEntity entity = response.getEntity();
				// If the response does not enclose an entity, there is no need
				if (entity != null) {
					InputStream instream = entity.getContent();
					result = RestClient.convertStreamToString(instream);
				}
			}
		} catch (Throwable t) {
			Log.d("RequestTask Throwable", "" + t);
			return null;
		}

		// Log.d("result", ""+result);
		return result;

	}
}
