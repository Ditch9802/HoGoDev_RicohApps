package jp.co.ricoh.ssdk.sample.app.print.activity;

import android.content.Context;
import android.content.res.Resources;

import jp.co.ricoh.ssdk.sample.app.print.R;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSampleApplication;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSettingSupportedHolder;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintResolution;

import java.util.List;
import java.util.Map;

public class PrintResolutionUtil {
    
    public static String getPrintResolutionResourceString(Context context, PrintResolution printResolution){
        String ret = null;
//        String ret = context.getString(R.string.color_color);

        switch (printResolution) {
            case DPI_1200_1200_1:
                ret = context.getString(R.string.resolution_1200_1200_1);
                break;
            case DPI_1200_1200_2:
                ret = context.getString(R.string.resolution_1200_1200_2);
                break;
            case DPI_1200_600_1:
                ret = context.getString(R.string.resolution_1200_600_1);
                break;
            case DPI_200_200_1:
                ret = context.getString(R.string.resolution_200_200_1);
                break;
            case DPI_300_300_1:
                ret = context.getString(R.string.resolution_300_300_1);
                break;
            case DPI_400_400_1:
                ret = context.getString(R.string.resolution_400_400_1);
                break;
            case DPI_600_1200_1:
                ret = context.getString(R.string.resolution_600_1200_1);
                break;
            case DPI_600_600_1:
                ret = context.getString(R.string.resolution_600_600_1);
                break;
            case DPI_600_600_2:
                ret = context.getString(R.string.resolution_600_600_2);
                break;
            case DPI_600_600_4:
                ret = context.getString(R.string.resolution_600_600_4);
                break;
            case DPI_600_600_8:
                ret = context.getString(R.string.resolution_600_600_8);
                break;
            default:
                ret = null;
        }

        return ret;
    }

    /**
     * 色を表す文字列に応じたPrintResolutionオブジェクトを取得します。
     * Obtains the PrintResolution object from the print color text.
     *
     * @param context メインアクティビティへのコンテキスト
     *                Context of MainActivity
     * @param colorValue 色を表す文字列
     *                   Text value to indicate print color
     * @return PrintResolutionオブジェクト
     *         PrintResolution object
     */
    public static PrintResolution getPrintResolutionFromResourceString(Context context, String resolutionValue) {
        Resources resource = context.getResources();
        PrintResolution retResolution = null;
//        PrintResolution retColor = PrintResolution.COLOR;

        if(resource.getString(R.string.resolution_1200_1200_1).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_1200_1200_1;
        } else if(resource.getString(R.string.resolution_1200_1200_2).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_1200_1200_2;
        } else if(resource.getString(R.string.resolution_1200_600_1).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_1200_600_1;
        } else if(resource.getString(R.string.resolution_200_200_1).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_200_200_1;
        } else if(resource.getString(R.string.resolution_300_300_1).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_300_300_1;
        } else if(resource.getString(R.string.resolution_400_400_1).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_400_400_1;
        }else if(resource.getString(R.string.resolution_600_1200_1).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_600_1200_1;
        } else if(resource.getString(R.string.resolution_600_600_1).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_600_600_1;
        } else if(resource.getString(R.string.resolution_600_600_2).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_600_600_2;
        } else if(resource.getString(R.string.resolution_600_600_4).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_600_600_4;
        } else if(resource.getString(R.string.resolution_600_600_8).equals(resolutionValue)) {
            retResolution = PrintResolution.DPI_600_600_8;
        }  
        else {
            return null; 
        }

        return retResolution;
    }

    /**
     * 設定可能な色を取得します。
     * Obtains the list of supported colors.
     *
     * @param context メインアクティビティのコンテキスト
     *                Context of MainActivity
     * @return 設定可能な色を示すPrintResolutionオブジェクトのリスト
     *         List of supported ScanColor objects.
     */
    public static List<PrintResolution> getSelectablePrintResolutionList(Context context){
        PrintSampleApplication app = (PrintSampleApplication)context.getApplicationContext();
        Map<PrintFile.PDL, PrintSettingSupportedHolder> supportedMap = app.getSettingSupportedDataHolders();
        PrintFile.PDL currentPDL = ((MainActivity)context).getSettingHolder().getSelectedPDL();

        if(null == currentPDL){
            return null;
        }

        return supportedMap.get(currentPDL).getSelectablePrintResolutionList();
    }
}
