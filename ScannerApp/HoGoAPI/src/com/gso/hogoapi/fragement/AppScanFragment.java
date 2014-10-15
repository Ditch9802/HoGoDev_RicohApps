package com.gso.hogoapi.fragement;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.ricoh.ssdk.sample.app.scan.activity.AddressActivity;
import jp.co.ricoh.ssdk.sample.app.scan.activity.DialogUtil;
import jp.co.ricoh.ssdk.sample.app.scan.activity.PreviewActivity;
import jp.co.ricoh.ssdk.sample.app.scan.activity.ScanFragment;
import jp.co.ricoh.ssdk.sample.app.scan.application.DestinationSettingDataHolder;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanSampleApplication;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanSettingDataHolder;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanStateMachine;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanStateMachine.ScanEvent;
import jp.co.ricoh.ssdk.sample.function.common.impl.AsyncConnectState;
import jp.co.ricoh.ssdk.sample.function.scan.ScanPDF;
import jp.co.ricoh.ssdk.sample.function.scan.ScanService;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanServiceAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.AddressbookDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.AddressbookDestinationSetting.DestinationKind;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.DestinationSettingItem;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.FtpAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.MailAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.NcpAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OccuredErrorLevel;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScannerState;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScannerStateReason;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScannerStateReasons;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.SmbAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.event.ScanServiceAttributeEvent;
import jp.co.ricoh.ssdk.sample.function.scan.event.ScanServiceAttributeListener;

import org.apache.http.entity.mime.content.FileBody;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gso.hogoapi.APIType;
import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.R;
import com.gso.hogoapi.model.FileData;
import com.gso.hogoapi.model.FileUpload;
import com.gso.hogoapi.model.ResponseData;
import com.gso.hogoapi.service.DataParser;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;

public class AppScanFragment extends Fragment implements IServiceListener {

	private final static String TAG = ScanFragment.class.getSimpleName();
	public static boolean IS_PREVIEW;
	/**
	 * Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Â®Ã§Â¨Â®Ã¥Ë†Â¥
	 * Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¨Â­Â¦Ã¥â€˜Å Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡Ã£ï¿½Â«Ã¤Â½Â¿Ã§â€�Â¨Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * Application type Used for setting system warning dialog.
	 */
	private final static String ALERT_DIALOG_APP_TYPE_SCANNER = "SCANNER";

	/**
	 * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â³Ã£Æ’â€”Ã£Æ’Â«Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Â®Ã£â€šÂªÃ£Æ’â€“Ã£â€šÂ¸Ã£â€šÂ§Ã£â€šÂ¯Ã£Æ’Ë†
	 * Application object
	 */
	private ScanSampleApplication mApplication;

	/**
	 * Ã¨Â¨Â­Ã¥Â®Å¡Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½â€¹Ã£â€šâ€°Ã£ï¿½Â®Ã©â‚¬Å¡Ã§Å¸Â¥Ã£â€šâ€™Ã¥ï¿½â€”Ã£ï¿½â€˜Ã¥ï¿½â€“Ã£â€šâ€¹Ã£Æ’â€“Ã£Æ’Â­Ã£Æ’Â¼Ã£Æ’â€°Ã£â€šÂ­Ã£Æ’Â£Ã£â€šÂ¹Ã£Æ’Ë†Ã£Æ’Â¬Ã£â€šÂ·
	 * Ã£Æ’Â¼Ã£Æ’ï¿½Ã£Æ’Â¼ Broadcast receiver to accept intents from setting dialog
	 */
	private BroadcastReceiver mReceiver;

	/**
	 * Ã¨ÂªÂ­Ã¥ï¿½â€“Ã£â€šÂ«Ã£Æ’Â©Ã£Æ’Â¼Ã¨Â¨Â­Ã¥Â®Å¡Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³ Scan color setting button
	 */
	private Button mButtonColor;

	/**
	 * Ã£Æ’â€¢Ã£â€šÂ¡Ã£â€šÂ¤Ã£Æ’Â«Ã¨Â¨Â­Ã¥Â®Å¡Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³ File setting button
	 */
	private Button mButtonFileSetting;

	/**
	 * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã©ï¿½Â¢Ã¨Â¨Â­Ã¥Â®Å¡Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³ Scan side setting button
	 */
	private Button mButtonSide;

	/**
	 * Ã¥Â®â€ºÃ¥â€¦Ë†Ã¨Â¨Â­Ã¥Â®Å¡Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³ Destination setting button
	 */
	private Button mButtonDestination;

	/**
	 * Ã¨ÂªÂ­Ã¥ï¿½â€“Ã©â€“â€¹Ã¥Â§â€¹Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³ Scan start button
	 */
	private RelativeLayout mButtonStart;

	/**
	 * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã§Å Â¶Ã¦â€¦â€¹Ã£Æ’ÂªÃ£â€šÂ¹Ã£Æ’Å Ã£Æ’Â¼ Scan service attribute
	 * listener
	 */
	private ScanServiceAttributeListener mScanServiceAttrListener;

	/**
	 * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã¨Â¨Â­Ã¥Â®Å¡ Scan setting
	 */
	private ScanSettingDataHolder mScanSettingDataHolder;

	/**
	 * Ã¥Â®â€ºÃ¥â€¦Ë†Ã¨Â¨Â­Ã¥Â®Å¡ Destination Setting
	 */
	private DestinationSettingDataHolder mDestSettingDataHolder;

	/**
	 * Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¼Ã£Æ’Ë†Ã£Æ’Å¾Ã£â€šÂ·Ã£Æ’Â³ State machine
	 */
	private ScanStateMachine mStateMachine;

	/**
	 * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã£ï¿½Â¨Ã¦Å½Â¥Ã§Â¶Å¡Ã£ï¿½â„¢Ã£â€šâ€¹Ã£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯ Task to connect with
	 * scan service
	 */
	private ScanServiceInitTask mScanServiceInitTask;

	/**
	 * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã§Å Â¶Ã¦â€¦â€¹Ã¨Â¡Â¨Ã§Â¤ÂºÃ£Æ’Â©Ã£Æ’â„¢Ã£Æ’Â« Scan service state display
	 * label
	 */
	private TextView text_state;

	/**
	 * Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¨Â­Â¦Ã¥â€˜Å Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Å’Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Â¦Ã£ï¿½â€žÃ£â€šâ€¹Ã£ï¿½â€¹Ã£ï¿½Â®Ã£Æ’â€¢Ã£Æ’Â©Ã£â€šÂ° Flag to
	 * indicate if system warning screen is displayed
	 */
	private volatile boolean mAlertDialogDisplayed = false;

	/**
	 * Ã§ï¿½Â¾Ã¥Å“Â¨Ã§â„¢ÂºÃ§â€�Å¸Ã£ï¿½â€”Ã£ï¿½Â¦Ã£ï¿½â€žÃ£â€šâ€¹Ã£â€šÂ¨Ã£Æ’Â©Ã£Æ’Â¼Ã£ï¿½Â®Ã£â€šÂ¨Ã£Æ’Â©Ã£Æ’Â¼Ã£Æ’Â¬Ã£Æ’â„¢Ã£Æ’Â« Level of the
	 * currently occurring error
	 */
	private OccuredErrorLevel mLastErrorLevel = null;

	/**
	 * Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¨Â­Â¦Ã¥â€˜Å Ã§â€�Â»Ã©ï¿½Â¢Ã¨Â¡Â¨Ã§Â¤ÂºÃ£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯ Asynchronous task to request to
	 * display system warning screen
	 */
	private AlertDialogDisplayTask mAlertDialogDisplayTask = null;

