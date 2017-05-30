package com.byteshaft.restaurantadmin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.byteshaft.restaurantadmin.accountfragments.ChangePassword;
import com.byteshaft.restaurantadmin.accountfragments.Login;
import com.byteshaft.restaurantadmin.gettersetter.OrderDetails;
import com.byteshaft.restaurantadmin.menu.MenuMain;
import com.byteshaft.restaurantadmin.restaurantfragments.Promotions;
import com.byteshaft.restaurantadmin.restaurantfragments.TablesFragment;
import com.byteshaft.restaurantadmin.restaurantfragments.TodayOrders;
import com.byteshaft.restaurantadmin.restaurantfragments.UpdateRestaurant;
import com.byteshaft.restaurantadmin.utils.AppGlobals;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static HashMap<String, ArrayList<OrderDetails>> sHashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sHashMap = new HashMap<>();
        Log.i("TAG", AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_RESTAURANT_NAME));
        Log.i("TAG", AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        Log.i("TAG","id" + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID));
        View headerView;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.name);
        TextView email = (TextView) headerView.findViewById(R.id.email);
        name.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_RESTAURANT_NAME));
        email.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        loadFragment(new TablesFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.tables) {
            loadFragment(new TablesFragment());
        } else if (id == R.id.restaurant_menu) {
            loadFragment(new MenuMain());
        } else if (id == R.id.promotions) {
            loadFragment(new Promotions());

        } else if (id == R.id.update_restaurant) {
            loadFragment(new UpdateRestaurant());

        } else if (id == R.id.change_password) {
            loadFragment(new ChangePassword());

        } else if (id == R.id.today_orders) {
            loadFragment(new TodayOrders());

        } else if (id == R.id.admin_logout) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Confirmation");
            alertDialogBuilder.setMessage("Do you really want to logout?")
                    .setCancelable(false).setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            AppGlobals.clearSettings();
                            loadFragment(new Login());
                        }
                    });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commit();
    }




}
