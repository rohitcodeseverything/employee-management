package com.newgen.ems.model;

public enum EmployeeStatus {

    ACTIVE("Active"),
    ON_LEAVE("On leave"),
    ON_NOTICE("Serving notice");
    private String displayName;
    EmployeeStatus(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
