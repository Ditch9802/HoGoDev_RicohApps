package com.gso.hogoapi.fragement;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.R;
import com.gso.hogoapi.adapter.HistoryAdapter;
import com.gso.hogoapi.model.ResponseHistory;
import com.gso.hogoapi.service.Api;
import com.gso.hogoapi.service.ApiImpl;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;

public class HistoryFragment extends Fragment implements IServiceListener {

	private final Api api = new ApiImpl();
	private HistoryAdapter adapter;
	private AsyncTask<Void, Void, ResponseHistory> task;
	private ProgressDialog mDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_history, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View empty = view.findViewById(R.id.tvEmpty);
		ListView listView = (ListView) view.findViewById(R.id.list_view);
		listView.setEmptyView(empty);
		adapter = new HistoryAdapter(getActivity().getApplicationContext());
		listView.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshHistory();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(mDialog != null) {
			mDialog.dismiss();	
		}
		
	}

	private void refreshHistory() {
		final String sEcho = "2";
		final String displayStart = "0";
		final String displayLength = "10";
		final String Type = "405,406,407,411,412";
		final String StartDate = "01/01/1970 00:00:00";
		final String StopDate = "09/21/2014 23:59:59";
		final String SessionID = HoGoApplication.instace().getToken(
				getActivity().getApplicationContext());
		task = new AsyncTask<Void, Void, ResponseHistory>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mDialog = ProgressDialog.show(getActivity(), null, "loading...",false, true);
			}
			@Override
			protected ResponseHistory doInBackground(Void... params) {
				return api.getAllHistory(sEcho, displayStart, displayLength,
						Type, StartDate, StopDate, SessionID);
			}

			@Override
			protected void onPostExecute(ResponseHistory result) {
				super.onPostExecute(result);
				if(mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}
				if (result != null && "OK".equals(result.status)) {
					adapter.changeDataSet(result.history_detail);
				}
			}
		};
		task.execute();
	}

	@Override
	public void onCompleted(Service service, ServiceResponse result) {
		if (result.getAction() == ServiceAction.ActionGetAllHistory) {

		}
	}

}
