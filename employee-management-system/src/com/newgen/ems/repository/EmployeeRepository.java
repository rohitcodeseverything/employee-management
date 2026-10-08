package com.newgen.ems.repository;

import com.newgen.ems.model.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeRepository {

    private final List<Employee> employees = new ArrayList<>();

    public void add(Employee employee) {

        if (search(employee.getId()) != null)  {
            throw new DuplicateEmployeeIdException("Employee id " + employee.getId() + " already exists");
        }

        employees.add(employee);
    }

    public Employee findById(int id) throws EmployeeNotFoundException {

        Employee employee = search(id);
        if (employee == null) {
            throw new EmployeeNotFoundException("No employee found with id" + id);
        }
        return employee;
    }

    // Section 12's
    private Employee search(int id) {

        for (Employee employee : employees) {
                if (employee.getId() == id) {
                    return employee;
                }
        }
        return null;
    }

    public List<Employee> findAll() {
        return new ArrayList<>(employees);
    }

    public int size() {
        return employees.size();
    }

    public List<Integer> findAllIds() {
        List<Integer> ids = new ArrayList<>();
        for(Employee employee : employees) {
           ids.add(employee.getId());
        }
        return ids;
    }
}
