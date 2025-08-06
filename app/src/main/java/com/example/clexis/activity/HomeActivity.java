package com.example.clexis.activity;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.clexis.R;
import com.example.clexis.fragments.AddFragment;
import com.example.clexis.fragments.HomeFragment;
import com.example.clexis.fragments.LibraryFragment;
import com.example.clexis.fragments.ProfileFragment;
import com.example.clexis.fragments.SocialsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView logo;
    private TextView headerText;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        EdgeToEdge.enable(this);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        logo = findViewById(R.id.logo);
        headerText = findViewById(R.id.pageHeader);
        if (bottomNavigationView == null) {
            throw new IllegalStateException("BottomNavigationView not found in layout");
        }

        // Load the default fragment
        if (savedInstanceState == null) {
            currentFragment = new HomeFragment();
            loadFragment(currentFragment, false);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = currentFragment;


            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                headerText.setVisibility(GONE);
                logo.setVisibility(View.VISIBLE);
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.library) {
                setHeader("Library");
                selectedFragment = new LibraryFragment();
            } else if (itemId == R.id.add) {
                setHeader("AI Tools");
                selectedFragment = new AddFragment();
            } else if (itemId == R.id.socials) {
                setHeader("Socials");
                selectedFragment = new SocialsFragment();
            } else if (itemId == R.id.profile) {
                setHeader("Profile");
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != currentFragment) {
                currentFragment = selectedFragment;
                loadFragment(selectedFragment, true);
            }
            return true;
        });
    }

    public void setHeader(String headername){
        headerText.setVisibility(View.VISIBLE);
        headerText.setText(headername);
        logo.setVisibility(GONE);
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}