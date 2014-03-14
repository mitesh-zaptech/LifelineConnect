package com.rmrdevelopment.lifelineconnect.activities;

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
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import com.rmrdevelopment.lifelineconnect.utils.Constant;
import com.rmrdevelopment.lifelineconnect.utils.RestClient;

@SuppressLint("NewApi")
public class MessageDetails extends Fragment {

	FragmentManager fm;
	Button btnreply, btnplay, btnpause, btnTasks, btnSpeaker;
	TextView btnprev, btnnext;
	TextView txtFrom, txtSent, title;
	RelativeLayout relativeBack;
	ProgressBar progressBar;
	ListView listview;
	int position;
	RelativeLayout relativeMiddle;
	Animation animFromLeft, animFromRight;
	public static MediaPlayer mediaPlayer;
	public static boolean updateProgress = false;
	Handler hn;

	RelativeLayout relativeInfo;
	TextView txtInfo;
	Animation fade_in, fade_out;
	Button btnInfo, btnClose;

	// BaseActivity:
	Context context;
	String response;
	JSONObject json_str;
	String Valid;
	String strRequest = null;
	String data_array;
	JSONArray array = null;
	ProgressDialog progressDialog;

	AudioManager audioManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Left", "onCreateView()");
		return inflater.inflate(R.layout.messagedetails, container, false);
	}

	@SuppressWarnings("deprecation")
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
						btnplay.setBackgroundResource(R.drawable.play_icon);
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
		btnpause = (Button) getView().findViewById(R.id.btnpause);
		btnSpeaker = (Button) getView().findViewById(R.id.btnSpeaker);
		progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
		btnprev = (TextView) getView().findViewById(R.id.btnprev);
		btnnext = (TextView) getView().findViewById(R.id.btnnext);
		relativeMiddle = (RelativeLayout) getView().findViewById(R.id.middle);
		listview = (ListView) getView().findViewById(R.id.lst);

		title = (TextView) getView().findViewById(R.id.title);
		title.setTypeface(Home.type);

		relativeInfo = (RelativeLayout) getView().findViewById(R.id.infolayout);
		btnInfo = (Button) getView().findViewById(R.id.btninfo);
		btnTasks = (Button) getView().findViewById(R.id.btnTasks);
		btnClose = (Button) getView().findViewById(R.id.btnclose);
		txtInfo = (TextView) getView().findViewById(R.id.txtinfo);
		txtInfo.setText(Constant.messageDetailsInfo);

		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(1000);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(1000);

		animFromRight = AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), R.anim.enter_from_right);
		animFromLeft = AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), R.anim.enter_from_left);

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

		if (position == 0) {
			btnprev.setEnabled(false);
		}

		if (position == LLCApplication.getVoicemailList().size() - 1) {
			btnnext.setEnabled(false);
		}

		if (LLCApplication.getSpeaker().equals("1")) {
			audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			audioManager.setWiredHeadsetOn(false);
			audioManager.setSpeakerphoneOn(true);

			btnSpeaker.setBackgroundResource(R.drawable.headphone);
		} else {
			audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			audioManager.setWiredHeadsetOn(true);
			audioManager.setSpeakerphoneOn(false);

			btnSpeaker.setBackgroundResource(R.drawable.speaker);
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
					btnSpeaker.setBackgroundResource(R.drawable.speaker);

					audioManager = (AudioManager) context
							.getSystemService(Context.AUDIO_SERVICE);
					audioManager.setMode(AudioManager.MODE_IN_CALL);
					audioManager.setWiredHeadsetOn(true);
					audioManager.setSpeakerphoneOn(false);
				} else {
					LLCApplication.setSpeaker("1");
					btnSpeaker.setBackgroundResource(R.drawable.headphone);

					audioManager = (AudioManager) context
							.getSystemService(Context.AUDIO_SERVICE);
					audioManager.setMode(AudioManager.MODE_IN_CALL);
					audioManager.setWiredHeadsetOn(false);
					audioManager.setSpeakerphoneOn(true);
				}

				ContentValues values = new ContentValues();
				values.put("speaker", "" + LLCApplication.getSpeaker());
				Splash.db.update("user", values, "pk=1",null);
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

				Intent intent = new Intent(getActivity(), ReplyMessage.class);
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
						btnplay.setBackgroundResource(R.drawable.play_icon);
					} else {
						btnplay.setBackgroundResource(R.drawable.pause);
						mediaPlayer.start();
						updateProgress = true;
						new Thread(new Runnable() {

							public void run() {
								while (mediaPlayer != null
										&& mediaPlayer.getCurrentPosition() < mediaPlayer
												.getDuration()
										&& updateProgress) {
									progressBar.setProgress(mediaPlayer
											.getCurrentPosition());

									try {
										Thread.sleep(1);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}).start();

					}
				}
			}
		});

		btnpause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mediaPlayer != null) {
					mediaPlayer.pause();
					mediaPlayer.seekTo(0);
					progressBar.setProgress(0);
					updateProgress = false;
					btnplay.setBackgroundResource(R.drawable.play_icon);
				}
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
					} else {
						btnnext.setEnabled(true);
						btnprev.setEnabled(true);
					}
					if (position != 0) {
						btnprev.setEnabled(true);
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
					} else {
						btnprev.setEnabled(true);
					}

					if (position != LLCApplication.getVoicemailList().size() - 1) {
						btnnext.setEnabled(true);
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
	}

	protected void enableComponents() {
		// TODO Auto-generated method stub
		relativeBack.setEnabled(true);
		btnplay.setEnabled(true);
		btnpause.setEnabled(true);
		btnprev.setEnabled(true);
		btnnext.setEnabled(true);
		btnInfo.setEnabled(true);
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		relativeBack.setEnabled(false);
		btnplay.setEnabled(false);
		btnpause.setEnabled(false);
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
		btnplay.setBackgroundResource(R.drawable.play_icon);
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
			}

			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mediaPlayer = new MediaPlayer();
					String streamPath = ""
							+ Constant.messagePath
							+ LLCApplication.getVoicemailList().get(pos)
									.get("FileName");
					try {
						mediaPlayer.setDataSource(streamPath);
						mediaPlayer
								.setAudioStreamType(AudioManager.MODE_IN_CALL);
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
										// TODO Auto-generated method stub
										btnplay.setBackgroundResource(R.drawable.pause);
										mediaPlayer.start();
										progressBar.setMax(mediaPlayer
												.getDuration());
										progressBar.setProgress(0);
										updateProgress = true;
										new Thread(new Runnable() {

											public void run() {
												try {
													while (mediaPlayer != null
															&& mediaPlayer
																	.getCurrentPosition() < mediaPlayer
																	.getDuration()
															&& updateProgress) {
														progressBar
																.setProgress(mediaPlayer
																		.getCurrentPosition());

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

						mediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {

									@Override
									public void onCompletion(MediaPlayer mp) {
										// TODO Auto-generated method stub
										mediaPlayer.seekTo(0);
										progressBar.setProgress(0);
										updateProgress = false;
										btnplay.setBackgroundResource(R.drawable.play_icon);
									}
								});
					}
				}
			});
			t.start();

		}
	}

	@SuppressLint("DefaultLocale")
	class CustomAdapter extends BaseAdapter {

		ArrayList<HashMap<String, String>> locallist;

		public CustomAdapter(ArrayList<HashMap<String, String>> VoicemailList) {
			// TODO Auto-generated constructor stub
			locallist = VoicemailList;
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

		@SuppressLint({ "NewApi", "DefaultLocale" })
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

			name.setText("" + locallist.get(position).get("FirstName") + " "
					+ locallist.get(position).get("LastName"));
			tag.setText("" + locallist.get(position).get("Tag"));

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
						LLCApplication.getVoicemailList().get(position)
								.put("ListenedTo", "1");
					}
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		
		if (audioManager != null) {
			audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.MODE_IN_CALL);
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
				btnplay.setBackgroundResource(R.drawable.play_icon);
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

	@SuppressWarnings("deprecation")
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
