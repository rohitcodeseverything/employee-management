package com.newgen.ems.model;

import java.io.Serializable;
import java.util.Objects;

public abstract class Employee implements Payable , Cloneable {

    private final int id;
    private String name;
    private String department;
    protected double baseSalary;
    private boolean active;

    public Employee(int id, String name, String department, double baseSalary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.baseSalary = baseSalary;
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public abstract String designation();

    @Override
    public double calculateMonthlySalary() {
        return baseSalary;
    }





    @Override
    public String toString() {
        String extras = extraFields();

        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", baseSalary=" + baseSalary +
                ", active=" + active +
                (extras.isEmpty() ? "" : ", " + extras) +
                '}';
    }


    // Subclasses return their own "name=value" pairs, so toString() stays
    // defined in exactly one place.
    protected String extraFields() {
        return "";
    }

    // Shallow copy via Object.clone(). Enough here because every field is a
    // primitive or an immutable String; once Employee holds a mutable object
    // (a skills collection, say), the copy has to duplicate that too.
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
