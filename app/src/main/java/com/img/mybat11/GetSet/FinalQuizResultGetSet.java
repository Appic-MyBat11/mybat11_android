package com.img.mybat11.GetSet;

import java.util.ArrayList;

public class FinalQuizResultGetSet {

    ArrayList<FinalQuestionGetSet> language;
    String total_question,right_answer,total_point,time,attemp_question,missed_question;


    public ArrayList<FinalQuestionGetSet> getLanguage() {
        return language;
    }

    public void setLanguage(ArrayList<FinalQuestionGetSet> language) {
        this.language = language;
    }

    public String getTotal_question() {
        return total_question;
    }

    public void setTotal_question(String total_question) {
        this.total_question = total_question;
    }

    public String getRight_answer() {
        return right_answer;
    }

    public void setRight_answer(String right_answer) {
        this.right_answer = right_answer;
    }

    public String getTotal_point() {
        return total_point;
    }

    public void setTotal_point(String total_point) {
        this.total_point = total_point;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAttemp_question() {
        return attemp_question;
    }

    public void setAttemp_question(String attemp_question) {
        this.attemp_question = attemp_question;
    }

    public String getMissed_question() {
        return missed_question;
    }

    public void setMissed_question(String missed_question) {
        this.missed_question = missed_question;
    }
}
