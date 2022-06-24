package com.example.loaescuela.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.loaescuela.R;
import com.example.loaescuela.data.SessionPrefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar myToolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    CoordinatorLayout coordinatorLayout;
    FrameLayout frameLayout;


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_assists) {
            startActivity(new Intent(this, AssistsAndIncomesStudentActivity.class));
        } else if (id == R.id.nav_payments) {
             startActivity(new Intent(this, ListIncomesDayActivity.class));
        } else if (id == R.id.nav_create_student) {
            startActivity(new Intent(this, CreateStudentActivity.class));
        } else if (id == R.id.nav_resum) {
            startActivity(new Intent(getBaseContext(),AssistsResumByDayActivity.class));
        } else if (id == R.id.nav_planillas) {
            startActivity(new Intent(this, PlanillasActivity.class));

        }  else if (id == R.id.nav_students) {
            startActivity(new Intent(this, StudentsListActivity.class));

        }else if( id == R.id.nav_home){
           // selectFragment(0);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 1 - Configure Toolbar
    private void configureToolBar(){
        this.myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerLayout.setScrimColor(getResources().getColor(R.color.shadownav));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = findViewById(R.id.nav_view);

        View headerLayout = navigationView.getHeaderView(0);
        TextView name = headerLayout.findViewById(R.id.user_name);
        //name.setText(SessionPrefs.get(this).getName());
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
      /*  MenuItem liquidations = menu.findItem(R.id.nav_liquidations);
        MenuItem workers = menu.findItem(R.id.nav_workers);
        MenuItem prices = menu.findItem(R.id.nav_prices);
        MenuItem allOrders = menu.findItem(R.id.nav_orders);
        MenuItem zones = menu.findItem(R.id.nav_zones);
        MenuItem home = menu.findItem(R.id.nav_home);*/


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        frameLayout =  findViewById(R.id.main_content);
        coordinatorLayout=findViewById(R.id.rl);



        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }


        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main_drawer;
    }

    public void signOut(){
        SessionPrefs.get(getBaseContext()).logOut();
        startActivity(new Intent(getBaseContext(), LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void hideToolbar(){
        myToolbar.setVisibility(View.GONE);
    }

    public void showToolbar(){
        myToolbar.setVisibility(View.VISIBLE);
    }


}