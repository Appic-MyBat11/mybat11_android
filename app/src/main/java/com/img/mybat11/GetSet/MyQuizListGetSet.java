package com.img.mybat11.GetSet;



public class MyQuizListGetSet {

    String name,start_date,status,launch_status,final_status,image;

    int quiz_id,totaljoinedcontest,totalwinningamount,question;


    public int getQuestion() {
        return question;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLaunch_status() {
        return launch_status;
    }

    public void setLaunch_status(String launch_status) {
        this.launch_status = launch_status;
    }

    public String getFinal_status() {
        return final_status;
    }

    public void setFinal_status(String final_status) {
        this.final_status = final_status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(int quiz_id) {
        this.quiz_id = quiz_id;
    }

    public int getTotaljoinedcontest() {
        return totaljoinedcontest;
    }

    public void setTotaljoinedcontest(int totaljoinedcontest) {
        this.totaljoinedcontest = totaljoinedcontest;
    }

    public int getTotalwinningamount() {
        return totalwinningamount;
    }

    public void setTotalwinningamount(int totalwinningamount) {
        this.totalwinningamount = totalwinningamount;
    }
}
