package com.img.mybat11.GetSet;

import java.util.ArrayList;

public class challengesGetSet {

    int id,maximum_user,status,joinedusers,multi_entry,confirmed_challenge,totalwinners,is_running,catid,multientry_limit;
    ArrayList<priceCardGetSet> price_card;
    String matchkey,isselectedid,refercode,catname,contest_type,bonus_percentage,type,c_type,bonus_type;
    int entryfee;
    int win_amount,is_bonus,winning_percentage;
    Boolean isselected,isjoined;

    public String getBonus_type() {
        return bonus_type;
    }

    public void setBonus_type(String bonus_type) {
        this.bonus_type = bonus_type;
    }

    public String getC_type() {
        return c_type;
    }

    public void setC_type(String c_type) {
        this.c_type = c_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getTotalwinners() {
        return totalwinners;
    }

    public void setTotalwinners(int totalwinners) {
        this.totalwinners = totalwinners;
    }

    public int getIs_running() {
        return is_running;
    }

    public void setIs_running(int is_running) {
        this.is_running = is_running;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public String getBonus_percentage() {
        return bonus_percentage;
    }

    public void setBonus_percentage(String bonus_percentage) {
        this.bonus_percentage = bonus_percentage;
    }

    public int getMultientry_limit() {
        return multientry_limit;
    }

    public void setMultientry_limit(int multientry_limit) {
        this.multientry_limit = multientry_limit;
    }

    public ArrayList<priceCardGetSet> getPrice_card() {
        return price_card;
    }

    public void setPrice_card(ArrayList<priceCardGetSet> price_card) {
        this.price_card = price_card;
    }

    public String getMatchkey() {
        return matchkey;
    }

    public void setMatchkey(String matchkey) {
        this.matchkey = matchkey;
    }

    public String getIsselectedid() {
        return isselectedid;
    }

    public void setIsselectedid(String isselectedid) {
        this.isselectedid = isselectedid;
    }

    public String getRefercode() {
        return refercode;
    }

    public void setRefercode(String refercode) {
        this.refercode = refercode;
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

    public int getEntryfee() {
        return entryfee;
    }

    public void setEntryfee(int entryfee) {
        this.entryfee = entryfee;
    }

    public int getWin_amount() {
        return win_amount;
    }

    public void setWin_amount(int win_amount) {
        this.win_amount = win_amount;
    }

    public int getIs_bonus() {
        return is_bonus;
    }

    public void setIs_bonus(int is_bonus) {
        this.is_bonus = is_bonus;
    }

    public int getWinning_percentage() {
        return winning_percentage;
    }

    public void setWinning_percentage(int winning_percentage) {
        this.winning_percentage = winning_percentage;
    }

    public Boolean getIsselected() {
        return isselected;
    }

    public void setIsselected(Boolean isselected) {
        this.isselected = isselected;
    }

    public Boolean getIsjoined() {
        return isjoined;
    }

    public void setIsjoined(Boolean isjoined) {
        this.isjoined = isjoined;
    }
}
