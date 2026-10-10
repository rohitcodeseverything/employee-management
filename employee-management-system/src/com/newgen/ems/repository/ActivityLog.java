package com.newgen.ems.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ActivityLog {

    private final int capacity;
    private final LinkedList<String> entries = new LinkedList<>();

    public ActivityLog(int capacity) {
        this.capacity = capacity;
    }

    public void record(String entry) {
        entries.add(entry);

        if(entries.size() > capacity) {
            entries.removeFirst();
        }
    }


    public List<String> latestFirst() {
        return Collections.unmodifiableList(new ArrayList<>(entries.reversed()));
    }

}
