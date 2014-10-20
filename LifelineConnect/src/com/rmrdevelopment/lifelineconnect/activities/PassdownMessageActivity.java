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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.utils.Constant;

@SuppressLint("NewApi")
public class PassdownMessageActivity extends BaseActivityClass {

	private Context mContext = this;
	
	private Typeface type;
	
	private EditText editMessage;
	private EditText editName;
	
	private int position;
	private int msgType = 0;
	

	private Button btnPass;
	private Button btnHome;
	private Button btnSearch;
	
	private RelativeLayout relativeList;
	private RelativeLayout layoutIndividual;
	
	private ListView listview;
	private ListView searchedListview;
	
	private TextView txtTag;
	private TextView txtUpline;
	
	private String msgStr = "";
	private String strtags = "";
	private String strIDs = "0";
	private String memberUserID = "";

	private RelativeLayout relativeBack;
	private RelativeLayout relativeInfo;
	
	private TextView txtInfo;
	private TextView title;
	
	private Animation fade_in;
	private Animation fade_out;
	
	private Button btnInfo;
	private Button btnClose;
	private Button btnCancel;

	// Recording audio;
	private MediaRecorder recorder = null;
	private String filePath = "";
	private String RectLength = "";
	private MediaPlayer mediaPlayer;
	private MyCountDownTimer timer;
	private MyCountDownTimer1 timer1;
	private ProgressBar progressBar;
	private boolean flagRecording = false;
	
	private Button btnRecord;
	private Button btnStop;
	private Button btnPlay;
	
	private TextView txtRecording;
	private TextView txtSec;
	private TextView txtVoice;
	private TextView txtMessage;
	
	private LinearLayout layoutVoice;
	private LinearLayout layoutMessage;
	private LinearLayout layoutAudio;
	private RelativeLayout relativeProgress; 
	private int passdownValue=1;

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

		if(LLCApplication.getCanVoiceTag().equalsIgnoreCase("0")){
			layoutVoice.setVisibility(View.GONE);
			layoutVoice.setBackgroundColor(Color.parseColor("#ffffff"));
			layoutMessage.setBackgroundColor(Color.parseColor("#00aacf"));
			txtVoice.setTextColor(Color.parseColor("#000000"));
			txtMessage.setTextColor(Color.parseColor("#ffffff"));

			layoutAudio.setVisibility(View.GONE);
			relativeProgress.setVisibility(View.GONE);
		}
		
		if(layoutAudio.getVisibility() == View.VISIBLE){
			txtTag.setVisibility(View.GONE);
			layoutIndividual.setVisibility(View.GONE);
			txtUpline.setVisibility(View.GONE);
			
			layoutVoice.setBackgroundColor(Color.parseColor("#00aacf"));
			layoutMessage.setBackgroundColor(Color.parseColor("#ffffff"));
			txtVoice.setTextColor(Color.parseColor("#ffffff"));
			txtMessage.setTextColor(Color.parseColor("#000000"));

			layoutAudio.setVisibility(View.VISIBLE);
			relativeProgress.setVisibility(View.VISIBLE);
		}
		
		if(msgType==0){//passdown
			txtTag.setVisibility(View.VISIBLE);
			layoutIndividual.setVisibility(View.GONE);
			txtUpline.setVisibility(View.GONE);
		}else if(msgType==1){//passup
			txtTag.setVisibility(View.GONE);
			layoutIndividual.setVisibility(View.GONE);
			txtUpline.setVisibility(View.VISIBLE);
		}else if(msgType==2){//individual
			txtTag.setVisibility(View.GONE);
			layoutIndividual.setVisibility(View.VISIBLE);
			txtUpline.setVisibility(View.GONE);
		}
		
		btnPass.setEnabled(false);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		super.onDestroy();
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
		relativeBack = (RelativeLayout) findViewById(R.id.relback);
		title = (TextView) findViewById(R.id.title);
		editMessage = (EditText) findViewById(R.id.msgtag);
		btnPass = (Button) findViewById(R.id.btnpass);
		title.setTypeface(type);
		btnHome = (Button) findViewById(R.id.btnhome);

		btnSearch = (Button) findViewById(R.id.btnsearch);
		editName = (EditText) findViewById(R.id.editname);
		layoutIndividual = (RelativeLayout) findViewById(R.id.layoutIndividual);
		searchedListview = (ListView) findViewById(R.id.searchlist);

		relativeList = (RelativeLayout) findViewById(R.id.listlayout);
		listview = (ListView) findViewById(R.id.lst);
		btnCancel = (Button) findViewById(R.id.btncancel);
		txtTag = (TextView) findViewById(R.id.txttag);
		txtUpline = (TextView) findViewById(R.id.txtUpline);

		relativeInfo = (RelativeLayout) findViewById(R.id.infolayout);
		btnInfo = (Button) findViewById(R.id.btninfo);
		btnClose = (Button) findViewById(R.id.btnclose);
		txtInfo = (TextView) findViewById(R.id.txtinfo);

