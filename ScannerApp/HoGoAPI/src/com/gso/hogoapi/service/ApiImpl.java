package com.gso.hogoapi.service;

import org.json.JSONObject;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Query;

import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.model.PackageDistributionHeaderResponse;
import com.gso.hogoapi.model.ResponseHistory;
import com.gso.serviceapilib.API;

public class ApiImpl implements Api {

	private final Api api;

	public ApiImpl() {
//		RequestInterceptor requestInterceptor = new RequestInterceptor() {
//			@Override
//			public void intercept(RequestInterceptor.RequestFacade request) {
//				request.addHeader("Content-Type", "application/json");
//			}
//		};

		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint(""+API.hostURL)
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

	
	@Override
	@POST("/v1/GetPackageDistributionDetail")
	public Response getPackageDistributionDetail(
			@Query("SessionID") String sessionID,
			@Query("PackageID") String packageID) {
		try {
			return api.getPackageDistributionDetail(sessionID, packageID);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	@Override
	@POST("/v1/GetPackageDistributionHeader")
	public PackageDistributionHeaderResponse getPackageDistributionHeader(
			@Query("SessionID") String sessionID,
			@Query("PackageID") String packageID) {
		try {
			return api.getPackageDistributionHeader(sessionID, packageID);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

}
