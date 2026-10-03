package com.newgen.ems.repository;

import com.newgen.ems.model.Employee;

import java.util.Arrays;

public class EmployeeRepository {

    private Employee[] employees;
    private int count;

    public EmployeeRepository(int initialCapacity) {
        this.employees = new Employee[initialCapacity];
        this.count = 0;
    }

    public void add(Employee employee) {



        if ((size() > 0) && search(employee.getId()) != null)  {
            throw new DuplicateEmployeeIdException("Employee id " + employee.getId() + " already exists");
        }

        if(count == employees.length) {
            int newCapacity = employees.length * 2;
            System.out.printf("Repository full (capacity %d) -- growing to %d.%n", employees.length, newCapacity);
            employees = Arrays.copyOf(employees, newCapacity);
        }
        employees[count] = employee;
        count++;
    }

    public Employee findById(int id) throws EmployeeNotFoundException {

        Employee employee = search(id);
        if (employee == null) {
            throw new EmployeeNotFoundException("No employee found with id" + id);
        }
        return employee;

    }

    private Employee search(int id) {

        for (Employee employee : employees) {
            if(employee != null) {
                if (employee.getId() == id) {
                    return employee;
                }
            }
        }
        return null;
    }

    public Employee[] findAll() {
        return Arrays.copyOf(employees, count);
    }

    public int size() {
        return count;
    }

}
