package com.newgen.ems.model;

public record Payslip(int employeeId, String employeeName, String designation, String department, double salary) {

    public Payslip {

        if(employeeName == null || designation == null || department == null) {
            throw new IllegalArgumentException(" A payslip needs a name, designation and department");
        }

    }

    public static Payslip of(Employee employee) {
        return new Payslip(employee.getId(),
                employee.getName(),
                employee.designation(),
                employee.getDepartment(),
                employee.getBaseSalary());
    }
}
