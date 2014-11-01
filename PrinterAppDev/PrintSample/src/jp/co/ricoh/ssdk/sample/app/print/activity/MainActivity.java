/**
 * Copyright (C) 2013 RICOH Co.,LTD.
 * All rights reserved.
 */

package jp.co.ricoh.ssdk.sample.app.print.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import jp.co.ricoh.ssdk.sample.app.print.R;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSampleApplication;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSettingDataHolder;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSettingSupportedHolder;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintStateMachine;
import jp.co.ricoh.ssdk.sample.function.common.impl.AsyncConnectState;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile.PDL;
import jp.co.ricoh.ssdk.sample.function.print.PrintService;
import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintRequestAttribute;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.Copies;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PageTray;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PaperSide;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintColor;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintResolution;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.Staple;
import jp.co.ricoh.ssdk.sample.function.print.event.PrintServiceAttributeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * プリントサンプルアプリのメインアクティビティです。
 * Main activity of the print sample application.
 */
public class MainActivity extends Activity{
    
    private TextView mPaperSource;
    private TextView mPaperSize;
    private TextView mQuanlity;
    private TextView mColor;
    private TextView mLayoutOption;
    private TextView mFinishingOption;
    private TextView mNumberCopies;
    
    private RelativeLayout rlStartPrint;
    private Button btnSaveSettings;
    private Button btnReturnDefault;
    private Button btnBack;
    
    private final static String TAG = MainActivity.class.getSimpleName();

    /**
     * 印刷に必要な設定値
     * Print settings.
     */
    private PrintSettingDataHolder mHolder = new PrintSettingDataHolder();

    /**
     * 印刷に実現するために必要なイベントを受け取るためのリスナー
     * Listener to receive print service attribute event.
     */
    private PrintServiceAttributeListenerImpl mServiceAttributeListener;

    /**
     * 印刷枚数を指定するボタン
     * Button to set the number of prints.
     */
    private Button mPrintCountBtn;

    /**
     * 印刷カラーを指定するボタン
     * Print color setting button
     */
    private Button mPrintColorBtn;

    /**
     * 印刷ファイル選択ダイアログを開くボタン
     * Button to open the print file selection dialog.
     */
    private Button mSelectFileBtn;

    /**
     * その他の設定ダイアログを開くためのボタン
     * Button to open the other setting dialog.
     */
    private LinearLayout mOtherSettingLayout;

    /**
     * 印刷開始ボタン
     * Button to start printing.
     */
    private RelativeLayout mStartLayout;

    private String fileName;
    
    private String key;

