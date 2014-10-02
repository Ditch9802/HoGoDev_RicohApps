package com.gso.hogoapi.fragement;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gso.hogoapi.APIType;
import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.MainActivity;
import com.gso.hogoapi.R;
import com.gso.hogoapi.model.LoginData;
import com.gso.hogoapi.service.DataParser;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;

public class LoginFragment extends Fragment implements IServiceListener {

	/**
	 * @param args
	 */

	private Object mUserName;
	private Object mPassword;
	private boolean mIsKeepMein = true;
	private EditText mEtUsername;
	private EditText mEtUserpassword;
	private Button btnCreateAccount;

	private ProgressDialog mDialog;

	OnLoginFragmentListener mLoginFragmentListener;

	public interface OnLoginFragmentListener {
		public void onCreateAccount();

		public void onStartScreen();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mLoginFragmentListener = (OnLoginFragmentListener) activity;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDialog = new ProgressDialog(getActivity());
		mDialog.setMessage("Processing...");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.login_fragment, container, false);
		mEtUsername = (EditText) v.findViewById(R.id.et_user_name);
		mEtUserpassword = (EditText) v.findViewById(R.id.et_user_password);
		btnCreateAccount = (Button) v.findViewById(R.id.btnLogin);

		// set listeners
		mEtUserpassword.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// do something
//					mLoginFragmentListener.onStartScreen();
					exeLogin();
				}
				return false;
			}
		});
		btnCreateAccount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mLoginFragmentListener.onCreateAccount();
			}
		});

		return v;
	}

	OnKeyListener mKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if ((event.getAction() == KeyEvent.ACTION_DOWN)
					&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
				// onLoginClicked();
				mLoginFragmentListener.onStartScreen();

			}
			// Returning false allows other listeners to react to the press.
			return false;
		}
	};

	public void onLoginClicked() {
		if (checkInputData()) {
			Log.w("Login", "DONE");
			// mDialog.show();
			// exeLogin();
		} else {
			Log.w("Login", "FAILED");
		}

	}

	private boolean checkInputData() {
		// TODO Auto-generated method stub
		boolean isEnough = true;
		Animation shake = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);
		String user = mEtUsername.getText().toString();
		String pass = mEtUserpassword.getText().toString();
		if (user.length() == 0) {
			mEtUsername.setFocusable(true);
			mEtUsername.startAnimation(shake);
			mEtUsername.requestFocus();
			isEnough = false;
		}
		if (pass.length() == 0) {
			mEtUserpassword.setFocusable(true);
			mEtUserpassword.startAnimation(shake);
			mEtUserpassword.requestFocus();
			isEnough = false;
		} else {
		}
		return isEnough;
	}

	private void exeLogin() {
		// TODO Auto-generated method stub
		mUserName = mEtUsername.getText().toString();// "lxanh@tma.com.vn"
		mPassword = "" + md5(mEtUserpassword.getText().toString());// "e10adc3949ba59abbe56e057f20f883e"
		Service service = new Service(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EmailAddress", mUserName);
		params.put("Password", mPassword);
		params.put("KeepMeLogin", mIsKeepMein);
		service.login(ServiceAction.ActionLogin, APIType.LOGIN, params);
	}

	@Override
	public void onCompleted(com.gso.serviceapilib.Service service,
			ServiceResponse result) {
		mDialog.dismiss();

		if (result.isSuccess()
				&& result.getAction() == ServiceAction.ActionLogin) {
			String resultString = (String) result.getData();
			Log.d("onCompleted", "onCompleted " + result.getAction() + " "
					+ resultString);
			if (resultString != null) {
				DataParser parser = new DataParser(true);
				LoginData parseData = (LoginData) parser
						.parseLogin(resultString);
				if (parseData.isStatus()) {
					Toast.makeText(getActivity(), "Login success",
							Toast.LENGTH_LONG).show();
					HoGoApplication.instace().setToken(getActivity(),
							parseData.getToken());

					mLoginFragmentListener.onStartScreen();
				} else {
					Toast.makeText(getActivity(), "Login Fail",
							Toast.LENGTH_LONG).show();
				}
			}

		}
	}

	public static final String md5(final String toEncrypt) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("md5");
			digest.update(toEncrypt.getBytes());
			final byte[] bytes = digest.digest();
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(String.format("%02X", bytes[i]));
			}
			return sb.toString().toLowerCase();
		} catch (Exception exc) {
			return ""; // Impossibru!
		}
	}

}
