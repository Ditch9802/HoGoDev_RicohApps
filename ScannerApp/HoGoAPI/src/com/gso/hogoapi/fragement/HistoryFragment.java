package com.gso.hogoapi.fragement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import retrofit.client.Response;
import rx.Subscription;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.MainActivity;
import com.gso.hogoapi.R;
import com.gso.hogoapi.adapter.HistoryAdapter;
import com.gso.hogoapi.model.Document;
import com.gso.hogoapi.model.History;
import com.gso.hogoapi.model.HistoryDetail;
import com.gso.hogoapi.model.PackageDistributionHeaderResponse;
import com.gso.hogoapi.model.Recipient;
import com.gso.hogoapi.model.ResponseHistory;
import com.gso.hogoapi.service.Api;
import com.gso.hogoapi.service.ApiImpl;
import com.gso.hogoapi.util.JsonHelper;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;

public class HistoryFragment extends Fragment implements IServiceListener {

	private static final String TAG = HistoryFragment.class.getSimpleName();
	private final Api api = new ApiImpl();
	private HistoryAdapter adapter;
	private AsyncTask<Void, Void, List<History>> task;
	private ProgressDialog mDialog;
	private List<History> data;
	private Subscription subscription;
	private Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_history, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mContext =getActivity();
		((MainActivity)mContext).setScreenTitle(mContext.getResources().getString(R.string.title_activity_send_history));
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View empty = view.findViewById(R.id.tvEmpty);
		ListView listView = (ListView) view.findViewById(R.id.list_view);
		listView.setEmptyView(empty);
		adapter = new HistoryAdapter(getActivity().getApplicationContext());
		listView.setAdapter(adapter);
		
		subscription = ViewObservable.text((EditText) view.findViewById(R.id.etSearch), false)
			.debounce(400, TimeUnit.MILLISECONDS)
			.map(new Func1<EditText, String>() {

				@Override
				public String call(EditText arg0) {
					return arg0.getText().toString();
				}
			})
			.observeOn(Schedulers.newThread())
			.map(new Func1<String, List<History>>() {

				@Override
				public List<History> call(String arg0) {
					return search(arg0);
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Action1<List<History>>() {

				@Override
				public void call(List<History> arg0) {
					adapter.changeDataSet(arg0);
				}
			});
		
	}
	
	private List<History> search(String query) {
		Log.d(TAG, "Query: " + query);
		if(data == null || data.size() == 0) {
			return null;
		}
		
		final List<History> result = new ArrayList<History>();
		for (History history : data) {
			if(TextUtils.isEmpty(query) || history.recipientEmail.contains(query)) {
				result.add(history);
			}
		}
		return result;
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
		if(subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
		
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private void refreshHistory() {
		final String sEcho = "2";
		final String displayStart = "0";
		final String displayLength = "10";
		final String Type = "405,406,407,411,412";
		final String StartDate = "01/01/1970 00:00:00";
		final String StopDate = sdf.format(new Date());
		final String SessionID = HoGoApplication.instace().getToken(
				getActivity().getApplicationContext());
		task = new AsyncTask<Void, Void, List<History>>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mDialog = ProgressDialog.show(getActivity(), null, "loading...",false, true);
			}
			@Override
			protected List<History> doInBackground(Void... params) {
				ResponseHistory responseHistory = api.getAllHistory(sEcho, displayStart, displayLength,
						Type, StartDate, StopDate, SessionID);
				if (responseHistory != null && "OK".equals(responseHistory.status)) {
                    List<History> result = new ArrayList<History>();
					for (History history : responseHistory.history_detail) {
						PackageDistributionHeaderResponse packageDistributionHeaderResponse = api.getPackageDistributionHeader(SessionID, history.packID);
						if("OK".equals(packageDistributionHeaderResponse.status)) {
							Response response = api.getPackageDistributionDetail(SessionID, history.packID);
							if(response == null || response.getStatus() != 200) {
								Log.d("HistoryFragment", "responseStatus: " + (response == null ? "" : response.getStatus()));
								return null;
							}
							try {
								String jsonString = readStream(response.getBody().in());
								JSONObject jsonObject = JsonHelper.fromJsonString(jsonString);
								if(jsonObject == null) return null;
								String status = JsonHelper.getValue(jsonObject, "status");
								if("OK".equals(status)) {
									JSONObject detailObject = JsonHelper.getJsonObject(jsonObject, "detail");
									if(detailObject == null) {
										return null;
									}

									for (Document document : packageDistributionHeaderResponse.documents) {
										JSONObject documentObject = JsonHelper.getJsonObject(detailObject, document.id);
										if(documentObject == null) continue;
										for (Recipient recipient: packageDistributionHeaderResponse.recipients) {
											JSONObject recipientObject = JsonHelper.getJsonObject(documentObject, recipient.id);
											if(recipientObject == null) continue;
											
											HistoryDetail historyDetail = JsonHelper.fromJson(recipientObject.toString(), HistoryDetail.class);
											if(historyDetail==null) continue;
											
											History item = new History();
											item.historyID = history.historyID; 
											item.historyType = history.historyType;
											item.packID = history.packID;
											item.documentName =  document.title;
											item.recipientName = recipient.name;
											item.recipientEmail = recipient.email;
											item.createDate = history.createDate;
											item.point = history.point;
											item.paymentType = history.paymentType;
											item.total = history.total;
											item.status = historyDetail.status;
											item.num_of_download = historyDetail.num_of_download;
											item.be_download_id = historyDetail.be_download_id;
											item.opened_status = historyDetail.opened_status;
											item.opened_date = historyDetail.opened_date;
											result.add(item);
										}
									}	

								}
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							
						}
					}
                    return result;
				}
				return null;
			}

			private String readStream(InputStream in) throws IOException {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            if (in != null) {
	              byte[] buf = new byte[2048];
	              int r;
	              while ((r = in.read(buf)) != -1) {
	                baos.write(buf, 0, r);
	              }
	            }
	            return new String(baos.toByteArray());
			}
			@Override
			protected void onPostExecute(List<History> result) {
				super.onPostExecute(result);
				if(mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}
				if (result != null) {
					adapter.changeDataSet(result);
					data = new ArrayList<History>();
					for (History history : result) {
						data.add(history);
					}
				} else {
					showAlert("Error", "Can not get data");
				}
			}
			
		};
		task.execute();
	}
	
	private void showAlert(String string, String string2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onCompleted(Service service, ServiceResponse result) {
		if (result.getAction() == ServiceAction.ActionGetAllHistory) {

		}
	}

}
