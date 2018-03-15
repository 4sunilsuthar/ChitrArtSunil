package com.chitrart.sunil.chitrart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayPostActivity extends AppCompatActivity {

    private static final String TAG = "DPActivity";
    private String postKey = null;
    private DatabaseReference mDatabase, mDatabaseUsers;
    private FirebaseAuth maAuth;

    private ImageView singlePostImage, singlePostUserProfile;
    private TextView singlePostTitle, singlePostDesc, singlePostUserName, singlePostDateTime;
    private Button btnDeletePost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_post);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        postKey = getIntent().getExtras().getString("PostId");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ChitrArt");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        maAuth = FirebaseAuth.getInstance();
        singlePostImage = findViewById(R.id.display_post_image);
        singlePostTitle = findViewById(R.id.display_post_title);
        singlePostDesc = findViewById(R.id.display_post_desc);
        singlePostUserName = findViewById(R.id.display_post_by_user);
        singlePostUserProfile = findViewById(R.id.display_post_profile_image);
        singlePostDateTime = findViewById(R.id.display_post_date_time);
        btnDeletePost = findViewById(R.id.btn_delete_post);
        //set button visibility to false
        btnDeletePost.setVisibility(View.INVISIBLE);

        //set delete button's onClickListener()
        btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
                Log.e(TAG, "new PostKey is : " + postKey);
                mDatabase.child(postKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        startActivity(new Intent(DisplayPostActivity.this, MainActivity.class));

                    }
                });

            }
        });
        mDatabase.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    String postTitle = (String) dataSnapshot.child("title").getValue();
                    String postImage = (String) dataSnapshot.child("image").getValue();
                    String postDesc = (String) dataSnapshot.child("description").getValue();
                    long postDate = (long) dataSnapshot.child("timestamp").getValue();

                    //getting values from Users object


                    String postUserId = (String) dataSnapshot.child("uid").getValue();
                    mDatabaseUsers.child(postUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String postUserProfile = (String) dataSnapshot.child("image").getValue();
                            String postUserNickName = (String) dataSnapshot.child("nickName").getValue();
                            singlePostUserName.setText(postUserNickName);
                            Picasso.with(DisplayPostActivity.this).load(postUserProfile).into(singlePostUserProfile);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    singlePostTitle.setText(postTitle);
                    singlePostDesc.setText(postDesc);

                    String txtDate = new MainActivity().getDateDiff(postDate);
//                Log.e(TAG,"value of Date is "+txtDate);
                    singlePostDateTime.setText(txtDate);
                    Picasso.with(DisplayPostActivity.this).load(postImage).into(singlePostImage);


                    //check user id and show delete button if user is the one who posted that image
                    if (maAuth.getCurrentUser().getUid().equals(postUserId)) {
                        btnDeletePost.setVisibility(View.VISIBLE);
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "Null Pointer Exception Occurred");
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
