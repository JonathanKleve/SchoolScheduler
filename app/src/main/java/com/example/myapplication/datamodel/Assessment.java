package com.example.myapplication.datamodel;
import java.time.LocalDate;
public class Assessment {
    private int id;
    private String title;
    private String type;
    private LocalDate start;
    private LocalDate end;
    private int courseId;

    public Assessment(int id, String title, String type, LocalDate start, LocalDate end, int courseId){
        this.id = id;
        this.title = title;
        this.type = type;
        this.start = start;
        this.end = end;
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
