package net.keyring.bookend.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.app.print.activity.MainActivity;

import net.keyring.bookend.Logput;
import net.keyring.bookend.NewContentsDlDialog;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.Consts.PurchaseState;
import net.keyring.bookend.Consts.ResponseCode;
import net.keyring.bookend.action.DialogAction;
import net.keyring.bookend.action.MainListAction;
import net.keyring.bookend.adapter.DocumentListAdapter;
import net.keyring.bookend.adapter.MainListAdapter;
import net.keyring.bookend.asynctask.HoGoViewTask;
import net.keyring.bookend.asynctask.SendWebBookShelfTask;
import net.keyring.bookend.asynctask.ViewTask;
import net.keyring.bookend.asynctask.ViewTask.ViewListener;
import net.keyring.bookend.asynctask.ViewTaskParam;
import net.keyring.bookend.asynctask.SendWebBookShelfTask.RegistConfirmListener;
import net.keyring.bookend.asynctask.StartUpCheckTask;
import net.keyring.bookend.asynctask.StartUpCheckTask.StartUpCheckListener;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.billing.PurchaseObserver;
import net.keyring.bookend.billing.ResponseHandler;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.constant.ConstRegist;
import net.keyring.bookend.constant.ConstStartUp;
import net.keyring.bookend.service.BillingService;
import net.keyring.bookend.service.NewDownloadService;
import net.keyring.bookend.service.BillingService.RequestPurchase;
import net.keyring.bookend.service.BillingService.RestoreTransactions;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class HoGoDocumentListActivity extends BookendActivity implements RegistConfirmListener, ConstRegist,ViewListener, StartUpCheckListener, ConstStartUp, Const, ConstList{
	
	private DocumentListAdapter 		mAdapter;
	/** getIntent */
	private Intent						mGetIntent;
	/** ListActionクラス */
	private MainListAction				mMainListAction;
	/** 表示データリストMap */
	private ArrayList<Map<String, Object>> mData;
	/** リストビュー */
	private ListView					mMainList;
	
	private ImageView 					mImgRefresh, mImgPrint, mImgDelete, mImgLogout;
	/** MarketBillingServiceに接続して(binding)、アプリに代わって Android Market にメッセージを送るサービス */
    private BillingService				mBillingService;	
	/** アンドロイドマーケットからコールバックを受取りUIを更新するクラス */
    private DungeonsPurchaseObserver	mDungeonsPurchaseObserver;
    /** ハンドラー */
    private Handler						mHandler;
    /** download情報マップ */
	private Map<String, String>			mQueryList;
	
    private LinearLayout 				mLayout;
	
    private RegistConfirmListener mRegistConfirmListener = null;
    private ViewListener mViewListener = null;
	private static final String TAG = HoGoDocumentListActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.start);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, "onStart");
		setContentView(R.layout.start);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG, "onRestart");
		try{
			if(getString(R.string.splash_background_white).equals("1")){
				RelativeLayout layout = (RelativeLayout)findViewById(R.id.start);
				layout.setBackgroundColor(Color.WHITE);
			}
		}catch(NullPointerException e){
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		if (!Preferences.sDefaultCheck) {
			if (mPref == null) mPref = new Preferences(getApplicationContext());
			mPref.getUserID();
			if (!Utils.isConnected(getApplicationContext()) && StringUtil.isEmpty(Preferences.sUserID)) {
				// 初起動時オフラインの場合はエラーダイアログ表示
				dialog(DIALOG_ID_ERROR, getString(R.string.first_activation_offline));
			} else {
				//dialog(DIALOG_ID_PROGRESS, getString(R.string.starting));
				StartUpCheckTask startUpCheckTask = new StartUpCheckTask(this);
				startUpCheckTask.execute(getApplicationContext());
			}
		}else{
			main();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i(TAG, "onStop");
		mData = null;
		mAdapter = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		if(mBillingService != null){
			mBillingService.unbind();
		}
	}
	
	
	public void main(){
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 
        
		mGetIntent = getIntent();
		if (Preferences.sDeleteCapture) {
			Preferences.sDeleteCapture = false;
			Toast.makeText(this, this.getString(R.string.delete_capture), Toast.LENGTH_SHORT).show();
		}
		// デバックモードチェック
		if (Utils.isDebugMode(this.getApplicationContext())) {
			// デバックモード有効：NG
			dialog(DIALOG_ID_ERROR, getString(R.string.debug_mode_message));
		} else {
			//	二重に処理しないよう、処理済みフラグを付けておく
			boolean isProcessed = mGetIntent.getBooleanExtra(IS_PROCESSED, false);
			Logput.v("isProcessed = " + isProcessed);
			
			//	FLAG_ACTIVITY_LAUNCHED_FROM_HISTORYが立っている場合はURLを処理しない
			//	*URLを処理するのはブラウザから起動された場合のみなのでHistoryから起動された場合は無視する
			if((mGetIntent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
				Logput.v("Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY found.");
				isProcessed = true;
			}
			
			// ダウンロードURLがあればIntentから取得
			Uri getURL = mGetIntent.getData();
			int schemeFlag = Utils.isDownloadLink(getURL, getString(R.string.custom_name));
			if (!isProcessed && schemeFlag == SCHEME_BEHTTP) {
				download(getURL);
				mGetIntent.putExtra(IS_PROCESSED, true);
			} else if (!isProcessed && schemeFlag == SCHEME_BEINFO) {
				// Webページへ
				if (mMainListAction == null) mMainListAction = new MainListAction();
				if (!mMainListAction.toWebInfo(getApplicationContext(), getURL)) {
					// URL取得エラーの場合はそのままメインページ表示
					listView(setSort());
				}
				mGetIntent.putExtra(IS_PROCESSED, true);
			} else {
				// メインコンテンツリスト表示
				listView(setSort());
			}
			// STGモードの場合は少しの間表示
			if (Preferences.sMode) {
				Toast.makeText(this, "STG MODE", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private String[] mFromTemplate = { THUMB_URL, TITLE, AUTHOR, DATE, EXPIRY, NOPR };
	private int[] mToTemplate = { R.id.imgProfileBook, R.id.tvTitleBook, R.id.tvFromUser, R.id.tvDate, R.id.tvExpiryBook, R.id.tvNPR };
	
	private void listView(int sort) {
		setContentView(R.layout.activity_document_list);
		
		Toast.makeText(HoGoDocumentListActivity.this, "If you want to get new Document, please press on Refresh button", Toast.LENGTH_SHORT).show();
		
		mLayout = (LinearLayout) findViewById(R.id.llLayoutDocumentList);
		mMainList = (ListView) mLayout.findViewById(R.id.lvDocumentList);
		
		mImgRefresh = (ImageView) mLayout.findViewById(R.id.imgRefresh);
		mImgPrint = (ImageView) mLayout.findViewById(R.id.imgPrint);
		mImgDelete = (ImageView) mLayout.findViewById(R.id.imgDelete);
		mImgLogout = (ImageView) mLayout.findViewById(R.id.imgLogout);
		
		if (mMainListAction == null) mMainListAction = new MainListAction();
		
		mRegistConfirmListener = this;
		mViewListener = this;
		mData = mMainListAction.getBooksData(this.getApplicationContext());
		mAdapter = new DocumentListAdapter(this, mData, R.layout.layout_item_document, mFromTemplate, mToTemplate);
		mMainList = mMainListAction.setMainList(mMainList, mData, mAdapter);
		
		mImgRefresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SendWebBookShelfTask RegistCheck = new SendWebBookShelfTask(mRegistConfirmListener, HoGoDocumentListActivity.this);
				RegistCheck.execute("");
//				listView(Preferences.sSort_main);
			}
		});
		mImgDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<Integer> listSelectedPosition = mAdapter.getListSelectedPosition();
				if(listSelectedPosition.size() <= 0){
					Toast.makeText(HoGoDocumentListActivity.this, "Please select book which you want to delete", Toast.LENGTH_SHORT).show();
				}else{
					AlertDialog.Builder dialog = mMainListAction.setClickButtonDelete(HoGoDocumentListActivity.this, mData, mAdapter, listSelectedPosition);
					if(dialog == null){
						Toast.makeText(getApplicationContext(), "Contents Detail ... None.", Toast.LENGTH_SHORT).show();
					}else{
						dialog.setPositiveButton(
								getString(R.string.delete),
								new OnClickListener() {
									// 削除ボタン
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
										if (!mMainListAction.deleteListBook(getApplicationContext())) {
											Toast.makeText(HoGoDocumentListActivity.this, "Delete : fail", Toast.LENGTH_SHORT).show();
										}
										// list更新
										listView(Preferences.sSort_main);
									}
								});
						// 閉じるボタン
						dialog.setNegativeButton(getString(R.string.close), null);
						dialog.show();
					}
				}
				
				//
//				int positionSelected = mAdapter.getPositionSelected();
//				if(positionSelected != -1){
//					AlertDialog.Builder dialog = mMainListAction
//							.setListLongClickDialog(HoGoDocumentListActivity.this, mData, mAdapter, positionSelected);
//					if (dialog == null) {
//						Toast.makeText(getApplicationContext(), "Contents Detail ... None.", Toast.LENGTH_SHORT).show();
//					} else {
//						dialog.setPositiveButton(
//								getString(R.string.delete),
//								new OnClickListener() {
//									// 削除ボタン
//									@Override
//									public void onClick(DialogInterface dialog, int which) {
//										if (!mMainListAction.delete(getApplicationContext())) {
//											Toast.makeText(HoGoDocumentListActivity.this, "Delete : fail", Toast.LENGTH_SHORT).show();
//										}
//										// list更新
//										listView(Preferences.sSort_main);
//									}
//								});
//						// 閉じるボタン
//						dialog.setNegativeButton(getString(R.string.close), null);
//						dialog.show();
//					}
//				}else{
//					Toast.makeText(HoGoDocumentListActivity.this, "Choose book to delete", Toast.LENGTH_SHORT).show();
//				}
				
			}
		});
		
		mImgPrint.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(HoGoDocumentListActivity.this, HoGoPrintSettingActivity.class);
