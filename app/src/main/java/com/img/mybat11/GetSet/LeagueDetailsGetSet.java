package com.img.mybat11.GetSet;

import java.util.ArrayList;


public class LeagueDetailsGetSet {

    String bonus_type,catname,contest_type,matchkey,refercode,isselectedid,bonus_percentage,plus,pricecard_type,type;
    double entryfee,win_amount,winning_percentage;
    int id,catid,maximum_user,status,joinedusers,multi_entry,confirmed_challenge,is_running,is_bonus,multientry_limit,totalwinners,isuserjoin,total_joined;
    boolean isselected;
    ArrayList<teamsGetSet> jointeams,myjointeams;
    ArrayList<priceCardGetSet> price_card;

    public String getBonus_type() {
        return bonus_type;
    }

    public void setBonus_type(String bonus_type) {
        this.bonus_type = bonus_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getContest_type() {
        return contest_type;
    }

    public void setContest_type(String contest_type) {
        this.contest_type = contest_type;
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

    public String getIsselectedid() {
        return isselectedid;
    }

    public void setIsselectedid(String isselectedid) {
        this.isselectedid = isselectedid;
    }

    public String getBonus_percentage() {
        return bonus_percentage;
    }

    public void setBonus_percentage(String bonus_percentage) {
        this.bonus_percentage = bonus_percentage;
    }

    public String getPlus() {
        return plus;
    }

    public void setPlus(String plus) {
        this.plus = plus;
    }

    public String getPricecard_type() {
        return pricecard_type;
    }

    public void setPricecard_type(String pricecard_type) {
        this.pricecard_type = pricecard_type;
    }

    public double getEntryfee() {
        return entryfee;
    }

    public void setEntryfee(double entryfee) {
        this.entryfee = entryfee;
    }

    public double getWin_amount() {
        return win_amount;
    }

    public void setWin_amount(double win_amount) {
        this.win_amount = win_amount;
    }

    public double getWinning_percentage() {
        return winning_percentage;
    }

    public void setWinning_percentage(double winning_percentage) {
        this.winning_percentage = winning_percentage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public int getMaximum_user() {
        return maximum_user;
    }

    public void setMaximum_user(int maximum_user) {
        this.maximum_user = maximum_user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getJoinedusers() {
        return joinedusers;
    }

    public void setJoinedusers(int joinedusers) {
        this.joinedusers = joinedusers;
    }

    public int getMulti_entry() {
        return multi_entry;
    }

    public void setMulti_entry(int multi_entry) {
        this.multi_entry = multi_entry;
    }

    public int getConfirmed_challenge() {
        return confirmed_challenge;
    }

    public void setConfirmed_challenge(int confirmed_challenge) {
        this.confirmed_challenge = confirmed_challenge;
    }

    public int getIs_running() {
        return is_running;
    }

    public void setIs_running(int is_running) {
        this.is_running = is_running;
    }

    public int getIs_bonus() {
        return is_bonus;
    }

    public void setIs_bonus(int is_bonus) {
        this.is_bonus = is_bonus;
    }

    public int getMultientry_limit() {
        return multientry_limit;
    }

    public void setMultientry_limit(int multientry_limit) {
        this.multientry_limit = multientry_limit;
    }

    public int getTotalwinners() {
        return totalwinners;
    }

    public void setTotalwinners(int totalwinners) {
        this.totalwinners = totalwinners;
    }

    public int getIsuserjoin() {
        return isuserjoin;
    }

    public void setIsuserjoin(int isuserjoin) {
        this.isuserjoin = isuserjoin;
    }

    public int getTotal_joined() {
        return total_joined;
    }

    public void setTotal_joined(int total_joined) {
        this.total_joined = total_joined;
    }

    public boolean isIsselected() {
        return isselected;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    public ArrayList<teamsGetSet> getJointeams() {
        return jointeams;
    }

    public void setJointeams(ArrayList<teamsGetSet> jointeams) {
        this.jointeams = jointeams;
    }

    public ArrayList<teamsGetSet> getMyjointeams() {
        return myjointeams;
    }

    public void setMyjointeams(ArrayList<teamsGetSet> myjointeams) {
        this.myjointeams = myjointeams;
    }

    public ArrayList<priceCardGetSet> getPrice_card() {
        return price_card;
    }

    public void setPrice_card(ArrayList<priceCardGetSet> price_card) {
        this.price_card = price_card;
    }
}
