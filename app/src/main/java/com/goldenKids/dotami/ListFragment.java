package com.goldenKids.dotami;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListFragment extends Fragment {
    public ArrayList<ListItem> listitems = new ArrayList<>();
    public RecyclerView recyclerView;
    public ListAdapter listAdapter;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_search);
        listAdapter =new ListAdapter(listitems,getActivity());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        test();
        //listAdapter.setListItems(listitems);






        return view;
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
                        System.out.println("받아오는건 성공");
                        for (int i = 0; i < potholeReportsArray.length(); i++) {
                            JSONObject reportObject = potholeReportsArray.getJSONObject(i); // 배열 내 각 객체 추출
                            String reportID = reportObject.getString("ReportID");
                            String dateReported = reportObject.getString("DateReported");
                            String location = reportObject.getString("Location");
                            String[] latLng = location.split(",");
                            double lat,lang;
                            try {
                                lat =Double.parseDouble(latLng[0]);
                                lang =Double.parseDouble(latLng[1]);

                            } catch (NumberFormatException e) {
                                // 위치 정보 파싱 오류가 발생한 경우 다음 루프로 넘어갑니다.
                                continue;
                            }
                            String part="";
                            String[] loname = getAddress(getActivity(),lat,lang).split(" ");
                            for (int j = 2; j < loname.length; j++) {
                                part += loname[j]+" ";
                            }
                            System.out.println(part);
                            listitems.add(new ListItem(part,dateReported,location,reportID));
                        }
                        listAdapter.setListItems(listitems);
                        System.out.println("이거까지 성공함");

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
}