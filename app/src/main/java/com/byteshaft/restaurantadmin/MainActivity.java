package com.byteshaft.restaurantadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.restaurantadmin.accountfragments.ChangePassword;
import com.byteshaft.restaurantadmin.accountfragments.Login;
import com.byteshaft.restaurantadmin.gettersetter.TableDetail;
import com.byteshaft.restaurantadmin.restaurantfragments.Promotions;
import com.byteshaft.restaurantadmin.restaurantfragments.UpdateRestaurant;
import com.byteshaft.restaurantadmin.utils.AppGlobals;
import com.byteshaft.restaurantadmin.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemClickListener, HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private GridView tableView;
    private Button mAddTableButton;
    public ArrayList<TableDetail> tableDetails;
    private TableAdapter tableAdapter;
    public ArrayList<Integer> alreadyAddedTableNumber;
    private static MainActivity sInstance;
    private int currentArraySize = 0;
    public static boolean updated = false;

    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sInstance = this;
        alreadyAddedTableNumber = new ArrayList<>();
        tableView = (GridView) findViewById(R.id.table_view);
        mAddTableButton = (Button) findViewById(R.id.add_table_button);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tableDetails = new ArrayList<>();
        Log.i("TAG", AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
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
        tableView.setOnItemClickListener(this);
        mAddTableButton.setOnClickListener(this);
        getTables();
        tableAdapter = new TableAdapter(tableDetails);
        tableView.setAdapter(tableAdapter);
        tableView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), TableDetails.class);
                intent.putExtra("serializer" , tableDetails.get(i));
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tableDetails.size() > currentArraySize || updated) {
            tableAdapter.notifyDataSetChanged();
            if (updated) {
                updated = false;
            } else {
                currentArraySize = tableDetails.size();
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.tables) {

        } else if (id == R.id.restaurant_menu) {

        } else if (id == R.id.promotions) {
            loadFragment(new Promotions());

        } else if (id == R.id.update_restaurant) {
            loadFragment(new UpdateRestaurant());

        } else if (id == R.id.change_password) {
            loadFragment(new ChangePassword());

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

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, TableDetails.class));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void getTables() {
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%srestaurant/tables/", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                System.out.println(request.getResponseURL());
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText());
                        try {
                            JSONArray jsonArray = new JSONArray(request.getResponseText());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TableDetail tableDetail = new TableDetail();
                                tableDetail.setId(jsonObject.getInt("id"));
                                tableDetail.setRestaurantId(jsonObject.getInt("id"));
                                tableDetail.setServiceAble(jsonObject.getBoolean("serviceable"));
                                tableDetail.setTableNumber(jsonObject.getInt("table_number"));
                                tableDetail.setNumberOfChair(jsonObject.getInt("number_of_chairs"));
                                tableDetail.setMinimumBookingTime(jsonObject.getInt("minimum_booking_time"));
                                tableDetail.setLocationInRestaurant(jsonObject.getString("location"));
                                alreadyAddedTableNumber.add(jsonObject.getInt("table_number"));
                                tableDetails.add(tableDetail);
                                tableAdapter.notifyDataSetChanged();
                            }
                            currentArraySize = tableDetails.size();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
        }

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }


    private class TableAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private ArrayList<TableDetail> addTables;

        public TableAdapter(ArrayList<TableDetail> addTables) {
            this.addTables = addTables;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.delegate_tables, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tableNumber = (TextView) convertView.findViewById(R.id.table_number_text_view);
                viewHolder.tableStatus = (TextView) convertView.findViewById(R.id.status_text_view);
                viewHolder.tableImage = (ImageView) convertView.findViewById(R.id.table_image_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TableDetail tableDetails = addTables.get(position);
            viewHolder.tableNumber.setText(String.valueOf(tableDetails.getTableNumber()));
            if (tableDetails.isServiceAble()) {
                viewHolder.tableStatus.setText("Available");
            } else {
                viewHolder.tableStatus.setText("Reserved");
            }
            viewHolder.tableImage.setImageResource(R.mipmap.main_table);
            return convertView;
        }

        @Override
        public int getCount() {
            return addTables.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }

        private class ViewHolder {
            TextView tableNumber;
            TextView tableStatus;
            ImageView tableImage;
        }

    }
}
