package com.rmrdevelopment.lifelineconnect.fragments;

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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.activities.BaseActivityClass;
import com.rmrdevelopment.lifelineconnect.activities.PassdownMessageActivity;
import com.rmrdevelopment.lifelineconnect.activities.ReplyMessageActivity;
import com.rmrdevelopment.lifelineconnect.utils.Constant;
import com.rmrdevelopment.lifelineconnect.utils.RestClient;

public class MDMenuListFragment extends Fragment {

	private ListView listview;
	
	private String response;
	private JSONObject json_str;
	private ProgressDialog progressDialog;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listview = (ListView) getView().findViewById(R.id.lst);
		
		/*
		 * if (LLCApplication.getVoicemailList()
		 * .get(LLCApplication.getCurrentDownlinePosition())
		 * .get("CanReply").equals("false")) { String[] names1 = { "Pass Up",
		 * "Pass Down", "Individual Pass", "Mark as New", "Archive", "Delete",
		 * "Close" }; listview.setAdapter(new CustomAdapter(names1)); } else {
		 * String[] names2 = { "Pass Up", "Pass Down", "Reply",
		 * "Individual Pass", "Mark as New", "Archive", "Delete", "Close" };
		 * listview.setAdapter(new CustomAdapter(names2)); }
		 */

		ArrayList<String> names = new ArrayList<String>();

		if (LLCApplication.getVoicemailList()
				.get(LLCApplication.getCurrentDownlinePosition())
				.get("CanPassUp") != null) {
			if (LLCApplication.getVoicemailList().get(LLCApplication.getCurrentDownlinePosition())
					.get("CanPassUp").equals("true")
					|| LLCApplication.getVoicemailList()
							.get(LLCApplication.getCurrentDownlinePosition())
							.get("CanPassUp").equals("1")) {
				names.add("Pass Up");
			}
		} else {
			names.add("Pass Up");
		}

		if (LLCApplication.getVoicemailList()
				.get(LLCApplication.getCurrentDownlinePosition())
				.get("CanPassDown").equals("true")
				|| LLCApplication.getVoicemailList()
						.get(LLCApplication.getCurrentDownlinePosition())
						.get("CanPassDown").equals("1")) {
			names.add("Pass Down");
		}

		if (LLCApplication.getVoicemailList()
				.get(LLCApplication.getCurrentDownlinePosition())
				.get("CanReply").equals("true")
				|| LLCApplication.getVoicemailList()
						.get(LLCApplication.getCurrentDownlinePosition())
						.get("CanReply").equals("1")) {
			names.add("Reply");
		}
		
		if (LLCApplication.getCanSeeDownline().equals("true")
				|| LLCApplication.getCanSeeDownline().equals("1")) {
			names.add("Individual Pass");
		}

		names.add("Mark as New");
		names.add("Archive");
		names.add("Delete");
		names.add("Close");

