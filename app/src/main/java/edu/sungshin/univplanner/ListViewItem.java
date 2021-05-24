package edu.sungshin.univplanner;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private String lectureName;
    private String d_day;
    private String deadline;
    private String isDone;

    public void setLectureName(String name){
        this.lectureName = name;
    }

    public void setD_day(String day){
        this.d_day = day;
    }

    public void setDeadline(String dead){
        this.deadline = dead;
    }

    public void setIsDone(String isDone){
        this.isDone = isDone;
    }

    public String getLectureName(){
        return this.lectureName;
    }

    public String getD_day(){
        return this.d_day;
    }
    public String getDeadline(){
        return this.deadline;
    }
    public String getIsDone(){
        return this.isDone;
    }
}
