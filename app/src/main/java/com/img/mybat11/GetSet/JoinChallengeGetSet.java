package com.img.mybat11.GetSet;

import java.util.ArrayList;


public class JoinChallengeGetSet {

    String message,getjoinedpercentage,refercode;
    boolean status,is_private,multi_entry;
    int joinedusers,joinid;
    ArrayList<JoinedTeamDetailsGetSet> joined;

    public ArrayList<JoinedTeamDetailsGetSet> getJoined() {
        return joined;
    }

    public void setJoined(ArrayList<JoinedTeamDetailsGetSet> joined) {
        this.joined = joined;
    }

    public int getJoinid() {
        return joinid;
    }

    public void setJoinid(int joinid) {
        this.joinid = joinid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGetjoinedpercentage() {
        return getjoinedpercentage;
    }

    public void setGetjoinedpercentage(String getjoinedpercentage) {
        this.getjoinedpercentage = getjoinedpercentage;
    }

    public String getRefercode() {
        return refercode;
    }

    public void setRefercode(String refercode) {
        this.refercode = refercode;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isIs_private() {
        return is_private;
    }

    public void setIs_private(boolean is_private) {
        this.is_private = is_private;
    }

    public boolean isMulti_entry() {
        return multi_entry;
    }

    public void setMulti_entry(boolean multi_entry) {
        this.multi_entry = multi_entry;
    }

    public int getJoinedusers() {
        return joinedusers;
    }

    public void setJoinedusers(int joinedusers) {
        this.joinedusers = joinedusers;
    }
}
