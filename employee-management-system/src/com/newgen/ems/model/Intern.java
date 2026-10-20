package com.newgen.ems.model;

import java.time.LocalDate;

public class Intern extends Employee {

    private String metorName;

    public Intern(int id, String name, Department department, double baseSalary, LocalDate hireDate,
                  String metorName) {
        super(id, name, department, baseSalary, hireDate);
        this.metorName = metorName;
    }

    public String getMetorName() { return metorName; }

    @Override
    public String designation() {
        return "Intern";
    }

    @Override
    protected String extraFields() {
        return "mentorName='" + metorName + "'";
    }
}
