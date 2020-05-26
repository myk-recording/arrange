package com.readboy.newcurriculum;

public class Course {
    private String subject;
    private String courseName;
    private String teacher;
    private String classroom;
    private String tackle;
    private String day;
    private String classStart;
    private String classEnd;

    public Course(){}

    public Course(String subject, String courseName, String teacher, String classroom, String tackle, String day
            , String classStart, String classEnd) {
        this.subject = subject;
        this.courseName = courseName;
        this.teacher = teacher;
        this.classroom = classroom;
        this.tackle = tackle;
        this.day = day;
        this.classStart = classStart;
        this.classEnd = classEnd;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getTackle() {
        return tackle;
    }

    public void setTackle(String tackle) {
        this.tackle = tackle;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getClassEnd() {
        return classEnd;
    }

    public void setClassEnd(String classEnd) {
        this.classEnd = classEnd;
    }

    public String getClassStart() {
        return classStart;
    }

    public void setClassStart(String classStart) {
        this.classStart = classStart;
    }
}
