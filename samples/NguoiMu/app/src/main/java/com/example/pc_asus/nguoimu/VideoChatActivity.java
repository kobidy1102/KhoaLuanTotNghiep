package com.example.pc_asus.nguoimu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VideoChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headView= navigationView.getHeaderView(0);
        final ImageView img= (ImageView) headView.findViewById(R.id.img_bar_avatar);
        final TextView tv_name=(TextView) headView.findViewById(R.id.tv_bar_name);
        final String[] photoURL = new String[1];

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid= mCurrentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("NguoiMu").child("Users").child(uid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_name.setText(dataSnapshot.child("name").getValue().toString());
                photoURL[0] =dataSnapshot.child("photoURL").getValue().toString();
               // Picasso.with(VideoChatActivity.this).load(dataSnapshot.child("photoURL").getValue().toString()).into(img);
                RequestOptions  requestOptions = new RequestOptions();
                requestOptions.fitCenter();
                requestOptions.placeholder(R.mipmap.user);
                Glide.with(getApplicationContext())
                        .load(photoURL[0])
                        .apply(requestOptions)
                        //   .override(200,150)
                        .into(img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        getMenuInflater().inflate(R.menu.video_chat, menu);
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

        if (id == R.id.nav_accountSetting) {
            startActivity(new Intent(VideoChatActivity.this,AccountSettingsActivity.class));
           // Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_friends) {
            Toast.makeText(this, "friends", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(VideoChatActivity.this,SignInActivity.class));
            finish();
          //  Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
