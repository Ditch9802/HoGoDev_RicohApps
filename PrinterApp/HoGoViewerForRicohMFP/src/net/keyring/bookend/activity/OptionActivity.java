package net.keyring.bookend.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.asynctask.CheckMailAddressTask;
import net.keyring.bookend.asynctask.CheckMailAddressTask.CheckAddressListener;
import net.keyring.bookend.constant.ConstViewDefault;
import net.keyring.bookend.request.BookendPin2;
import net.keyring.bookend.request.RegistMailAddress2;
import net.keyring.bookend.util.DecryptUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * メニュー - オプション表示画面
 * 
 * @author Hamaji
 * 
 */
public class OptionActivity extends BookendActivity implements
		ConstViewDefault, CheckAddressListener {
	/** PackageInfoクラス */
	private PackageInfo mPackageInfo = null;
	/** メールアドレス変更ボタン */
	private Button mChangeAddressBtn;
	/** リセットボタン */
	private Button mResetBtn;
	/** バージョン表示 */
	private TextView mVersionText;
	/** セットしたメールアドレス表示 */
	private TextView mMailAddressText;
	/** 表示アドレス */
	private String mAddress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		setContentView(R.layout.option);
	}

	@Override
	public void onResume() {
		super.onResume();
		Logput.v("------< OptionActivity >------");
		setContentView(R.layout.option);
		mLayout = (LinearLayout) findViewById(R.id.option);
		// Version情報セット
		setVersion();
		
		try{
			TableRow mailTitle = (TableRow)findViewById(R.id.option_mail_title);
			TableRow mailText = (TableRow)findViewById(R.id.option_confirm_address);
			TableRow mailSendBtn = (TableRow)findViewById(R.id.option_mail_send_btn);
			// 設定したメールアドレス取得
			mMailAddressText = (TextView) mLayout.findViewById(R.id.confirm_address);
			// アドレス変更ボタン
			mChangeAddressBtn = (Button) mLayout.findViewById(R.id.addressChangeBtn);
			// リセットボタン
			mResetBtn = (Button) mLayout.findViewById(R.id.reset);
			if(getString(R.string.webshelf_btn_flag).equals("1")){
				// Web書庫ボタンをなくすフラグが立っていた場合はメールアドレス表示を消す
				mailTitle.setVisibility(View.GONE);
				mailText.setVisibility(View.GONE);
				mailSendBtn.setVisibility(View.GONE);
			}else{
				mailTitle.setVisibility(View.VISIBLE);
				mailText.setVisibility(View.VISIBLE);
				mailSendBtn.setVisibility(View.VISIBLE);
				// メールアドレス情報セット
				setMailAddress();
			}

		/*	
			//	(デバッグ用)ログ出力を行っている場合はDBファイルを書き出す
			if(Logput.isLogcat) {
				File dstFile = new File(Environment.getExternalStorageDirectory().getPath() + "/bookend.db");
				SQLiteEngine.copyDB(dstFile);
			}
		*/	
			
			// Delete 20140731@kaz
			// オプション画面のデバッグ用ボタンを削除
		/*
			((Button)findViewById(R.id.print_pdf1)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Logput.v("------ Print Normal PDF ------");
						String filePath = copyFileFromRaw(R.raw.test_normal, "test_normal.pdf");
						printPdf(filePath, null);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			((Button)findViewById(R.id.print_pdf2)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Logput.v("------ Print Password PDF ------");
						//	UserPassword: abcdefg
						//	OwnerPassword: 123456
						String filePath = copyFileFromRaw(R.raw.test_password, "test_password.pdf");
						printPdf(filePath, "abcdefg");
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			((Button)findViewById(R.id.print_pdf3)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Logput.v("------ Print KRPDFX PDF ------");
						//	ContentsID: 2d1f95ffcd9b4085bb4a9cdac8de75fe
						//	Key: 2864353D3B68282C3D773860520D664A2F700D3B555B6D475B2569344A274F25
						byte[] userPassword = DecryptUtil.base16dec("2864353D3B68282C3D773860520D664A2F700D3B555B6D475B2569344A274F25");
						String filePath = copyFileFromRaw(R.raw.test_krpdfx, "test_krpdfx.pdf");
						
						//	Print PDF file with User Password
						//printPdf(filePath, new String(userPassword));
						
						//	Decrypt PDF file by User Password
						FileOutputStream output = openFileOutput("test_krpdfx-dec.pdf", Context.MODE_WORLD_READABLE);
						Utils.decryptPdf(filePath, output, userPassword);
						
						//	Print decrypted PDF file
						String outputFilePath = getFileStreamPath("test_krpdfx-dec.pdf").getAbsolutePath();
						printPdf(outputFilePath, null);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			((Button)findViewById(R.id.print_pdf4)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Logput.v("------ Print KRPDFX2 PDF ------");
						//	ContentsID: d4bd50f9d2314b21ba1c89f63e2ce86b
						//	Key: 273e46665a312b3969572d76435b6e5a256b284a56464e6830643152383c7d57
						byte[] userPassword = DecryptUtil.base16dec("273e46665a312b3969572d76435b6e5a256b284a56464e6830643152383c7d57");
						String filePath = copyFileFromRaw(R.raw.test_krpdfx2, "test_krpdfx2.pdf");
						
						//	Print PDF file with User Password
						//printPdf(filePath, new String(userPassword));
						
						//	Decrypt PDF file by User Password
						FileOutputStream output = openFileOutput("test_krpdfx2-dec.pdf", Context.MODE_WORLD_READABLE);
						Utils.decryptPdf(filePath, output, userPassword);
						
						//	Print decrypted PDF file
						String outputFilePath = getFileStreamPath("test_krpdfx2-dec.pdf").getAbsolutePath();
						printPdf(outputFilePath, null);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
*/			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Copy file from application resource to internal storage.
	 * @param id
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private String copyFileFromRaw(int id, String fileName) throws FileNotFoundException, IOException {
		InputStream input = this.getResources().openRawResource(id);
		OutputStream output = this.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
		
		int DEFAULT_BUFFER_SIZE = 1024 * 1024;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
		
		input.close();
		output.close();
		
		return this.getFileStreamPath(fileName).getAbsolutePath();
	}
	
	/**
	 * Print PDF file
	 * @param filePath
	 * @param password
	 */
	private void printPdf(String filePath, String password) {
		Logput.v("filePath = " + filePath);
		Logput.v("password = " + password);
		// ********* HoGo Custom *********
	    // STEP2: Call Print service and send FilePath and Passwwod to Printer
		Intent intent = new Intent(this, jp.co.ricoh.ssdk.sample.app.print.activity.MainActivity.class);
		// Set value of File Path
		intent.putExtra("path", filePath);
		// The value of Password, result=pdfPassword 
		if(password != null) {
			intent.putExtra("key", password);
		}
		startActivity(intent);
	}

	// Activity破棄時に実行
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mChangeAddressBtn != null) {
			mChangeAddressBtn.setOnClickListener(null);
			mChangeAddressBtn = null;
		}
		mVersionText = null;
		finish();
	}

	// Activityの再開時に実行
	@Override
	public void onRestart() {
		super.onRestart();
		mVersionText.setEnabled(true);
	}

	// Activityの停止時に実行
	@Override
	public void onStop() {
		super.onStop();
		mVersionText.setEnabled(false);
		mChangeAddressBtn.setEnabled(false);
	}
	
	/**
	 * バージョン情報をセット
	 */
	public void setVersion(){
		mVersionText = (TextView) findViewById(R.id.version);
		if (mVersionText == null ) return; 
		try {
			mPackageInfo = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			Logput.e(e.getMessage(), e);
		}
		mVersionText.setText(mPackageInfo.versionName);
	}
	
	/**
	 * メールアドレス情報をセット
	 */
	public void setMailAddress(){
		// ネットワーク状態確認・static変数に保存
		if (Utils.isConnected(getApplicationContext())) {
			// ONLINE - CheckMailAddressリクエストを行う
			CheckMailAddressTask checkMailAddress = new CheckMailAddressTask(	this, OptionActivity.this);
			checkMailAddress.execute("");
		} else {
			// OFFLINE
			// アドレス変更ボタンを非アクティブに
			mChangeAddressBtn.setFocusable(false);
			mChangeAddressBtn.setFocusableInTouchMode(false);
			mChangeAddressBtn.setEnabled(false);
			// リセットボタンを非アクティブに
			mResetBtn.setFocusable(false);
			mResetBtn.setFocusableInTouchMode(false);
			mResetBtn.setEnabled(false);
			if (mPref == null) {
				mPref = new Preferences(this);
			}
			mAddress = mPref.getMailAddress();
			if (StringUtil.isEmpty(mAddress)) {
				// 未登録の場合は「未登録」表示
				mAddress = getString(R.string.unregistered);
			}
		}
		setAddress();
	}
	
	private void setAddress() {
		mMailAddressText.setText(mAddress);
		// デバックモードチェック
		if (Utils.isDebugMode(this.getApplicationContext())) {
			// デバックモード有効：NG
			dialog(DIALOG_ID_ERROR, getString(R.string.debug_mode_message));
		}
	}

	/**
	 * CheckMailAddressバックグラウンド処理からの返り値
	 * 
	 * @param mailAddress
	 *            メールアドレス
	 */
	@Override
	public void result_checkAddress(String mailAddress) {
		if (StringUtil.isEmpty(mailAddress)) {
			// status=70000以外 : NG
			// 「未登録」表示
			mAddress = getString(R.string.unregistered);
			// アドレス変更ボタンを非アクティブに
			mChangeAddressBtn.setFocusable(false);
			mChangeAddressBtn.setFocusableInTouchMode(false);
			mChangeAddressBtn.setEnabled(false);
			// リセットボタンを非アクティブに
			mResetBtn.setFocusable(false);
			mResetBtn.setFocusableInTouchMode(false);
			mResetBtn.setEnabled(false);
		} else {
			mAddress = mailAddress;
		}
		setAddress();
	}

	/**
	 * メールアドレス入力画面
	 */
	private void regist_view() {
		setContentView(R.layout.mail_address_regist);
		mLayout = (LinearLayout) findViewById(R.id.mail_address_regist);
		// 進行メッセージ
		TextView regist_message = (TextView) mLayout
				.findViewById(R.id.regist_message);
		// アドレス入力欄
		final TextView address = (EditText) findViewById(R.id.mail_address);
		// Pin入力欄
		final TextView input_pin = (EditText) findViewById(R.id.pin);
		// Pin送信ボタン
		final Button pin_ok = (Button) findViewById(R.id.pin_ok);
		if (mPref == null)
			mPref = new Preferences(getApplicationContext());
		String mailAddress_temporary = mPref.getMailAddress_temporary();
		if (StringUtil.isEmpty(mailAddress_temporary)) {
			// 変更したい新規アドレスが未設定の場合
			regist_message.setText(R.string.change_address_message);
		} else {
			address.setText(mailAddress_temporary);
			// メールアドレスが登録済みの場合(PIN入力メッセージ)
			String message = getString(R.string.shelf_regist_after_mes, Preferences.sMailAddress);
			regist_message.setText(message);
			// Pin入力欄にフォーカスできるように
			input_pin.setFocusable(true);
			input_pin.setFocusableInTouchMode(true);
			input_pin.setEnabled(true);
			input_pin.requestFocus();
			pin_ok.setEnabled(true);
		}
		// 入力されたアドレスを取得
		Button send = (Button) findViewById(R.id.mail_address_send);
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SpannableStringBuilder sp = (SpannableStringBuilder) address.getText();
				String mail_address = sp.toString();
				if (Utils.checkMailAddress(mail_address)) {
					// 入力されたメールアドレスを一時保存
					if (mPref == null)
						mPref = new Preferences(getApplicationContext());
					mPref.setMailAddress_temporary(mail_address);
					// 入力されたメールアドレスをチェックする
					RegistMailAddress2 registMailAddress2 = new RegistMailAddress2(
							getApplicationContext());
					String status = registMailAddress2.execute(mail_address,
							true);
					if (Utils.equal_str("71000", status)) {
						// RegistMailAddress2 - OK
						// Pin入力へ(画面更新)
						setContentView(R.layout.mail_address_regist);
						mLayout = (LinearLayout) findViewById(R.id.mail_address_regist);
						regist_view();
					} else {
						// RegeistMailAddress2 - NG
						// 一時保存していたメールアドレス・チェックコードをNullに
						mPref.setMailAddress_temporary(null);
						mPref.setCheckCode(null);
						dialog(DIALOG_ID_VIEW,
								registMailAddress2.getDescription());
					}
				} else {
					// 入力されたメールアドレスに不正があった場合はエラーアラート表示して初期画面に戻る
					dialog(DIALOG_ID_VIEW,
							getString(R.string.mailaddress_false));
				}
			}
		});

		// PIN
		// 入力されたPINを取得
		pin_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SpannableStringBuilder sp = (SpannableStringBuilder) input_pin
						.getText();
				String pin = sp.toString();
				// 入力された文字列が数値ではない場合ははじく
				if (!StringUtil.isInteger(pin))
					dialog(DIALOG_ID_VIEW, getString(R.string.pin_false));
				// 10byte(5文字)以下PIN文字列ははじく
				else if (pin.length() > 5)
					dialog(DIALOG_ID_VIEW, getString(R.string.pin_false));
				// 入力されたPINを計算チェック
				else if (!StringUtil.check_pin(pin))
					dialog(DIALOG_ID_VIEW, getString(R.string.pin_false));
				else {
					// BookendPIN2
					BookendPin2 bookendPin2 = new BookendPin2();
					if (bookendPin2.execute(getApplicationContext(), pin)) {
						// 変更完了 - オプション画面表示
						onResume();
					} else {
						dialog(DIALOG_ID_VIEW, bookendPin2.getDescription());
					}
				}
			}
		});
	}
	
	/**
	 * アドレス変更ボタンアクション
	 * @param view
	 */
	public void addressChangeAction(View view){
		// メールアドレス・PIN入力画面に遷移
		setContentView(R.layout.mail_address_regist);
		mLayout = (LinearLayout) findViewById(R.id.mail_address_regist);
		regist_view();
	}
	
	/**
	 * リセットボタンアクション
	 * @param view
	 */
	public void resetAction(View view){
		// リセット確認アラーと表示
		if (mPref == null) {
			mPref = new Preferences(OptionActivity.this);
		}
		String message = mPref.getMailAddress() + "\n" + getString(R.string.reset_message);
		dialog(DIALOG_ID_RESET, message);
	}

}
