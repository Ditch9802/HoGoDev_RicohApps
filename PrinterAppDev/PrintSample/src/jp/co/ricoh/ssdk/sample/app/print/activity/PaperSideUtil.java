package jp.co.ricoh.ssdk.sample.app.print.activity;

import android.content.Context;
import android.content.res.Resources;

import jp.co.ricoh.ssdk.sample.app.print.R;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSampleApplication;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSettingSupportedHolder;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PaperSide;

import java.util.List;
import java.util.Map;

public class PaperSideUtil {

    public static String getPaperSideResourceString(Context context, PaperSide paperSide){
        String ret = null;
//        String ret = context.getString(R.string.color_color);

        switch (paperSide) {
            case ONE_SIDED:
                ret = context.getString(R.string.paper_side_one_sided);
                break;
            case TOP_TO_TOP:
                ret = context.getString(R.string.paper_side_top_to_top);
                break;
            case TOP_TO_BOTTOM:
                ret = context.getString(R.string.paper_side_top_to_bottom);
                break;
            case MAGAZINE_LEFT:
                ret = context.getString(R.string.paper_side_magazine_left);
                break;
            case MAGAZINE_RIGHT:
                ret = context.getString(R.string.paper_side_magazine_right);
                break;
            case BOOKLET_LEFT:
                ret = context.getString(R.string.paper_side_booklet_left);
                break;
            case BOOKLET_RIGHT:
                ret = context.getString(R.string.paper_side_booklet_right);
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
    public static PaperSide getPaperSideFromResourceString(Context context, String sideValue) {
        Resources resource = context.getResources();
        PaperSide retSide = null;
//        PrintColor retColor = PrintColor.COLOR;

        if(resource.getString(R.string.paper_side_one_sided).equals(sideValue)) {
            retSide = PaperSide.ONE_SIDED;
        } else if(resource.getString(R.string.paper_side_top_to_top).equals(sideValue)) {
            retSide = PaperSide.TOP_TO_TOP;
        } else if(resource.getString(R.string.paper_side_top_to_bottom).equals(sideValue)) {
            retSide = PaperSide.TOP_TO_BOTTOM;
        } else if(resource.getString(R.string.paper_side_magazine_left).equals(sideValue)) {
            retSide = PaperSide.MAGAZINE_LEFT;
        } else if(resource.getString(R.string.paper_side_magazine_right).equals(sideValue)) {
            retSide = PaperSide.MAGAZINE_RIGHT;
        } else if(resource.getString(R.string.paper_side_booklet_left).equals(sideValue)) {
            retSide = PaperSide.BOOKLET_LEFT;
        } else if(resource.getString(R.string.paper_side_booklet_right).equals(sideValue)) {
            retSide = PaperSide.BOOKLET_RIGHT; 
        }else {
            return null; 
        }

        return retSide;
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
    public static List<PaperSide> getSelectablePaperSideList(Context context){
        PrintSampleApplication app = (PrintSampleApplication)context.getApplicationContext();
        Map<PrintFile.PDL, PrintSettingSupportedHolder> supportedMap = app.getSettingSupportedDataHolders();
        PrintFile.PDL currentPDL = ((MainActivity)context).getSettingHolder().getSelectedPDL();

        if(null == currentPDL){
            return null;
        }

        return supportedMap.get(currentPDL).getSelectablePaperSideList();
    }
}
