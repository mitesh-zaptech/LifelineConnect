package com.rmrdevelopment.lifelineconnect.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rmrdevelopment.lifelineconnect.LLCApplication;

public class SampleListFragment1 extends Fragment {

	ListView listview;
	TextView txtTitle;
	Typeface type;

	String response;
	JSONObject json_str;
	String Valid;
	String strRequest = null;
	String data_array;
	JSONArray array = null;
	ProgressDialog progressDialog;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listview = (ListView) getView().findViewById(R.id.lst);
		txtTitle = (TextView) getView().findViewById(R.id.title);

		type = Typeface.createFromAsset(getActivity().getAssets(), "font.ttf");
		// txtTitle.setTypeface(type);

		String[] names = { "Settings",
				"logged in as " + LLCApplication.getUsername(),
				"My Distribution Lists", "My Settings", "Help",
				"Privacy Policy", "Terms of Service", "Close" };
		listview.setAdapter(new CustomAdapter(names));
	}

	class CustomAdapter extends BaseAdapter {

		String[] list;

		public CustomAdapter(String[] names) {
			// TODO Auto-generated constructor stub
			list = names;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.length;
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
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View row = convertView;
			if (row == null) {
				row = getActivity().getLayoutInflater().inflate(
						R.layout.taskslist_row, null);
			}

			TextView txtName = (TextView) row.findViewById(R.id.name);
			txtName.setText("" + list[position]);

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					switch (position) {
					case 0:
						BaseActivityClass.baseAct1.toggle();
						break;
					case 1:
						break;
					case 2:
						Intent intent2 = new Intent(getActivity(),
								DistroList.class);
						getActivity().startActivity(intent2);
						getActivity().overridePendingTransition(
								R.anim.enter_from_left, R.anim.hold_bottom);
						break;
					case 3:
						Intent intent6 = new Intent(getActivity(),
								MySettings.class);
						getActivity().startActivity(intent6);
						getActivity().overridePendingTransition(
								R.anim.enter_from_left, R.anim.hold_bottom);
						break;
					case 4:
						Intent intent3 = new Intent(getActivity(), Help.class);
						intent3.putExtra("pos",0);
						getActivity().startActivity(intent3);
						getActivity().overridePendingTransition(
								R.anim.enter_from_left, R.anim.hold_bottom);
						break;
					case 5:
						Intent intent4 = new Intent(getActivity(), Help.class);
						intent4.putExtra("pos",1);
						getActivity().startActivity(intent4);
						getActivity().overridePendingTransition(
								R.anim.enter_from_left, R.anim.hold_bottom);
						break;
					case 6:
						Intent intent5 = new Intent(getActivity(), Help.class);
						intent5.putExtra("pos",2);
						getActivity().startActivity(intent5);
						getActivity().overridePendingTransition(
								R.anim.enter_from_left, R.anim.hold_bottom);
						break;
					case 7:
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
