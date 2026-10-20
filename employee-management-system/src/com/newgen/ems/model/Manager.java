package com.newgen.ems.model;

import java.time.LocalDate;

public class Manager extends Employee implements Promotable{

    private int teamSize;

    public Manager(int id, String name, Department department, double baseSalary, LocalDate hireDate,
                   int teamSize) {
        super(id, name, department, baseSalary, hireDate);
        this.teamSize = teamSize;
    }

    public int getTeamSize() { return teamSize; }


    @Override
    public double calculateMonthlySalary() {
        double teamBonus = teamSize * 50.0;
        return baseSalary + teamBonus;
    }

    @Override
    public String designation() {
        return "Manager";
    }


    @Override
    public String nextRole() {
        return "Senior Manager";
    }

    @Override
    protected String extraFields() {
        return "teamSize=" + teamSize;
    }


}
