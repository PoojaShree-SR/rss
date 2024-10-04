package com.example.rssfeedapp;

public class RSSItem {
    private String title;
    private String link;

    public RSSItem(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}
