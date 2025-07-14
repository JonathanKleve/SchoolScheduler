package com.example.myapplication.datamodel;

import java.time.LocalDate;
import java.util.ArrayList;

public class Term {
    private int id;
    private String title;
    private LocalDate start;
    private LocalDate end;

    public Term(int id, String title, LocalDate start, LocalDate end){
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
