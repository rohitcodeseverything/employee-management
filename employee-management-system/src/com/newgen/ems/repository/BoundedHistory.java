package com.newgen.ems.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BoundedHistory<T> {

    private final int capacity;
    private final LinkedList<T> entries = new LinkedList<>();

    public BoundedHistory(int capacity) {
        this.capacity = capacity;
    }

    public void record(T entry) {
        entries.add(entry);

        if(entries.size() > capacity) {
            entries.removeFirst();
        }
    }


    public List<T> latestFirst() {
        return Collections.unmodifiableList(new ArrayList<>(entries.reversed()));
    }

}
