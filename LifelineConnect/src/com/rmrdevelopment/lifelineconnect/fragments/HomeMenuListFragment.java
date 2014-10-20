package com.rmrdevelopment.lifelineconnect.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rmrdevelopment.lifelineconnect.LLCApplication;
import com.rmrdevelopment.lifelineconnect.R;
import com.rmrdevelopment.lifelineconnect.activities.BaseActivityClass;
import com.rmrdevelopment.lifelineconnect.activities.DistroListActivity;
import com.rmrdevelopment.lifelineconnect.activities.HelpActivity;
import com.rmrdevelopment.lifelineconnect.activities.HomeSlidingFragmentActivity;
import com.rmrdevelopment.lifelineconnect.activities.MySettingsActivity;

public class HomeMenuListFragment extends Fragment {

	private ListView listview;
	
	private List<String> optionsArrList = new ArrayList<String>();
	public CustomAdapter cAdapter= null;

	
	@SuppressLint("InflateParams") 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}
	

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listview = (ListView) getView().findViewById(R.id.lst);
		
		//Turn On-OFF Notification Set From Prefs
		String strNotificationOption="Notification";
		/*String strNotificationOption= "Turn Off Notification";
		if(!Splash.sharedPreferences.getBoolean("Notification_Flag", true)){
			strNotificationOption= "Turn On Notification";
		}*/
		optionsArrList.clear();
		if(LLCApplication.getCanSeeDownline().equals("0") || 
				LLCApplication.getCanSeeDownline().equals("false")){
			String[] names2 = { "Settings",
					"logged in as " + LLCApplication.getUsername(),
					 "My Settings", "Help",
					"Privacy Policy", "Terms of Service", strNotificationOption, "Close" };
			optionsArrList.clear();
			optionsArrList = new ArrayList<String>(Arrays.asList(names2));
			//listview.setAdapter(new CustomAdapter(names2));
		}
		else{
			String[] names1 = { "Settings",
					"logged in as " + LLCApplication.getUsername(),
					"My Distribution Lists", "My Settings", "Help",
					"Privacy Policy", "Terms of Service", strNotificationOption, "Close" };
			optionsArrList.clear();
			optionsArrList = new ArrayList<String>(Arrays.asList(names1));
			//listview.setAdapter(new CustomAdapter(names1));
		}
		cAdapter= new CustomAdapter(optionsArrList);
		listview.setAdapter(cAdapter);
		
		
	}
	
	

	class CustomAdapter extends BaseAdapter {

		List<String> list;

		public CustomAdapter(List<String> names) {
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
		
		public void update(int pos, String strItem){
			list.set(pos, strItem);
			cAdapter.notifyDataSetChanged();
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

			final TextView txtName = (TextView) row.findViewById(R.id.name);
			txtName.setText("" + list.get(position));
			final ToggleButton  btnNotification= (ToggleButton)row.findViewById(R.id.toggleButtonNotification);
			if(LLCApplication.getCanSeeDownline().equals("0") || 
					LLCApplication.getCanSeeDownline().equals("false")){
				if(position==6){
					btnNotification.setVisibility(View.VISIBLE);
				}else{
					btnNotification.setVisibility(View.GONE);
				}
			}else{
				if(position==7){
					btnNotification.setVisibility(View.VISIBLE);
				}else{
					btnNotification.setVisibility(View.GONE);
				}
			}
			if(LLCApplication.getReceiveNotifications().equals("1")){
				btnNotification.setBackgroundResource(R.drawable.on);
				btnNotification.setChecked(true);
			}else{
				btnNotification.setBackgroundResource(R.drawable.off);
				btnNotification.setChecked(false);
			}
			
			btnNotification.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					Log.d("btnNotification", "Click");
					Log.d("btnNotification isChecked", ""+isChecked);
					
					if (isChecked) {
						
						buttonView
								.setBackgroundResource(R.drawable.on);
						LLCApplication.setReceiveNotifications("1");
					} else {
						buttonView
								.setBackgroundResource(R.drawable.off);
						LLCApplication.setReceiveNotifications("0");
					}
					((HomeSlidingFragmentActivity)getActivity()).SaveSettings();
				}
			});
			
			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.i("RowClick", "position : "+position);
					switch (position) {
					
					case 0:
						BaseActivityClass.baseAct1.toggle();
						break;
					case 1:
						break;
					case 2:
						if(LLCApplication.getCanSeeDownline().equals("0") || 
								LLCApplication.getCanSeeDownline().equals("false")){
							Intent intent6 = new Intent(getActivity(),
									MySettingsActivity.class);
							getActivity().startActivity(intent6);
							getActivity().overridePendingTransition(
									R.anim.enter_from_left, R.anim.hold_bottom);
						}
						else{
							Intent intent2 = new Intent(getActivity(),
									DistroListActivity.class);
							getActivity().startActivity(intent2);
							getActivity().overridePendingTransition(
									R.anim.enter_from_left, R.anim.hold_bottom);
						}
						
						break;
					case 3:
						if(LLCApplication.getCanSeeDownline().equals("0") || 
								LLCApplication.getCanSeeDownline().equals("false")){
							Intent intent3 = new Intent(getActivity(), HelpActivity.class);
							intent3.putExtra("pos",0);
							getActivity().startActivity(intent3);
							getActivity().overridePendingTransition(
									R.anim.enter_from_left, R.anim.hold_bottom);
						}
						else{
							Intent intent6 = new Intent(getActivity(),
									MySettingsActivity.class);
							getActivity().startActivity(intent6);
							getActivity().overridePendingTransition(
									R.anim.enter_from_left, R.anim.hold_bottom);
						}
						
						break;
					case 4:
						if(LLCApplication.getCanSeeDownline().equals("0") || 
								LLCApplication.getCanSeeDownline().equals("false")){
							Intent intent4 = new Intent(getActivity(), HelpActivity.class);
							intent4.putExtra("pos",1);
							getActivity().startActivity(intent4);
							getActivity().overridePendingTransition(
									R.anim.enter_from_left, R.anim.hold_bottom);
						}
						else{
							Intent intent3 = new Intent(getActivity(), HelpActivity.class);
							intent3.putExtra("pos",0);
							getActivity().startActivity(intent3);
							getActivity().overridePendingTransition(
									R.anim.enter_from_left, R.anim.hold_bottom);
						}
						break;
					case 5:
						if(LLCApplication.getCanSeeDownline().equals("0") || 
								LLCApplication.getCanSeeDownline().equals("false")){
							Intent intent5 = new Intent(getActivity(), HelpActivity.class);
							intent5.putExtra("pos",2);
							getActivity().startActivity(intent5);
							getActivity().overridePendingTransition(
									R.anim.enter_from_left, R.anim.hold_bottom);
						}
						else{
							Intent intent4 = new Intent(getActivity(), HelpActivity.class);
							intent4.putExtra("pos",1);
							getActivity().startActivity(intent4);
							getActivity().overridePendingTransition(
									R.anim.enter_from_left, R.anim.hold_bottom);
						}
						break;
					case 6:
						if(LLCApplication.getCanSeeDownline().equals("0") || 
								LLCApplication.getCanSeeDownline().equals("false")){
							//Turn On-OFF Notification
							
						}
						else{
							Intent intent5 = new Intent(getActivity(), HelpActivity.class);
							intent5.putExtra("pos",2);
							getActivity().startActivity(intent5);
							getActivity().overridePendingTransition(
									R.anim.enter_from_left, R.anim.hold_bottom);
						}
						
						break;
					case 7:
						if(LLCApplication.getCanSeeDownline().equals("0") || 
								LLCApplication.getCanSeeDownline().equals("false")){
							BaseActivityClass.baseAct1.toggle();
						}
						else{
							//Turn On-OFF Notification
							
						}
						break;
					case 8:
						BaseActivityClass.baseAct1.toggle();
						break;
					default:
						break;
					}

				}
			});
			return row;
		}

	}

}
