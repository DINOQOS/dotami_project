package com.goldenKids.dotami;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FavoritesListFragment extends Fragment {
    public ArrayList<ListItem> listitems = new ArrayList<>();
    public RecyclerView recyclerView;
    public ListAdapter listAdapter;



    public static FavoritesListFragment newInstance(String param1, String param2) {
        FavoritesListFragment fragment = new FavoritesListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_search_f);
        listAdapter =new ListAdapter(listitems,getActivity());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        //test();
        checkfavorite("1");





        return view;
    }

    public void test(String[] numbers){
        List<String> numberList = Arrays.asList(numbers);
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
                            if (numberList.contains(reportID)){
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
    public void checkfavorite(String uid){
        PotholeReportService.getFavorites(uid, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String favoriteReports = response.body();
                    System.out.println(favoriteReports);

                    Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                    Matcher matcher = pattern.matcher(favoriteReports);

                    if (matcher.find()) {
                        // 정규 표현식에 매칭되는 부분 추출
                        String matched = matcher.group(1);

                        String[] numbers = matched.split(",");
                        for (String number : numbers) {
                            int num = Integer.parseInt(number.trim());

                        }
                        test(numbers);
                    }
                } else {

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