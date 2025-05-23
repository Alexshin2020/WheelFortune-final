package com.example.wheelfortune;

public class SpinHistoryItem {
    private String dateTime;
    private String prize;

    public SpinHistoryItem(String dateTime, String prize) {
        this.dateTime = dateTime;
        this.prize = prize;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getPrize() {
        return prize;
    }

    @Override
    public String toString() {
        return dateTime + ": " + prize;
    }
}