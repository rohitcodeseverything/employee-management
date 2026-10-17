package com.newgen.ems.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Developer extends Employee implements Promotable {

    private String primaryLanguage;

    private static final Map<String, Double> SKILL_BONUS_RATES =
            Map.of("java", 0.10, "python", 0.10);
    private static final double DEFAULT_STATIC_RATE = 0.05;

    public Developer(int id, String name, Department department, double baseSalary,  String primaryLanguage) {
        super(id, name, department, baseSalary);
        this.primaryLanguage = primaryLanguage;
    }


    public String getPrimaryLanguage() { return primaryLanguage; }


    @Override
    public double calculateMonthlySalary() {
      Double listedRate = SKILL_BONUS_RATES.get(primaryLanguage);
      double bonusRate = listedRate != null ? listedRate : DEFAULT_STATIC_RATE;
      double skillBonus = baseSalary * bonusRate;
      return baseSalary + skillBonus;
    }

    @Override
    public String designation() {
        return "Software Developer";
    }


    @Override
    public String nextRole() {
        return "Senior Developer";
    }

    @Override
    protected String extraFields() {
        return "primaryLanguage='" + primaryLanguage + "'";
    }
}
