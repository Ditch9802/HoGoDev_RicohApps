package com.gso.hogoapi.fragement;

import java.io.File;

import com.artifex.mupdf.MuPDFFragment;
import com.gso.hogoapi.R;
import com.gso.hogoapi.model.FileUpload;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class PreviewFragment extends MuPDFFragment implements OnClickListener {

	private FileUpload mFileUpload;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mFileUpload = (FileUpload) bundle.getSerializable("file");
		core = openFile(Uri.decode(mFileUpload.getPdfPath()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_preview, null);

		if (core == null) {
			ImageView imgPreview = (ImageView) v.findViewById(R.id.img_preview);
			imgPreview.setVisibility(View.VISIBLE);
			Picasso.with(getActivity())
					.load(new File(mFileUpload.getJpgPath())).into(imgPreview);
		} else {
			FrameLayout imgPreviewContainer = (FrameLayout) v
					.findViewById(R.id.img_preview_container);
			View superView = super.onCreateView(inflater, imgPreviewContainer,
					savedInstanceState);
			imgPreviewContainer.addView(superView, new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
		}

		v.findViewById(R.id.btn_next_preview).setOnClickListener(this);
		v.findViewById(R.id.btn_back_preview).setOnClickListener(this);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_next_preview) {

		} else if (v.getId() == R.id.btn_next_preview) {

		}
	}

}
