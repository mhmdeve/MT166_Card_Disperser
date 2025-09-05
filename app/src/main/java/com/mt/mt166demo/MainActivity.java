package com.mt.mt166demo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance = null;
    private static boolean bIsTrans = true;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // updated layout

        instance = this;
        Application.cr.MtSetLanguage(bIsTrans);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach tabs to ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(getString(R.string.base));
                            break;
                        case 1:
                            tab.setText(getString(R.string.TypeAB));
                            break;
                        case 2:
                            tab.setText(getString(R.string.RFM1));
                            break;
                        case 3:
                            tab.setText(getString(R.string.UL));
                            break;
                    }
                }).attach();

        setStatus(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.connect) {
            ConnDialog dlg = new ConnDialog(this);
            dlg.setCloseListener(isOk -> {
                if (isOk) {
                    setStatus(true);
                }
            });
            dlg.show();
        } else if (id == R.id.disconnect) {
            try {
                Application.cr.MtDisConnect();
                setStatus(false);
            } catch (Exception e) {
                Application.showError(e.getMessage(), this);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setStatus(boolean isConnected) {
        String title = getString(R.string.app_name);
        if (isConnected) {
            String status = getString(R.string.connected);
            title = title + " [" + status + "]";
        }
        setTitle(title);
    }
}
