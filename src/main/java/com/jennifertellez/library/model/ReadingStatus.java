package com.jennifertellez.library.model;

public enum ReadingStatus {
    TO_READ("To Read"),
    CURRENTLY_READING("Currently Reading"),
    FINISHED("Finished"),
    DNF("Did Not Finished");

    private final String displayName;

    ReadingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
