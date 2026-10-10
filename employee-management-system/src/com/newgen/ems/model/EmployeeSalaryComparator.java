package com.newgen.ems.model;

import java.util.Comparator;

public class EmployeeSalaryComparator implements Comparator<Employee> {
    @Override
    public int compare(Employee first, Employee second) {
        return Double.compare(second.calculateMonthlySalary(), first.calculateMonthlySalary());
    }
}