	/**
	 * Ã£Æ’Â¡Ã£â€šÂ¤Ã£Æ’Â³Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã¨ÂµÂ·Ã¥â€¹â€¢Ã¦Â¸Ë†Ã£ï¿½Â¿Ã£Æ’â€¢Ã£Æ’Â©Ã£â€šÂ°
	 * trueÃ£ï¿½Â§Ã£ï¿½â€šÃ£â€šÅ’Ã£ï¿½Â°Ã£â‚¬ï¿½Ã£ï¿½â„¢Ã£ï¿½Â§Ã£ï¿½Â«MainActivityÃ£ï¿½Å’Ã¨ÂµÂ·Ã¥â€¹â€¢Ã¦Â¸Ë†Ã£ï¿½Â¿Ã£ï¿½Â§Ã£ï¿½â„¢Ã£â‚¬â€š
	 * MainActivity running flag If true, another Mainactivity instance is
	 * running.
	 */
	private boolean mMultipleRunning = false;
	private CheckBox chkPreview;
	OnFinishedScanningListener mFinishedScanningListener;

	public interface OnFinishedScanningListener {
		public void onFinishedScanning(FileUpload result);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mFinishedScanningListener = (OnFinishedScanningListener) activity;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_scan_settings, container,
				false);
	}

	/**
	 * Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã£ï¿½Å’Ã§â€�Å¸Ã¦Ë†ï¿½Ã£ï¿½â€¢Ã£â€šÅ’Ã£â€šâ€¹Ã£ï¿½Â¨Ã¥â€˜Â¼Ã£ï¿½Â³Ã¥â€¡ÂºÃ£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * [Ã¥â€¡Â¦Ã§ï¿½â€ Ã¥â€ â€¦Ã¥Â®Â¹] (1)Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Â®Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“
	 * (2)Ã¨Â¨Â­Ã¥Â®Å¡Ã£Æ’â€“Ã£Æ’Â­Ã£Æ’Â¼Ã£Æ’â€°Ã£â€šÂ­Ã£Æ’Â£Ã£â€šÂ¹Ã£Æ’Ë†Ã£Æ’Â¬Ã£â€šÂ·Ã£Æ’Â¼Ã£Æ’ï¿½Ã£Æ’Â¼Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡
	 * (3)Ã¥Â®â€ºÃ¥â€¦Ë†Ã¦Å’â€¡Ã¥Â®Å¡Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡
	 * (4)Ã¨ÂªÂ­Ã£ï¿½Â¿Ã¥ï¿½â€“Ã£â€šÅ Ã£â€šÂ«Ã£Æ’Â©Ã£Æ’Â¼Ã©ï¿½Â¸Ã¦Å Å¾Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡
	 * (5)Ã£Æ’â€¢Ã£â€šÂ¡Ã£â€šÂ¤Ã£Æ’Â«Ã¥Â½Â¢Ã¥Â¼ï¿½Ã©ï¿½Â¸Ã¦Å Å¾Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡
	 * (6)Ã¥Å½Å¸Ã§Â¨Â¿Ã©ï¿½Â¢Ã©ï¿½Â¸Ã¦Å Å¾Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡
	 * (7)Ã£ï¿½ï¿½Ã£ï¿½Â®Ã¤Â»â€“Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡ (8)Ã¨ÂªÂ­Ã¥ï¿½â€“Ã©â€“â€¹Ã¥Â§â€¹Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡
	 * (9)Ã¥ï¿½â€žÃ£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£ï¿½Â®Ã§â€žÂ¡Ã¥Å Â¹Ã¥Å’â€“ (10)Ã£Æ’ÂªÃ£â€šÂ¹Ã£Æ’Å Ã£Æ’Â¼Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“
	 * 
	 * Called when an activity is created. [Processes] (1) Initialize
	 * application (2) Set setting broadcast receiver (3) Set destination
	 * setting button (4) Set scan color setting button (5) Set file setting
	 * button (6) Set scan side setting button (7) Set other settings button (8)
	 * Set start button (9) Disable buttons (10) Initialize listener
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (getNumActivities(getActivity().getPackageName()) > 1) {
			Log.i(TAG, "Another MainActivity instance is already running.");
			mMultipleRunning = true;
			getActivity().finish();
			return;
		}

		// text_dest_title = (TextView) view.findViewById(R.id.text_dest_title);
		// img_dest_icon = (ImageView)view.findViewById(R.id.img_dest_icon);

		// (1)
		mApplication = (ScanSampleApplication) getActivity().getApplication();
		mScanServiceAttrListener = new ScanServiceAttributeListenerImpl(
				new Handler());
		mScanSettingDataHolder = mApplication.getScanSettingDataHolder();
		mDestSettingDataHolder = mApplication.getDestinationSettingDataHolder();
		mStateMachine = mApplication.getStateMachine();
		mStateMachine.registActivity(getActivity());
		text_state = (TextView) view.findViewById(R.id.text_state);

		// (2)
		IntentFilter filter = new IntentFilter();
		filter.addAction(DialogUtil.INTENT_ACTION_SUB_ACTIVITY_RESUMED);
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (DialogUtil.INTENT_ACTION_SUB_ACTIVITY_RESUMED
						.equals(action)) {
					startAlertDialogDisplayTask();
				}
			}
		};
		getActivity().registerReceiver(mReceiver, filter);

		// (3)
		mButtonDestination = (Button) view.findViewById(R.id.btn_destination);
		;
		mButtonDestination.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog dialog = DialogUtil.createDestTypeDialog(
						getActivity(), mDestSettingDataHolder);
				DialogUtil.showDialog(dialog);
			}
		});

		// (4)
		mButtonColor = (Button) view.findViewById(R.id.btn_color);
		mButtonColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog dialog = DialogUtil.createColorSettingDialog(
						getActivity(), mScanSettingDataHolder);
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface paramDialogInterface) {
						String label = getResources().getString(
								mScanSettingDataHolder.getSelectedColorLabel());
						mButtonColor.setText(label);
					}
				});
				DialogUtil.showDialog(dialog);
			}
		});

		// (5)
		mButtonFileSetting = (Button) view.findViewById(R.id.btn_file);
		//Hogo edited to use the scanresolution
//		mButtonFileSetting.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				AlertDialog dialog = DialogUtil.createFileSettingDialog(
//						getActivity(), mScanSettingDataHolder);
//				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//					@Override
//					public void onDismiss(DialogInterface paramDialogInterface) {
//						String label = getResources().getString(
//								mScanSettingDataHolder
//										.getSelectedFileSettingLabel());
//						mButtonFileSetting.setText(label);
//					}
//				});
//				DialogUtil.showDialog(dialog);
//			}
//		});
		
		mButtonFileSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				AlertDialog dialog = DialogUtil.createResolutionSettingDialog(
						getActivity(), mScanSettingDataHolder);
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface paramDialogInterface) {
						String label = getResources().getString(
								mScanSettingDataHolder
										.getSelectedResolutionLabel());
						mButtonFileSetting.setText(label);
					}
				});
				DialogUtil.showDialog(dialog);
			}
		});

		// (6)
		mButtonSide = (Button) view.findViewById(R.id.btn_side);
		mButtonSide.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog dialog = DialogUtil.createSideSettingDialog(
						getActivity(), mScanSettingDataHolder);
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface paramDialogInterface) {
						String label = getResources().getString(
								mScanSettingDataHolder.getSelectedSideLabel());
						mButtonSide.setText(label);
					}
				});
				DialogUtil.showDialog(dialog);
			}
		});

		// (7)
		chkPreview = (CheckBox) view.findViewById(R.id.chk_show_preview);

		chkPreview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				IS_PREVIEW = chkPreview.isChecked();
			}
		});
		
		// (8)
		mButtonStart = (RelativeLayout) view.findViewById(R.id.btn_start);
		mButtonStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mStateMachine.procScanEvent(ScanEvent.REQUEST_JOB_START);
			}
		});

		// (9)
		disableSettingKey();
		disableStartKey();

		// (10)
		if (mScanServiceInitTask != null) {
			mScanServiceInitTask.cancel(false);
		}
		mScanServiceInitTask = new ScanServiceInitTask();
		mScanServiceInitTask.execute();

		// send event
		mStateMachine.procScanEvent(ScanEvent.ACTIVITY_CREATED);
	}

	/**
	 * Ã©ï¿½Â·Ã§Â§Â»Ã¥â€¦Ë†Ã£ï¿½Â®Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã£ï¿½â€¹Ã£â€šâ€°Ã£ï¿½Â®Ã§Âµï¿½Ã¦Å¾Å“Ã£â€šâ€™Ã¥ï¿½â€”Ã£ï¿½â€˜Ã¥ï¿½â€“Ã£â€šÅ Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * [Ã¥â€¡Â¦Ã§ï¿½â€ Ã¥â€ â€¦Ã¥Â®Â¹]
	 * (1)Ã£Æ’â€”Ã£Æ’Â¬Ã£Æ’â€œÃ£Æ’Â¥Ã£Æ’Â¼Ã£ï¿½â€¹Ã£â€šâ€°Ã¦Ë†Â»Ã£ï¿½Â£Ã£ï¿½Â¦Ã£ï¿½ï¿½Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½Ã§Âµï¿½Ã¦Å¾Å“Ã£ï¿½Â«Ã¥Â¿
	 * Å“Ã£ï¿½ËœÃ£ï¿½Â¦Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¼Ã£Æ’Ë†Ã£Æ’Å¾Ã£â€šÂ·Ã£Æ’Â³Ã£ï¿½Â«Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†Ã£â€šâ€™Ã©â‚¬ï¿½Ã¤Â¿Â¡Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * (2)Ã¥Â®â€ºÃ¥â€¦Ë†Ã¨
	 * Â¨Â­Ã¥Â®Å¡Ã£ï¿½â€¹Ã£â€šâ€°Ã¦Ë†Â»Ã£ï¿½Â£Ã£ï¿½Â¦Ã£ï¿½ï¿½Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â¯Ã£â‚¬ï¿½Ã§Âµï¿½Ã¦Å¾Å“Ã£ï¿½Â«Ã¥Â¿Å“Ã£ï¿½ËœÃ£ï¿½Â¦Ã¥Â®â€ºÃ¥â€¦Ë†Ã¨Â¡
	 * Â¨Ã§Â¤ÂºÃ¦Â¬â€žÃ£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤ÂºÃ£â€šâ€™Ã¦â€ºÂ´Ã¦â€“Â°Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * 
	 * Receive the result from the activity of the changed state. [Processes]
	 * (1) When returned from preview, sends event to the state machine
	 * accordingly to the result. (2) When returned from destination setting,
	 * updates the display of the destination display area accordingly to the
	 * result.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		// (1)
		if (requestCode == PreviewActivity.REQUEST_CODE_PREVIEW_ACTIVITY) {
			if (resultCode == Activity.RESULT_OK) {
				mStateMachine.procScanEvent(ScanEvent.REQUEST_JOB_END);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				mStateMachine.procScanEvent(ScanEvent.REQUEST_JOB_CANCEL);
			} else {
				/* do nothing */
			}
		}
		// (2)
		else if (requestCode == AddressActivity.REQUEST_CODE_ADDRESS_ACTIVITY) {
			if (resultCode == Activity.RESULT_OK) {
				String keyDisplay = intent
						.getStringExtra(AddressActivity.KEY_DISPLAY);
				updateDestinationLabel(keyDisplay);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				/* do nothing */
			} else {
				/* do nothing */
			}
		} else {
			/* do nothing */
		}
	}

	/**
	 * Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã£ï¿½Â®Ã¥â€ ï¿½Ã©â€“â€¹Ã¦â„¢â€šÃ£ï¿½Â«Ã¥â€˜Â¼Ã£ï¿½Â³Ã¥â€¡ÂºÃ£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * Ã£â€šÂ¨Ã£Æ’Â©Ã£Æ’Â¼Ã£ï¿½Â®Ã§
	 * â„¢ÂºÃ§â€�Å¸Ã¦Å“â€°Ã§â€žÂ¡Ã£â€šâ€™Ã©ï¿½Å¾Ã¥ï¿½Å’Ã¦Å“Å¸Ã£ï¿½Â§Ã¦Â¤Å“Ã¦Å¸Â»Ã£ï¿½â€”Ã£â‚¬ï¿½Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã£ï¿½Â§Ã£ï¿½â€šÃ£â€šÅ’Ã£ï¿½Â°Ã£â€šÂ·Ã£â€š
	 * Â¹Ã£Æ’â€ Ã£Æ’Â Ã¨Â­Â¦Ã¥â€˜Å Ã§â€�Â»Ã©ï¿½Â¢Ã¥Ë†â€¡Ã¦â€ºÂ¿Ã£ï¿½Ë†Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Called when the activity is
	 * resumed. Checks error occurrence asynchronously and switches to a system
	 * warning screen if necessary.
	 */
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();

		if (mMultipleRunning) {
			return;
		}

		startAlertDialogDisplayTask();
	}

	/**
	 * Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã£ï¿½Â®Ã¥ï¿½Å“Ã¦Â­Â¢Ã¦â„¢â€šÃ£ï¿½Â«Ã¥â€˜Â¼Ã£ï¿½Â³Ã¥â€¡ÂºÃ£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¨Â­
	 * Â¦Ã¥â€˜Å Ã§â€�Â»Ã©ï¿½Â¢Ã¨Â¡Â¨Ã§Â¤ÂºÃ£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯Ã£ï¿½Å’Ã¥Â®Å¸Ã¨Â¡Å’Ã¤Â¸Â­Ã£ï¿½Â§Ã£ï¿½â€šÃ£â€šÅ’Ã£ï¿½Â°Ã£â‚¬ï¿½Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’
	 * Â³Ã£â€šÂ»Ã£Æ’Â«Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Called when the activity is stopped. If the system
	 * warning screen display task is in process, the task is cancelled.
	 */
	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();

		stopAlertDialogDisplayTask();
	}

	/**
	 * Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã£ï¿½Å’Ã§Â Â´Ã¦Â£â€žÃ£ï¿½â€¢Ã£â€šÅ’Ã£â€šâ€¹Ã©Å¡â€ºÃ£ï¿½Â«Ã¥â€˜Â¼Ã£ï¿½Â³Ã¥â€¡ÂºÃ£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * [Ã¥â€¡Â¦Ã§ï¿½â€ Ã¥â€ â€¦Ã¥Â®Â¹]
	 * (1)Ã£Æ’Â¡Ã£â€šÂ¤Ã£Æ’Â³Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã§Âµâ€šÃ¤Âºâ€ Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†Ã£â€šâ€™Ã£â€šÂ¹Ã£Æ’
	 * â€ Ã£Æ’Â¼Ã£Æ’Ë†Ã£Æ’Å¾Ã£â€šÂ·Ã£Æ’Â³Ã£ï¿½Â«Ã©â‚¬ï¿½Ã£â€šâ€¹
	 * Ã¨ÂªÂ­Ã¥ï¿½â€“Ã¤Â¸Â­Ã£ï¿½Â§Ã£ï¿½â€šÃ£â€šÅ’Ã£ï¿½Â°Ã£â‚¬ï¿½Ã¨ÂªÂ­Ã¥ï¿½â€“Ã£ï¿½Å’Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â«Ã£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * (2)Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’
	 * â€œÃ£â€šÂ¹Ã£ï¿½â€¹Ã£â€šâ€°Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†Ã£Æ’ÂªÃ£â€šÂ¹Ã£Æ’Å Ã£Æ’Â¼Ã£ï¿½Â¨Ã£Æ’â€“Ã£Æ’Â­Ã£Æ’Â¼Ã£Æ’â€°Ã£â€šÂ­Ã£Æ’Â£Ã£â€šÂ¹Ã£Æ’Ë†Ã£Æ’
	 * Â¬Ã£â€šÂ·Ã£Æ’Â¼Ã£Æ’ï¿½Ã£Æ’Â¼Ã£â€šâ€™Ã©â„¢Â¤Ã¥Å½Â»Ã£ï¿½â„¢Ã£â€šâ€¹
	 * (3)Ã©ï¿½Å¾Ã¥ï¿½Å’Ã¦Å“Å¸Ã£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯Ã£ï¿½Å’Ã¥Â®Å¸Ã¨Â¡Å’Ã¤Â¸Â­Ã£ï¿½Â Ã£ï¿½Â£Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â«Ã£ï¿½â„¢Ã£â€šâ€¹
	 * (4)Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Â®Ã¤Â¿ï¿½Ã¦Å’ï¿½Ã£Æ’â€¡Ã£Æ’Â¼Ã£â€šÂ¿Ã£â€šâ€™Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“Ã£ï¿½â„¢Ã£â€šâ€¹
	 * (5)Ã¥ï¿½â€šÃ§â€¦Â§Ã£â€šâ€™Ã§Â Â´Ã¦Â£â€žÃ£ï¿½â„¢Ã£â€šâ€¹
	 * 
	 * Called when the activity is destroyed. [Processes] (1) Send MainActivity
	 * destoyed event to the state machine. If scanning is in process, scanning
	 * is cancelled. (2) Removes the event listener and the broadcast receiver
	 * from the service. (3) If asynchronous task is in process, the task is
	 * cancelled. (4) Initializes the data saved to the application. (5)
	 * Discards references
	 */
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();

		// if MainActivity another instance is already running, then exit
		// without doing anything
		if (mMultipleRunning) {
			return;
		}

		// (1)
		mStateMachine.procScanEvent(ScanEvent.ACTIVITY_DESTROYED);

		// (2)
		ScanService scanService = mApplication.getScanService();
		try {
			scanService
					.removeScanServiceAttributeListener(mScanServiceAttrListener);
		} catch (IllegalStateException e) {
			/* the listener is not registered. */
		}
		getActivity().unregisterReceiver(mReceiver);

		// (3)
		stopAlertDialogDisplayTask();
		if (mScanServiceInitTask != null) {
			mScanServiceInitTask.cancel(false);
			mScanServiceInitTask = null;
		}

		// (4)
		mApplication.init();

		// (5)
		mApplication = null;
		mReceiver = null;
		mButtonColor = null;
		mButtonFileSetting = null;
		mButtonSide = null;
		mButtonDestination = null;
		mButtonStart = null;
		mScanSettingDataHolder = null;
		mDestSettingDataHolder = null;
		mStateMachine = null;
		mScanServiceAttrListener = null;
	}

	/**
	 * Ã¥Â®â€ºÃ¥â€¦Ë†Ã¨Â¡Â¨Ã§Â¤ÂºÃ£Æ’Â©Ã£Æ’â„¢Ã£Æ’Â«Ã£â€šâ€™Ã¦â€ºÂ´Ã¦â€“Â°Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š [Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½Â®Ã¥Â½Â¢Ã¥Â¼ï¿½]
	 * (1)Ã£Æ’â€¢Ã£â€šÂ©Ã£Æ’Â«Ã£Æ’â‚¬Ã¯Â¼Ë†Ã§â€ºÂ´Ã¦Å½Â¥Ã¥â€¦Â¥Ã¥Å â€ºÃ¯Â¼â€° - Ã£Æ’â€¢Ã£â€šÂ©Ã£Æ’Â«Ã£Æ’â‚¬Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ³Ã£Æ’Â³ Ã¯Â¼â€¹
	 * Ã£Æ’â€¢Ã£â€šÂ©Ã£Æ’Â«Ã£Æ’â‚¬Ã£Æ’â€˜Ã£â€šÂ¹ + Ã¤Â»Â»Ã¦â€žï¿½Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€” (2)Ã£Æ’Â¡Ã£Æ’Â¼Ã£Æ’Â«Ã¯Â¼Ë†Ã§â€ºÂ´Ã¦Å½Â¥Ã¥â€¦Â¥Ã¥Å â€ºÃ¯Â¼â€° -
	 * Ã£Æ’Â¡Ã£Æ’Â¼Ã£Æ’Â«Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ³Ã£Æ’Â³ + Ã£Æ’Â¡Ã£Æ’Â¼Ã£Æ’Â«Ã£â€šÂ¢Ã£Æ’â€°Ã£Æ’Â¬Ã£â€šÂ¹ + Ã¤Â»Â»Ã¦â€žï¿½Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€”
	 * (3)Ã£Æ’â€¢Ã£â€šÂ©Ã£Æ’Â«Ã£Æ’â‚¬Ã¯Â¼Ë†Ã£â€šÂ¢Ã£Æ’â€°Ã£Æ’Â¬Ã£â€šÂ¹Ã¥Â¸Â³Ã©ï¿½Â¸Ã¦Å Å¾Ã¯Â¼â€° - Ã£Æ’â€¢Ã£â€šÂ©Ã£Æ’Â«Ã£Æ’â‚¬Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ³Ã£Æ’Â³ +
	 * Ã¤Â»Â»Ã¦â€žï¿½Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€” (4)Ã£Æ’Â¡Ã£Æ’Â¼Ã£Æ’Â«Ã¯Â¼Ë†Ã£â€šÂ¢Ã£Æ’â€°Ã£Æ’Â¬Ã£â€šÂ¹Ã¥Â¸Â³Ã©ï¿½Â¸Ã¦Å Å¾Ã¯Â¼â€° -
	 * Ã£Æ’Â¡Ã£Æ’Â¼Ã£Æ’Â«Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ³Ã£Æ’Â³ + Ã¤Â»Â»Ã¦â€žï¿½Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€”
	 * 
	 * Updates the destination display label. This method must be called from UI
	 * thread. [Display format] (1) For email: address book - icon (folder) +
	 * folder path + string (2) For email: manual entry - icon (mail) + email
	 * path + string (3) For folder: address book - icon (icon) + string (4) For
	 * folder: manual entry - icon (mail) + string
	 * 
	 * @param optStr
	 *            : Ã¤Â»Â»Ã¦â€žï¿½Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€” Specified string
	 */
	public void updateDestinationLabel(String optStr) {

		DestinationSettingDataHolder destHolder = mApplication
				.getDestinationSettingDataHolder();
		DestinationSettingItem destItem = destHolder
				.getDestinationSettingItem();

		DestinationKind destKind = null;
		String str = null;

		enableStartKey();

		// (1)
		if (destItem instanceof FtpAddressManualDestinationSetting) {

			FtpAddressManualDestinationSetting item = (FtpAddressManualDestinationSetting) destItem;
			destKind = DestinationKind.FOLDER;
			str = item.getPath();

		} else if (destItem instanceof SmbAddressManualDestinationSetting) {

			SmbAddressManualDestinationSetting item = (SmbAddressManualDestinationSetting) destItem;
			destKind = DestinationKind.FOLDER;
			str = item.getPath();

		} else if (destItem instanceof NcpAddressManualDestinationSetting) {

			NcpAddressManualDestinationSetting item = (NcpAddressManualDestinationSetting) destItem;
			destKind = DestinationKind.FOLDER;
			str = item.getPath();

		}
		// (2)
		else if (destItem instanceof MailAddressManualDestinationSetting) {

			MailAddressManualDestinationSetting item = (MailAddressManualDestinationSetting) destItem;
			destKind = DestinationKind.MAIL;
			str = item.getMailAddress();

		}
		// (3)(4)
		else if (destItem instanceof AddressbookDestinationSetting) {

			AddressbookDestinationSetting item = (AddressbookDestinationSetting) destItem;
			destKind = item.getDestinationKind();
			str = "";

		} else {
			disableStartKey();
			/* do nothing */
		}

		if (optStr != null) {
			str = str + " " + optStr;
		}
		updateDestinationLabel(destKind, str);
	}

	/**
	 * Ã¥Â®â€ºÃ¥â€¦Ë†Ã¨Â¡Â¨Ã§Â¤ÂºÃ£Æ’Â©Ã£Æ’â„¢Ã£Æ’Â«Ã£â€šâ€™Ã¦â€ºÂ´Ã¦â€“Â°Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Ã¨Â¡Â¨Ã§Â¤ÂºÃ¥Â½Â¢Ã¥Â¼ï¿½Ã£ï¿½Â¯
	 * Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ³Ã£Æ’Â³(Ã£Æ’â€¢Ã£â€šÂ©Ã£Æ’Â«Ã£Æ’â‚¬/Ã£Æ’Â¡Ã£Æ’Â¼Ã£Æ’Â«Ã¯Â¼â€°Ã¯Â¼â€¹ Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€” Ã£ï¿½Â§Ã£ï¿½â„¢Ã£â‚¬â€š Updates the
	 * destination display label. Display format is
	 * "icon (folder/email) + string".
	 * 
	 * @param destKind
	 * @param str
	 */
	private void updateDestinationLabel(DestinationKind destKind, String str) {
		mButtonDestination.setText(str);

		// if (destKind == DestinationKind.FOLDER) {
		//
		// img_dest_icon.setImageResource(R.drawable.icon_folder_small);
		// text_dest_title.setText(str);
		// img_dest_icon.setVisibility(View.VISIBLE);
		// text_dest_title.setVisibility(View.VISIBLE);
		//
		// } else if (destKind == DestinationKind.MAIL) {
		//
		// img_dest_icon.setImageResource(R.drawable.icon_mail_small);
		// text_dest_title.setText(str);
		// img_dest_icon.setVisibility(View.VISIBLE);
		// text_dest_title.setVisibility(View.VISIBLE);
		//
		// } else {
		// img_dest_icon.setVisibility(View.INVISIBLE);
		// text_dest_title.setVisibility(View.INVISIBLE);
		// }
	}

	/**
	 * Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¨Â­Â¦Ã¥â€˜Å Ã§â€�Â»Ã©ï¿½Â¢Ã¨Â¡Â¨Ã§Â¤ÂºÃ¨Â¦ï¿½Ã¦Â±â€šÃ£ï¿½Â«Ã¦Â¸Â¡Ã£ï¿½â„¢Ã§Å Â¶Ã¦â€¦â€¹Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€”Ã£â€šâ€™Ã§â€�Å¸Ã¦Ë†ï¿½Ã£ï¿½â€”Ã£
	 * ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Creates the state string to be passed to system warning screen
	 * display request.
	 * 
	 * @param state
	 *            Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã§Å Â¶Ã¦â€¦â€¹ State of scan service
	 * @return Ã§Å Â¶Ã¦â€¦â€¹Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€” State string
	 */
	private String makeAlertStateString(ScannerState state) {
		String stateString = "";
		if (state != null) {
			stateString = state.toString();
		}
		return stateString;
	}

	/**
	 * Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¨Â­Â¦Ã¥â€˜Å Ã§â€�Â»Ã©ï¿½Â¢Ã¨Â¡Â¨Ã§Â¤ÂºÃ¨Â¦ï¿½Ã¦Â±â€šÃ£ï¿½Â«Ã¦Â¸Â¡Ã£ï¿½â„¢Ã§Å Â¶Ã¦â€¦â€¹Ã§ï¿½â€ Ã§â€�Â±Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€”Ã£â€šâ€™Ã§â€�
	 * Å¸Ã¦Ë†ï¿½Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * Ã¨Â¤â€¡Ã¦â€¢Â°Ã£ï¿½Â®Ã§Å Â¶Ã¦â€¦â€¹Ã§ï¿½â€ Ã§â€�Â±Ã£ï¿½Å’Ã£ï¿½â€šÃ£ï¿½Â£Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½1Ã£ï¿½Â¤Ã§â€ºÂ®Ã£ï¿½Â®Ã§Å Â¶Ã¦â€¦
	 * â€¹Ã§ï¿½â€ Ã§â€�Â±Ã£ï¿½Â®Ã£ï¿½Â¿Ã£â€šâ€™Ã¦Â¸Â¡Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Creates the state reason string to be
	 * passed to the system warning screen display request. If multiple state
	 * reasons exist, only the first state reason is passed.
	 * 
	 * @param stateReasons
	 *            Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Å Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã§Å Â¶Ã¦â€¦â€¹Ã§ï¿½â€ Ã§â€�Â± Scan service state reason
	 * @return Ã§Å Â¶Ã¦â€¦â€¹Ã§ï¿½â€ Ã§â€�Â±Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€” State reason string
	 */
	private String makeAlertStateReasonString(ScannerStateReasons stateReasons) {
		String reasonString = "";
		if (stateReasons != null) {
			Object[] reasonArray = stateReasons.getReasons().toArray();
			if (reasonArray != null && reasonArray.length > 0) {
				reasonString = reasonArray[0].toString();
			}
		}
		return reasonString;
	}

	/**
	 * Ã¦Å’â€¡Ã¥Â®Å¡Ã£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Å¸Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Å’Ã£Æ’â€¢Ã£â€šÂ©Ã£â€šÂ¢Ã£â€šÂ°Ã£Æ’Â©Ã£Æ’Â³Ã£Æ’â€°Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Â«Ã£
	 * ï¿½â€šÃ£â€šâ€¹Ã£ï¿½â€¹Ã£â€šâ€™Ã¥ï¿½â€“Ã¥Â¾â€”Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Obtains whether or not the specified
	 * application is in the foreground state.
	 * 
	 * @param packageName
	 *            Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Â®Ã£Æ’â€˜Ã£Æ’Æ’Ã£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ¸Ã¥ï¿½ï¿½ Application
	 *            package name
	 * @return Ã£Æ’â€¢Ã£â€šÂ©Ã£â€šÂ¢Ã£â€šÂ°Ã£Æ’Â©Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’â€°Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Â«Ã£ï¿½â€šÃ£â€šâ€¹Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â«true If the
	 *         application is in the foreground state, true is returned.
	 */
	private boolean isForegroundApp(String packageName) {
		boolean result = false;
		ActivityManager am = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : list) {
			if (packageName.equals(info.processName)) {
				result = (info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
				break;
			}
		}
		return result;
	}

	/**
	 * Ã¦Å’â€¡Ã¥Â®Å¡Ã£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Å¸Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Â®Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’Æ’Ã£
	 * â€šÂ¯Ã£ï¿½Â®Ã¦Å“â‚¬Ã¤Â¸Å Ã¤Â½ï¿½Ã£â€šÂ¯Ã£Æ’Â©Ã£â€šÂ¹Ã£â€šâ€™Ã¥ï¿½â€“Ã¥Â¾â€”Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Obtains the top class in the
	 * activity stack of the specified application.
	 * 
	 * @param packageName
	 *            Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Â®Ã£Æ’â€˜Ã£Æ’Æ’Ã£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ¸Ã¥ï¿½ï¿½ Application
	 *            package name
	 * @return Ã¦Å“â‚¬Ã¤Â¸Å Ã¤Â½ï¿½Ã£â€šÂ¯Ã£Æ’Â©Ã£â€šÂ¹Ã£ï¿½Â®FQCNÃ£â€šÂ¯Ã£Æ’Â©Ã£â€šÂ¹Ã¥ï¿½ï¿½.
	 *         Ã¥ï¿½â€“Ã¥Â¾â€”Ã£ï¿½Â§Ã£ï¿½ï¿½Ã£ï¿½ÂªÃ£ï¿½â€žÃ¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â¯null The name of the FQCN class name
	 *         of the top class. If the name cannot be obtained, null is
	 *         returned.
	 */
	private String getTopActivityClassName(String packageName) {
		ActivityManager am = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(30);
		for (RunningTaskInfo info : list) {
			if (packageName.equals(info.topActivity.getPackageName())) {
				return info.topActivity.getClassName();
			}
		}
		return null;
	}

	/**
	 * Ã¦Å’â€¡Ã¥Â®Å¡Ã£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Å¸Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Â®Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’Æ’Ã£
	 * â€šÂ¯Ã¥â€ â€¦Ã£ï¿½Â®Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã¦â€¢Â°Ã£â€šâ€™Ã¥ï¿½â€“Ã¥Â¾â€”Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Obtains the number
	 * of activities in the activity stack of the specified application.
	 * 
	 * @param packageName
	 *            Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Â®Ã£Æ’â€˜Ã£Æ’Æ’Ã£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ¸Ã¥ï¿½ï¿½ Application
	 *            package name
	 * @return Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã¦â€¢Â°. Ã¥ï¿½â€“Ã¥Â¾â€”Ã£ï¿½Â§Ã£ï¿½ï¿½Ã£ï¿½ÂªÃ£ï¿½â€žÃ¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â¯0 The number
	 *         of activitys. If the number cannot be obtained, 0 is returned.
	 */
	private int getNumActivities(String packageName) {
		ActivityManager am = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(30);
		for (RunningTaskInfo info : list) {
			if (packageName.equals(info.topActivity.getPackageName())) {
				return info.numActivities;
			}
		}
		return 0;
	}

	public void onJobCompleted() {
		Log.d(TAG, "onJobCompleted");
		final ScanPDF scanPDF = new ScanPDF(mApplication.getScanJob());
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				final String filePath = scanPDF.getImageFilePath();
				Log.d(TAG, "file path: " + filePath);
				return null;
			}
		}.execute();
	}

	/**
	 * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Å Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã£ï¿½Â®Ã§Å Â¶Ã¦â€¦â€¹Ã¥Â¤â€°Ã¦â€ºÂ´Ã§â€ºÂ£Ã¨Â¦â€“Ã£Æ’ÂªÃ£â€šÂ¹Ã£Æ’Å Ã£Æ’Â¼Ã£ï¿½Â§Ã£ï¿½â„¢Ã£â‚¬â€š
	 * [Ã¥â€¡Â¦Ã§ï¿½â€ Ã¥â€ â€¦Ã¥Â®Â¹]
	 * (1)Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã£ï¿½Â®Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Â«Ã£â€šË†Ã£ï¿½Â£Ã£ï¿½Â¦Ã£â‚¬ï¿½Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’
	 * Â³Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã§Å Â¶Ã¦â€¦â€¹Ã¨Â¡Â¨Ã§Â¤ÂºÃ£Æ’Â©Ã£Æ’â„¢Ã£Æ’Â«Ã£â€šâ€™Ã¦â€ºÂ¸Ã£ï¿½ï¿½Ã¦ï¿½â€ºÃ£ï¿½Ë†Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * (2)Ã£â€šÂ¨Ã£Æ’Â©Ã£Æ’Â¼Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤ÂºÃ£Æ’Â»Ã¦â€ºÂ´Ã¦â€“Â°Ã£Æ’Â»Ã©ï¿½Å¾Ã¨Â¡Â¨Ã§Â¤ÂºÃ¨Â¦ï¿½Ã¦Â±â€šÃ£â€šâ€™Ã¨Â¡Å’Ã£ï¿½â€žÃ£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * The listener class to monitor scan service attribute changes. [Processes]
	 * (1) Rewrites the scan service state display label accordingly to the scan
	 * service state. (2) Requests to display/update/hide error screens.
	 */
	class ScanServiceAttributeListenerImpl implements
			ScanServiceAttributeListener {

		/**
		 * UI thread handler
		 */
		private Handler mHandler;

		ScanServiceAttributeListenerImpl(Handler handler) {
			mHandler = handler;
		}

		@Override
		public void attributeUpdate(final ScanServiceAttributeEvent event) {
			ScannerState state = (ScannerState) event.getAttributes().get(
					ScannerState.class);
			ScannerStateReasons stateReasons = (ScannerStateReasons) event
					.getAttributes().get(ScannerStateReasons.class);
			OccuredErrorLevel errorLevel = (OccuredErrorLevel) event
					.getAttributes().get(OccuredErrorLevel.class);

			String stateLabel = "";

			// (1)
			switch (state) {
			case IDLE:
				Log.d(TAG, "ScannerState : IDLE");
				stateLabel = getString(R.string.txid_scan_t_state_ready);
				break;
			case MAINTENANCE:
				Log.d(TAG, "ScannerState : MAINTENANCE");
				stateLabel = getString(R.string.txid_scan_t_state_maintenance);
				break;
			case PROCESSING:
				Log.d(TAG, "ScannerState : PROCESSING");
				stateLabel = getString(R.string.txid_scan_t_state_scanning);
				break;
			case STOPPED:
				Log.d(TAG, "ScannerState : STOPPED");
				stateLabel = getString(R.string.txid_scan_t_state_stopped);
				break;
			case UNKNOWN:
				Log.d(TAG, "ScannerState : UNKNOWN");
				stateLabel = getString(R.string.txid_scan_t_state_unknown);
				break;
			default:
				Log.d(TAG, "ScannerState : never reach here ...");
				/* never reach here */
				break;
			}

			if (stateReasons != null) {
				Set<ScannerStateReason> reasonSet = stateReasons.getReasons();
				for (ScannerStateReason reason : reasonSet) {
					switch (reason) {
					case COVER_OPEN:
						stateLabel = getString(R.string.txid_scan_t_state_reason_cover_open);
						break;
					case MEDIA_JAM:
						stateLabel = getString(R.string.txid_scan_t_state_reason_media_jam);
						break;
					case PAUSED:
						stateLabel = getString(R.string.txid_scan_t_state_reason_paused);
						break;
					case OTHER:
						stateLabel = getString(R.string.txid_scan_t_state_reason_other);
						break;
					default:
						/* never reach here */
						break;
					}
				}
			}

			// final String result = stateLabel;
			// mHandler.post(new Runnable() {
			// @Override
			// public void run() {
			// text_state.setText(result);
			// }
			// });

			// (2)
			if (OccuredErrorLevel.ERROR.equals(errorLevel)
					|| OccuredErrorLevel.FATAL_ERROR.equals(errorLevel)) {

				String stateString = makeAlertStateString(state);
				String reasonString = makeAlertStateReasonString(stateReasons);

				if (mLastErrorLevel == null) {
					// Normal -> Error
					if (isForegroundApp(getActivity().getPackageName())) {
						mApplication.displayAlertDialog(
								ALERT_DIALOG_APP_TYPE_SCANNER, stateString,
								reasonString);
						mAlertDialogDisplayed = true;
					}
				} else {
					// Error -> Error
					if (mAlertDialogDisplayed) {
						mApplication.updateAlertDialog(
								ALERT_DIALOG_APP_TYPE_SCANNER, stateString,
								reasonString);
					}
				}
				mLastErrorLevel = errorLevel;

			} else {
				if (mLastErrorLevel != null) {
					// Error -> Normal
					if (mAlertDialogDisplayed) {
						String activityName = getTopActivityClassName(getActivity()
								.getPackageName());
						if (activityName == null) {
							activityName = ScanFragment.class.getName();
						}
						mApplication.hideAlertDialog(
								ALERT_DIALOG_APP_TYPE_SCANNER, activityName);
						mAlertDialogDisplayed = false;
					}
				}
				mLastErrorLevel = null;
			}
		}
	}

	/**
	 * Ã©ï¿½Å¾Ã¥ï¿½Å’Ã¦Å“Å¸Ã£ï¿½Â§Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã£ï¿½Â¨Ã£ï¿½Â®Ã¦Å½Â¥Ã§Â¶Å¡Ã£â€šâ€™Ã¨Â¡Å’Ã£ï¿½â€žÃ£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * [Ã¥â€¡Â¦Ã§ï¿½â€ Ã¥â€ â€¦Ã¥Â®Â¹]
	 * (1)Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã£ï¿½Â®Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†Ã£â€šâ€™Ã¥ï¿½â€”Ã¤Â¿Â¡Ã£ï¿½â„¢Ã£â€šâ€¹Ã£Æ’ÂªÃ£
	 * â€šÂ¹Ã£Æ’Å Ã£Æ’Â¼Ã£â€šâ€™Ã¨Â¨Â­Ã¥Â®Å¡Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * Ã¦Â©Å¸Ã¥â„¢Â¨Ã£ï¿½Å’Ã¥Ë†Â©Ã§â€�Â¨Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½Â«Ã£ï¿½ÂªÃ£â€šâ€¹Ã£ï¿½â€¹Ã£â‚¬ï¿½Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’
	 * Â³Ã£â€šÂ»Ã£Æ’Â«Ã£ï¿½Å’Ã¦Å Â¼Ã£ï¿½â€¢Ã£â€šÅ’Ã£â€šâ€¹Ã£ï¿½Â¾Ã£ï¿½Â§Ã£Æ’ÂªÃ£Æ’Ë†Ã£Æ’Â©Ã£â€šÂ¤Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * (2)Ã©ï¿½Å¾Ã¥ï¿½Å’Ã¦Å“Å¸Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†Ã£ï¿½Â®Ã¦Å½Â¥Ã§Â¶Å¡Ã§Â¢ÂºÃ¨Âªï¿½Ã£â€šâ€™Ã¨Â¡Å’Ã£ï¿½â€žÃ£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * Ã¦Å½Â¥Ã§Â¶Å¡Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½Â«Ã£
	 * ï¿½ÂªÃ£â€šâ€¹Ã£ï¿½â€¹Ã£â‚¬ï¿½Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â«Ã£ï¿½Å’Ã¦Å Â¼Ã£ï¿½â€¢Ã£â€šÅ’Ã£â€šâ€¹Ã£ï¿½Â¾Ã£ï¿½Â§Ã£Æ’ÂªÃ£Æ’Ë†Ã£Æ’Â©Ã£â€šÂ¤Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * (3
	 * )Ã¦Å½Â¥Ã§Â¶Å¡Ã£ï¿½Â«Ã¦Ë†ï¿½Ã¥Å Å¸Ã£ï¿½â€”Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â¯Ã£â‚¬ï¿½Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â¼Ã£Æ’â€œÃ£â€šÂ¹Ã£ï¿½â€¹Ã£â€šâ€°Ã¥ï¿½â€žÃ¨Â¨Â­Ã¥Â®
	 * Å¡Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡Ã¥ï¿½Â¯Ã¨Æ’Â½Ã¥â‚¬Â¤Ã£â€šâ€™Ã¥ï¿½â€“Ã¥Â¾â€”Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * 
	 * Connects with the scan service asynchronously. [Processes] (1) Sets the
	 * listener to receive scan service events. This task repeats until the
	 * machine becomes available or cancel button is touched. (2) Confirms the
	 * asynchronous connection. This task repeats until the connection is
	 * confirmed or cancel button is touched. (3) After the machine becomes
	 * available and connection is confirmed, obtains job setting values.
	 */
	class ScanServiceInitTask extends AsyncTask<Void, Void, Integer> {

		AsyncConnectState addListenerResult = null;
		AsyncConnectState getAsyncConnectStateResult = null;

		@Override
		protected Integer doInBackground(Void... params) {

			final ScanService scanService = mApplication.getScanService();

			// (1)
			while (true) {
				if (isCancelled()) {
					return -1;
				}
				addListenerResult = scanService
						.addScanServiceAttributeListener(mScanServiceAttrListener);

				if (addListenerResult == null) {
					sleep(100);
					continue;
				}

				if (addListenerResult.getState() == AsyncConnectState.STATE.CONNECTED) {
					break;
				}

				if (addListenerResult.getErrorCode() == AsyncConnectState.ERROR_CODE.NO_ERROR) {
					// do nothing
				} else if (addListenerResult.getErrorCode() == AsyncConnectState.ERROR_CODE.BUSY) {
					sleep(10000);
				} else if (addListenerResult.getErrorCode() == AsyncConnectState.ERROR_CODE.TIMEOUT) {
					// do nothing
				} else if (addListenerResult.getErrorCode() == AsyncConnectState.ERROR_CODE.INVALID) {
					return 0;
				} else {
					// unknown state
					return 0;
				}
			}

			if (addListenerResult.getState() != AsyncConnectState.STATE.CONNECTED) {
				return 0;
			}

			// (2)
			while (true) {
				if (isCancelled()) {
					return -1;
				}
				getAsyncConnectStateResult = scanService.getAsyncConnectState();

				if (getAsyncConnectStateResult == null) {
					sleep(100);
					continue;
				}

				if (getAsyncConnectStateResult.getState() == AsyncConnectState.STATE.CONNECTED) {
					break;
				}

				if (getAsyncConnectStateResult.getErrorCode() == AsyncConnectState.ERROR_CODE.NO_ERROR) {
					// do nothing
				} else if (getAsyncConnectStateResult.getErrorCode() == AsyncConnectState.ERROR_CODE.BUSY) {
					sleep(10000);
				} else if (getAsyncConnectStateResult.getErrorCode() == AsyncConnectState.ERROR_CODE.TIMEOUT) {
					// do nothing
				} else if (getAsyncConnectStateResult.getErrorCode() == AsyncConnectState.ERROR_CODE.INVALID) {
					return 0;
				} else {
					// unknown state
					return 0;
				}
			}

			// (3)
			if (addListenerResult.getState() == AsyncConnectState.STATE.CONNECTED
					&& getAsyncConnectStateResult.getState() == AsyncConnectState.STATE.CONNECTED) {
				mScanSettingDataHolder.init(scanService);
			}

			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (addListenerResult == null) {
				Log.d(TAG, "addScanServiceAttributeListener:null");
			} else {
				Log.d(TAG, "addScanServiceAttributeListener:"
						+ addListenerResult.getState() + ","
						+ addListenerResult.getErrorCode());
			}
			if (getAsyncConnectStateResult == null) {
				Log.d(TAG, "getAsyncConnectState:null");
			} else {
				Log.d(TAG,
						"getAsyncConnectState:"
								+ getAsyncConnectStateResult.getState() + ","
								+ getAsyncConnectStateResult.getErrorCode());
			}

			if (result != 0) {
				/* canceled. */
				return;
			}

			if (addListenerResult.getState() == AsyncConnectState.STATE.CONNECTED
					&& getAsyncConnectStateResult.getState() == AsyncConnectState.STATE.CONNECTED) {
				// connection succeeded.
				mButtonColor.setText(mScanSettingDataHolder
						.getSelectedColorLabel());
				mButtonFileSetting.setText(mScanSettingDataHolder
						.getSelectedResolutionLabel());
				mButtonSide.setText(mScanSettingDataHolder
						.getSelectedSideLabel());
				enableSettingKey();
				enableStartKey();
				mStateMachine.procScanEvent(ScanEvent.ACTIVITY_BOOT_COMPLETED);
			} else {
				// the connection is invalid.
				mStateMachine.procScanEvent(ScanEvent.ACTIVITY_BOOT_FAILED);
			}
		}

		/**
		 * Ã¦Å’â€¡Ã¥Â®Å¡Ã£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Å¸Ã¦â„¢â€šÃ©â€“â€œÃ£â€šÂ«Ã£Æ’Â¬Ã£Æ’Â³Ã£Æ’Ë†Ã£â€šÂ¹Ã£Æ’Â¬Ã£Æ’Æ’Ã£Æ’â€°Ã£â€šâ€™Ã£â€šÂ¹Ã£Æ’ÂªÃ£Æ’Â¼Ã£Æ’â€”Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢
		 * Ã£â‚¬â€š sleep for the whole of the specified interval
		 */
		private void sleep(long time) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				Log.w(TAG, "", e);
			}
		}
	}

	/**
	 * Ã¨ÂªÂ­Ã¥ï¿½â€“Ã©â€“â€¹Ã¥Â§â€¹Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£â€šâ€™Ã¦Å“â€°Ã¥Å Â¹Ã¥Å’â€“Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Enables the start button.
	 */
	private void enableStartKey() {
		mButtonStart.setEnabled(true);
	}

	/**
	 * Ã¨ÂªÂ­Ã¥ï¿½â€“Ã©â€“â€¹Ã¥Â§â€¹Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£â€šâ€™Ã§â€žÂ¡Ã¥Å Â¹Ã¥Å’â€“Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Disables the start button.
	 */
	private void disableStartKey() {
		mButtonStart.setEnabled(false);
	}

	/**
	 * Ã¥ï¿½â€žÃ¨Â¨Â­Ã¥Â®Å¡Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£â€šâ€™Ã¦Å“â€°Ã¥Å Â¹Ã¥Å’â€“Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Enables setting buttons.
	 */
	private void enableSettingKey() {
		mButtonColor.setEnabled(true);
		mButtonFileSetting.setEnabled(true);
		mButtonSide.setEnabled(true);
		mButtonDestination.setEnabled(true);
	}

	/**
	 * Ã¥ï¿½â€žÃ¨Â¨Â­Ã¥Â®Å¡Ã£Æ’Å“Ã£â€šÂ¿Ã£Æ’Â³Ã£â€šâ€™Ã§â€žÂ¡Ã¥Å Â¹Ã¥Å’â€“Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Disables setting buttons.
	 */
	private void disableSettingKey() {
		mButtonColor.setEnabled(false);
		mButtonFileSetting.setEnabled(false);
		mButtonSide.setEnabled(false);
		mButtonDestination.setEnabled(false);
	}

	/**
	 * Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¨Â­Â¦Ã¥â€˜Å Ã§â€�Â»Ã©ï¿½Â¢Ã¨Â¡Â¨Ã§Â¤ÂºÃ£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯Ã£â€šâ€™Ã©â€“â€¹Ã¥Â§â€¹Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š Starts the
	 * alert dialog display task.
	 */
	private void startAlertDialogDisplayTask() {
		if (mAlertDialogDisplayTask != null) {
			mAlertDialogDisplayTask.cancel(false);
		}
		mAlertDialogDisplayTask = new AlertDialogDisplayTask();
		mAlertDialogDisplayTask.execute();
	}

	/**
	 * Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¨Â­Â¦Ã¥â€˜Å Ã§â€�Â»Ã©ï¿½Â¢Ã¨Â¡Â¨Ã§Â¤ÂºÃ£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯Ã£â€šâ€™Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â«Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
	 * Stop the alert dialog display task.
	 */
	private void stopAlertDialogDisplayTask() {
		if (mAlertDialogDisplayTask != null) {
			mAlertDialogDisplayTask.cancel(false);
			mAlertDialogDisplayTask = null;
		}
	}

	/**
	 * Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¨Â­Â¦Ã¥â€˜Å Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤ÂºÃ¦Å“â€°Ã§â€žÂ¡Ã£â€šâ€™Ã¥Ë†Â¤Ã¦â€“Â­Ã£ï¿½â€”Ã£â‚¬ï¿½Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã£ï¿½ÂªÃ¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â¯Ã¨
	 * Â¡Â¨Ã§Â¤ÂºÃ¨Â¦ï¿½Ã¦Â±â€šÃ£â€šâ€™Ã¨Â¡Å’Ã£ï¿½â€ Ã©ï¿½Å¾Ã¥ï¿½Å’Ã¦Å“Å¸Ã£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯Ã£ï¿½Â§Ã£ï¿½â„¢Ã£â‚¬â€š The asynchronous task to
	 * judge to display system warning screen and to request to display the
	 * screen if necessary.
	 */
	class AlertDialogDisplayTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			ScanServiceAttributeSet attributes = mApplication.getScanService()
					.getAttributes();
			OccuredErrorLevel errorLevel = (OccuredErrorLevel) attributes
					.get(OccuredErrorLevel.class);

			if (OccuredErrorLevel.ERROR.equals(errorLevel)
					|| OccuredErrorLevel.FATAL_ERROR.equals(errorLevel)) {
				ScannerState state = (ScannerState) attributes
						.get(ScannerState.class);
				ScannerStateReasons stateReasons = (ScannerStateReasons) attributes
						.get(ScannerStateReasons.class);

				String stateString = makeAlertStateString(state);
				String reasonString = makeAlertStateReasonString(stateReasons);
				if (isCancelled()) {
					return null;
				}

				mApplication.displayAlertDialog(ALERT_DIALOG_APP_TYPE_SCANNER,
						stateString, reasonString);

				mAlertDialogDisplayed = true;
				mLastErrorLevel = errorLevel;
			}
			return null;
		}

	}

	public void onFinishedScanning(FileUpload result) {
		Log.d("onFinishedScanning", "isPreview:" + chkPreview.isChecked());
//		if (chkPreview.isChecked()) {
//			mFinishedScanningListener.onFinishedScanning(result);
//		} else 
		{
			// upload and encode file
			Service service = new Service(this);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("SessionID",
					HoGoApplication.instace().getToken(getActivity()));
			try {
				String oldFile = getFileNameWithoutExtn(result.getPdfPath());
				// String url = mFileUpload.getPdfPath().replace(oldFile,
				// mEtFilePath.getText());
				File file = new File("" + result.getPdfPath());
				if (file.exists()) {
					FileBody encFile = new FileBody(file, "pdf");
					params.put("File", encFile);
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			service.login(ServiceAction.ActionUpload, APIType.UPLOAD, params);
		}
	}

	public static String getFileNameWithoutExtn(String url) {
		String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
		String fileNameWithoutExtn = fileName.substring(0,
				fileName.lastIndexOf('.'));
		return fileNameWithoutExtn;
	}

	@Override
	public void onCompleted(Service service, ServiceResponse result) {
		if (result.isSuccess()
				&& result.getAction() == ServiceAction.ActionUpload) {
			Log.d("Upload onCompleted", "onCompleted " + result.getData());
			DataParser parser = new DataParser(true);
			ResponseData resData = parser.parseUpdateResult((String) result
					.getData());
			FileData parseData = (FileData) resData.getData();

			if ("OK".equals(resData.getStatus())) {
				// if(getActivity()!=null &&!getActivity().isFinishing()){
				// Toast.makeText(getActivity(),
				// "Document uploaded successfully, please waiting for encoding data.",
				// Toast.LENGTH_LONG).show();
				// parseData.setFileTitle(""+mEtFilePath.getText().toString());
				// }
				// ((MainActivity) getActivity()).deleteFile(mFileUpload);
				// ((MainActivity) getActivity()).gotoEncodeScreen(parseData);
			} else if ("SessionIdNotFound"
					.equalsIgnoreCase(resData.getStatus())) {
				HoGoApplication.instace().setToken(getActivity(), null);
				// ((MainActivity) getActivity()).gotologinScreen();
			} else {
				if (getActivity() != null) {
					if (!getActivity().isFinishing())
						Toast.makeText(getActivity(), "Upload Fail",
								Toast.LENGTH_LONG).show();
				}

			}
		}
	}

}