    /**
     * アクティビティが生成されると呼び出されます。
     * [処理内容]
     *   (1)アプリケーションの初期化
     *   (2)印刷ファイル選択ボタンの設定
     *   (3)印刷枚数ボタンの設定
     *   (4)印刷カラーボタンの設定
     *   (5)その他の設定ボタンの設定
     *   (6)印刷開始ボタンの設定
     *   (7)リスナー初期化
     *
     * Called when an activity is created.
     * [Processes]
     *   (1)initialize application
     *   (2)Set the print file selection button.
     *   (3)Set the number of copies setting button.
     *   (4)Set the Print Color setting button.
     *   (5)Set the other setting button.
     *   (6)Set the print start button.
     *   (7)Initialize Listener
     * */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hogo_main);
        if (getIntent().getExtras() != null) {
            
           // ********* HoGo Custom *********
           // Capture filepath and password
            fileName = getIntent().getExtras().getString("path");
            Log.e(TAG, "FileName from bundle onCreate: " + fileName);
            key = getIntent().getExtras().getString("key");
            
            
            // new PrintServiceInitTask().execute();
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_settings_not_found,
                    Toast.LENGTH_LONG).show();
            finish();
        }

        // (1)
        initialize();
        

        // // (2)
        // mSelectFileBtn = (Button) findViewById(R.id.btn_select_file);
        // mSelectFileBtn.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // List<String> fileList = new ArrayList<String>();
        // fileList.add(v.getContext().getString(R.string.assets_file_sample_01));
        // fileList.add(v.getContext().getString(R.string.assets_file_sample_02));
        //
        // AlertDialog dlg = DialogUtil.selectFileDialog(v.getContext(),
        // fileList);
        // dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
        //
        // /**
        // * 印刷ファイル選択ダイアログが閉じると呼び出されるメソッドです。 [処理内容]
        // * (1)メインアクティビティが保持する設定値の更新
        // * (2)メインアクティビティが保持する設定値に応じてボタンの表示を更新 The method when the
        // * print file selection dialog closed. [Processes]
        // * (1)Updates the setting value saved on MainActivity
        // * (2)Updates the button displays according to the setting
        // * value saved on MainActivity
        // */
        // @Override
        // public void onDismiss(DialogInterface arg0) {
        // // (1)
        // updateSettings();
        // // (2)
        // updateSettingButtons();
        // }
        // });
        // DialogUtil.showDialog(dlg, DialogUtil.DEFAULT_DIALOG_WIDTH);
        // }
        // });
        //
        // // (3)
        // mPrintCountBtn = (Button) findViewById(R.id.btn_print_page);
        // mPrintCountBtn.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // AlertDialog dlg = DialogUtil.createPrintCountDialog(v.getContext());
        // dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
        //
        // @Override
        // public void onDismiss(DialogInterface arg0) {
        // if (null != mHolder.getSelectedCopiesValue()) {
        // String printCount = mHolder.getSelectedCopiesValue().getValue()
        // .toString();
        // mPrintCountBtn.setText(printCount);
        // }
        // }
        // });
        // DialogUtil.showDialog(dlg, DialogUtil.DEFAULT_DIALOG_WIDTH);
        // }
        // });
        //
        // // (4)
        // mPrintColorBtn = (Button) findViewById(R.id.btn_print_color);
        // mPrintColorBtn.setOnClickListener(new View.OnClickListener() {
        // /**
        // * 印刷カラー設定ボタンが押下されると呼び出されるメソッドです。 [処理内容] (1)設定可能なカラーを取得
        // * (2)印刷カラー設定ダイアログを表示 The method called when the print color setting
        // * button clicked. [Processes] (1)Obtains the supported print colors
        // * (2)Displays the print color setting dialog
        // */
        // @Override
        // public void onClick(View view) {
        // // (1)
        // PDL selectedPDL = getSettingHolder().getSelectedPDL();
        // Map<PDL, PrintSettingSupportedHolder> map =
        // ((PrintSampleApplication) getApplication())
        // .getSettingSupportedDataHolders();
        // PrintSettingSupportedHolder holder = map.get(selectedPDL);
        //
        // // (2)
        // AlertDialog dlg =
        // DialogUtil.createPrintColorDialog(view.getContext(),
        // holder.getSelectablePrintColorList());
        //
        // dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
        // @Override
        // public void onDismiss(DialogInterface dialogInterface) {
        // if (null != mHolder.getSelectedPrintColorValue()) {
        // String printColor = PrintColorUtil.getPrintColorResourceString(
        // MainActivity.this, mHolder.getSelectedPrintColorValue());
        // mPrintColorBtn.setText(printColor);
        // }
        // }
        // });
        // DialogUtil.showDialog(dlg, DialogUtil.DEFAULT_DIALOG_WIDTH);
        // }
        // });
        //
        // // (5)
        // mOtherSettingLayout = (LinearLayout) findViewById(R.id.btn_other);
        // mOtherSettingLayout.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // AlertDialog dlg =
        // DialogUtil.createOtherSettingDialog(v.getContext());
        // if (null == dlg) {
        // return;
        // }
        // DialogUtil.showDialog(dlg, DialogUtil.INPUT_DIALOG_WIDTH);
        // }
        // });
        //
        // // (6)
        // mStartLayout = (RelativeLayout) findViewById(R.id.layout_start);
        // mStartLayout.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // if (null == mHolder ||
        // null == mHolder.getSelectedPrintAssetFileName() ||
        // null == mHolder.getSelectedPDL()) {
        // Toast.makeText(v.getContext(), R.string.error_settings_not_found,
        // Toast.LENGTH_LONG).show();
        // return;
        // }
        // ((PrintSampleApplication) getApplication()).startPrint(mHolder);
        // }
        // });

        // (7)
        initializeListener();

    }

    /**
     * アプリケーションが破棄される際に呼び出されます。
     * [処理内容]
     *   (1)メインアクティビティ終了イベントをステートマシンに送る
     *      ジョブ実行中であれば、ジョブがキャンセルされます。
     *   (2)サービスからイベントリスナーを除去する
     *
     * Called when the application is destroy.
     * [Processes]
     *   (1) Send MainActivity destoyed event to the state machine.
     *       If job is in process, it is cancelled.
     *   (2)Removes the event listener from PrintService.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // (1)
        ((PrintSampleApplication) getApplication()).getStateMachine().procPrintEvent(
                PrintStateMachine.PrintEvent.CHANGE_APP_ACTIVITY_DESTROYED);

        // (2)
        if (mServiceAttributeListener != null) {
            PrintService service = ((PrintSampleApplication) getApplication()).getPrintService();
            service.removePrintServiceAttributeListener(mServiceAttributeListener);
        }
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
        mServiceAttributeListener = new PrintServiceAttributeListenerImpl(this, new Handler());

        // (2)
        new PrintServiceInitTask().execute(mServiceAttributeListener);

        // (3)
        ((PrintSampleApplication) getApplication()).getStateMachine().procPrintEvent(
                PrintStateMachine.PrintEvent.CHANGE_APP_ACTIVITY_INITIAL);
    }
    
    
    private void hogoUpdateSettingButton(){
        if (null != mHolder.getSelectedCopiesValue()) {
            mNumberCopies.setEnabled(true);
            mNumberCopies.setText(mHolder.getSelectedCopiesValue().getValue().toString());
        } else {
            mNumberCopies.setText("");
            mNumberCopies.setEnabled(false);
        }

        if (null != mHolder.getSelectedPrintColorValue()) {
            mColor.setEnabled(true);
            mColor.setText(PrintColorUtil.getPrintColorResourceString(MainActivity.this,
                    mHolder.getSelectedPrintColorValue()));
        } else {
            mNumberCopies.setText("");
            mNumberCopies.setEnabled(false);
        }

       
    }
    /**
     * 各設定値ボタンの表示を更新します。 Update setting button displays.
     */
    private void updateSettingButtons() {
        TextView fileNameTxt = (TextView) findViewById(R.id.text_select_file);
        fileNameTxt.setText(mHolder.getSelectedPrintAssetFileName());

        if (null != mHolder.getSelectedCopiesValue()) {
            mPrintCountBtn.setEnabled(true);
            mPrintCountBtn.setText(mHolder.getSelectedCopiesValue().getValue().toString());
        } else {
            mPrintCountBtn.setText("");
            mPrintCountBtn.setEnabled(false);
        }

        if (null != mHolder.getSelectedPrintColorValue()) {
            mPrintColorBtn.setEnabled(true);
            mPrintColorBtn.setText(PrintColorUtil.getPrintColorResourceString(MainActivity.this,
                    mHolder.getSelectedPrintColorValue()));
        } else {
            mPrintColorBtn.setText("");
            mPrintColorBtn.setEnabled(false);
        }

        if (null != mHolder.getSelectedStaple()) {
            mOtherSettingLayout.setEnabled(true);

        } else {
            mOtherSettingLayout.setEnabled(false);
        }

    }

    /**
     * メイン画面の初期化を行います。 Initialize the main screen.
     */
    private void initSetting() {
        // mHolder.setSelectedPrintAssetFileName(getString(R.string.assets_file_sample_01));
       // ********* HoGo Custom *********
       // Set file path and password
        mHolder.setSelectedFileName(fileName);
        Log.e(TAG, "FileName: " + fileName);
        if(key != null) {
            mHolder.setPassword(key);
        }
        
        mPaperSource = (TextView)findViewById(R.id.tvPaperSource);
        mPaperSize = (TextView)findViewById(R.id.tvPaperSize);
        mQuanlity = (TextView)findViewById(R.id.tvQuanlity);
        mColor = (TextView)findViewById(R.id.tvColor);
        mLayoutOption = (TextView)findViewById(R.id.tvLayoutOptions);
        mFinishingOption = (TextView)findViewById(R.id.tvFinishingOption);
        mNumberCopies = (TextView)findViewById(R.id.tvNumberCopies);
        
        rlStartPrint = (RelativeLayout)findViewById(R.id.rlStartPrint);
        btnSaveSettings = (Button)findViewById(R.id.btnSaveSettings);
        btnReturnDefault = (Button)findViewById(R.id.btnReturnDefault);
        btnBack = (Button)findViewById(R.id.btnBack);
        
        updateSettings();
//        updateSettingButtons();
//        hogoUpdateSettingButton();
        mainViewSetting();
    }

    
    private void mainViewSetting(){
        
        rlStartPrint.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(null == mHolder ||
                        null == mHolder.getSelectedPDL()){
                    Toast.makeText(v.getContext(), R.string.error_settings_not_found, Toast.LENGTH_LONG).show();
                    return;
                }
                ((PrintSampleApplication)getApplication()).startPrint(mHolder);
            }
        });
        btnSaveSettings.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String color = mColor.getText().toString();
                String pageTray = mPaperSource.getText().toString();
                String paperSide = mLayoutOption.getText().toString();
                String printResolution = mQuanlity.getText().toString();
                String copies = mHolder.getSelectedCopiesValue().getValue().toString();
                
                Log.e(TAG, "Number copy text: " + copies);
                PreferenceUtils.setString(MainActivity.this, PreferenceUtils.PRINT_COLOR, color);
                PreferenceUtils.setString(MainActivity.this, PreferenceUtils.PRINT_NUMBER_COPIES, copies);
                PreferenceUtils.setString(MainActivity.this, PreferenceUtils.PAPER_SOURCE, pageTray);
                PreferenceUtils.setString(MainActivity.this, PreferenceUtils.PAPER_SIDE, paperSide);
                PreferenceUtils.setString(MainActivity.this, PreferenceUtils.PRINT_RESOLUTION, printResolution);
            }
        });
        
        btnReturnDefault.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
                PreferenceUtils.clearPrefs(MainActivity.this);
                
                mHolder.setSelectedCopiesValue(
                        new Copies(Integer.parseInt(getString(R.string.default_copies))));
                mNumberCopies.setText(mHolder.getSelectedCopiesValue().getValue().toString());
