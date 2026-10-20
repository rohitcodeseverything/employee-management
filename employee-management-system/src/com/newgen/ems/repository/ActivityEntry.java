package com.newgen.ems.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ActivityEntry(LocalDateTime at, String message) {

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ActivityEntry {

        if (at==null || message == null) {
            throw new IllegalArgumentException("An Activity entry needs a time and message");
        }
    }

    @Override
    public String toString() {
        return STAMP.format(at) + " " +  message;
    }
}
