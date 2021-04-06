package com.img.mybat11.GetSet;

import java.util.ArrayList;

public class NotificationGetSet {

    ArrayList<NotificationSingleGetSet> today,previous;

    public ArrayList<NotificationSingleGetSet> getPrevious() {
        return previous;
    }

    public void setPrevious(ArrayList<NotificationSingleGetSet> previous) {
        this.previous = previous;
    }

    public ArrayList<NotificationSingleGetSet> getToday() {
        return today;
    }

    public void setToday(ArrayList<NotificationSingleGetSet> today) {
        this.today = today;
    }
}