		//new for Recording 
		relativeProgress = (RelativeLayout) findViewById(R.id.r4);
		layoutAudio = (LinearLayout) findViewById(R.id.layoutAudio);
		layoutVoice = (LinearLayout) findViewById(R.id.layoutVoice);
		layoutMessage = (LinearLayout) findViewById(R.id.layoutMessage);
		txtVoice = (TextView) findViewById(R.id.txtVoice);
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		btnRecord = (Button) findViewById(R.id.btnrecord);
		btnPlay = (Button) findViewById(R.id.btnplay);
		btnStop = (Button) findViewById(R.id.btnstop);
		txtRecording = (TextView) findViewById(R.id.txtrecording);
		txtSec = (TextView) findViewById(R.id.txtsec);

		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(200);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(200);

		if (msgType == 0) {
			txtInfo.setText("" + Constant.passdownMessageInfo);
			title.setText("Pass Down Message");
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
			layoutIndividual.setVisibility(View.GONE);
			txtUpline.setVisibility(View.GONE);
			passdownValue= 1;
		} else if (msgType == 1) {
			txtInfo.setText("" + Constant.passupMessageInfo);
			title.setText("Pass Up Message");
			msgStr = "Are you sure you want to passup this Message?";
			txtTag.setVisibility(View.GONE);
			layoutIndividual.setVisibility(View.GONE);
			txtUpline.setVisibility(View.VISIBLE);
			txtUpline.setText("Your upline is: " + LLCApplication.getUplineName());
			passdownValue= 0;
		} else if (msgType == 2) {
			txtInfo.setText("" + Constant.IndividualPassInfo);
			title.setText("Individual Pass Message");
			msgStr = "Are you sure you want to pass individual this Message?";
			txtTag.setVisibility(View.GONE);
			layoutIndividual.setVisibility(View.VISIBLE);
			txtUpline.setVisibility(View.GONE);
			passdownValue= 0;
		}

