package com.rmrdevelopment.lifelineconnect.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.utils.Constant;

public class ReplyMessageActivity extends BaseActivityClass {

	private TextView title;
	private TextView txtVoice;
	private TextView txtMessage;
	private TextView txtRecording;
	private TextView txtSec;
	private TextView txtInfo;
	
	private RelativeLayout relativeBack;
	private RelativeLayout relativeProgress;
	private RelativeLayout relativeInfo;
	
	private Typeface type;
	
	private LinearLayout layoutVoice;
	private LinearLayout layoutMessage;
	private LinearLayout layoutAudio;
	
	private EditText editmsgtag;
	private ProgressBar progressBar;
	
	private Button btnReply;
	private Button btnCancel;
	private Button btnRecord;
	private Button btnStop;
	private Button btnPlay;
	private Button btnHome;
	private Button btnInfo;
	private Button btnClose;
	
	private int position;

	// Recording audio;
	private MediaRecorder recorder = null;
	private String filePath = "";
	private String RectLength="";
	
	private MediaPlayer mediaPlayer;
	private MyCountDownTimer timer;
	private MyCountDownTimer1 timer1;
	
	private boolean flagRecording = false;
	
	private Animation fade_in;
	private Animation fade_out;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.replymessage);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			position = extras.getInt("pos");
		}
		type = Typeface.createFromAsset(getAssets(), "font.ttf");

		init();
		clickEvents();
		
		/*if(LLCApplication.getCanVoiceTag().equalsIgnoreCase("0")){
			layoutVoice.setVisibility(View.GONE);
			layoutVoice.setBackgroundColor(Color.parseColor("#ffffff"));
			layoutMessage.setBackgroundColor(Color.parseColor("#00aacf"));
			txtVoice.setTextColor(Color.parseColor("#000000"));
			txtMessage.setTextColor(Color.parseColor("#ffffff"));

			layoutAudio.setVisibility(View.GONE);
			relativeProgress.setVisibility(View.GONE);
		}*/
		
		btnReply.setEnabled(false);
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

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		relativeBack = (RelativeLayout) findViewById(R.id.relback);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(type);
		btnHome = (Button) findViewById(R.id.btnhome);

		relativeProgress = (RelativeLayout) findViewById(R.id.r4);
		layoutAudio = (LinearLayout) findViewById(R.id.layoutAudio);
		layoutVoice = (LinearLayout) findViewById(R.id.layoutVoice);
		layoutMessage = (LinearLayout) findViewById(R.id.layoutMessage);
		txtVoice = (TextView) findViewById(R.id.txtVoice);
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		editmsgtag = (EditText) findViewById(R.id.msgtag);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		btnReply = (Button) findViewById(R.id.btnreply);
		btnCancel = (Button) findViewById(R.id.btncancel);
		btnRecord = (Button) findViewById(R.id.btnrecord);
		btnPlay = (Button) findViewById(R.id.btnplay);
		btnStop = (Button) findViewById(R.id.btnstop);
		txtRecording = (TextView) findViewById(R.id.txtrecording);
		txtSec = (TextView) findViewById(R.id.txtsec);

		relativeInfo = (RelativeLayout) findViewById(R.id.infolayout);
		btnInfo = (Button) findViewById(R.id.btninfo);
		btnClose = (Button) findViewById(R.id.btnclose);
		txtInfo = (TextView) findViewById(R.id.txtinfo);
		txtInfo.setText("" + Constant.ReplyMessageInfo);

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
	}

	private void clickEvents() {
		// TODO Auto-generated method stub
		btnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ReplyMessageActivity.this,
						HomeSlidingFragmentActivity.class);
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
					progressBar.setProgress(0);
					timer1.cancel();

					btnPlay.setEnabled(true);
					btnStop.setEnabled(false);
					btnRecord.setEnabled(true);

					btnRecord.setAlpha(1.0f);
					btnStop.setAlpha(0.5f);
					btnPlay.setAlpha(1.0f);
					btnReply.setEnabled(true);
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

				btnReply.setEnabled(false);
				
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
						
						btnReply.setEnabled(true);
					}
				});

			}
		});

		btnReply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mediaPlayer!=null){
					if(mediaPlayer.isPlaying()){
						mediaPlayer.stop();
					}
				}
				String msg = editmsgtag.getText().toString();
				if (layoutAudio.getVisibility() == View.GONE) {
					if (msg.length() > 0) {
						if (isOnline()) {
							replyWithText(msg, position);
						} else {
							Toast.makeText(getApplicationContext(),
									"" + Constant.network_error,
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"please enter message", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					if (filePath.length() > 0) {
						if (isOnline()) {
							replyWithVoice(msg, position, filePath);
						} else {
							Toast.makeText(getApplicationContext(),
									"" + Constant.network_error,
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"please record an audio", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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

				layoutAudio.setVisibility(View.VISIBLE);
				relativeProgress.setVisibility(View.VISIBLE);
				isMessageComposed();
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

				layoutAudio.setVisibility(View.GONE);
				relativeProgress.setVisibility(View.GONE);
				isMessageComposed();
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
		
		editmsgtag.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (layoutAudio.getVisibility() == View.GONE) {
					if (s.length() > 0) {
						btnReply.setEnabled(true);
					}else{
						btnReply.setEnabled(false);
					}
				}
					
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	
	public void isMessageComposed(){
		String msg = editmsgtag.getText().toString();
		if (layoutAudio.getVisibility() == View.GONE) {
			if (msg.length() > 0) {
				btnReply.setEnabled(true);
			} else {
				btnReply.setEnabled(false);
			}
		} else {
			if (filePath.length() > 0) {
				btnReply.setEnabled(true);
			} else {
				btnReply.setEnabled(false);
			}
		}
	}

	public void replyWithVoice(final String message, final int position,
			final String filePath) {

		progressDialog = ProgressDialog.show(ReplyMessageActivity.this, null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String resp = "";
				String PassTo = ""
						+ LLCApplication.getVoicemailList().get(position)
								.get("UserID");

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
							+ "&msgtag=" + query +"&MessageID="+LLCApplication.getVoicemailList().get(position).get("ID");

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
		progressDialog = ProgressDialog.show(ReplyMessageActivity.this, null,
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
		File file = new File(filepath, Constant.File_Name);
		if (!file.exists()) {
			file.mkdirs();
		}

		filePath = filepath + "/" + System.currentTimeMillis() + ".3gp";
		return filePath;
	}

	@SuppressLint("NewApi")
	private void startRecording() {

		txtRecording.setVisibility(View.VISIBLE);
		progressBar.setProgress(0);
		progressBar.setMax(90);
		
		btnReply.setEnabled(false);

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
		
		btnReply.setEnabled(true);

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

				RectLength=""+seconds;
				Log.i("RectLength", "" + RectLength);
			}
		});

	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(ReplyMessageActivity.this, "Error: " + what + ", " + extra,
					Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(ReplyMessageActivity.this,
					"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
					.show();
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
		layoutAudio.setEnabled(true);
		layoutMessage.setEnabled(true);
		editmsgtag.setEnabled(true);
		btnRecord.setEnabled(true);
		btnPlay.setEnabled(true);
		btnStop.setEnabled(true);
		progressBar.setEnabled(true);
		btnReply.setEnabled(true);
		btnCancel.setEnabled(true);
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(false);
		relativeBack.setEnabled(false);
		layoutAudio.setEnabled(false);
		layoutMessage.setEnabled(false);
		editmsgtag.setEnabled(false);
		btnRecord.setEnabled(false);
		btnPlay.setEnabled(false);
		btnStop.setEnabled(false);
		progressBar.setEnabled(false);
		btnReply.setEnabled(false);
		btnCancel.setEnabled(false);
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
