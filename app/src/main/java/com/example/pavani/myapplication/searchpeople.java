package com.example.pavani.myapplication;

public class searchpeople
{
    public String Fullname, prof_image;

    public searchpeople()
    {

    }

    public searchpeople(String fullname, String prof_image)
    {
        Fullname = fullname;
        this.prof_image = prof_image;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getProf_image() {
        return prof_image;
    }

    public void setProf_image(String prof_image) {
        this.prof_image = prof_image;
    }
}
