package com.example.eventfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private static final String SEARCH = "SEARCH";
    private static final String FAVORITES = "FAVORITES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_EventFinder);
        setContentView(R.layout.activity_main);

        ViewPager2 view_pager_2_main_tab = findViewById(R.id.view_pager_main_tab);
        TabLayout main_tab_layout = findViewById(R.id.main_tab_layout);
        AdapterMainViewPager2 adapter = new AdapterMainViewPager2(getSupportFragmentManager(), getLifecycle());
        view_pager_2_main_tab.setAdapter(adapter);

        new TabLayoutMediator(main_tab_layout, view_pager_2_main_tab, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(SEARCH);
                        break;
                    case 1:
                        tab.setText(FAVORITES);
                        break;
                }
            }
        }).attach();
    }
}