//				startActivity(i);
				List<Integer> listSelectedPosition = mAdapter.getListSelectedPosition();
				if(listSelectedPosition.size() <= 0){
					Toast.makeText(HoGoDocumentListActivity.this, "Please select book which you want to print", Toast.LENGTH_SHORT).show();
				}else{
//					int positionSelected = mAdapter.getPositionSelected();
					int positionSelected = listSelectedPosition.get(0);
					int book_status = mMainListAction.listClickAction(
							getApplicationContext(), mMainList, mData, mAdapter, positionSelected);
					viewCheck(book_status, mMainListAction.getBook());
				}
				
			}
		});
	}
	
	/**
	 * クリックされたコンテンツチェック
	 * 
	 * @param status
	 */
	private void viewCheck(int status, BookBeans book) {
		switch (status) {
		case EXPIRYDATE_NG: // 閲覧期限切れ
			// Webサイト誘導情報がセットされている場合はエラーメッセージと一緒に表示して誘導を行う
			expiryDateNavidialog(book.getDownload_id());
			break;
		case BROWSE_NG: // 閲覧回数が過ぎている
			dialog(DIALOG_ID_VIEW, getString(R.string.browse_count_error));
			break;
		case INVALID_PLATFORM_NG: // Androidでの閲覧が許可されていない
			dialog(DIALOG_ID_VIEW, getString(R.string.invalid_platform_error));
			break;
		case FILE_NONE: // ファイルが存在しない
			dialog(DIALOG_ID_VIEW, getString(R.string.contents_no));
			break;
		case OS_TIME_NG: // OSの時計が過去に戻っていた場合
			dialog(DIALOG_ID_VIEW, getString(R.string.clock_check_error));
			break;
		case OFFLINE_BROWSE_COUNT: // 閲覧回数が制限されているコンテンツはオフラインでは閲覧できない
			dialog(DIALOG_ID_VIEW, getString(R.string.offline_browse_count_error));
			break;
		default:	// ファイルチェック後ビューア起動
			ViewTask viewTask = new ViewTask(HoGoDocumentListActivity.this, mViewListener);
			viewTask.execute(new ViewTaskParam(book, false));
			break;
		}
	}
	
	private int setSort() {
		mPref = new Preferences(this.getApplicationContext());
		int sort = mPref.getSort();
		if (sort == -1) {
			// ソート方法がセットされていない場合はダウンロード日時降順
			mPref.setSort(net.keyring.bookend.constant.ConstDB.DESCEND_DATE);
			sort = Preferences.sSort_main;
		}
		return sort;
	}
	
	private void download(Uri downloadURL) {
		// Queryチェック
		if (mMainListAction == null) mMainListAction = new MainListAction();
		Map<String, String> queryList = mMainListAction.getContentsDetail(
				this.getApplicationContext(), downloadURL);
		listView(setSort());
		if (queryList == null) {
			dialog(DIALOG_ID_VIEW, getString(R.string.already_downloaded_error));
		} else if (queryList.size() <= 0) {
			dialog(DIALOG_ID_VIEW, getString(R.string.already_downloaded_error));
		} else {
			mHandler = new Handler();
	        mDungeonsPurchaseObserver = new DungeonsPurchaseObserver(mHandler);
	        if(mBillingService == null){
	        	mBillingService = new BillingService();
	        	mBillingService.setContext(this);
	        	ResponseHandler.register(mDungeonsPurchaseObserver);
	        }
	        
			this.mQueryList = queryList;
			// ダウンロード確認ダイアログ mCallback_instance
			new NewContentsDlDialog(HoGoDocumentListActivity.this, mBillingService, queryList).show();
		}
	}

	@Override
	public void result_startUpCheck(Map<String, Object> startUpCheckList) {
		// TODO Auto-generated method stub
		try{
			int status = (Integer) startUpCheckList.get(STATUS_KEY);
			//	 初期起動時チェックOKまたはバージョンチェックのエラーの場合は通常通り起動する
			if(status == ConstStartUp.CHECK_OK || status == ConstStartUp.CHECK_ERROR_VERSION_CHECK) {
				// デフォルトチェック済
				Preferences.sDefaultCheck = true;
				// メインリスト表示
				main();
			}
			else if(status == ConstStartUp.CHECK_ERROR_CHECKACTIVATION) {
				//	CheckActivationエラーの場合はオフラインとして起動する
				//	*Utils.isConnected()の処理を変更するためPreferences.sOfflineフラグをセットする
				Preferences.sOffline = true;
				Preferences.sDefaultCheck = true;
				main();
			}
			else {
				//	それ以外の場合はエラー
				check_startUp(status, startUpCheckList);			
			}
		} catch(NullPointerException e) {
			Preferences.sDefaultCheck = false;
			Logput.w("StartUp Check : ERROR", e);
			dialog(DIALOG_ID_ERROR, "StartUp Check : ERROR");
		}
	}
	
	private void check_startUp(int status, Map<String, Object> startUpCheckList) {
		String error_message = (String) startUpCheckList.get(DIALOG_MESSAGE);
		if (status == CHECK_VERSIONUP_FORCE) {
			// 強制アップデート
			String url = (String) startUpCheckList.get(UPDATE_URL);
			if (mDialogAction == null) mDialogAction = new DialogAction();
			mDialogAction.setVerUP_URL(url);
			dialog(DIALOG_ID_UPDATE_FORCE, error_message);
		} else if (status == CHECK_VERSIONUP_USUALLY) {
			// 通常アップデート
			String url = (String) startUpCheckList.get(UPDATE_URL);
			if (mDialogAction == null) mDialogAction = new DialogAction();
			mDialogAction.setVerUP_URL(url);
			dialog(DIALOG_ID_UPDATE_UTILL, error_message);
		} else if (status == CHECK_REQUEST_ACTIVATION) {
			dialog(DIALOG_ID_REACTIVATION, error_message);
		} else {
			dialog(DIALOG_ID_ERROR, error_message);
		}
	}

	private class DungeonsPurchaseObserver extends PurchaseObserver implements ViewListener{
    	/**
    	 * コンストラクタ - アンドロイドマーケットからコールバックを受取りUIを更新するクラス
    	 * @param handler
    	 */
        public DungeonsPurchaseObserver(Handler handler) {
            super(HoGoDocumentListActivity.this, handler);
        }
        
    	/**
    	 * ViewTask戻り値
    	 * @param 画面を更新するかどうかフラグ
    	 */
    	@Override
    	public void result_view(String errorMessage){
    		removeDialog(DIALOG_ID_PROGRESS);
    		if(errorMessage != null){
    			dialog(DIALOG_ID_VIEW, errorMessage);
    		}
    	}

        /**
         * Androidマーケットが応答するときに呼び出されるコールバック
         */
        @Override
        public void onBillingSupported(boolean supported) {
            Logput.i("supported: " + supported);
            if (!supported) {
            	// アプリ内課金に対応していない場合はダイヤログ表示
                dialog(DIALOG_BILLING_NOT_SUPPORTED_ID, getString(R.string.billing_not_supported_title),getString(R.string.billing_not_supported_message));
            }
        }

        /**
         * アイテムが購入、払い戻し、またはキャンセルされたときに呼び出されるコールバック<br>
		 * 呼び出した{@link BillingService requestPurchase（文字列）}に応じて呼び出される
         */
        @Override
        public void onPurchaseStateChange(PurchaseState purchaseState, String itemId, long purchaseTime, String developerPayload) {
            Logput.i("onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
            
            if (developerPayload == null) {
            	Logput.d("product:" + itemId + " activity:" + purchaseState);
            } else {
            	Logput.d("product:" + itemId + " activity:" + purchaseState + "\n\t" + developerPayload);
            }
            
            if(purchaseState == PurchaseState.PURCHASED){
        		// [購入]…ダウンロード開始
        		Logput.d("DL : " + mQueryList);
        		// DL中のものがなければダウンロード開始
				if(!NewDownloadService.sDlFlag){
					Logput.d("Set queryList.");
					NewDownloadService.sQueryList = mQueryList;
				}else{
					Logput.i("Other file downloading.");
					Toast.makeText(HoGoDocumentListActivity.this, "Other file downloading.", Toast.LENGTH_SHORT);
				}
        	}else{
        		mQueryList = null;
        	}
        }

        /**
         * RequestPurchase要求に対して市場からの応答コードを受信したときに呼び出される
         */
        @Override
        public void onRequestPurchaseResponse(RequestPurchase request,
                ResponseCode responseCode) {
            Logput.d("[product]" + request.mProductId + " [responseCode]" + responseCode);
            
            if (responseCode == ResponseCode.RESULT_OK) {
                Logput.i(">>purchase was successfully sent to server");
            } else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
                Logput.i(">>user canceled purchase");
            } else {
                Logput.i(">>purchase failed");
            }
        }


        /**
         * {@link RestoreTransactions}要求に対してはAndroidマーケットから取得してレスポンスコードを受信
         */
        @Override
        public void onRestoreTransactionsResponse(RestoreTransactions request,
                ResponseCode responseCode) {
            if (responseCode == ResponseCode.RESULT_OK) {
                Logput.d("completed RestoreTransactions request");
            } else {
                Logput.i("RestoreTransactions error: " + responseCode);
            }
        }
    }

	@Override
	public void result_registConfirm(int message) {
		// TODO Auto-generated method stub
		switch(message){
		case SEND_REGIST_MAILADDRESS:	// メールアドレス登録画面へ遷移
			Intent login = new Intent(this, RegistFormActivity.class);
			startActivity(login);
			break;
		case SEND_WEB_BOOK_SHELF:		// Web書庫画面に遷移
			Intent webBookShelf = new Intent(this, HoGoWebBookShelfActivity.class);
			startActivity(webBookShelf);
			break;
			
		case RESET_FLAG:				// 他のクライアントでリセットされたためアプリ終了ダイアログ表示
			dialog(DIALOG_ID_ERROR, getString(R.string.reset_finish, getString(R.string.app_name)));
			break;
		case DB_ERROR:					//　DB登録エラー発生：メイン画面のまま
			dialog(DIALOG_ID_VIEW, getString(R.string.db_error));
			break;
		case GET_CONTENTS_ERROR:		// GetContentsリクエストエラー:メイン画面のまま
			dialog(DIALOG_ID_VIEW, getString(R.string.getcontents_error));
			break;
		case GET_AWS_INFO_NG:			// GetAwsInfoリクエストエラー 
			dialog(DIALOG_ID_VIEW, getString(R.string.status_false));
			break;
		case GET_AWS_INFO_50010:		// GetAwsInfo - パラメーターエラー
			dialog(DIALOG_ID_VIEW, getString(R.string.status_parameter_error) + 50010);
			break;
		case GET_AWS_INFO_50011:		// GetAwsInfo - サービス停止中
			dialog(DIALOG_ID_VIEW, getString(R.string.getawsinfo_status_50011));
			break;
		case GET_AWS_INFO_50012:		// GetAwsInfo - メンテナンス中
			dialog(DIALOG_ID_VIEW, getString(R.string.getawsinfo_status_50012));
			break;
		case GET_AWS_INFO_50099:		// GetAwsInfo - サーバ内部エラー
			dialog(DIALOG_ID_VIEW, getString(R.string.status_server_internal_error) + 50099);
			break;
		}
	
	}

	@Override
	public void result_view(String result) {
		removeDialog(DIALOG_ID_PROGRESS);
		if(result != null){
			dialog(DIALOG_ID_VIEW, result);
		}
		// list更新
		listView(Preferences.sSort_main);
		
	}
}
