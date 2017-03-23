package com.example.kaixin.final_experiment;

public class NoteItem {
    private String content;
    private String time;

    public NoteItem(String content, String time){
        this.content = content;
        this.time = time;
    }

    public String getContent() {
        return this.content;
    }

    public String getTime() {
        return this.time;
    }
}
