package com.img.mybat11.GetSet;

import java.util.ArrayList;


public class SelectedTeamsGetSet {

    public String getTeamnumber() {
        return teamnumber;
    }

    public void setTeamnumber(String teamnumber) {
        this.teamnumber = teamnumber;
    }

    public String getTeamid() {
        return teamid;
    }

    public void setTeamid(String teamid) {
        this.teamid = teamid;
    }

    public ArrayList<SelectedPlayersGetSet> getPlayer() {
        return player;
    }

    public void setPlayer(ArrayList<SelectedPlayersGetSet> player) {
        this.player = player;
    }

    public String getCaptain() {
        return captain;
    }

    public void setCaptain(String captain) {
        this.captain = captain;
    }

    public String getVicecaptain() {
        return vicecaptain;
    }

    public void setVicecaptain(String vicecaptain) {
        this.vicecaptain = vicecaptain;
    }

    public String getPlayer_type() {
        return player_type;
    }

    public void setPlayer_type(String player_type) {
        this.player_type = player_type;
    }

    String teamnumber,teamid,captain,vicecaptain,player_type;
    ArrayList <SelectedPlayersGetSet> player;
}
