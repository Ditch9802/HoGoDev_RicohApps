package net.keyring.bookend.activity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.ricoh.ssdk.sample.app.print.activity.DialogUtil;
import jp.co.ricoh.ssdk.sample.app.print.activity.PrintColorUtil;
import jp.co.ricoh.ssdk.sample.app.print.activity.StapleUtil;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSampleApplication;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSettingDataHolder;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSettingSupportedHolder;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintStateMachine;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile.PDL;
import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintRequestAttribute;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.Copies;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintColor;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.Staple;
import net.keyring.bookend.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class HoGoPrintSettingActivity extends Activity implements OnClickListener{
	
	private TextView mPaperSource;
	private TextView mPaperSize;
	private TextView mQuanlity;
	private TextView mColor;
	private TextView mLayoutOption;
	private TextView mFinishingOption;
	private TextView mNumberCopies;
	
	private String fileName;
    
    private String key;
	/**
     * 印刷に必要な設定値
     * Print settings.
     */
    private PrintSettingDataHolder mHolder = new PrintSettingDataHolder();
    /**
     * 印刷に実現するために必要なイベントを受け取るためのリスナー
     * Listener to receive print service attribute event.
     */
//    private PrintServiceAttributeListenerImpl mServiceAttributeListener;
	
	private static final String TAG = HoGoPrintSettingActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_print_setting);
		if (getIntent().getExtras() != null) {
            
	           // ********* HoGo Custom *********
	           // Capture filepath and password
	            fileName = getIntent().getExtras().getString("path");
	            key = getIntent().getExtras().getString("key");

	            // new PrintServiceInitTask().execute();
	        } else {
	            Toast.makeText(getApplicationContext(), R.string.error_settings_not_found,
	                    Toast.LENGTH_LONG).show();
	            finish();
	        }
		initView();
		
		// (7)
        initializeListener();
		
	}
	
	/**
     * アプリケーションを初期化します。
     * [処理内容]
     *   (1)PrintServiceからイベントを受信するリスナーを生成
     *   (2)UIスレッドを使用するためのタスクを生成
     *   (3)ステートマシンに初期化イベントを送信
     *
     * Initialize the application.
     * [Processes]
     *   (1)Create the listener that receives events from PrintService.
     *   (2)Create and start the PrintService initialize task.
     *   (3)Post the initial event to state machine.
     */
    private void initializeListener() {
        // (1)
//        mServiceAttributeListener = new PrintServiceAttributeListenerImpl(this, new Handler());

        // (2)
//        new PrintServiceInitTask().execute(mServiceAttributeListener);

        // (3)
        ((PrintSampleApplication) getApplication()).getStateMachine().procPrintEvent(
                PrintStateMachine.PrintEvent.CHANGE_APP_ACTIVITY_INITIAL);
    }

	private void initView() {
		mPaperSource = (TextView)findViewById(R.id.tvPaperSource);
		mPaperSize = (TextView)findViewById(R.id.tvPaperSize);
		mQuanlity = (TextView)findViewById(R.id.tvQuanlity);
		mColor = (TextView)findViewById(R.id.tvColor);
		mLayoutOption = (TextView)findViewById(R.id.tvLayoutOptions);
		mFinishingOption = (TextView)findViewById(R.id.tvFinishingOption);
		mNumberCopies = (TextView)findViewById(R.id.tvNumberCopies);
		
		mPaperSource.setOnClickListener(this);
		mPaperSize.setOnClickListener(this);
		mQuanlity.setOnClickListener(this);
		mColor.setOnClickListener(this);
		mLayoutOption.setOnClickListener(this);
		mFinishingOption.setOnClickListener(this);
		mNumberCopies.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//			case R.id.tvPaperSource:
//				
//				break;
//			case R.id.tvPaperSize:
//				
//				break;
//			case R.id.tvQuanlity:
//				
//				break;
//			case R.id.tvColor:
//				//(1)
//                PDL selectedPDL = getSettingHolder().getSelectedPDL();
//                Map<PDL,PrintSettingSupportedHolder> map =
//                        ((PrintSampleApplication)getApplication()).getSettingSupportedDataHolders();
//                PrintSettingSupportedHolder holder = map.get(selectedPDL);
//
//                //(2)
//                AlertDialog dlg = DialogUtil.createPrintColorDialog(HoGoPrintSettingActivity.this, holder.getSelectablePrintColorList());
//
//                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        if(null != mHolder.getSelectedPrintColorValue()) {
//                            String printColor = PrintColorUtil.getPrintColorResourceString(HoGoPrintSettingActivity.this,mHolder.getSelectedPrintColorValue());
//                            mColor.setText(printColor);
//                        }
//                    }
//                });
//                DialogUtil.showDialog(dlg, DialogUtil.DEFAULT_DIALOG_WIDTH);
//				break;
//			case R.id.tvLayoutOptions:
//		
//				break;
//			case R.id.tvFinishingOption:
//				
//				break;
//			case R.id.tvNumberCopies:
//				
//				break;
//			default:
//				break;
//		}		
	}
	
	/**
     * メイン画面の初期化を行います。 Initialize the main screen.
     */
    private void initSetting() {
        // mHolder.setSelectedPrintAssetFileName(getString(R.string.assets_file_sample_01));
       // ********* HoGo Custom *********
       // Set file path and password
        mHolder.setSelectedFileName(fileName);
        if(key != null) {
            mHolder.setPassword(key);
        }
        updateSettings();
        // updateSettingButtons();
    }
    /**
     * 印刷に必要な各種設定値を更新します。
     * [処理内容]
     *  (1)ファイル名から印刷ファイルのPDLを確定する
     *  (2)印刷ファイルのPDLで設定可能な値を取得する
     *  (3)設定可能な項目に初期値を設定する
     *
     *  Updates print setting values.
     *  [Processes]
     *    (1)Determines the print file PDL from the file name
     *    (2)Obtains the supported value of print file PDL
     *    (3)Set the initial value to the available settings.
     * */
    private boolean updateSettings(){

        // String filename = mHolder.getSelectedPrintAssetFileName();
        String filename = mHolder.getSelectedFileName();
        if (null == filename) {
            return false;
        }

        // (1)
        mHolder.setSelectedPDL(currentPDL(filename));

        if (null == mHolder.getSelectedPDL()) {
            return false;
        }

        // (2)
        Set<Class<? extends PrintRequestAttribute>> categories;
        Map<PDL, PrintSettingSupportedHolder> supportedHolderMap =
                ((PrintSampleApplication) getApplication()).getSettingSupportedDataHolders();
        categories = supportedHolderMap.get(mHolder.getSelectedPDL()).getSelectableCategories();

        if (null == categories) {
            return false;
        }

        // (3)
        if (categories.contains(Copies.class)) {
            mHolder.setSelectedCopiesValue(
                    new Copies(Integer.parseInt(getString(R.string.default_copies))));
        } else {
            mHolder.setSelectedCopiesValue(null);
        }
        if (categories.contains(Staple.class)) {
            List<Staple> stapleList = StapleUtil.getSelectableStapleList(this);

            if (stapleList != null) {
                mHolder.setSelectedStaple(stapleList.get(0));
            } else {
                mHolder.setSelectedStaple(null);
            }
        } else {
            mHolder.setSelectedStaple(null);
        }
        if (categories.contains(PrintColor.class)) {
            List<PrintColor> printColorList = PrintColorUtil.getSelectablePrintColorList(this);

            if (printColorList != null) {
                mHolder.setSelectedPrintColorValue(printColorList.get(0));
                //mHolder.setSelectedPrintColorValue(printColorList.get(1));
           } else {
                //mHolder.setSelectedStaple(null);
               mHolder.setSelectedPrintColorValue(null);
           }
        }
        return true;
    }
    
    /**
     * 印刷ジョブのPDLを決定します。
     * [処理内容]
     *   (1)選択中のファイルの拡張子を取得
     *   (2)設定可能なPDLの一覧を取得
     *   (3)プリンタに送信するPDLを決定
     * [注意]
     *   本サンプルではファイルの拡張子からPDLを判別していますが、
     *   PDLはファイルの拡張子から確定できるものではありません。
     *
     * Sets PDL of the print job.
     * [Processes]
     *   (1)Obtains the extension of the selected file.
     *   (2)Obtains a list of supported PDL.
     *   (3)Determines the PDL to be sent to the printer.
     * @param fileName
     * @return PDL
     */
    public PrintFile.PDL currentPDL(String fileName) {

        if (fileName == null) {
            return null;
        }

        // (1)
        PrintFile.PDL currentPDL = null;
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

        // (2)
        Set<PDL> pdlList = ((PrintSampleApplication) getApplication())
                .getSettingSupportedDataHolders().keySet();

        // (3)
        if (ext.equals(getString(R.string.file_extension_PDF))
                && pdlList.contains(PDL.PDF)) {
            currentPDL = PDL.PDF;
        } else if (ext.equals(getString(R.string.file_extension_PRN))
                && pdlList.contains(PDL.RPCS)) {
            currentPDL = PDL.RPCS;
        } else if (ext.equals(getString(R.string.file_extension_XPS))
                && pdlList.contains(PDL.XPS)) {
            currentPDL = PDL.XPS;
        } else {
            currentPDL = PDL.PDF;
        }
        return currentPDL;
    }
    
	public PrintSettingDataHolder getSettingHolder(){
        return mHolder;
    }

}
