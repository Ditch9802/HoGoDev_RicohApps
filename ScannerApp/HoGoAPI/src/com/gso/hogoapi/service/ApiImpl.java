package com.gso.hogoapi.service;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.POST;
import retrofit.http.Query;

import com.gso.hogoapi.model.ResponseHistory;

public class ApiImpl implements Api {

	private final Api api;

	public ApiImpl() {
		RequestInterceptor requestInterceptor = new RequestInterceptor() {
			@Override
			public void intercept(RequestInterceptor.RequestFacade request) {
				request.addHeader("Content-Type", "application/json");
			}
		};

		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint("http://avalanche.hogodoc.com/HoGo/api")
//				.setRequestInterceptor(requestInterceptor)
				.setLogLevel(RestAdapter.LogLevel.FULL).build();

		api = restAdapter.create(Api.class);
	}

	@Override
	@POST("/v1/GetAllHistory")
	public ResponseHistory getAllHistory(@Query("sEcho") String sEcho,
			@Query("displayStart") String displayStart,
			@Query("displayLength") String displayLength,
			@Query("Type") String Type, @Query("StartDate") String StartDate,
			@Query("StopDate") String StopDate,
			@Query("SessionID") String SessionID) {
		try {
			return api.getAllHistory(sEcho, displayStart, displayLength, Type,
					StartDate, StopDate, SessionID);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

}
