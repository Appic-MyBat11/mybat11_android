package com.img.mybat11.GetSet;

import java.util.ArrayList;

public class JoinedChallengesGetSet {

    String userpoints,name,matchkey,refercode,contest_type,matchstatus,is_private,type;
    int challenge_id,pricecardstatus,confirmed,is_bonus,joinedusers,joinid,status,teamid,totalwinners,userrank=0,userteamnumber=0,winning_percentage,maximum_user,multi_entry;
    boolean can_invite;
    Double entryfee,winamount,totalwinning=0.0;
    ArrayList<priceCardGetSet> price_card = new ArrayList<>();
    ArrayList<JoinedTeamDetailsGetSet> jointeams;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getContest_type() {
        return contest_type;
    }

    public void setContest_type(String contest_type) {
        this.contest_type = contest_type;
    }

    public String getMatchstatus() {
        return matchstatus;
    }

    public void setMatchstatus(String matchstatus) {
        this.matchstatus = matchstatus;
    }

    public int getMaximum_user() {
        return maximum_user;
    }

    public void setMaximum_user(int maximum_user) {
        this.maximum_user = maximum_user;
    }

    public int getMulti_entry() {
        return multi_entry;
    }

    public void setMulti_entry(int multi_entry) {
        this.multi_entry = multi_entry;
    }

    public int getIs_bonus() {
        return is_bonus;
    }

    public void setIs_bonus(int is_bonus) {
        this.is_bonus = is_bonus;
    }

    public int getPricecardstatus() {
        return pricecardstatus;
    }

    public void setPricecardstatus(int pricecardstatus) {
        this.pricecardstatus = pricecardstatus;
    }

    public String getUserpoints() {
        return userpoints;
    }

    public void setUserpoints(String userpoints) {
        this.userpoints = userpoints;
    }

    public int getUserrank() {
        return userrank;
    }

    public void setUserrank(int userrank) {
        this.userrank = userrank;
    }

    public int getUserteamnumber() {
        return userteamnumber;
    }

    public void setUserteamnumber(int userteamnumber) {
        this.userteamnumber = userteamnumber;
    }

    public int getWinning_percentage() {
        return winning_percentage;
    }

    public void setWinning_percentage(int winning_percentage) {
        this.winning_percentage = winning_percentage;
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

    public String getRefercode() {
        return refercode;
    }

    public void setRefercode(String refercode) {
        this.refercode = refercode;
    }

    public int getTeamid() {
        return teamid;
    }

    public void setTeamid(int teamid) {
        this.teamid = teamid;
    }

    public String getIs_private() {
        return is_private;
    }

    public void setIs_private(String is_private) {
        this.is_private = is_private;
    }

    public int getJoinid() {
        return joinid;
    }

    public void setJoinid(int joinid) {
        this.joinid = joinid;
    }

    public int getChallenge_id() {
        return challenge_id;
    }

    public void setChallenge_id(int challenge_id) {
        this.challenge_id = challenge_id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isCan_invite() {
        return can_invite;
    }

    public void setCan_invite(boolean can_invite) {
        this.can_invite = can_invite;
    }

    public Double getEntryfee() {
        return entryfee;
    }

    public void setEntryfee(Double entryfee) {
        this.entryfee = entryfee;
    }

    public Double getWinamount() {
        return winamount;
    }

    public void setWinamount(Double winamount) {
        this.winamount = winamount;
    }

    public ArrayList<priceCardGetSet> getPrice_card() {
        return price_card;
    }

    public void setPrice_card(ArrayList<priceCardGetSet> price_card) {
        this.price_card = price_card;
    }

    public Double getTotalwinning() {
        return totalwinning;
    }

    public void setTotalwinning(Double totalwinning) {
        this.totalwinning = totalwinning;
    }

    public ArrayList<JoinedTeamDetailsGetSet> getJointeams() {
        return jointeams;
    }

    public void setJointeams(ArrayList<JoinedTeamDetailsGetSet> jointeams) {
        this.jointeams = jointeams;
    }
}
