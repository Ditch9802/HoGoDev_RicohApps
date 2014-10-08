package com.gso.hogoapi.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;

public class JsonHelper {
	private static final Gson GSON = new Gson();
	private static final String TAG = JsonHelper.class.getSimpleName();

	public static JSONObject fromJsonString(String jsonString) {
		Log.d(TAG, "json " + jsonString);
		try { 
			return new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getJsonObject(JSONObject target, String key) {
		try {
			return target.getJSONObject(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> T getValue(JSONObject target, String key) {
		try {
			return (T) target.get(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> T fromJson(String jsonString, Class<T> classOfT) {
		return GSON.fromJson(jsonString, classOfT);
	}
}
