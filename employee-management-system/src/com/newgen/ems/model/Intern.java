package com.newgen.ems.model;

public class Intern extends Employee {

    private String metorName;

    public Intern(int id, String name, String department, double baseSalary, String metorName) {
        super(id, name, department, baseSalary);
        this.metorName = metorName;
    }

    public String getMetorName() { return metorName; }

    @Override
    public String designation() {
        return "Intern";
    }
}
