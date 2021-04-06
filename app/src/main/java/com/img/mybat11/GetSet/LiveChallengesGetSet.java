package com.img.mybat11.GetSet;

import java.util.ArrayList;


public class LiveChallengesGetSet {

    public ArrayList<JoinTeamGetSet> getJointeams() {
        return jointeams;
    }

    public void setJointeams(ArrayList<JoinTeamGetSet> jointeams) {
        this.jointeams = jointeams;
    }

    public ArrayList<priceCardGetSet> getPrice_card() {
        return price_card;
    }

    public void setPrice_card(ArrayList<priceCardGetSet> price_card) {
        this.price_card = price_card;
    }

    public int getUserrank() {
        return userrank;
    }

    public void setUserrank(int userrank) {
        this.userrank = userrank;
    }

    public int getChallenge_id() {
        return challenge_id;
    }

    public void setChallenge_id(int challenge_id) {
        this.challenge_id = challenge_id;
    }

    public int getMaximum_user() {
        return maximum_user;
    }

    public void setMaximum_user(int maximum_user) {
        this.maximum_user = maximum_user;
    }

    public int getJoinedusers() {
        return joinedusers;
    }

    public void setJoinedusers(int joinedusers) {
        this.joinedusers = joinedusers;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getTotalwinners() {
        return totalwinners;
    }

    public void setTotalwinners(int totalwinners) {
        this.totalwinners = totalwinners;
    }

    public int getTeamid() {
        return teamid;
    }

    public void setTeamid(int teamid) {
        this.teamid = teamid;
    }

    public int getIs_private() {
        return is_private;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    public int getMulti_entry() {
        return multi_entry;
    }

    public void setMulti_entry(int multi_entry) {
        this.multi_entry = multi_entry;
    }

    public int getJoinid() {
        return joinid;
    }

    public void setJoinid(int joinid) {
        this.joinid = joinid;
    }

    public int getTeam_number_get() {
        return team_number_get;
    }

    public void setTeam_number_get(int team_number_get) {
        this.team_number_get = team_number_get;
    }

    public double getEntryfee() {
        return entryfee;
    }

    public void setEntryfee(double entryfee) {
        this.entryfee = entryfee;
    }

    public String getMatchstatus() {
        return matchstatus;
    }

    public void setMatchstatus(String matchstatus) {
        this.matchstatus = matchstatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatchkey() {
        return matchkey;
    }

    public void setMatchkey(String matchkey) {
        this.matchkey = matchkey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPdfname() {
        return pdfname;
    }

    public void setPdfname(String pdfname) {
        this.pdfname = pdfname;
    }

    public boolean isCan_invite() {
        return can_invite;
    }

    public void setCan_invite(boolean can_invite) {
        this.can_invite = can_invite;
    }

    ArrayList<JoinTeamGetSet> jointeams;
    ArrayList<priceCardGetSet>  price_card;
    int userrank,challenge_id,maximum_user,joinedusers,confirmed,totalwinners,teamid,is_private,multi_entry,joinid,team_number_get;
    double entryfee;
    String matchstatus,name,matchkey,status,pdfname;
    boolean can_invite;
}
