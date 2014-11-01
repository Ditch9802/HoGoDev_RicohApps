package jp.co.ricoh.ssdk.sample.app.print.activity;

import android.content.Context;
import android.content.res.Resources;

import jp.co.ricoh.ssdk.sample.app.print.R;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSampleApplication;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSettingSupportedHolder;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PageTray;

import java.util.List;
import java.util.Map;

public class PageTrayUtil {

    /**
     * 印刷カラーの設定値から表示文字を取得します。
     * Obtains text label from scan color setting value
     *
     * @param context
     * @param printColor
     * @return
     */
    public static String getPageTrayResourceString(Context context, PageTray pageSource){
        String ret = null;
        switch (pageSource) {
            case AUTO:
                ret = context.getString(R.string.page_source_auto);
                break;
            case LARGE_CAPACITY:
                ret = context.getString(R.string.page_source_large_capacity);
                break;
            case MANUAL:
                ret = context.getString(R.string.page_source_manual);
                break;
            case TRAY1:
                ret = context.getString(R.string.page_source_tray1);
                break;
            case TRAY2:
                ret = context.getString(R.string.page_source_tray2);
                break;
            case TRAY3:
                ret = context.getString(R.string.page_source_tray3);
                break;
            case TRAY4:
                ret = context.getString(R.string.page_source_tray4);
                break;
            case TRAY5:
                ret = context.getString(R.string.page_source_tray5);
                break;
            case TRAY6:
                ret = context.getString(R.string.page_source_tray6);
                break;
            case TRAY7:
                ret = context.getString(R.string.page_source_tray7);
                break;
            case TRAY8:
                ret = context.getString(R.string.page_source_tray8);
                break;
            case TRAY9:
                ret = context.getString(R.string.page_source_tray9);
                break;
            default:
                ret = null;
        }

        return ret;
    }

    /**
     * 色を表す文字列に応じたPrintColorオブジェクトを取得します。
     * Obtains the PrintColor object from the print color text.
     *
     * @param context メインアクティビティへのコンテキスト
     *                Context of MainActivity
     * @param colorValue 色を表す文字列
     *                   Text value to indicate print color
     * @return PrintColorオブジェクト
     *         PrintColor object
     */
    public static PageTray getPageTrayFromResourceString(Context context, String sourceValue) {
        Resources resource = context.getResources();
        PageTray retSource = null;
//        PageSource retSource = PageSource.AUTO;

        if(resource.getString(R.string.page_source_auto).equals(sourceValue)) {
            retSource = PageTray.AUTO;
        } else if(resource.getString(R.string.page_source_large_capacity).equals(sourceValue)) {
            retSource = PageTray.LARGE_CAPACITY;
        } else if(resource.getString(R.string.page_source_manual).equals(sourceValue)) {
            retSource = PageTray.MANUAL;
        } else if(resource.getString(R.string.page_source_tray1).equals(sourceValue)) {
            retSource = PageTray.TRAY1;
        } else if(resource.getString(R.string.page_source_tray2).equals(sourceValue)) {
            retSource = PageTray.TRAY2;
        } else if(resource.getString(R.string.page_source_tray3).equals(sourceValue)) {
            retSource = PageTray.TRAY3;
        } else if(resource.getString(R.string.page_source_tray4).equals(sourceValue)) {
            retSource = PageTray.TRAY4;
        } else if(resource.getString(R.string.page_source_tray5).equals(sourceValue)) {
            retSource = PageTray.TRAY5;
        } else if(resource.getString(R.string.page_source_tray6).equals(sourceValue)) {
            retSource = PageTray.TRAY6;
        } else if(resource.getString(R.string.page_source_tray7).equals(sourceValue)) {
            retSource = PageTray.TRAY7;
        } else if(resource.getString(R.string.page_source_tray8).equals(sourceValue)) {
            retSource = PageTray.TRAY8;
        } else if(resource.getString(R.string.page_source_tray9).equals(sourceValue)) {
            retSource = PageTray.TRAY9;
        } else {
            return null; 
        }

        return retSource;
    }

    /**
     * 設定可能な色を取得します。
     * Obtains the list of supported colors.
     *
     * @param context メインアクティビティのコンテキスト
     *                Context of MainActivity
     * @return 設定可能な色を示すPrintColorオブジェクトのリスト
     *         List of supported ScanColor objects.
     */
    public static List<PageTray> getSelectablePageTrayList(Context context){
        PrintSampleApplication app = (PrintSampleApplication)context.getApplicationContext();
        Map<PrintFile.PDL, PrintSettingSupportedHolder> supportedMap = app.getSettingSupportedDataHolders();
        PrintFile.PDL currentPDL = ((MainActivity)context).getSettingHolder().getSelectedPDL();

        if(null == currentPDL){
            return null;
        }

        return supportedMap.get(currentPDL).getSelectablePageTrayList();
    }

}