//                mNumberCopies.setText(getString(R.string.default_copies));
                
                List<PrintColor> printColorList = PrintColorUtil.getSelectablePrintColorList(MainActivity.this);

                if (printColorList != null) {
                    mHolder.setSelectedPrintColorValue(printColorList.get(0));
                    mColor.setText(PrintColorUtil.getPrintColorResourceString(MainActivity.this,
                    mHolder.getSelectedPrintColorValue()));
                }
                
                List<PageTray> pageTrayList = PageTrayUtil.getSelectablePageTrayList(MainActivity.this);

                if (pageTrayList != null) {
                    mHolder.setSelectedPageTrayValue(pageTrayList.get(0));
                    mColor.setText(PageTrayUtil.getPageTrayResourceString(MainActivity.this,
                    mHolder.getSelectedPageTrayValue()));
                    
                }
                
                List<PaperSide> paperSideList = PaperSideUtil.getSelectablePaperSideList(MainActivity.this);

                if (paperSideList != null) {
                    mHolder.setSelectedPaperSideValue(paperSideList.get(0));
                    mLayoutOption.setText(PaperSideUtil.getPaperSideResourceString(MainActivity.this,
                    mHolder.getSelectedPaperSideValue()));
                    
                }
                
                List<PrintResolution> printResolutionList = PrintResolutionUtil.getSelectablePrintResolutionList(MainActivity.this);

                if (printResolutionList != null) {
                    mHolder.setSelectedPrintResolutionValue(printResolutionList.get(0));
                    mColor.setText(PrintResolutionUtil.getPrintResolutionResourceString(MainActivity.this,
                    mHolder.getSelectedPrintResolutionValue()));
                    
                }
            }
        });
        
        btnBack.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPaperSource.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                PDL selectedPDL = getSettingHolder().getSelectedPDL();
                Map<PDL,PrintSettingSupportedHolder> map =
                        ((PrintSampleApplication)getApplication()).getSettingSupportedDataHolders();
                PrintSettingSupportedHolder holder = map.get(selectedPDL);
              //(2)
                AlertDialog dlg = DialogUtil.createPageSourceDialog(MainActivity.this, holder.getSelectablePageTrayList());

                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(null != mHolder.getSelectedPageTrayValue()) {
                            String pageSource = PageTrayUtil.getPageTrayResourceString(MainActivity.this,mHolder.getSelectedPageTrayValue());
                            mPaperSource.setText(pageSource);
                        }
                    }
                });
                DialogUtil.showDialog(dlg, DialogUtil.DEFAULT_DIALOG_WIDTH);
            }
        });
