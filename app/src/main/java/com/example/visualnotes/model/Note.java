// model/Note.java
package com.example.visualnotes.model;

public class Note {
    private String imagePath;
    private String text;
    private String date;
    private String path;

    public Note(String imagePath, String text, String date, String path) {
        this.imagePath = imagePath;
        this.text = text;
        this.date = date;
        this.path = path;


    }

    public String getImagePath() { return imagePath; }
    public String getText() { return text; }
    public String getDate() { return date; }
    public String getPath() { return path; }
}

