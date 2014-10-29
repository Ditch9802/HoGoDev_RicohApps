package jp.co.ricoh.ssdk.sample.app.print.activity;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

    public static final String HOGO_PRINT_PREFS = "hogo_print_prefs";
    public static final String PRINT_COLOR = "print_color";
    public static final String PAPER_SOURCE = "print_paper_source";
    public static final String PAPER_SIDE = "print_paper_side";
    public static final String PRINT_RESOLUTION = "print_resolution";
    public static final String PRINT_NUMBER_COPIES = "print_number_copies";
    public static boolean setString(Context context, String key, String value){
        SharedPreferences prefs = context.getSharedPreferences(HOGO_PRINT_PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        return editor.commit();
    }
    
    public static String getString(Context context, String key){
        SharedPreferences prefs = context.getSharedPreferences(HOGO_PRINT_PREFS, 0);
        String value = prefs.getString(key, "");
        return value;
    }
    
    public static void clearPrefs(Context context){
        SharedPreferences settings = context.getSharedPreferences(HOGO_PRINT_PREFS, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }
}