		listview.setAdapter(new CustomAdapter(LLCApplication.getDistroLists()));

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
				Intent intent = new Intent(PassdownMessageActivity.this,
						HomeSlidingFragmentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
			}
		});

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);

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
				relativeList.setVisibility(View.VISIBLE);
				btnHome.setVisibility(View.GONE);
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
				btnHome.setVisibility(View.VISIBLE);
				relativeList.setVisibility(View.GONE);
				relativeList.startAnimation(fade_out);
				enableComponents();
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
				if(msgType==0){//passdown
					txtTag.setVisibility(View.VISIBLE);
					layoutIndividual.setVisibility(View.GONE);
					txtUpline.setVisibility(View.GONE);
				}else if(msgType==1){//passup
					txtTag.setVisibility(View.GONE);
					layoutIndividual.setVisibility(View.GONE);
					txtUpline.setVisibility(View.VISIBLE);
				}else if(msgType==2){//individual
					txtTag.setVisibility(View.GONE);
					layoutIndividual.setVisibility(View.VISIBLE);
					txtUpline.setVisibility(View.GONE);
				}
				
				layoutVoice.setBackgroundColor(Color.parseColor("#ffffff"));
				layoutMessage.setBackgroundColor(Color.parseColor("#00aacf"));
				txtVoice.setTextColor(Color.parseColor("#000000"));
				txtMessage.setTextColor(Color.parseColor("#ffffff"));

				layoutAudio.setVisibility(View.GONE);
				relativeProgress.setVisibility(View.GONE);
				isMessageComposed();
			}
		});

		btnInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				btnHome.setVisibility(View.GONE);
				relativeInfo.setVisibility(View.VISIBLE);
				relativeInfo.startAnimation(fade_in);
				disableComponents();
			}
		});

		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				btnHome.setVisibility(View.VISIBLE);
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
				if(mediaPlayer!=null){
					if(mediaPlayer.isPlaying()){
						Toast.makeText(PassdownMessageActivity.this, "Please, stop playing first.", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle(Constant.Alert_Name);
				alert.setMessage("" + msgStr);
				alert.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

						
						String msg = editMessage.getText().toString();
						if(LLCApplication.getCanVoiceTag().equalsIgnoreCase("0")){
							draftTextMessage(msg);
						}else{
							if(layoutAudio.getVisibility() == View.GONE){
								draftTextMessage(msg);
							}else{
								draftVoiceMessage(msg);
							}
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
					
					btnPass.setEnabled(true);
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

				btnPass.setEnabled(false);
				
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
						
						btnPass.setEnabled(true);
					}
				});

			}
		});
		
		editMessage.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (layoutAudio.getVisibility() == View.GONE) {
					if (s.length() > 0) {
						btnPass.setEnabled(true);
					}else{
						btnPass.setEnabled(false);
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
		String msg = editMessage.getText().toString();
		if (layoutAudio.getVisibility() == View.GONE) {
			if (msg.length() > 0) {
				btnPass.setEnabled(true);
			} else {
				btnPass.setEnabled(false);
			}
		} else {
			if (filePath.length() > 0) {
				btnPass.setEnabled(true);
			} else {
				btnPass.setEnabled(false);
			}
		}
	}
	
	public void draftVoiceMessage(String msg){
		if (filePath.length() > 0) {
			if (isOnline()) {
				if (msgType==2 && memberUserID.length() <= 0) {
					Toast.makeText(
							getApplicationContext(),
							"Select atleast one member.",
							Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if(msgType==0 && strIDs.length() <= 0){
					Toast.makeText(
							getApplicationContext(),
							"Select atleast one Downline team.",
							Toast.LENGTH_SHORT)
							.show();
					return;
				}
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
	
	public void draftTextMessage(String msg){
		if (msg.length() > 0) {
			if (isOnline()) {
				if (msgType == 0) {
					PassDown(msg, position);
				} else if (msgType == 1) {
					PassUp(msg, position);
				} else if (msgType == 2) {
					if (memberUserID.length() > 0) {
						PassToTeamNumber(msg, position);
					} else {
						Toast.makeText(
								getApplicationContext(),
								"Select atleast one member.",
								Toast.LENGTH_SHORT)
								.show();
					}
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

	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, Constant.File_Name);
		if (!file.exists()) {
			file.mkdirs();
		}

		filePath = filepath + "/" + System.currentTimeMillis() + ".3gp";
		return filePath;
	}

	//new for Recording audio
	@SuppressLint("NewApi")
	private void startRecording() {

		txtRecording.setVisibility(View.VISIBLE);
		progressBar.setProgress(0);
		progressBar.setMax(90);
		
		btnPass.setEnabled(false);

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
		
		btnPass.setEnabled(true);

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
			Toast.makeText(PassdownMessageActivity.this, "Error: " + what + ", " + extra,
					Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(PassdownMessageActivity.this,
					"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
					.show();
		}
	};


	protected void SearchUsers(final String searchString) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(PassdownMessageActivity.this, null,
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
					btnHome.setVisibility(View.GONE);
				}
			}
		});
	}

	protected void PassDown(final String message, final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(PassdownMessageActivity.this, null,
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
		progressDialog = ProgressDialog.show(PassdownMessageActivity.this, null,
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

				Log.e("PassUpMap", ">>>" + map);
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
		progressDialog = ProgressDialog.show(PassdownMessageActivity.this, null,
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
				map.put("PassTo", "" + memberUserID);

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

	public void replyWithVoice(final String message, final int position,
			final String filePath) {

		progressDialog = ProgressDialog.show(PassdownMessageActivity.this, null,
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
							+ RectLength + "&PassDown="+passdownValue+"&PassTo=" + PassTo
							+ "&msgtag=" + query + "&MessageID="+ LLCApplication.getVoicemailList()
									.get(position).get("ID");
					
					//For pass up Don't need to send PassTo
					if(msgType==0){//passdown
						url = Constant.sSiteUrl
								+ "desktopmodules/teamtalkapp/uploadmessage.aspx?os=android&User_ID="
								+ LLCApplication.getUserId() + "&RecLength="
								+ RectLength + "&PassDown="+passdownValue+"&PassTo=" + strIDs
								+ "&msgtag=" + query + "&MessageID="+ LLCApplication.getVoicemailList()
										.get(position).get("ID");
					}else if(msgType==1){ //passup
						url = Constant.sSiteUrl
								+ "desktopmodules/teamtalkapp/uploadmessage.aspx?os=android&User_ID="
								+ LLCApplication.getUserId() + "&RecLength="
								+ RectLength + "&PassDown="+passdownValue //"&PassTo=" + PassTo
								+ "&msgtag=" + query + "&MessageID="+ LLCApplication.getVoicemailList()
										.get(position).get("ID");	
					}else if(msgType==2){ //Individual
						url = Constant.sSiteUrl
								+ "desktopmodules/teamtalkapp/uploadmessage.aspx?os=android&User_ID="
								+ LLCApplication.getUserId() + "&RecLength="
								+ RectLength + "&PassDown="+passdownValue+"&PassTo=" + memberUserID
								+ "&msgtag=" + query + "&MessageID="+ LLCApplication.getVoicemailList()
										.get(position).get("ID");
					}
					
					Log.e("replyWithVoice msgType="+msgType, ">> "+url);
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
				Log.e("Resp Upload", "" + resp);
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

	protected void enableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(true);
		relativeBack.setEnabled(true);
		editMessage.setEnabled(true);
		//btnPass.setEnabled(true);
		txtTag.setEnabled(true);
		isMessageComposed();
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(false);
		relativeBack.setEnabled(false);
		editMessage.setEnabled(false);
		btnPass.setEnabled(false);
		txtTag.setEnabled(false);
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

		@SuppressLint("InflateParams") @Override
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
					btnHome.setVisibility(View.VISIBLE);
					relativeList.startAnimation(fade_out);
					enableComponents();
					editName.setText("" + list.get(pos).get("FullName"));
				}
			});

			return row;
		}
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

		@SuppressLint("InflateParams") @Override
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

	static class ViewHolder {
		protected TextView name, isdownline;
		protected ImageView img;
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
