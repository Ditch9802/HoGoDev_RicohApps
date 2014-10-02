package com.gso.hogoapi.service;

import com.gso.hogoapi.model.ResponseHistory;

import retrofit.http.POST;
import retrofit.http.Query;

public interface Api {

	@POST("/v1/GetAllHistory")
	public ResponseHistory getAllHistory(@Query("sEcho") String sEcho,
			@Query("displayStart") String displayStart,
			@Query("displayLength") String displayLength,
			@Query("Type") String Type, @Query("StartDate") String StartDate,
			@Query("StopDate") String StopDate,
			@Query("SessionID") String SessionID);
}
