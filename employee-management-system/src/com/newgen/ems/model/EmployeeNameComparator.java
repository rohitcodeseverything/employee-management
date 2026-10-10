package com.newgen.ems.model;

import java.util.Comparator;

public class EmployeeNameComparator implements Comparator<Employee> {

    @Override
    public int compare(Employee first, Employee second) {
        return first.getName().compareToIgnoreCase(second.getName());
    }
}
