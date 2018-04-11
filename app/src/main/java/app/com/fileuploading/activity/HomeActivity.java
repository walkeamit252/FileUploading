package app.com.fileuploading.activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import app.com.fileuploading.R;
import app.com.fileuploading.fragment.ProfileFragment;
import app.com.fileuploading.fragment.UploadFileFragment;
import app.com.fileuploading.fragment.UploadedListFragment;
import app.com.fileuploading.model.SignupModel;
import app.com.fileuploading.utils.Constants;


public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private TextView mTxtUserName;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mTxtUserName=(TextView)findViewById(R.id.txt_profile_user_name);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.icon_menu_list);

        setDefaultView();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);
/*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        mTxtUserName = (TextView)header.findViewById(R.id.txt_profile_user_name);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        Gson gson = new Gson();
        String json = prefs.getString(Constants.USER_MODEL, "");
        if(!TextUtils.isEmpty(json)){
            SignupModel model = gson.fromJson(json, SignupModel.class);
            if(model!=null){
                mTxtUserName.setText(model.getName());
            }
        }




    }



    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_upload_file:
                fragmentClass = UploadFileFragment.class;
                break;

            case R.id.nav_list_file:
                fragmentClass = UploadedListFragment.class;
                break;

            case R.id.nav_view_profile:
                fragmentClass = ProfileFragment.class;
                break;

            case R.id.nav_log_out:
                logout();
                mDrawerLayout.closeDrawers();
                return;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
    }


    public void setDefaultView() {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = UploadFileFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();
        mDrawerLayout.closeDrawers();
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Signout");
        builder.setMessage("Are you sure you want to signout?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                FirebaseAuth.getInstance().signOut();
                dialog.dismiss();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}