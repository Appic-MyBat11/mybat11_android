package com.img.mybat11.GetSet;



public class QuizListGetSet {

    String name,status,launch_status,final_status,start_date,image;

    int id,question,joined;


    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

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

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
