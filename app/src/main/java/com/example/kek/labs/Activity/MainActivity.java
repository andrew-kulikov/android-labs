package com.example.kek.labs.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kek.labs.Data.Storage;
import com.example.kek.labs.Managers.FileManager;
import com.example.kek.labs.Managers.ImageManager;
import com.example.kek.labs.Models.User;
import com.example.kek.labs.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private NavController controller;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUser();
        setupNavController();
        setupNavigationDrawer();
    }

    private void setupUser() {
        User user = new FileManager().getUser("storage.json");

        Storage.setApplicationUser(user);
    }

    private void setupNavController() {
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        controller = host.getNavController();
    }

    public void refreshHeader() {
        NavigationView navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        ImageView logo = headerView.findViewById(R.id.accountLogo);
        new ImageManager().LoadImage(
                logo,
                "logo.jpg",
                R.drawable.about);

        ((TextView) headerView.findViewById(R.id.header_email_text)).setText(Storage.getApplicationUser().getEmail());
    }

    private void setupNavigationDrawer() {
        NavigationView navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        ImageView logo = headerView.findViewById(R.id.accountLogo);

        new ImageManager().LoadImage(
                logo,
                "logo.jpg",
                R.drawable.about);
        ((TextView) headerView.findViewById(R.id.header_email_text)).setText(Storage.getApplicationUser().getEmail());

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationUI.setupWithNavController(navView, controller);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.accountInfoFragment);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.logout_menu_item:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.home_nav_item:
                        controller.navigate(R.id.homeFragment);
                        break;
                    case R.id.about_nav_item:
                        controller.navigate(R.id.aboutFragment);
                        break;
                    case R.id.info_nav_item:
                        controller.navigate(R.id.accountInfoFragment);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        switch (id) {
            case R.id.about_item:
                controller.navigate(R.id.aboutFragment);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
