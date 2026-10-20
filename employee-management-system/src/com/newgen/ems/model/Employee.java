package com.newgen.ems.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public abstract class Employee implements Payable , Cloneable, Comparable<Employee> {

    private final int id;
    private String name;
    private Department department;
    protected double baseSalary;
    private final LocalDate hireDate;
    private EmployeeStatus status;

    private Set<String> skills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    public Employee(int id, String name, Department department, double baseSalary,
                    LocalDate hireDate) {

        this.id = id;
        this.name = name;
        this.department = department;
        this.baseSalary = baseSalary;
        this.hireDate = hireDate;
        this.status = EmployeeStatus.ACTIVE;
    }

    public Employee(int id, String name, double baseSalary) {
        this(id, name, Department.UNASSIGNED, baseSalary,  LocalDate.now());
    }

    public Period tenure(LocalDate asOf) {
        return Period.between(hireDate, asOf);
    }

    public LocalDate getHireDate() {
        return hireDate;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public abstract String designation();

    public boolean isActive() {
        return status == EmployeeStatus.ACTIVE;
    }

    @Override
    public double calculateMonthlySalary() {
        return baseSalary;
    }


    public boolean addSkill(String skill) {

        String trimmed =  skill.trim();
        if(trimmed.isEmpty()) {
            return false;
        }
        return skills.add(trimmed);
    }

    public Set<String> getSkills() {
        return Collections.unmodifiableSet(skills);
    }


    @Override
    public String toString() {
        String extras = extraFields();

        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", baseSalary=" + baseSalary +
                ", hireDate=" + hireDate +
                ", status=" + status +
                ", skills=" + skills +
                (extras.isEmpty() ? "" : ", " + extras) +
                '}';
    }

    @Override
    public int compareTo(Employee other) {
        return Integer.compare(id, other.id);
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
        Employee copy = (Employee) super.clone();
        copy.skills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        copy.skills.addAll(skills);
        return copy;
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