//        mPaperSize.setOnClickListener(this);
        mQuanlity.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                PDL selectedPDL = getSettingHolder().getSelectedPDL();
                Map<PDL,PrintSettingSupportedHolder> map =
                        ((PrintSampleApplication)getApplication()).getSettingSupportedDataHolders();
                PrintSettingSupportedHolder holder = map.get(selectedPDL);

                //(2)
                AlertDialog dlg = DialogUtil.createPrintResolutionDialog(MainActivity.this, holder.getSelectablePrintResolutionList());

                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(null != mHolder.getSelectedPrintResolutionValue()) {
                            String printResolution = PrintResolutionUtil.getPrintResolutionResourceString(MainActivity.this,mHolder.getSelectedPrintResolutionValue());
                            mQuanlity.setText(printResolution);
                        }
                    }
                });
                DialogUtil.showDialog(dlg, DialogUtil.DEFAULT_DIALOG_WIDTH);
            }
        });
        mLayoutOption.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PDL selectedPDL = getSettingHolder().getSelectedPDL();
                Map<PDL,PrintSettingSupportedHolder> map =
                        ((PrintSampleApplication)getApplication()).getSettingSupportedDataHolders();
                PrintSettingSupportedHolder holder = map.get(selectedPDL);

                //(2)
                AlertDialog dlg = DialogUtil.createPaperSideDialog(MainActivity.this, holder.getSelectablePaperSideList());

                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(null != mHolder.getSelectedPaperSideValue()) {
                            String paperSide = PaperSideUtil.getPaperSideResourceString(MainActivity.this,mHolder.getSelectedPaperSideValue());
                            mLayoutOption.setText(paperSide);
                        }
                    }
                });
                DialogUtil.showDialog(dlg, DialogUtil.DEFAULT_DIALOG_WIDTH);
            }
        });
