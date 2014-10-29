package com.gso.hogoapi.fragement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gso.hogoapi.APIType;
import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.MainActivity;
import com.gso.hogoapi.R;
import com.gso.hogoapi.adapter.AddressBookAdapter;
import com.gso.hogoapi.model.AddressBookItem;
import com.gso.hogoapi.model.ResponseData;
import com.gso.hogoapi.model.TransferData;
import com.gso.hogoapi.service.DataParser;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;

public class AddressBookFragement extends DialogFragment implements IServiceListener {

	private ListView lvFriends;
	public List<AddressBookItem> addressBookSelected;
	private Context context;
	private List<AddressBookItem> addressList;
//	private ProgressBar prBar;
	private Button btnDone;
	private AddressBookAdapter adapter;
	private EditText etSearch;
	private String mMailTo;

	public AddressBookFragement(String mailTo) {
		// TODO Auto-generated constructor stub
		mMailTo = mailTo;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.address_fragment, container, false);
        context = getActivity();
//		prBar = (ProgressBar) v.findViewById(R.id.prgBar);
        lvFriends = (ListView) v.findViewById(R.id.lvFriends);
        btnDone = (Button) v.findViewById(R.id.btn_done);
        addressBookSelected = new ArrayList<AddressBookItem>();
        v.findViewById(R.id.cbxTag).setVisibility(View.GONE);
        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onDoneClicked(v);
            }
        });
        exeGetData(null);
        final FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final FrameLayout.LayoutParams contentViewLP = new FrameLayout.LayoutParams(800,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentViewLP.gravity = Gravity.CENTER;
        frameLayout.addView(v, contentViewLP);
        
        etSearch = (EditText)v.findViewById(R.id.et_search_addressbook);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if(arg1 == EditorInfo.IME_ACTION_SEARCH){
					exeSearch(arg0.getText().toString());
					InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 
		                                      InputMethodManager.RESULT_UNCHANGED_SHOWN);
					return true;
				}
				return false;
			}
		});
        return frameLayout;
    }

	protected void exeSearch(String string) {
		// TODO Auto-generated method stub
		List<AddressBookItem> datas = new ArrayList<AddressBookItem>();
		bindAddressBookData(datas);
		exeGetData(string);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	public void onDoneClicked(View v) {

		// TODO Auto-generated method stub
		addressList = getItemSelected();
		Bundle b = new Bundle();
		TransferData data = new TransferData();
		data.setList(addressList);
		b.putSerializable("data", data);
		Intent intent = new Intent();
		intent.putExtras(b);
//		this.setArguments(b);
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
		dismiss();
	}

	private List<AddressBookItem> getItemSelected() {
		// TODO Auto-generated method stub
		List<AddressBookItem> list = new ArrayList<AddressBookItem>();
		if (adapter != null) {
			List<AddressBookItem> dataListView = adapter.getData();
			if (dataListView != null) {
				for (AddressBookItem item : dataListView) {
					if (item.isSelected()) {
						list.add(item);
					}
				}
			}
		}
		return list;
	}

	private void exeGetData(String searchString) {
		// TODO Auto-generated method stub
//		setProgressBarShowing(true);
		Service service = new Service(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SessionID", HoGoApplication.instace().getToken(getActivity()));
		params.put("DisplayLength", "" + 20);
		params.put("DisplayStart", "0");
		params.put("sEcho", "0");
		if(searchString !=null){
			params.put("SearchString",""+searchString);
		}

		service.login(ServiceAction.ActionGetAddressbook, APIType.ADDRESS_BOOK, params);
		((MainActivity) getActivity()).setProgressVisibility(true);
	}

//	private void setProgressBarShowing(boolean b) {
//		// TODO Auto-generated method stub
//		prBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
//	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private List<AddressBookItem> getAddressBook() {
		// TODO Auto-generated method stub
		List<AddressBookItem> list = new ArrayList<AddressBookItem>();
		for (int i = 0; i < 10; i++) {
			AddressBookItem item = new AddressBookItem();
			list.add(item);
		}
		return list;
	}

	private void bindAddressBookData(List<AddressBookItem> friendList) {
		// TODO Auto-generated method stub
		if(adapter == null){
			adapter = new AddressBookAdapter(context, friendList);
			lvFriends.setAdapter(adapter);
		}else{
			adapter.changeData(friendList);
			adapter.notifyDataSetChanged();
		}

		
	}

	public void onChecked(View v) {
		CheckBox cbx = (CheckBox) v;
		Log.d("onChecked", "onChecked" + cbx.isChecked());
		if (cbx.isChecked()) {
			addressList.add((AddressBookItem) v.getTag());
		} else {
			addressList.remove((AddressBookItem) v.getTag());
		}
	}

	@Override
	public void onCompleted(Service service, ServiceResponse result) {
		// TODO Auto-generated method stub
		if (result.isSuccess() && result.getAction() == ServiceAction.ActionGetAddressbook) {
			Log.d("onCompleted", "onCompleted " +result.getAction()+ result.getData());
			DataParser parser = new DataParser(true);
			ResponseData resData = parser.parseAddressBookResponse((String) result.getData());

			if (resData.getStatus().equalsIgnoreCase("OK")) {
				List<AddressBookItem> addressBookList = (List<AddressBookItem>) resData.getData();
				addressBookList = filterList(addressBookList);
				bindAddressBookData(addressBookList);
			} else if (resData.getStatus().equalsIgnoreCase("SessionIdNotFound")) {
				HoGoApplication.instace().setToken(getActivity(), null);
				((MainActivity) getActivity()).gotologinScreen();
			} else {
				Toast.makeText(getActivity(), "Get address Fail", Toast.LENGTH_LONG).show();
				// ((MainActivity) getActivity()).setProgressVisibility(false);
			}
		} else {
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "Get address Fail", Toast.LENGTH_LONG).show();
				}
			});
			
		}
//		setProgressBarShowing(false);
		((MainActivity) getActivity()).setProgressVisibility(false);
	}
	private List<AddressBookItem> filterList(
			List<AddressBookItem> addressBookList) {
		List<AddressBookItem> result = new ArrayList<AddressBookItem>();
		// TODO Auto-generated method stub
		for(AddressBookItem item: addressBookList){
			if(!mMailTo.contains(item.getEmail())){
				result.add(item);
			}
		}
		return result;
	}
}
