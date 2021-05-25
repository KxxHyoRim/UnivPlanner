package edu.sungshin.univplanner;

public class ListViewItem_assignment {
    private String lectureName_a;
    private String assignmentName;
    private String d_day_a;
    private String deadline_a;
    private String isDone_a;

    public void setLectureName(String name){
        this.lectureName_a = name;
    }
    public void setAssignmentName(String name_a){
        this.assignmentName = name_a;
    }
    public void setD_day(String day){
        this.d_day_a = day;
    }

    public void setDeadline(String dead){
        this.deadline_a = dead;
    }

    public void setIsDone(String isDone){
        this.isDone_a = isDone;
    }

    public String getLectureName(){
        return this.lectureName_a;
    }
    public String getAssignmentName() { return this.assignmentName;}
    public String getD_day(){
        return this.d_day_a;
    }
    public String getDeadline(){
        return this.deadline_a;
    }
    public String getIsDone(){
        return this.isDone_a;
    }
}
