package com.my.wallet.test;

public class Artist {

    private int photo;
    private String name;
    private String bio;

    public Artist (int photo, String name, String bio) {
        this.photo = photo;
        this.name = name;
        this.bio = bio;
    }

    public int getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }
}
