package com.gso.hogoapi;

import com.gso.hogoapi.fragement.LoginFragment;
import com.gso.hogoapi.fragement.LoginFragment.OnLoginFragmentListener;
import com.gso.hogoapi.fragement.SignUpFragment;
import com.gso.hogoapi.fragement.StartScreenFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class HomeActivity extends FragmentActivity implements OnLoginFragmentListener{

	private final String TAG_FRAGMENT_LOGIN = "TAG_FRAGMENT_LOGIN";
	private final String TAG_FRAGMENT_SIGNUP = "TAG_FRAGMENT_SIGNUP";
	private final String TAG_FRAGMENT_START_SCREEN = "TAG_FRAGMENT_START_SCREEN";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.content_frame);

		if(arg0 == null) {
			addFragment(TAG_FRAGMENT_LOGIN, false);
		}
	}

	private void addFragment(String tag, boolean backward) {
		Fragment fragContent = null;
		if (tag == TAG_FRAGMENT_LOGIN) {
			fragContent = new LoginFragment();
		} else if (tag == TAG_FRAGMENT_SIGNUP) {
			fragContent = new SignUpFragment();
		} else if (tag == TAG_FRAGMENT_START_SCREEN) {
			fragContent = new StartScreenFragment();
		}

		if (fragContent != null) {
			FragmentTransaction mTransaction = getSupportFragmentManager()
					.beginTransaction();
			mTransaction.replace(R.id.content_frame, fragContent, tag);
			if(backward)
				mTransaction.addToBackStack(null);

			mTransaction.commit();
		}
	}

	@Override
	public void onCreateAccount() {
		addFragment(TAG_FRAGMENT_SIGNUP, true);
	}

	@Override
	public void onStartScreen() {
		addFragment(TAG_FRAGMENT_START_SCREEN, false);
	}
}
