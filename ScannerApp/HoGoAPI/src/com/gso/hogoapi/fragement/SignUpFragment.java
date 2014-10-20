package com.gso.hogoapi.fragement;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gso.hogoapi.APIType;
import com.gso.hogoapi.R;
import com.gso.hogoapi.model.SignUpData;
import com.gso.hogoapi.service.DataParser;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;

public class SignUpFragment extends Fragment implements IServiceListener {

	private EditText etFirstName;
	private EditText etLastName;
	private EditText etCompany;
	private EditText etEmail;
	private EditText etPassword;
	private EditText etPasswordConfirm;
	private TextView tvTermAndCondition;
	private Button btnCreateUser;

	private ProgressDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDialog = new ProgressDialog(getActivity());
		mDialog.setMessage("Processing...");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_signup, container, false);
		etFirstName = (EditText) v.findViewById(R.id.et_first_name);
		etLastName = (EditText) v.findViewById(R.id.et_last_name);
		etCompany = (EditText) v.findViewById(R.id.et_company);
		etEmail = (EditText) v.findViewById(R.id.et_email);
		etPassword = (EditText) v.findViewById(R.id.et_password);
		etPasswordConfirm = (EditText) v.findViewById(R.id.et_password_confirm);
		tvTermAndCondition = (TextView) v
				.findViewById(R.id.tv_terms_and_conditions);
		btnCreateUser = (Button) v.findViewById(R.id.btn_create_user);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		btnCreateUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkInputData()) {
					mDialog.show();
					exeSignUp();
				}
			}
		});
	}

	private boolean checkInputData() {
		boolean valid = true;
		String firstName = etFirstName.getText().toString().trim();
		String lastName = etLastName.getText().toString().trim();
		String email = etEmail.getText().toString();
		String password = etPassword.getText().toString();
		String passwordConfirm = etPasswordConfirm.getText().toString();

		if (firstName.length() == 0) {
			etFirstName.setError("please enter your First Name");
			valid = false;
		} else if (lastName.length() == 0) {
			etLastName.setError("please enter your Last Name");
			valid = false;
		} else if (email.length() == 0
				|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
						.matches()) {
			etEmail.setError("E-mail is not valid");
			valid = false;
		} else if (password.length() < 6 || password.contains(" ")) {
			etPassword
					.setError("Password should be at least 6 characters and must not contain space character");
			valid = false;
		} else if (!password.equals(passwordConfirm)) {
			etPassword.setError("Passwords does not match");
			valid = false;
		}

		return valid;
	}

	private void exeSignUp() {
		Service service = new Service(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ServiceID", "hogo");
		params.put("ServiceKey", "hogo");
		params.put("EmailAddress", etEmail.getText().toString());
		params.put("Password", etPassword.getText().toString());
		params.put("FirstName", etFirstName.getText().toString());
		params.put("MiddleName", "");
		params.put("LastName", etLastName.getText().toString());
		params.put("Company", etCompany.getText().toString());
		params.put("TimeZone", -5);

		service.login(ServiceAction.ActionSignUp, APIType.SIGNUP, params);
	}

	@Override
	public void onCompleted(Service service, ServiceResponse result) {
		mDialog.dismiss();

		boolean status = false;
		String msg = "Failed to create account. Please try again";
		if (result.getAction() == ServiceAction.ActionSignUp
				&& result.isSuccess()) {
			try {
				SignUpData data = new DataParser(true)
						.parseSignUp((String) result.getData());
				status = data.isSuccess();
				if (status)
					msg = "Account created. You will receive activation email.";
				else if (!data.getDesc().equals(""))
					msg = data.getDesc();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		final boolean success = status;
		new AlertDialog.Builder(getActivity()).setMessage(msg).setTitle("")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (success) {
							// navigate to login screen
						}
					}
				}).create().show();

		Log.w("SignUpFragment", result.getData() + " " + result.isSuccess());
	}

}
