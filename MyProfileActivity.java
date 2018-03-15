package com.chitrart.sunil.chitrart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyProfileActivity extends AppCompatActivity {
    private static final String TAG = "MyProfileActivity";
    private String userKey = null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ImageView imgProfile;
    private TextView txtEmailName, txtDisplayName, txtEmail, txtBioMsg;
    private Button btnEditProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        userKey = mAuth.getCurrentUser().getUid();
        imgProfile = findViewById(R.id.img_my_profile);
        txtEmailName = findViewById(R.id.tv_email_name);
        txtDisplayName = findViewById(R.id.tv_display_name);
        txtEmail = findViewById(R.id.tv_email_address);
        txtBioMsg = findViewById(R.id.tv_bio_msg);
        btnEditProfile = findViewById(R.id.btn_edit_profile);

        // for the action bar back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mDatabase.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //fetching data with dataSnapshot object
                String userProfileImage = (String) dataSnapshot.child("image").getValue();
                String displayName = (String) dataSnapshot.child("nickName").getValue();
                String bioText = (String) dataSnapshot.child("bioText").getValue();
                String nameEmail = mAuth.getCurrentUser().getDisplayName();
                String email= mAuth.getCurrentUser().getEmail();

                txtEmailName.setText(nameEmail);
                txtEmail.setText(email);
                if(userProfileImage != null) {
                    txtDisplayName.setText(displayName);
                    txtBioMsg.setText(bioText);
                    Picasso.with(MyProfileActivity.this).load(userProfileImage).into(imgProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set edit profile button's onClickListener()
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileActivity.this, SetProfileActivity.class));
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
