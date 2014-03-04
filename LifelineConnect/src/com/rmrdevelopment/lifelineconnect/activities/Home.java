package com.rmrdevelopment.lifelineconnect.activities;

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
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.utils.Constant;
import com.rmrdevelopment.lifelineconnect.utils.RestClient;

public class Home extends Fragment {

	FragmentManager fm;
	Button btnfromdownline, btnfromupline;
	RelativeLayout relativeFromDownline, relativeFromUpline, relativeArchive,
			relativeSendMessage;

	RelativeLayout relativeInfo, relativeLogin;
	TextView txtInfo;
	Animation fade_in, fade_out;
	Button btnInfo, btnClose, btnSettings;
	public static Typeface type;
	TextView txtFromDownline, txtFromUpline, txtArchive, txtSendMessage;
	TextView title;

	// BaseActivity:
	Context context = getActivity();
	String response;
	JSONObject json_str;
	String Valid;
	String strRequest = null;
	String data_array;
	JSONArray array = null;
	ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Left", "onCreateView()");
		return inflater.inflate(R.layout.home, container, false);
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		fm = getFragmentManager();
		fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {

			@Override
			public void onBackStackChanged() {
				Log.i("getBackStackEntryCount", ""
						+ getFragmentManager().getBackStackEntryCount());
				if (getFragmentManager().getBackStackEntryCount() == 0) {
					getActivity().finish();
				}
			}
		});

		init();
		clickEvents();
	}

	private void init() {
		// TODO Auto-generated method stub
		title = (TextView) getActivity().findViewById(R.id.title);
		relativeLogin = (RelativeLayout) getActivity().findViewById(
				R.id.rellogin);
		btnfromdownline = (Button) getActivity().findViewById(
				R.id.btnfromdownline);
		btnfromupline = (Button) getActivity().findViewById(R.id.btnfromupline);

		txtFromDownline = (TextView) getActivity().findViewById(
				R.id.txtfromdownline);
		txtFromUpline = (TextView) getActivity().findViewById(
				R.id.txtfromupline);
		txtArchive = (TextView) getActivity().findViewById(R.id.txtarchive);
		txtSendMessage = (TextView) getActivity().findViewById(
				R.id.txtsendmessage);

		relativeFromDownline = (RelativeLayout) getActivity().findViewById(
				R.id.relativefromdownline);
		relativeFromUpline = (RelativeLayout) getActivity().findViewById(
				R.id.relativefromupline);
		relativeArchive = (RelativeLayout) getActivity().findViewById(
				R.id.relativearchive);
		relativeSendMessage = (RelativeLayout) getActivity().findViewById(
				R.id.relativesendmessage);

		relativeInfo = (RelativeLayout) getActivity().findViewById(
				R.id.infolayout);
		btnInfo = (Button) getActivity().findViewById(R.id.btninfo);
		btnClose = (Button) getActivity().findViewById(R.id.btnclose);
		btnSettings = (Button) getActivity().findViewById(R.id.btnsettings);
		txtInfo = (TextView) getActivity().findViewById(R.id.txtinfo);
		txtInfo.setText(Constant.welcomeInfo);

		fade_in = new AlphaAnimation(0.0f, 1.0f);
		fade_in.setDuration(1000);
		fade_out = new AlphaAnimation(1.0f, 0.0f);
		fade_out.setDuration(1000);

		type = Typeface.createFromAsset(getActivity().getAssets(), "font.ttf");
		txtFromDownline.setTypeface(type);
		txtFromUpline.setTypeface(type);
		txtArchive.setTypeface(type);
		txtSendMessage.setTypeface(type);

		title.setTypeface(type);

		btnfromdownline.setText("" + LLCApplication.getCountMessagesDownline());
		btnfromupline.setText("" + LLCApplication.getCountMessagesUpline());
	}

	private void clickEvents() {
		// TODO Auto-generated method stub

		btnSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/*
				 * Intent intent = new Intent(getActivity(), DistroList.class);
				 * startActivity(intent);
				 * getActivity().overridePendingTransition
				 * (R.anim.enter_from_left, R.anim.hold_bottom);
				 */
				BaseActivityClass.baseAct1.toggle();
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

		relativeFromDownline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), Downline.class);
				intent.putExtra("tab", "0");
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);
			}
		});

		relativeFromUpline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), Downline.class);
				intent.putExtra("tab", "1");
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);
			}
		});

		relativeArchive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), Downline.class);
				intent.putExtra("tab", "2");
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);
			}
		});

		relativeSendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), SendMessage.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);
			}
		});

		relativeLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(
						getActivity());
				alert.setTitle(Constant.Alert_Name);
				alert.setMessage("Are you sure you want to logout?");
				alert.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

								ContentValues values = new ContentValues();
								values.put("userloggedin", "0");
								Splash.db.update("user", values, "pk=1");

								android.os.Handler hn = new android.os.Handler();
								hn.postDelayed(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method
										// stub
										LLCApplication.setUserloggedin(0);
										getActivity().finish();
										Intent intent = new Intent(
												getActivity(), Login.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
										getActivity()
												.overridePendingTransition(
														R.anim.hold_top,
														R.anim.exit_in_left);
									}
								}, 200);
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

	protected void enableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(true);
		relativeFromDownline.setEnabled(true);
		relativeFromUpline.setEnabled(true);
		relativeArchive.setEnabled(true);
		relativeSendMessage.setEnabled(true);
		relativeLogin.setEnabled(true);
	}

	protected void disableComponents() {
		// TODO Auto-generated method stub
		btnInfo.setEnabled(false);
		relativeFromDownline.setEnabled(false);
		relativeFromUpline.setEnabled(false);
		relativeArchive.setEnabled(false);
		relativeSendMessage.setEnabled(false);
		relativeLogin.setEnabled(false);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isOnline()) {
			GetUnreadMessageCount();
		} else {
			Toast.makeText(getActivity(), "" + Constant.network_error,
					Toast.LENGTH_SHORT).show();
		}
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
		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (response != null) {
						try {
							json_str = new JSONObject(response);

							btnfromdownline.setText(""
									+ json_str
											.getString("countMessagesDownline"));
							btnfromupline.setText(""
									+ json_str.getString("countMessagesUpline"));

						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			});
		}
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
