package com.gso.hogoapi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class SharedPreferencesManager {

	private Context _context;
	private SharedPreferences _sharedPreferences;

	public static final String SESSION_DATA = "SESSION_DATA";
	public static final String SCAN_DATA = "SCAN_DATA";
	public static final String SESSION = "SESSION";
	public static final String PAGE_SIZE ="PAGE_SIZE";
	public static final String COLOR ="COLOR";
	public static final String QUALITY ="QUALITY";
	public static final String PREVIEW ="PREVIEW";
	
	public SharedPreferencesManager(Context context) {
		_context = context;
		_sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(_context);
	}



	
	// ------------------------FACE BOOK---------------------------
	public boolean saveSession(String session) {
		Editor editor = _context.getSharedPreferences(SESSION_DATA,
				Context.MODE_PRIVATE).edit();
		editor.putString(SESSION, session);
		return editor.commit();
	}

	public void clearSession() {
		Editor editor = _context.getSharedPreferences(SESSION_DATA,
				Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public String loadSession() {
		SharedPreferences savedSession = _context.getSharedPreferences(
				SESSION_DATA, Context.MODE_PRIVATE);
		String session = savedSession.getString(SESSION, null);
		
		if (isSessionValid(session))
			return session;		
		else
			return null;
	}

	// --------------------------------------------------
	public boolean saveValue(String key, String value) {
		Editor editor = _context.getSharedPreferences(SCAN_DATA,
				Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		return editor.commit();
	}
	public String getValue(String key) {
		SharedPreferences savedSession = _context.getSharedPreferences(
				SCAN_DATA, Context.MODE_PRIVATE);
		return savedSession.getString(key, null);
		
	}
	private boolean isSessionValid(String session) {
		// TODO Auto-generated method stub
		return session == null? false : true;
	}




	public SharedPreferences getPrefs() {
		return _sharedPreferences;
	}
}