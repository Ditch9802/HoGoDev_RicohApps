package com.gso.hogoapi;

import com.gso.hogoapi.util.SharedPreferencesManager;
import com.gso.serviceapilib.API;
import com.gso.serviceapilib.ServiceAPILibApplication;
import com.gso.serviceapilib.ServiceAction;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanSampleApplication;

public class HoGoApplication extends ScanSampleApplication {

	private static SharedPreferencesManager sharedPreferenceManager;
	private static String sToken;
	/**
	 * @param args
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		API.hostURL = "http://avalanche.hogodoc.com/HoGoPDFX/api";

	}

	private static HoGoApplication sInstance;

	public static HoGoApplication instace() {
		if (sInstance == null) {
			sInstance = new HoGoApplication();
		}
		return sInstance;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String getToken(Context context) {
		// TODO Auto-generated method stub
		String token = sToken;
		if(sToken == null){
			SharedPreferencesManager account = getSharedPreferencesManager(context);
			if(account !=null){
				token = account.loadSession();
			}
		}
		return token;
	}
	public void setToken(Context context, String token){
		if(token!=null){
			getSharedPreferencesManager(context).saveSession(token);
		}else{
			getSharedPreferencesManager(context).clearSession();
		}
	}
	
	public void setToken(Context context, String token, boolean isKeepLoggedIn){
		if(token!=null){
			if(isKeepLoggedIn){
				sToken = token;
				getSharedPreferencesManager(context).saveSession(token);
			}else{
				sToken = token;
			}
			
		}else{
			getSharedPreferencesManager(context).clearSession();
		}
	}
	
	public SharedPreferencesManager getSharedPreferencesManager(Context context){
		if(sharedPreferenceManager == null){
			sharedPreferenceManager = new SharedPreferencesManager(context);
		}
		
		return sharedPreferenceManager;
	}

}
