package com.rmrdevelopment.lifelineconnect.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.utils.Constant;

public class SendMessage extends BaseActivityClass {

	Context mContext = this;
	TextView title;
	RelativeLayout relativeBack, layoutDistro, layoutIndividual, layoutUpline;
	Typeface type;
	LinearLayout layoutVoice, layoutMessage, layoutAudio;
	TextView txtVoice, txtMessage, txtRecording, txtSec, txtUpline;
	EditText editmsgtag, editName;
	ProgressBar progressBar;
	Button btnReply, btnRecord, btnStop, btnPlay, btnSearch;
	int position;
	String memberUserID = "";
	Spinner spinner;
	ArrayList<String> listAdapter = new ArrayList<String>();
	ArrayAdapter<String> dataAdapter;

	// Recording audio;
	private MediaRecorder recorder = null;
	String filePath = "";
	String RectLength = "";
	MediaPlayer mediaPlayer;
	MyCountDownTimer timer;
	MyCountDownTimer1 timer1;
	boolean flagRecording = false;

	RelativeLayout relativeList;
	ListView listview, searchedListview;
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
		setContentView(R.layout.sendmessage);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			position = extras.getInt("pos");
		}
		type = Typeface.createFromAsset(getAssets(), "font.ttf");

		init();
		clickEvents();
	}

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		relativeBack = (RelativeLayout) findViewById(R.id.relback);
		layoutDistro = (RelativeLayout) findViewById(R.id.layoutDistro);
		layoutIndividual = (RelativeLayout) findViewById(R.id.layoutIndividual);
		layoutUpline = (RelativeLayout) findViewById(R.id.layoutupline);
		txtUpline = (TextView) findViewById(R.id.txtupline);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(type);

		layoutAudio = (LinearLayout) findViewById(R.id.layoutAudio);
		layoutVoice = (LinearLayout) findViewById(R.id.layoutVoice);
		layoutMessage = (LinearLayout) findViewById(R.id.layoutMessage);
		txtVoice = (TextView) findViewById(R.id.txtVoice);
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		editmsgtag = (EditText) findViewById(R.id.msgtag);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		btnReply = (Button) findViewById(R.id.btnreply);
		btnRecord = (Button) findViewById(R.id.btnrecord);
		btnPlay = (Button) findViewById(R.id.btnplay);
		btnStop = (Button) findViewById(R.id.btnstop);
		txtRecording = (TextView) findViewById(R.id.txtrecording);
		txtSec = (TextView) findViewById(R.id.txtsec);
		spinner = (Spinner) findViewById(R.id.spinner);

		relativeList = (RelativeLayout) findViewById(R.id.listlayout);
		listview = (ListView) findViewById(R.id.lst);
		btnCancel = (Button) findViewById(R.id.btncancel);
		txtTag = (TextView) findViewById(R.id.txttag);
		searchedListview = (ListView) findViewById(R.id.searchlist);

		relativeInfo = (RelativeLayout) findViewById(R.id.infolayout);
		btnInfo = (Button) findViewById(R.id.btninfo);
		btnClose = (Button) findViewById(R.id.btnclose);
		txtInfo = (TextView) findViewById(R.id.txtinfo);
		txtInfo.setText("" + Constant.SendMessageInfo);

		btnSearch = (Button) findViewById(R.id.btnsearch);
		editName = (EditText) findViewById(R.id.editname);

		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(1000);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(1000);

		btnRecord.setEnabled(true);
		btnStop.setEnabled(false);
		btnPlay.setEnabled(false);

		btnRecord.setAlpha(1.0f);
		btnStop.setAlpha(0.5f);
		btnPlay.setAlpha(0.5f);

		txtUpline.setText("Your upline is: " + LLCApplication.getUplineName());
		Log.i("canseeDownline", "" + LLCApplication.getCanSeeDownline() + "\n"
				+ LLCApplication.getUplineName());
		if (LLCApplication.getCanSeeDownline().equals("0")) {
			spinner.setVisibility(View.GONE);
			layoutUpline.setVisibility(View.VISIBLE);
			layoutDistro.setVisibility(View.GONE);
			layoutIndividual.setVisibility(View.GONE);
		} else {
			spinner.setVisibility(View.VISIBLE);
			if (LLCApplication.getUplineName().length() > 0) {
				listAdapter.add("Upline");
			}
			listAdapter.add("Downline Distro. List");
			listAdapter.add("Individual Member");

		}

		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listAdapter);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

		if (isOnline()) {
			GetDistroLists();
		} else {
			Toast.makeText(getApplicationContext(),
					"" + Constant.network_error, Toast.LENGTH_SHORT).show();
		}
	}

	private void clickEvents() {
		// TODO Auto-generated method stub

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				switch (pos) {
				case 0:
					if (LLCApplication.getUplineName().length() > 0) {
						layoutUpline.setVisibility(View.VISIBLE);
						layoutDistro.setVisibility(View.GONE);
						layoutIndividual.setVisibility(View.GONE);
					} else {
						layoutVoice.setBackgroundColor(Color
								.parseColor("#00aacf"));
						layoutMessage.setBackgroundColor(Color
								.parseColor("#ffffff"));
						txtVoice.setTextColor(Color.parseColor("#ffffff"));
						txtMessage.setTextColor(Color.parseColor("#000000"));

						layoutDistro.setVisibility(View.VISIBLE);
						layoutIndividual.setVisibility(View.GONE);
						layoutUpline.setVisibility(View.GONE);
					}
					break;
				case 1:
					if (LLCApplication.getUplineName().length() > 0) {
						layoutVoice.setBackgroundColor(Color
								.parseColor("#00aacf"));
						layoutMessage.setBackgroundColor(Color
								.parseColor("#ffffff"));
						txtVoice.setTextColor(Color.parseColor("#ffffff"));
						txtMessage.setTextColor(Color.parseColor("#000000"));

						layoutDistro.setVisibility(View.VISIBLE);
						layoutIndividual.setVisibility(View.GONE);
						layoutUpline.setVisibility(View.GONE);
					} else {
						layoutVoice.setBackgroundColor(Color
								.parseColor("#ffffff"));
						layoutMessage.setBackgroundColor(Color
								.parseColor("#00aacf"));
						txtVoice.setTextColor(Color.parseColor("#000000"));
						txtMessage.setTextColor(Color.parseColor("#ffffff"));

						layoutDistro.setVisibility(View.GONE);
						layoutIndividual.setVisibility(View.VISIBLE);
						layoutUpline.setVisibility(View.GONE);
					}
					break;
				case 2:
					layoutVoice.setBackgroundColor(Color.parseColor("#ffffff"));
					layoutMessage.setBackgroundColor(Color
							.parseColor("#00aacf"));
					txtVoice.setTextColor(Color.parseColor("#000000"));
					txtMessage.setTextColor(Color.parseColor("#ffffff"));

					layoutDistro.setVisibility(View.GONE);
					layoutIndividual.setVisibility(View.VISIBLE);
					layoutUpline.setVisibility(View.GONE);
					break;

				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editmsgtag.getWindowToken(), 0);

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

		txtTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				searchedListview.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
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

		btnRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				flagRecording = true;
				startRecording();
			}
		});

		btnStop.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (flagRecording) {
					stopRecording();
				} else {
					if (mediaPlayer != null && mediaPlayer.isPlaying()) {
						mediaPlayer.pause();
					}
					timer1.cancel();

					btnPlay.setEnabled(true);
					btnStop.setEnabled(false);
					btnRecord.setEnabled(true);

					btnRecord.setAlpha(1.0f);
					btnStop.setAlpha(0.5f);
					btnPlay.setAlpha(1.0f);
				}
			}
		});

		btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				flagRecording = false;
				// Set MediaPlayer:
				if (mediaPlayer != null) {
					mediaPlayer.reset();
				}

				mediaPlayer = new MediaPlayer();
				try {
					mediaPlayer.setDataSource(filePath);
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

				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

					@SuppressLint("NewApi")
					@Override
					public void onPrepared(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						progressBar.setProgress(0);
						progressBar.setMax(mediaPlayer.getDuration() / 1000);

						mediaPlayer.start();
						btnPlay.setEnabled(false);
						btnStop.setEnabled(true);
						btnRecord.setEnabled(false);

						btnRecord.setAlpha(0.5f);
						btnStop.setAlpha(1.0f);
						btnPlay.setAlpha(0.5f);

						Log.i("milli..", "" + mediaPlayer.getDuration());
						timer1 = new MyCountDownTimer1(mediaPlayer
								.getDuration() + 1000, 1000);
						timer1.start();
					}
				});

				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@SuppressLint("NewApi")
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						timer1.cancel();
						mediaPlayer.seekTo(0);

						btnPlay.setEnabled(true);
						btnStop.setEnabled(false);
						btnRecord.setEnabled(true);

						btnRecord.setAlpha(1.0f);
						btnStop.setAlpha(0.5f);
						btnPlay.setAlpha(1.0f);
					}
				});

			}
		});

		btnReply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String msg = editmsgtag.getText().toString();
				if (filePath.length() > 0) {
					if (isOnline()) {
						if (layoutIndividual.getVisibility() == View.VISIBLE) {
							if (memberUserID.length() > 0) {
								replyWithVoice(msg, false, filePath);
							} else {
								Toast.makeText(getApplicationContext(),
										"Select atleast one member.",
										Toast.LENGTH_SHORT).show();
							}
						} else if (layoutDistro.getVisibility() == View.VISIBLE) {
							replyWithVoice(msg, true, filePath);
						} else {
							//for upline:
							replyWithVoice(msg, true, filePath);
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"" + Constant.network_error, Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"please record an audio", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		layoutVoice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				layoutVoice.setBackgroundColor(Color.parseColor("#00aacf"));
				layoutMessage.setBackgroundColor(Color.parseColor("#ffffff"));
				txtVoice.setTextColor(Color.parseColor("#ffffff"));
				txtMessage.setTextColor(Color.parseColor("#000000"));

				layoutDistro.setVisibility(View.VISIBLE);
				layoutIndividual.setVisibility(View.GONE);
			}
		});

		layoutMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				layoutVoice.setBackgroundColor(Color.parseColor("#ffffff"));
				layoutMessage.setBackgroundColor(Color.parseColor("#00aacf"));
				txtVoice.setTextColor(Color.parseColor("#000000"));
				txtMessage.setTextColor(Color.parseColor("#ffffff"));

				layoutDistro.setVisibility(View.GONE);
				layoutIndividual.setVisibility(View.VISIBLE);
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

	public void replyWithVoice(final String message,
			final boolean flagDistrolist, final String filePath) {

		progressDialog = ProgressDialog.show(SendMessage.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String resp = "";
				String PassTo = "";

				if (layoutUpline.getVisibility() == View.VISIBLE) {
					PassTo = LLCApplication.getUplineUserID();
				} else {
					if (flagDistrolist) {
						PassTo = strIDs;
					} else {
						PassTo = memberUserID;
					}
				}

				try {
					HttpClient client = new DefaultHttpClient();
					HttpResponse response = null;
					Log.i("PassTo",
							"" + PassTo + "\n" + LLCApplication.getUserId()
									+ "\n" + filePath);
					FileBody fbody = null;
					MultipartEntity entity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);

					File audio = new File(filePath);
					fbody = new FileBody(audio, "audio/wav");
					entity.addPart("userfile", fbody);

					String query = URLEncoder.encode(message, "UTF-8");
					String url = Constant.sSiteUrl
							+ "desktopmodules/teamtalkapp/uploadmessage.aspx?os=android&User_ID="
							+ LLCApplication.getUserId() + "&RecLength="
							+ RectLength + "&PassDown=1&PassTo=" + PassTo
							+ "&msgtag=" + query;

					HttpPost poster = new HttpPost(url);

					poster.setEntity(entity);
					response = client.execute(poster);

					BufferedReader rd = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					String line = null;
					while ((line = rd.readLine()) != null) {
						resp += line;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.d("Resp Upload", "" + resp);
				UpdatereplyWithVoice(resp);
			}
		});
		t.start();

	}

	protected void UpdatereplyWithVoice(final String response) {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				if (response != null) {
					if (response.equals("200")) {
						finish();
						overridePendingTransition(R.anim.hold_top,
								R.anim.exit_in_left);
					}
				}

			}
		});
	}

	protected void replyWithText(final String message, final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(SendMessage.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "replyWithText");
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
				UpdatereplyWithText();
			}
		});
		t.start();
	}

	private void UpdatereplyWithText() {
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

	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, Constant.Alert_Name);
		if (!file.exists()) {
			file.mkdirs();
		}

		filePath = file + "/" + System.currentTimeMillis() + ".3gp";
		return filePath;
	}

	@SuppressLint("NewApi")
	private void startRecording() {

		txtRecording.setVisibility(View.VISIBLE);
		progressBar.setProgress(0);
		progressBar.setMax(90);

		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());
		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);
		try {
			recorder.prepare();
			recorder.start();
			btnStop.setEnabled(true);
			btnRecord.setEnabled(false);
			btnPlay.setEnabled(false);

			btnRecord.setAlpha(0.5f);
			btnStop.setAlpha(1.0f);
			btnPlay.setAlpha(0.5f);

			timer = new MyCountDownTimer(91000, 1000);
			timer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private void stopRecording() {

		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
		}

		timer.cancel();
		txtRecording.setVisibility(View.GONE);
		btnStop.setEnabled(false);
		btnPlay.setEnabled(true);
		btnRecord.setEnabled(true);

		btnRecord.setAlpha(1.0f);
		btnStop.setAlpha(0.5f);
		btnPlay.setAlpha(1.0f);

		if (mediaPlayer != null) {
			mediaPlayer.reset();
		}

		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(filePath);
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

		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				RectLength = "" + mediaPlayer.getDuration();
				long timeInmillisec = Long.parseLong(RectLength);
				long duration = timeInmillisec / 1000;
				long hours = duration / 3600;
				long minutes = (duration - hours * 3600) / 60;
				long seconds = duration - (hours * 3600 + minutes * 60);

				RectLength = "" + seconds;
				Log.i("RectLength", "" + RectLength);
			}
		});

	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(SendMessage.this, "Error: " + what + ", " + extra,
					Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(SendMessage.this, "Warning: " + what + ", " + extra,
					Toast.LENGTH_SHORT).show();
		}
	};

	/*
	 * CountDownTimer timer = new CountDownTimer(91000, 1000) {
	 * 
	 * public void onTick(long millisUntilFinished) { int sec = (int)
	 * (millisUntilFinished / 1000); txtSec.setText("" + sec + " Sec");
	 * progressBar.setProgress(90 - sec);
	 * txtRecording.setVisibility(View.VISIBLE); }
	 * 
	 * public void onFinish() { txtRecording.setVisibility(View.GONE);
	 * txtRecording.setText("0 Sec"); stopRecording(); } };
	 */

	public class MyCountDownTimer extends CountDownTimer {

		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			txtRecording.setVisibility(View.GONE);
			txtRecording.setText("0 Sec");
			stopRecording();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			int sec = (int) (millisUntilFinished / 1000);
			txtSec.setText("" + sec + " Sec");
			progressBar.setProgress(90 - sec);
			txtRecording.setVisibility(View.VISIBLE);
		}

	}

	public class MyCountDownTimer1 extends CountDownTimer {

		int totalTime;

		public MyCountDownTimer1(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
			totalTime = (int) (millisInFuture / 1000);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			int sec = (int) (millisUntilFinished / 1000);
			txtSec.setText("" + (totalTime - sec) + " Sec");
			progressBar.setProgress(totalTime - sec);
		}

	}

	protected void enableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(true);
		relativeBack.setEnabled(true);
		txtTag.setEnabled(true);
		editmsgtag.setEnabled(true);
		btnRecord.setEnabled(true);
		btnStop.setEnabled(true);
		btnPlay.setEnabled(true);
		btnReply.setEnabled(true);
		btnSearch.setEnabled(true);
		layoutMessage.setEnabled(true);
		layoutVoice.setEnabled(true);
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(false);
		relativeBack.setEnabled(false);
		txtTag.setEnabled(false);
		editmsgtag.setEnabled(false);
		btnRecord.setEnabled(false);
		btnStop.setEnabled(false);
		btnPlay.setEnabled(false);
		btnReply.setEnabled(false);
		btnSearch.setEnabled(false);
		layoutMessage.setEnabled(false);
		layoutVoice.setEnabled(false);
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
		protected CheckBox tick;
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

					listview.setAdapter(new CustomAdapter(LLCApplication
							.getDistroLists()));
				}
			}
		});
	}

	protected void SearchUsers(final String searchString) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(SendMessage.this, null,
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

					searchedListview.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
					searchedListview.setAdapter(new SearchedCustomAdapter(
							LLCApplication.getWireSearchUsers()));
					relativeList.setVisibility(View.VISIBLE);
					relativeList.startAnimation(fade_in);
					disableComponents();
				}
			}
		});
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

}
