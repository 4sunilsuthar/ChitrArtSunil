package com.chitrart.sunil.chitrart;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetProfileActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseUsers;
    private StorageReference mStorageReference;
    private Uri mImageUri = null;
    private EditText edNickName, edBio;
    private ImageView imgBtnProfile;
    private TextView tvClickToEditMsg;
    private static final int GALLERY_REQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        edNickName = findViewById(R.id.ed_nick_name_profile);
        edBio = findViewById(R.id.ed_bio_profile);
        imgBtnProfile = findViewById(R.id.img_btn_profile);
        tvClickToEditMsg = findViewById(R.id.tv_click_to_edit);

        //for the action bar back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageReference = FirebaseStorage.getInstance().getReference().child("profile_image");
        String userKey = mAuth.getCurrentUser().getUid();
        mDatabaseUsers.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //fetching data with dataSnapshot object
                String userProfileImage = (String) dataSnapshot.child("image").getValue();
                String displayName = (String) dataSnapshot.child("nickName").getValue();
                String bioText = (String) dataSnapshot.child("bioText").getValue();

                if(userProfileImage != null) {
                    edNickName.setText(displayName);
                    edBio.setText(bioText);
                    Picasso.with(SetProfileActivity.this).load(userProfileImage).into(imgBtnProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    public void imageProfileClicked(View view) {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

        /*
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("Image*//*");
        startActivityForResult(galleryIntent, GALLERY_REQ);*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                imgBtnProfile.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }
    }
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                imgBtnProfile.setImageURI(mImageUri);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }*/

    public void saveBtnClicked(View view) {
        final String nickName = edNickName.getText().toString().trim();
        final String bio = edBio.getText().toString().trim();
        final String userId = mAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(nickName) && mImageUri != null && !TextUtils.isEmpty(bio)) {
            tvClickToEditMsg.setVisibility(View.GONE);
            StorageReference filepath = mStorageReference.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    mDatabaseUsers.child(userId).child("nickName").setValue(nickName);
                    mDatabaseUsers.child(userId).child("bioText").setValue(bio);
                    mDatabaseUsers.child(userId).child("image").setValue(downloadUrl);
                    Toast.makeText(getApplicationContext(), "Profile Setup Successfully!!!...", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SetProfileActivity.this,MainActivity.class)); //redirect to main activity
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Fill All Details to upload", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
