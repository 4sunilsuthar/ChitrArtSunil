package com.chitrart.sunil.chitrart;

import android.util.Log;

/**
 * Created by Admin on 13-02-2018.
 */

public class ImagePost {

    private String title, image, username,profileImg;

    public ImagePost() {
    }

    public ImagePost(String title, String image, String username, String profileImg) {
        this.title = title;
        this.profileImg = profileImg;
        this.image = image;
        this.username = username;
//        Log.d("MainActivity", "***********ImagePost Constructor |||title : " + title + " |||| desc : ");
    }

/*    public String getDesc() {
        return desc;
    }*/
    /*public void setDesc(String desc){
        this.desc = desc;
    }*/

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
