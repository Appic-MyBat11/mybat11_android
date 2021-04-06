package com.img.mybat11.GetSet;

import java.util.ArrayList;

public class CategoriesGetSet {

    String catname,sub_title,image;
    int catid,contest_limit;

    ArrayList<challengesGetSet> contest;


    public int getContest_limit() {
        return contest_limit;
    }

    public void setContest_limit(int contest_limit) {
        this.contest_limit = contest_limit;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public ArrayList<challengesGetSet> getContest() {
        return contest;
    }

    public void setContest(ArrayList<challengesGetSet> contest) {
        this.contest = contest;
    }
}
