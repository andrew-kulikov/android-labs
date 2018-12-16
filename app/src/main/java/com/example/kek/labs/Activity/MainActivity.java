package com.example.kek.labs.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kek.labs.Fragment.AccountEditFragment;
import com.example.kek.labs.Managers.ImageManager;
import com.example.kek.labs.Managers.UserManager;
import com.example.kek.labs.Models.User;
import com.example.kek.labs.R;
import com.example.kek.labs.Listeners.DownloadImageListener;
import com.example.kek.labs.Listeners.UserUpdateListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private UserManager userManager;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userManager = UserManager.getInstance(this);

        setupNavController();
        setupNavigationDrawer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        userManager = UserManager.getInstance(this);
    }

    private void setupNavController() {
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
    }

    public void refreshHeader() {
        NavigationView navView = findViewById(R.id.nav_view);
        final View headerView = navView.getHeaderView(0);
        ImageView logo = headerView.findViewById(R.id.accountLogo);

        DownloadImageListener listener = new DownloadImageListener() {
            @Override
            public void onImageDownloadFinished() {

            }
        };
        new ImageManager().LoadAvatar(logo, R.drawable.about, listener);

        userManager.getUser(new UserUpdateListener() {
            @Override
            public void onUpdateUser(User user) {
                ((TextView) headerView.findViewById(R.id.header_email_text)).setText(user.getEmail());
            }
        });
    }

    private void setupNavigationDrawer() {
        NavigationView navView = findViewById(R.id.nav_view);
        final View headerView = navView.getHeaderView(0);
        ImageView logo = headerView.findViewById(R.id.accountLogo);

        refreshHeader();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationUI.setupWithNavController(navView, navController);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.accountInfoFragment);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                if (currentFragment instanceof AccountEditFragment) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    showNavigateDialog(id);
                } else {
                    MainActivity.this.onNavigationItemSelected(id);
                }
                return true;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showNavigateDialog(final int id) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(getString(R.string.navigation_confirmation_header));
        alertBuilder.setMessage(getString(R.string.navigation_confirmation_content));
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.onNavigationItemSelected(id);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void onNavigationItemSelected(int id) {
        switch (id) {
            case R.id.logout_menu_item:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.home_nav_item:
                navController.navigate(R.id.homeFragment);
                break;
            case R.id.about_nav_item:
                navController.navigate(R.id.aboutFragment);
                break;
            case R.id.info_nav_item:
                navController.navigate(R.id.accountInfoFragment);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
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
                Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                if (currentFragment instanceof AccountEditFragment) {
                    showNavigateDialog(R.id.about_nav_item);
                } else {
                    navController.navigate(R.id.aboutFragment);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
