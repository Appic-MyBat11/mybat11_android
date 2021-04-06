package com.img.mybat11.GetSet;


public class JoinChallengeBalanceGetSet {

    public double getUsablebalance() {
        return usablebalance;
    }

    public void setUsablebalance(double usablebalance) {
        this.usablebalance = usablebalance;
    }

    public double getUsertotalbalance() {
        return usertotalbalance;
    }

    public void setUsertotalbalance(double usertotalbalance) {
        this.usertotalbalance = usertotalbalance;
    }

    public double getEntryfee() {
        return entryfee;
    }

    public void setEntryfee(double entryfee) {
        this.entryfee = entryfee;
    }

    public double getBonusused() {
        return bonusused;
    }

    public void setBonusused(double bonusused) {
        this.bonusused = bonusused;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    double usablebalance,usertotalbalance,entryfee,bonusused;

    boolean success;
    String msg;
}