//        mFinishingOption.setOnClickListener(this);
        mColor.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                PDL selectedPDL = getSettingHolder().getSelectedPDL();
                Map<PDL,PrintSettingSupportedHolder> map =
                        ((PrintSampleApplication)getApplication()).getSettingSupportedDataHolders();
                PrintSettingSupportedHolder holder = map.get(selectedPDL);

                //(2)
                AlertDialog dlg = DialogUtil.createPrintColorDialog(MainActivity.this, holder.getSelectablePrintColorList());

                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(null != mHolder.getSelectedPrintColorValue()) {
                            String printColor = PrintColorUtil.getPrintColorResourceString(MainActivity.this,mHolder.getSelectedPrintColorValue());
                            mColor.setText(printColor);
                        }
                    }
                });
                DialogUtil.showDialog(dlg, DialogUtil.DEFAULT_DIALOG_WIDTH);
            }
        });
        
        mNumberCopies.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AlertDialog dlg = DialogUtil.createPrintCountDialog(v.getContext());
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface arg0) {
                        if (null != mHolder.getSelectedCopiesValue()) {
                            String printCount = mHolder.getSelectedCopiesValue().getValue()
                                    .toString();
                            mNumberCopies.setText(printCount);
                        }
                    }
                });
                DialogUtil.showDialog(dlg, DialogUtil.DEFAULT_DIALOG_WIDTH);
            }
        });
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
            String copiesPrefs = PreferenceUtils.getString(this, PreferenceUtils.PRINT_NUMBER_COPIES);
            Log.e(TAG, "Number Copy prefs:" + copiesPrefs);
            if(copiesPrefs.equalsIgnoreCase("")){
                mHolder.setSelectedCopiesValue(
                        new Copies(Integer.parseInt(getString(R.string.default_copies))));
                mNumberCopies.setText(mHolder.getSelectedCopiesValue().getValue().toString());
            }else{
                
                mHolder.setSelectedCopiesValue(
                        new Copies(Integer.parseInt(copiesPrefs)));
                mNumberCopies.setText(copiesPrefs);
            }
            
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
                String colorPrefs = PreferenceUtils.getString(this, PreferenceUtils.PRINT_COLOR);
                Log.e(TAG, "Print color prefs: " + colorPrefs);
                if(colorPrefs.equalsIgnoreCase("")){
                    mHolder.setSelectedPrintColorValue(printColorList.get(0));
                    mColor.setText(PrintColorUtil.getPrintColorResourceString(MainActivity.this,
                            mHolder.getSelectedPrintColorValue()));
                }else{
                    PrintColor color = null;
                    for(int i = 0; i < printColorList.size(); i++){
                        String printColorValue = printColorList.get(i).getValue().toString();
                        Log.e(TAG, "PrintColor value: " + printColorValue + " PrintColor Prefs value: " + colorPrefs.toLowerCase());
                        if(printColorValue.equalsIgnoreCase(colorPrefs.toLowerCase())){
                            color = printColorList.get(i);
                            
                            mHolder.setSelectedPrintColorValue(color);
                            mColor.setText(PrintColorUtil.getPrintColorResourceString(MainActivity.this,
                                    mHolder.getSelectedPrintColorValue()));
                            break;
                        }else{
                            continue;
                        }
                    }
                    
                }
                
                //mHolder.setSelectedPrintColorValue(printColorList.get(1));
           } else {
                //mHolder.setSelectedStaple(null);
               mHolder.setSelectedPrintColorValue(null);
           }if (categories.contains(PageTray.class)) {
               List<PageTray> printPageSourceList = PageTrayUtil.getSelectablePageTrayList(this);
               if(printPageSourceList != null){
                   String paperSourcePrefs = PreferenceUtils.getString(this, PreferenceUtils.PAPER_SOURCE);
                   Log.e(TAG, "Print paper source prefs: " + paperSourcePrefs);
                   if(paperSourcePrefs.equalsIgnoreCase("")){
                       mHolder.setSelectedPageTrayValue(printPageSourceList.get(0));
                       mPaperSource.setText(PageTrayUtil.getPageTrayResourceString(MainActivity.this,
                               mHolder.getSelectedPageTrayValue()));
                   }else{
                       PageTray paperSource = null;
                       for(int i = 0; i < printPageSourceList.size(); i++){
                           String paperSourceString = printPageSourceList.get(i).getValue().toString();
                           Log.e(TAG, "PrintPaperSource value: " + paperSourceString + " PaperSource Prefs value: " + paperSourcePrefs.toLowerCase());
                           if(paperSourceString.equalsIgnoreCase(paperSourcePrefs.toLowerCase())){
                               paperSource = printPageSourceList.get(i);
                               
                               mHolder.setSelectedPageTrayValue(paperSource);
                               mPaperSource.setText(PageTrayUtil.getPageTrayResourceString(MainActivity.this,
                                       mHolder.getSelectedPageTrayValue()));
                               break;
                           }else{
                               continue;
                           }
                       }
                   }
                   
               }
           }else{
               mHolder.setSelectedPageTrayValue(null);
           }if (categories.contains(PaperSide.class)) {
               List<PaperSide> printPaperSideList = PaperSideUtil.getSelectablePaperSideList(this);
               if(printPaperSideList != null){
                   String paperSidePrefs = PreferenceUtils.getString(this, PreferenceUtils.PAPER_SIDE);
                   Log.e(TAG, "Print paper side prefs: " + paperSidePrefs);
                   if(paperSidePrefs.equalsIgnoreCase("")){
                       mHolder.setSelectedPaperSideValue(printPaperSideList.get(0));
                       mLayoutOption.setText(PaperSideUtil.getPaperSideResourceString(MainActivity.this,
                               mHolder.getSelectedPaperSideValue()));
                   }else{
                       PaperSide paperSide = null;
                       for(int i = 0; i < printPaperSideList.size(); i++){
                           String paperSideString = printPaperSideList.get(i).getValue().toString();
                           Log.e(TAG, "PrintPaperSide value: " + paperSideString + " PaperSide Prefs value: " + paperSidePrefs.toLowerCase());
                           if(paperSideString.equalsIgnoreCase(paperSidePrefs.toLowerCase())){
                               paperSide = printPaperSideList.get(i);
                               
                               mHolder.setSelectedPaperSideValue(paperSide);
                               mLayoutOption.setText(PaperSideUtil.getPaperSideResourceString(MainActivity.this,
                                       mHolder.getSelectedPaperSideValue()));
                               break;
                           }else{
                               continue;
                           }
                       }
                   }
                   
               }
           }else{
               mHolder.setSelectedPaperSideValue(null);
           }if (categories.contains(PrintResolution.class)) {
               List<PrintResolution> printResolutionList = PrintResolutionUtil.getSelectablePrintResolutionList(this);
               if(printResolutionList != null){
                   String printResolutionPrefs = PreferenceUtils.getString(this, PreferenceUtils.PRINT_RESOLUTION);
                   Log.e(TAG, "Print resolution prefs: " + printResolutionPrefs);
                   if(printResolutionPrefs.equalsIgnoreCase("")){
                       mHolder.setSelectedPrintResolutionValue(printResolutionList.get(0));
                       mQuanlity.setText(PrintResolutionUtil.getPrintResolutionResourceString(MainActivity.this,
                               mHolder.getSelectedPrintResolutionValue()));
                   }else{
                       PrintResolution printResolution = null;
                       for(int i = 0; i < printResolutionList.size(); i++){
                           String printResolutionString = printResolutionList.get(i).getValue().toString();
                           Log.e(TAG, "PrintResolution value: " + printResolutionString + " PrintResoluiton Prefs value: " + printResolutionPrefs.toLowerCase());
                           if(printResolutionString.equalsIgnoreCase(printResolutionPrefs.toLowerCase())){
                               printResolution = printResolutionList.get(i);
                               
                               mHolder.setSelectedPrintResolutionValue(printResolution);
                               mQuanlity.setText(PrintResolutionUtil.getPrintResolutionResourceString(MainActivity.this,
                                       mHolder.getSelectedPrintResolutionValue()));
                               break;
                           }else{
                               continue;
                           }
                       }
                   }
                   
               }
           }else{
               mHolder.setSelectedPrintResolutionValue(null);
           }
        }
        return true;
    }

    public PrintSettingDataHolder getSettingHolder() {
        return mHolder;
    }

    public void setSettingHolder(PrintSettingDataHolder holder) {
        this.mHolder = holder;
    }

    /**
     * アプリケーションを初期化します。 ここでは、ステートマシンの初期化を行います。 Initializes the application.
     * StateMachine is initialized here.
     */
    private void initialize() {

        PrintSampleApplication sampleApplication = (PrintSampleApplication) getApplication();
        PrintStateMachine stateMachine = sampleApplication.getStateMachine();
        stateMachine.setContext(this);
        
    }

    /**
     * 非同期でプリントサービスとの接続を行います。 Connects with the print service asynchronously.
     */
    class PrintServiceInitTask extends AsyncTask<PrintServiceAttributeListener, Void, Integer> {

        AsyncConnectState addListenerResult = null;
        AsyncConnectState getAsyncConnectStateResult = null;

        /**
         * UIスレッドのバックグラウンドで実行されるメソッドです。
         * [処理内容]
         *   (1)プリントサービスのイベントを受信するリスナーを設定します。
         *      機器が利用可能になるか、キャンセルが押されるまでリトライします。
         *   (2)非同期イベントの接続確認を行います。
         *      接続可能になるか、キャンセルが押されるまでリトライします。
         *   (3)接続に成功した場合は、プリントサービスから各設定の設定可能値を取得します。
         *
         * Runs in the background on the UI thread.
         * [Processes]
         *   (1) Sets the listener to receive print service events.
         *       This task repeats until the machine becomes available or cancel button is touched.
         *   (2) Confirms the asynchronous connection.
         *       This task repeats until the connection is confirmed or cancel button is touched.
         *   (3) After the machine becomes available and connection is confirmed,
         *       obtains job setting values.
         */
        @Override
        protected Integer doInBackground(PrintServiceAttributeListener... listeners) {

            PrintService printService = ((PrintSampleApplication) getApplication())
                    .getPrintService();

            // (1)
            while (true) {
                if (isCancelled()) {
                    return -1;
                }
                addListenerResult = printService.addPrintServiceAttributeListener(listeners[0]);

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
                getAsyncConnectStateResult = printService.getAsyncConnectState();

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

                List<PrintFile.PDL> supportedPDL = printService.getSupportedPDL();
                if (supportedPDL == null)
                    return null;

                for (PrintFile.PDL pdl : supportedPDL) {
                    ((PrintSampleApplication) getApplication())
                            .putPrintSettingSupportedHolder(pdl,
                                    new PrintSettingSupportedHolder(printService, pdl));
                }

            }

            return 0;
        }

        /**
         * doInBackground()実行後に呼び出されるメソッドです。
         *  Called after doInBackground().
         */
        @Override
        protected void onPostExecute(Integer result) {

            if (result != 0) {
                /* canceled. */
                return;
            }

            if (addListenerResult.getState() == AsyncConnectState.STATE.CONNECTED
                    && getAsyncConnectStateResult.getState() == AsyncConnectState.STATE.CONNECTED) {
                // connection succeeded.
                initSetting();
                ((PrintSampleApplication) getApplication()).getStateMachine().procPrintEvent(
                        PrintStateMachine.PrintEvent.CHANGE_APP_ACTIVITY_STARTED);

                if (null == mHolder ||
                        null == mHolder.getSelectedFileName() ||
                        null == mHolder.getSelectedPDL()) {
                    Toast.makeText(getApplicationContext(),
                            R.string.error_settings_not_found,
                            Toast.LENGTH_LONG).show();
                    return;
                }
//                ((PrintSampleApplication) getApplication()).startPrint(mHolder);
            }
            else {
                // the connection is invalid.
                ((PrintSampleApplication) getApplication()).getStateMachine().procPrintEvent(
                        PrintStateMachine.PrintEvent.CHANGE_APP_ACTIVITY_START_FAILED);
            }
        }

        /**
         * 指定された時間カレントスレッドをスリープします。
         * sleep for the whole of the specified interval
         */
        private void sleep(long time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                Log.w(TAG, "", e);
            }
        }
    }

    
}
