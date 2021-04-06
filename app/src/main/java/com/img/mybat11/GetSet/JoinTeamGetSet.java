package com.img.mybat11.GetSet;

/**
 * Created by Rohit ji on 02/02/2018.
 */

public class JoinTeamGetSet {

    String teamname,arrowname,winingamount,image,time;
    int teamid,teamnumber,userjoinid,userid,getcurrentrank,getlastrank;
    double points,lastpoints;
    boolean is_show;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public String getArrowname() {
        return arrowname;
    }

    public void setArrowname(String arrowname) {
        this.arrowname = arrowname;
    }

    public int getTeamid() {
        return teamid;
    }

    public void setTeamid(int teamid) {
        this.teamid = teamid;
    }

    public int getTeamnumber() {
        return teamnumber;
    }

    public void setTeamnumber(int teamnumber) {
        this.teamnumber = teamnumber;
    }

    public int getUserjoinid() {
        return userjoinid;
    }

    public void setUserjoinid(int userjoinid) {
        this.userjoinid = userjoinid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getGetcurrentrank() {
        return getcurrentrank;
    }

    public void setGetcurrentrank(int getcurrentrank) {
        this.getcurrentrank = getcurrentrank;
    }

    public int getGetlastrank() {
        return getlastrank;
    }

    public void setGetlastrank(int getlastrank) {
        this.getlastrank = getlastrank;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public double getLastpoints() {
        return lastpoints;
    }

    public void setLastpoints(double lastpoints) {
        this.lastpoints = lastpoints;
    }

    public String getWiningamount() {
        return winingamount;
    }

    public void setWiningamount(String winingamount) {
        this.winingamount = winingamount;
    }

    public boolean isIs_show() {
        return is_show;
    }

    public void setIs_show(boolean is_show) {
        this.is_show = is_show;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
