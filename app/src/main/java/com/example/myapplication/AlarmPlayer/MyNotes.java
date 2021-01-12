package com.example.myapplication.AlarmPlayer;

public class MyNotes {
    private	int	ids;
    private	String title;
    private	String detail;

    public MyNotes(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }

    public MyNotes(int ids, String title, String detail) {
        this.ids = ids;
        this.title = title;
        this.detail = detail;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}

