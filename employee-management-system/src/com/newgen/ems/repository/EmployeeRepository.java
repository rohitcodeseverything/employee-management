package com.newgen.ems.repository;

import com.newgen.ems.model.Employee;

import java.util.*;

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
        return Collections.unmodifiableList(new ArrayList<>(employees));
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

    public List<Employee> findByDepartment(String department) {
        List<Employee> matches = new ArrayList<>();

        for(Employee employee : employees) {
            if(employee.getDepartment().equalsIgnoreCase(department)) {
                matches.add(employee);
            }
        }

        return Collections.unmodifiableList(matches);

    }

    public List<Employee> findAllSorted(Comparator<Employee> order) {
        List<Employee> listOfEmloyee = new ArrayList<>(employees);
        listOfEmloyee.sort(order);
        return Collections.unmodifiableList(listOfEmloyee);
    }

    public Employee remove(int id) throws EmployeeNotFoundException {
        Employee employee = findById(id);
        // remove(Object) locates the element with equals() -- Section 15's
        // same-class-and-same-id rule is what makes this find the right one.
        employees.remove(employee);
        return employee;
    }

    public int removeDepartment(String department) {
        int removed = 0;
        Iterator<Employee> iterator = employees.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getDepartment().equalsIgnoreCase(department)) {
                iterator.remove();
                removed++;
            }
        }
        return removed;
    }
}
