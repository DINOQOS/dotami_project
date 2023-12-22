package com.goldenKids.dotami;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public class PotholeReportService {

    // 서버의 기본 URL
    private static final String BASE_URL = "http://43.201.22.205/";

    // Retrofit 인스턴스 생성
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // 서버에서 데이터를 가져오기 위한 인터페이스 정의

    public interface FlaskApiService {
        @GET("/get_pothole_reports")
        Call<String> getPotholeReports();
    }
    public interface FlaskApiService2 {
        @GET("/get_report")
        Call<ReportResponse> getReport(@Query("ReportID") String reportID);
    }
    public interface FlaskApiService3 {
        @POST("/manage_favorite")
        Call<String> manageFavorite(@Body FavoriteRequest request);
    }
    public interface FlaskApiService4 {
        @GET("/get_favorites")
        Call<String> getFavorites(@Query("uid") String uid);
    }


    // 서버에서 데이터를 가져오는 메서드
    public static void fetchPotholeReports(final Callback<String> callback) {
        FlaskApiService apiService = retrofit.create(FlaskApiService.class);
        Call<String> call = apiService.getPotholeReports();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String reports = response.body();
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("서버 응답 실패 - HTTP 상태 코드: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
    public static void fetchReport(String reportID, final Callback<ReportResponse> callback) {
        FlaskApiService2 apiService = retrofit.create(FlaskApiService2.class);
        Call<ReportResponse> call = apiService.getReport(reportID);

        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if (response.isSuccessful()) {
                    ReportResponse report = response.body();
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("서버 응답 실패 - HTTP 상태 코드: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
    public static void manageFavorite(String uid, String reportId, final Callback<String> callback) {
        FlaskApiService3 apiService = retrofit.create(FlaskApiService3.class);
        FavoriteRequest request = new FavoriteRequest(uid, reportId);

        Call<String> call = apiService.manageFavorite(request);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String message = response.body();
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("서버 응답 실패 - HTTP 상태 코드: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
    public static void getFavorites(String uid, final Callback<String> callback) {
        FlaskApiService4 apiService = retrofit.create(FlaskApiService4.class);

        // 사용자의 UID 값을 이용해 서버에 요청을 보냅니다.
        Call<String> call = apiService.getFavorites(uid);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String favoriteReports = response.body();
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("서버 응답 실패 - HTTP 상태 코드: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


}
