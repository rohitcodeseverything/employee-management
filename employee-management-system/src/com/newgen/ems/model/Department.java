package com.newgen.ems.model;

public enum Department {

    ENGINEERING("engineering"),
    HUMAN_RESOURCES("Human Resources"),
    SALES("Sales"),
    FINANCE("Finance"),
    UNASSIGNED("Unassigned");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
