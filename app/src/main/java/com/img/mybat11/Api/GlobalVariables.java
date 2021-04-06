package com.img.mybat11.Api;

import android.app.Application;

import com.img.mybat11.GetSet.CategoriesGetSet;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.GetSet.ReferuserGetSet;
import com.img.mybat11.GetSet.SelectedTeamsGetSet;
import com.img.mybat11.GetSet.captainListGetSet;
import com.img.mybat11.GetSet.challengesGetSet;
import com.img.mybat11.GetSet.priceCardGetSet;

import java.util.ArrayList;


public class GlobalVariables extends Application{

    public ArrayList<captainListGetSet> captainList = new ArrayList<>();
    public ArrayList<challengesGetSet> allChallenges = new ArrayList<>();
    public ArrayList<CategoriesGetSet> contestCategories = new ArrayList<>();
    public ArrayList<SelectedTeamsGetSet> teamList = new ArrayList<>();
    public ArrayList<MyTeamsGetSet> selectedTeamList = new ArrayList<>();
    public ArrayList<priceCardGetSet> priceCard = new ArrayList<>();
    public ArrayList<ReferuserGetSet> referList = new ArrayList<>();
    public String  plateformmode="cricket",matchKey = new String(), paytype="",multi_entry= new String(), Series= new String(),matchTime= new String(),promoCode= new String(),team1= new String(),team2= new String(),team1Image= new String(),team2image= new String(),status= new String();
    int challengeId=0, quizid=0, quizquestions=0;
    public static int teamCount;
    int max_teams = 10;
    boolean isprivate = false;

    boolean showPopup = true;

    public boolean isShowPopup() {
        return showPopup;
    }

    public void setShowPopup(boolean showPopup) {
        this.showPopup = showPopup;
    }

    public String getPlateformmode() {
        return plateformmode;
    }

    public int getQuizquestions() {
        return quizquestions;
    }

    public void setQuizquestions(int quizquestions) {
        this.quizquestions = quizquestions;
    }

    public void setPlateformmode(String plateformmode) {
        this.plateformmode = plateformmode;
    }

    public int getQuizid() {
        return quizid;
    }

    public void setQuizid(int quizid) {
        this.quizid = quizid;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setMulti_entry(String multi_entry) {
        this.multi_entry = multi_entry;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public ArrayList<priceCardGetSet> getPriceCard() {
        return priceCard;
    }

    public void setPriceCard(ArrayList<priceCardGetSet> priceCard) {
        this.priceCard = priceCard;
    }
    boolean batDialog = false, ballDialog = false;

    public boolean isBatDialog() {
        return batDialog;
    }

    public void setBatDialog(boolean batDialog) {
        this.batDialog = batDialog;
    }

    public boolean isBallDialog() {
        return ballDialog;
    }

    public void setBallDialog(boolean ballDialog) {
        this.ballDialog = ballDialog;
    }

    public ArrayList<captainListGetSet> getCaptainList() {
        return captainList;
    }

    public void setCaptainList(ArrayList<captainListGetSet> captainList) {
        this.captainList = captainList;
    }

    public  int getTeamCount() {
        return teamCount;
    }

    public  void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }
    public ArrayList<challengesGetSet> getAllChallenges() {
        return allChallenges;
    }

    public void setAllChallenges(ArrayList<challengesGetSet> allChallenges) {
        this.allChallenges = allChallenges;
    }

    public ArrayList<CategoriesGetSet> getContestCategories() {
        return contestCategories;
    }

    public void setContestCategories(ArrayList<CategoriesGetSet> contestCategories) {
        this.contestCategories = contestCategories;
    }

    public ArrayList<SelectedTeamsGetSet> getTeamList() {
        return teamList;
    }

    public void setTeamList(ArrayList<SelectedTeamsGetSet> teamList) {
        this.teamList = teamList;
    }

    public ArrayList<MyTeamsGetSet> getSelectedTeamList() {
        return selectedTeamList;
    }

    public void setSelectedTeamList(ArrayList<MyTeamsGetSet> selectedTeamList) {
        this.selectedTeamList = selectedTeamList;
    }

    public ArrayList<ReferuserGetSet> getReferList() {
        return referList;
    }

    public void setReferList(ArrayList<ReferuserGetSet> referList) {
        this.referList = referList;
    }
    public String getMulti_entry() {
        return multi_entry;
    }
    public String getMatchKey() {
        return matchKey;
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }

    public String getSeries() {
        return Series;
    }

    public void setSeries(String series) {
        Series = series;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getTeam1Image() {
        return team1Image;
    }

    public void setTeam1Image(String team1Image) {
        this.team1Image = team1Image;
    }

    public String getTeam2image() {
        return team2image;
    }

    public void setTeam2image(String team2image) {
        this.team2image = team2image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public int getMax_teams() {
        return max_teams;
    }

    public void setMax_teams(int max_teams) {
        this.max_teams = max_teams;
    }

    public boolean isIsprivate() {
        return isprivate;
    }

    public void setIsprivate(boolean isprivate) {
        this.isprivate = isprivate;
    }
}
