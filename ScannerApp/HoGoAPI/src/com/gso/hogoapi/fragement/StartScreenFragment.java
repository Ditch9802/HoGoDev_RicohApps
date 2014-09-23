package com.gso.hogoapi.fragement;

import com.gso.hogoapi.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class StartScreenFragment extends Fragment implements OnClickListener {

	public static final int BUTTON_SEND = 0;
	public static final int BUTTON_HISTORY = 1;
	public static final int BUTTON_ABOUT = 2;

	private Button btnSend;
	private Button btnHistory;
	private Button btnAbout;

	private OnStartScreenListener mListener;

	public interface OnStartScreenListener {
		public void onStartScreenButtonClicked(int button);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnStartScreenListener) activity;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_start_screen, container,
				false);
		btnAbout = (Button) v.findViewById(R.id.btn_about_start_screen);
		btnHistory = (Button) v.findViewById(R.id.btn_history_start_screen);
		btnSend = (Button) v.findViewById(R.id.btn_send_start_screen);

		btnAbout.setOnClickListener(this);
		btnHistory.setOnClickListener(this);
		btnSend.setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v) {
		if (mListener == null)
			return;

		int id = v.getId();
		if (id == btnAbout.getId())
			mListener.onStartScreenButtonClicked(BUTTON_ABOUT);
		else if (id == btnSend.getId())
			mListener.onStartScreenButtonClicked(BUTTON_SEND);
		else if (id == btnHistory.getId())
			mListener.onStartScreenButtonClicked(BUTTON_HISTORY);
	}
}
