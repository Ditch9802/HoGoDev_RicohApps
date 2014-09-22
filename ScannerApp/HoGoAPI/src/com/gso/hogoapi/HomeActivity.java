package com.gso.hogoapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jp.co.ricoh.ssdk.sample.app.scan.activity.ScanActivity;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanSampleApplication;
import jp.co.ricoh.ssdk.sample.function.scan.ScanPDF;

import com.gso.hogoapi.fragement.LoginFragment;
import com.gso.hogoapi.fragement.LoginFragment.OnLoginFragmentListener;
import com.gso.hogoapi.fragement.AppScanFragment;
import com.gso.hogoapi.fragement.SignUpFragment;
import com.gso.hogoapi.fragement.StartScreenFragment;
import com.gso.hogoapi.model.FileUpload;
import com.gso.hogoapi.util.pdf.JpegToPDF;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

public class HomeActivity extends ScanActivity implements
		OnLoginFragmentListener {
	
	final boolean isEmulatorMode = true;

	private final String TAG_FRAGMENT_LOGIN = "TAG_FRAGMENT_LOGIN";
	private final String TAG_FRAGMENT_SCAN_SETTINGS = "TAG_FRAGMENT_SCAN_SETTINGS";
	private final String TAG_FRAGMENT_SIGNUP = "TAG_FRAGMENT_SIGNUP";
	private final String TAG_FRAGMENT_START_SCREEN = "TAG_FRAGMENT_START_SCREEN";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.content_frame);

		if (arg0 == null) {
			addFragment(TAG_FRAGMENT_SCAN_SETTINGS, false);
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
	}

	private void addFragment(String tag, boolean backward) {
		Fragment fragContent = null;
		if (tag == TAG_FRAGMENT_LOGIN) {
			fragContent = new LoginFragment();
		} else if (tag == TAG_FRAGMENT_SIGNUP) {
			fragContent = new SignUpFragment();
		} else if (tag == TAG_FRAGMENT_START_SCREEN) {
			fragContent = new StartScreenFragment();
		} else if (tag == TAG_FRAGMENT_SCAN_SETTINGS) {
			fragContent = new AppScanFragment();
		}

		if (fragContent != null) {
			FragmentTransaction mTransaction = getSupportFragmentManager()
					.beginTransaction();
			mTransaction.replace(R.id.content_frame, fragContent, tag);
			if (backward)
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
	
	ScanPDF mScanPDF;
	@Override
	public void onJobCompleted() {
		super.onJobCompleted();
		mScanPDF = new ScanPDF(((ScanSampleApplication) getApplication()).getScanJob());
		/**
		 * Continue by change to send screen. After user click send. You can get
		 * inputStream by call ((MainActivity)getActivity).getPDFInputStream().
		 * */
		new AsyncTask<Void, Void, FileUpload>() {
			@Override
			protected FileUpload doInBackground(Void... params) {
				// How to get inputStream.
				final String localPath = HomeActivity.this.getFilesDir() + "/hogodoc_scan.jpg";
				final String pdfPath = HomeActivity.this.getFilesDir() + "/hogodoc_scan.pdf";			
				Log.d("pdfPath", "pdfPath" + pdfPath);
				InputStream in = null;
				try {
					// Log.d(TAG,"path: " + mScanPDF.getImageFilePath());
					if (isEmulatorMode) {
						// process output image
						write(mScanPDF.getImageInputStream(), localPath);
					} else
					{
						// process output PDF in real device
						write(mScanPDF.getImageInputStream(), pdfPath);
					}

					in = mScanPDF.getImageInputStream();
					if (in != null) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// update ui here
								Toast.makeText(getApplicationContext(),
										"Scan Job Completed! Please wait a moment ...",
										Toast.LENGTH_SHORT).show();
							}
						});
					}

					// Convert output image to PDF if running in Emulator mode
					if (isEmulatorMode) {
						JpegToPDF convert = new JpegToPDF();
						File file = new File(pdfPath);
						FileOutputStream fos = new FileOutputStream(file);
						boolean result = convert.convertJpegToPDF(localPath, fos);
						if (!result) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// update ui here
									Toast.makeText(getApplicationContext(),
											"Cannot convert Image to PDF!", Toast.LENGTH_SHORT)
											.show();
								}
							});
						}
					}

					FileUpload item = new FileUpload();
					item.setPdfPath(pdfPath);
					// item.setJpgPath(localPath);
					return item;
				} catch (IOException e) {
					final String exMessage = e.getMessage();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// update ui here
							Toast.makeText(getApplicationContext(), "Error: " + exMessage,
									Toast.LENGTH_LONG).show();
						}
					});

					e.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(FileUpload result) {
				super.onPostExecute(result);
				if (result != null) {
//					gotoUpdateScreen(result);
				}
			}
		}.execute();

	}
	
	public static void write(InputStream inStream, String output) throws IOException {
		final File outputFile = new File(output);
		final File parent = outputFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		FileOutputStream outStream = new FileOutputStream(outputFile);
		byte[] buf = new byte[1024];
		int l;
		while ((l = inStream.read(buf)) >= 0) {
			outStream.write(buf, 0, l);
		}
		inStream.close();
		outStream.flush();
		outStream.close();
	}
}
