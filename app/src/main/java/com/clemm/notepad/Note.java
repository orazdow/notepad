package com.clemm.notepad;

public class Note {

    private long id;
    private String date;
    private String title;
    private String content;
    private String password;

    public Note() {
        this.id = 0L;
        this.date = "";
        this.title = "";
        this.content = "";
        this.password = "";
    }

    public Note(long id, String date, String title, String content, String password) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.content = content;
        this.password = password;
    }

    public String getContent() {
        return this.content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return this.id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isLocked() {
        String password = this.password;
        return password != null && !password.isEmpty();
    }

    public String toString() {
        return this.title + "\n\n" + this.content;
    }
}



