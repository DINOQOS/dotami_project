package com.goldenKids.dotami;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotholeInfoActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView location,txtm_lat,txtm_lang,txtm_date;
    private CheckBox ckbtn;
    private String receivedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole_info);
        Intent intent = getIntent();
        imageView = findViewById(R.id.imageView_favorties_detail);
        location = findViewById(R.id.textView_location);
        txtm_lat = findViewById(R.id.txtm_lat);
        txtm_lang = findViewById(R.id.txtm_lang);
        txtm_date = findViewById(R.id.txtm_date);
        ckbtn = findViewById(R.id.checkbtn);


        receivedData = intent.getStringExtra("key");
        System.out.println(receivedData);
        setInfo(receivedData);
        checkbox("1");

        ckbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkbtn("1",receivedData);
            }
        });



    }
    public void setInfo(String id){
        PotholeReportService.fetchReport(id, new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if (response.isSuccessful()) {
                    ReportResponse report = response.body();
                    if (report != null) {
                        setImage(report.getImageUrl());
                        String[] latLng = report.getLocation().split(",");
                        setLocation(latLng);
                        txtm_date.setText("날짜 : "+report.getDateReported());
                    } else {
                        // 서버 응답에 데이터가 없는 경우 처리
                        System.out.println("애송이 실패다");
                    }
                } else {
                    // 서버 응답이 실패한 경우 처리
                    System.out.println("zz");
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                // 네트워크 오류 또는 예외 처리
                System.out.println(t.getMessage().toString());
            }
        });
    }

    public void setImage(String url){
        Picasso.get()
                .load(url)
               // .placeholder(R.drawable.baseline_downloading_24) // 로딩 중에 표시할 이미지 (선택사항)
                /*.error(R.drawable.error_image) // 이미지 로딩 오류 시 표시할 이미지 (선택사항)
                .fit() // 이미지를 ImageView에 맞게 크기 조정 (선택사항)
                .centerCrop() // 이미지를 ImageView에 맞게 크롭 (선택사항)*/
                .into(imageView); // 이미지를 ImageView에 표시
    }
    public void setLocation(String[] latLng){

        txtm_lat.setText("위도 : " +latLng[0]);

        txtm_lang.setText("경도 : " +latLng[1]);

        double lat = Double.parseDouble(latLng[0]);
        double lang = Double.parseDouble(latLng[1]);

        String part = "";
        String[] locationtxt = getAddress(this,lat,lang).split(" ");
        for (int i = 2; i < locationtxt.length; i++) {
            part += locationtxt[i]+" ";
        }
        location.setText("위치 : "+part);
    }
    public String getAddress(Context mContext, double lat, double lng)
    {
        String nowAddr ="현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;

        try
        {
            if (geocoder != null)
            {
                address = geocoder.getFromLocation(lat, lng, 1);
                if (address != null && address.size() > 0)
                {
                    nowAddr = address.get(0).getAddressLine(0).toString();
                }
            }
        }
        catch (IOException e)
        {
            Toast.makeText(mContext, "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return nowAddr;
    }
    public void checkbtn(String uid, String reportid) {
        PotholeReportService.manageFavorite(uid, reportid, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String message = response.body();
                    // 처리된 결과를 이용하여 원하는 작업을 수행
                    System.out.println(message);
                } else {
                    // 서버 응답 실패 시 처리
                    System.out.println("실패");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // 통신 실패 시 처리
                System.out.println(t.getMessage());
            }
        });

    }
    public void checkbox(String uid){
        PotholeReportService.getFavorites(uid, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String favoriteReports = response.body();
                    System.out.println(favoriteReports);
                    // favoriteReports 문자열을 원하는 방식으로 처리합니다.
                    // 정규 표현식 패턴
                    Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                    Matcher matcher = pattern.matcher(favoriteReports);

                    if (matcher.find()) {
                        // 정규 표현식에 매칭되는 부분 추출
                        String matched = matcher.group(1);

                        String[] numbers = matched.split(",");
                        for (String number : numbers) {
                            int num = Integer.parseInt(number.trim());
                            if (Integer.parseInt(receivedData)==num){
                                ckbtn.setChecked(true);
                            }

                        }
                    }
                } else {
                    // 실패 시 처리
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // 실패 시 처리
                System.out.println(t.getMessage());
            }
        });

    }


}