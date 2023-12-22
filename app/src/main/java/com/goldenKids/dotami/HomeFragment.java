package com.goldenKids.dotami;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements OnMapReadyCallback  {
    private GoogleMap googleMap;

    private LinearLayout linearLayout;
    private TextView txtd; //날짜
    private TextView txtl; //위치

    private ImageButton imgbtn;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initmap();
        test();
        txtl =view.findViewById(R.id.txt_locationinf);
        txtd = view.findViewById(R.id.txt_dateinf);
        linearLayout = view.findViewById(R.id.map_info_layout2);
        imgbtn = view.findViewById(R.id.btn_checkinf);

        return view;


    }
    public void initmap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "mapFragment is null");
        }
    }
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        System.out.println("실행");
        LatLng daejeonCityHall = new LatLng(36.3504119, 127.3845475);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(daejeonCityHall, 12f));
    }
    public void test(){
        PotholeReportService.fetchPotholeReports(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String jsonString = response.body();
                    System.out.println(jsonString);

                    try {
                        JSONObject responseJsonObject = new JSONObject(jsonString); // JSON 문자열을 JSONObject로 변환
                        JSONArray potholeReportsArray = responseJsonObject.getJSONArray("pothole_reports"); // "pothole_reports" 배열 추출

                        for (int i = 0; i < potholeReportsArray.length(); i++) {
                            JSONObject reportObject = potholeReportsArray.getJSONObject(i); // 배열 내 각 객체 추출
                            String reportID = reportObject.getString("ReportID");
                            String dateReported = reportObject.getString("DateReported");
                            String location = reportObject.getString("Location");
                            String[] latLng = location.split(",");
                            double latitude;
                            double longitude;

                            try {
                                latitude = Double.parseDouble(latLng[0]);
                                longitude = Double.parseDouble(latLng[1]);
                            } catch (NumberFormatException e) {
                                // 위치 정보 파싱 오류가 발생한 경우 다음 루프로 넘어갑니다.
                                System.out.println(e.getMessage());
                                continue;
                            }
                            // 마커를 지도에 추가합니다.
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .title(reportID)
                                    .snippet("Date Reported: " + dateReported);
                            googleMap.addMarker(markerOptions);
                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {

                                    linearLayout.setVisibility(View.VISIBLE);
                                    txtl.setText(marker.getPosition().toString());
                                    txtd.setText(marker.getSnippet());
                                    imgbtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getActivity(), PotholeInfoActivity.class);

                                            intent.putExtra("key", marker.getTitle());

                                            startActivity(intent);

                                        }
                                    });

                                    return true;
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    int statusCode = response.code();
                    System.out.println( "서버 응답 실패 - HTTP 상태 코드: " + statusCode);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("서버 통신 실패: " + t.getMessage());
            }
        });


    }




}