		listview.setAdapter(new CustomAdapter(names));

	}

	class CustomAdapter extends BaseAdapter {

		ArrayList<String> list;

		public CustomAdapter(ArrayList<String> names) {
			// TODO Auto-generated constructor stub
			list = names;
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

		@SuppressLint({ "NewApi", "InflateParams" })
		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View row = convertView;
			if (row == null) {
				row = getActivity().getLayoutInflater().inflate(
						R.layout.taskslist_row, null);
			}

			TextView txtName = (TextView) row.findViewById(R.id.name);
			txtName.setText("" + list.get(position));

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					if (list.get(position).equals("Pass Up")) {
						if (MessageDetailsFragment.mediaPlayer != null
								&& MessageDetailsFragment.mediaPlayer.isPlaying()) {
							MessageDetailsFragment.mediaPlayer.pause();
							MessageDetailsFragment.updateProgress = false;
						}
						Intent intent = new Intent(getActivity(),
								PassdownMessageActivity.class);
						intent.putExtra("pos", LLCApplication.getPosition());
						intent.putExtra("msgType", 1);
						startActivity(intent);
						getActivity().overridePendingTransition(
								R.anim.enter_from_left, R.anim.hold_bottom);
					} else if (list.get(position).equals("Pass Down")) {
						if (MessageDetailsFragment.mediaPlayer != null
								&& MessageDetailsFragment.mediaPlayer.isPlaying()) {
							MessageDetailsFragment.mediaPlayer.pause();
							MessageDetailsFragment.updateProgress = false;
						}
						Intent intent1 = new Intent(getActivity(),
								PassdownMessageActivity.class);
						intent1.putExtra("pos", LLCApplication.getPosition());
						intent1.putExtra("msgType", 0);
						startActivity(intent1);
						getActivity().overridePendingTransition(
								R.anim.enter_from_left, R.anim.hold_bottom);
					} else if (list.get(position).equals("Reply")) {
						if (MessageDetailsFragment.mediaPlayer != null
								&& MessageDetailsFragment.mediaPlayer.isPlaying()) {
							MessageDetailsFragment.mediaPlayer.pause();
							MessageDetailsFragment.updateProgress = false;
						}
						Intent intent2 = new Intent(getActivity(),
								ReplyMessageActivity.class);
						intent2.putExtra("pos", LLCApplication.getPosition());
						getActivity().startActivity(intent2);
						getActivity().overridePendingTransition(
								R.anim.enter_from_left, R.anim.hold_bottom);
					} else if (list.get(position).equals("Individual Pass")) {
						if (MessageDetailsFragment.mediaPlayer != null
								&& MessageDetailsFragment.mediaPlayer.isPlaying()) {
							MessageDetailsFragment.mediaPlayer.pause();
							MessageDetailsFragment.updateProgress = false;
						}
						Intent intent3 = new Intent(getActivity(),
								PassdownMessageActivity.class);
						intent3.putExtra("pos", LLCApplication.getPosition());
						intent3.putExtra("msgType", 2);
						startActivity(intent3);
						getActivity().overridePendingTransition(
								R.anim.enter_from_left, R.anim.hold_bottom);
					} else if (list.get(position).equals("Mark as New")) {
						
						AlertDialog.Builder alert1 = new AlertDialog.Builder(
								getActivity());
						alert1.setTitle(Constant.Alert_Name);
						alert1.setMessage("Are you sure you want to mark as new this Message?");
						alert1.setPositiveButton("YES",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();

										if (isOnline()) {
											SetMessageAsUnListened();
										} else {
											Toast.makeText(
													getActivity(),
													"" + Constant.network_error,
													Toast.LENGTH_SHORT).show();
										}

									}
								});
						alert1.setNegativeButton("NO",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}
								});
						alert1.create();
						alert1.show();
					} else if (list.get(position).equals("Archive")) {
						
						AlertDialog.Builder alert = new AlertDialog.Builder(
								getActivity());
						alert.setTitle(Constant.Alert_Name);
						alert.setMessage("Are you sure you want to archive this Message?");
						alert.setPositiveButton("YES",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();

										if (isOnline()) {
											ArchiveMessage(LLCApplication.getPosition());
										} else {
											Toast.makeText(
													getActivity(),
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
					} else if (list.get(position).equals("Delete")) {
						
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
					} else if (list.get(position).equals("Close")) {
						BaseActivityClass.baseAct.toggle();
					}

				}
			});

			return row;
		}
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
				map.put("MessageID",""+ LLCApplication.getVoicemailList().get(position).get("ID"));

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
						if (MessageDetailsFragment.mediaPlayer != null
								&& MessageDetailsFragment.mediaPlayer.isPlaying()) {
							MessageDetailsFragment.mediaPlayer.pause();
							MessageDetailsFragment.updateProgress = false;
						}

						getActivity().finish();
						getActivity().overridePendingTransition(
								R.anim.hold_top, R.anim.exit_in_left);
					}
				}
			}
		});
	}

	protected void ArchiveMessage(final int position) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(getActivity(), null,
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
						if (MessageDetailsFragment.mediaPlayer != null
								&& MessageDetailsFragment.mediaPlayer.isPlaying()) {
							MessageDetailsFragment.mediaPlayer.pause();
							MessageDetailsFragment.updateProgress = false;
						}

						getActivity().finish();
						getActivity().overridePendingTransition(
								R.anim.hold_top, R.anim.exit_in_left);

					}
				}

			}
		});
	}

	protected void SetMessageAsUnListened() {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(getActivity(), null,
				"Loading...	", true, false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Action", "SetMessageAsUnListened");
				map.put("User_ID", "" + LLCApplication.getUserId());
				map.put("MessageID",
						""
								+ LLCApplication.getVoicemailList()
										.get(LLCApplication.getPosition())
										.get("ID"));

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
						LLCApplication.getVoicemailList()
								.get(LLCApplication.getPosition())
								.put("ListenedTo", "0");

						if (MessageDetailsFragment.mediaPlayer != null
								&& MessageDetailsFragment.mediaPlayer.isPlaying()) {
							MessageDetailsFragment.mediaPlayer.pause();
							MessageDetailsFragment.updateProgress = false;
						}

						getActivity().finish();
						getActivity().overridePendingTransition(
								R.anim.hold_top, R.anim.exit_in_left);
					}
				}
			}
		});
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
