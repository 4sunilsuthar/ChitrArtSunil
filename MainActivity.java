package com.chitrart.sunil.chitrart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "MainActivity";
    private RecyclerView mImageList;
    private DatabaseReference mDatabase, mDatabaseUsers;
    Handler handler;
    Runnable runnable;
    Timer timer;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressBar progressBar = findViewById(R.id.progress_bar_loading);

        progressBar.setVisibility(View.VISIBLE);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                timer.cancel();
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 3000, 500);
        /*Toolbar toolbar = findViewById(R.id.toolbar); //find out why this Code ???
        setSupportActionBar(toolbar);*/

  /*
        LinearLayout linearLayout = findViewById(R.id.layout_main_linear);
        ProgressBar progressBar = new ProgressBar(MainActivity.this,null,android.R.attr.progressBarStyleLarge);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1000,1000);
        //Log.e(TAG,"Progress bar created !!!");


        linearLayout.addView(progressBar,params);
        //Log.e(TAG,"Progress bar View Set!!!");
        progressBar.setVisibility(View.VISIBLE);
        Log.e(TAG,"Progress bar Visible NOW!!!");//To show ProgressBar
        Toast.makeText(getApplicationContext(),"Porgress showing",Toast.LENGTH_SHORT).show();

*/
        Log.e(TAG, "Main Activity Called ");
        mImageList = findViewById(R.id.images_list);
        mImageList.setHasFixedSize(true);
        mImageList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ChitrArt");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        //authentication here
        mAuth = FirebaseAuth.getInstance();

        // Specify mAuthStateListener for changes in the States of user
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) { //meaning user has logged out
//                    Toast.makeText(getApplicationContext(), "User is Not Logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish(); //ends the current activity
                } else {

                    //meaning user is still logged in
                    //Toast.makeText(getApplicationContext(), ">>Welcome User<<", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        String userKey = mAuth.getCurrentUser().getUid();

        mDatabaseUsers.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //fetching data with dataSnapshot object
                String userProfileImage = (String) dataSnapshot.child("image").getValue();
                String displayName = (String) dataSnapshot.child("nickName").getValue();
                ImageView imgProfile = findViewById(R.id.user_profile_image);
                TextView tvUserName = findViewById(R.id.user_name);
                if (userProfileImage != null) {
                    Picasso.with(MainActivity.this).load(userProfileImage).into(imgProfile);
                    tvUserName.setText(displayName);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        mDatabaseUsers.child(userId).child("image").setValue(downloadUrl);

        FirebaseRecyclerAdapter<ImagePost, ImagePostViewHolder> firebaseRecyclerAdapter;
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ImagePost, ImagePostViewHolder>(ImagePost.class, R.layout.image_row_card, ImagePostViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(final ImagePostViewHolder viewHolder, final ImagePost model, int position) {
                final String postKey = getRef(position).getKey();


                if (postKey == null) {
//                    Toast.makeText(getApplicationContext(),"post is deleted",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "post " + i + " is Not available it is Deleted!!!");
                    Log.e(TAG, "PostKey for: " + postKey);
                    i++;
                } else {
                    Log.e(TAG, "post " + i + " is availabe ");
                    Log.e(TAG, "PostKey for: " + postKey);
                    i++;
                }

                mDatabase.child(postKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.e(TAG,"state is : "+dataSnapshot.hasChild("timestamp"));
                        if (!dataSnapshot.hasChild("timestamp")) {

                            Log.e(TAG, "I am Null so I have to die@########@@@");
                            // run my code here

                        }
                        String uid = (String) dataSnapshot.child("uid").getValue();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setImage(getApplicationContext(), model.getImage());
//                        Log.e(TAG,"Now PostKey for @@ : " +postKey);
                        //profile image code here get User Profile and show it in ImageView Circular
                        //get post Timestamp pass it to function getDateDiff to find the difference
                        //then store that value in the dateTime TextView
//                        if(dataSnapshot.child("timestamp").getValue()==null){
//
//                            return;
//                        }
                        try {
                            long postDate = (long) dataSnapshot.child("timestamp").getValue();
//                            long cDate = (long) ServerValue.TIMESTAMP;
//                            Map<String, String> cDate = ServerValue.TIMESTAMP;
//                            Log.e(TAG,"current timestamp is : "+cDate.get(".cs"));
//                            Log.e(TAG,"current timestamp is : "+cDate.toString());

                            viewHolder.setDateTime(getDateDiff(postDate));

                            //fetching user profile and username form other database object
                            mDatabaseUsers.child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String userProfileUrl = (String) dataSnapshot.child("image").getValue();
                                    String userNickName = (String) dataSnapshot.child("nickName").getValue();
                                    viewHolder.setProfileImage(getApplicationContext(), userProfileUrl);
                                    viewHolder.setUserName(userNickName);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } catch (NullPointerException e) {
                            Log.e(TAG, "Null Pointer Exception Occurred");
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, DisplayPostActivity.class).putExtra("PostId", postKey));
                    }
                });
            }
        };
        mImageList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void showMyProfile(View view) {
        startActivity(new Intent(this, MyProfileActivity.class));
    }

    public static class ImagePostViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ImagePostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView post_title = mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setUserName(String userName) {
            TextView postUserName = mView.findViewById(R.id.post_by_user);
            postUserName.setText(userName);
        }

        public void setProfileImage(Context ctx, String profileImg) {
            ImageView post_user_profile = mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileImg).into(post_user_profile);
        }

        public void setDateTime(String dateTime) {
            TextView post_date_time = mView.findViewById(R.id.post_date_time);
            post_date_time.setText(dateTime);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            //Firebase Sign Out
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "<<Good Bye See You Soon>>", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.addIcon) {
            startActivity(new Intent(this, PostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get a diff between two dates
     *
     * @param postDate the old date
     * @return the diff value, in the minutes, hours, days, or date
     */
    public String getDateDiff(long postDate) {
        try {
            final int SECOND = 1000;
            final int MINUTE = 60 * SECOND;
            final int HOUR = 60 * MINUTE;
            final int DAY = 24 * HOUR;

            //find difference between postTime and currentTime
            long ms = (new Date().getTime()) - postDate;
            /*
            Log.e(TAG,"Current timestamp : "+ new Date().getTime());
            Log.e(TAG,"post timestamp : "+postDate);
            Log.e(TAG,"difference bw timestamps : "+ms);

            */

            StringBuffer text = new StringBuffer("");
            //getting the locale date format like hindi or japanese
//            Log.e(TAG, " date and month is @@ -> " + new SimpleDateFormat("dd MMMM",new Locale("hi","IN")).format(postDate));

            if (ms > 4 * DAY) {
                text.append(new SimpleDateFormat("dd MMMM", Locale.ENGLISH).format(postDate));
            } else if (ms > DAY) {
                if (ms / DAY == 1)
                    text.append(" Yesterday");
                else
                    text.append(ms / DAY).append(" DAYS AGO");
            } else if (ms > HOUR) {
                if (ms / HOUR == 1)
                    text.append(ms / HOUR).append(" HOUR AGO");
                else
                    text.append(ms / HOUR).append(" HOURS AGO");
            } else if (ms > MINUTE) {
                if (ms / MINUTE == 1)
                    text.append(ms / MINUTE).append(" MINUTE AGO");
                else
                    text.append(ms / MINUTE).append(" MINUTES AGO");
            } else if (ms > SECOND) {
                text.append(" JUST NOW");
            }
//            Log.e(TAG, "time is " + text);
            return String.valueOf(text);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
