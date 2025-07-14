package com.example.myapplication.datamodel;

import java.time.LocalDate;
import java.util.ArrayList;

public class Course {
    private int id;
    private String title;
    private LocalDate start;
    private LocalDate end;
    private String status;
    private int instructorId;
    private String notes;
    private int termId;

    public Course(int id, String title, LocalDate start, LocalDate end, String status, int instructorId, String notes, int termId){
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.status = status;
        this.instructorId = instructorId;
        this.notes = notes;
        this.termId = termId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int term) {
        this.termId = term;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }
}
