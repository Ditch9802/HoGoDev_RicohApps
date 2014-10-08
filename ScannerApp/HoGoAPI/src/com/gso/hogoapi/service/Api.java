package com.gso.hogoapi.service;

import org.json.JSONObject;

import com.gso.hogoapi.model.PackageDistributionHeaderResponse;
import com.gso.hogoapi.model.ResponseHistory;

import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Query;

public interface Api {

	@POST("/v1/GetAllHistory")
	public ResponseHistory getAllHistory(
			@Query("sEcho") String sEcho,
			@Query("displayStart") String displayStart,
			@Query("displayLength") String displayLength,
			@Query("Type") String Type, @Query("StartDate") String StartDate,
			@Query("StopDate") String StopDate,
			@Query("SessionID") String SessionID);
	
	@POST("/v1/GetPackageDistributionHeader")
	public PackageDistributionHeaderResponse getPackageDistributionHeader(
			@Query("SessionID") String sessionID,
			@Query("PackageID") String packageID);


	@POST("/v1/GetPackageDistributionDetail")
	public Response getPackageDistributionDetail(
			@Query("SessionID") String sessionID,
			@Query("PackageID") String packageID);
}
