package com.goldenKids.dotami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.flagmentView, new HomeFragment()).commit();
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.navigation_home){
                    getSupportFragmentManager().beginTransaction().replace(R.id.flagmentView, new HomeFragment()).commit();
                }
                else if (item.getItemId()==R.id.navigation_search) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flagmentView, new ListFragment()).commit();
                }
                else if (item.getItemId()==R.id.navigation_plus) {
                    Intent intent = new Intent(MainActivity.this, PotholeDetectionActivity.class);
                    startActivity(intent);
                }
                else if (item.getItemId()==R.id.navigation_favorites) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flagmentView, new FavoritesListFragment()).commit();
                }
                else if (item.getItemId()==R.id.navigation_mypage) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flagmentView, new HomeFragment()).commit();
                }
                return true;
            }
        });

    }
}