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
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.fragments.HomeFragment;
import com.rmrdevelopment.lifelineconnect.fragments.HomeMenuListFragment;
import com.rmrdevelopment.lifelineconnect.slidingmenu.lib.SlidingMenu;
import com.rmrdevelopment.lifelineconnect.utils.Constant;
import com.rmrdevelopment.lifelineconnect.utils.RestClient;

public class HomeSlidingFragmentActivity extends BaseHomeActivity {

	//ProgressDialog progressDialog;
	private String respSaveSettings = "";
	
	
	
	public HomeSlidingFragmentActivity() {
		super(R.string.app_name);
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSlidingMenu().setMode(SlidingMenu.RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		setContentView(R.layout.content_frame);
		
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new HomeFragment()).commit();

		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two1);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame_two1, new HomeMenuListFragment())
				.commit();
	}
	
	
	public void switchContent(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}
	
	public void SaveSettings() {
		// TODO Auto-generated method stub
		/*progressDialog= ProgressDialog.show(this, null,
				"Saving...	", true, false);*/
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String deviceToken = ""
						+ Secure.getString(HomeSlidingFragmentActivity.this.getContentResolver(),
								Secure.ANDROID_ID);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "SaveSettings");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("ReceiveNotifications",
						"" + LLCApplication.getReceiveNotifications());
				map.put("token", "" + deviceToken);
				Log.i("map", "" + map);
				respSaveSettings = callAPI(map);
				Log.i("respSaveSettings", "" + respSaveSettings);
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

				/*if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}*/

				if (respSaveSettings != null) {
					int status = 0;
					try {
						JSONObject json_str = new JSONObject(respSaveSettings);
						status = json_str.getInt("Status");
						if(status!=1){
							Toast.makeText(HomeSlidingFragmentActivity.this, "Not able to save settings.", Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
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

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
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
