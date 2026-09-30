package com.newgen.ems.model;

public interface Payable {

    double calculateMonthlySalary();

    default double calculateAnnualSalary() {
        return calculateMonthlySalary() * 12;
    }
}
