package com.newgen.ems.repository;

import com.newgen.ems.model.Department;
import com.newgen.ems.model.Employee;
import com.newgen.ems.model.EmployeeStatus;

import java.time.LocalDateTime;
import java.util.*;

public class EmployeeRepository {

    private static final int ACTIVITY_LIMIT = 10;
    private static final int REMOVED_LIMIT = 5;


    private static final int HISTORY_LIMIT = 10;
    private final Map<Integer, Employee> employees = new LinkedHashMap<>();
    private final BoundedHistory<ActivityEntry> activity = new BoundedHistory<>(ACTIVITY_LIMIT);
    private final BoundedHistory<Employee> recentlyRemoved = new BoundedHistory<>(REMOVED_LIMIT);

    public void add(Employee employee) {
        if (employees.get(employee.getId()) != null)  {
            throw new DuplicateEmployeeIdException("Employee id " + employee.getId() + " already exists");
        }
        employees.put(employee.getId(), employee);
        log("Added  " + describe(employee));
    }

    public Employee findById(int id) throws EmployeeNotFoundException {

        Employee employee = employees.get(id);
        if (employee == null) {
            throw new EmployeeNotFoundException("No employee found with id" + id);
        }
        return employee;
    }


    public List<Employee> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(employees.values()));
    }

    public int size() {
        return employees.size();
    }

    public List<Integer> findAllIds() {
        List<Integer> ids = new ArrayList<>();
        for(Employee employee : employees.values()) {
           ids.add(employee.getId());
        }
        return ids;
    }

    public List<Employee> findByDepartment(Department department) {
        List<Employee> matches = new ArrayList<>();

        for(Employee employee : employees.values()) {
            if(employee.getDepartment() == department) {
                matches.add(employee);
            }
        }

        return Collections.unmodifiableList(matches);

    }

    public List<Employee> findAllSorted(Comparator<? super Employee> order) {
        List<Employee> listOfEmloyee = new ArrayList<>(employees.values());
        listOfEmloyee.sort(order);
        return Collections.unmodifiableList(listOfEmloyee);
    }

    public Employee remove(int id) throws EmployeeNotFoundException {
        Employee employee = findById(id);
        // remove(Object) locates the element with equals() -- Section 15's
        // same-class-and-same-id rule is what makes this find the right one.
        employees.remove(id);
        log("Removed  " + describe(employee));
        recentlyRemoved.record(employee);
        return employee;
    }

    public int removeDepartment(Department department) {
        int removed = 0;
        Iterator<Employee> iterator = employees.values().iterator();
        while (iterator.hasNext()) {
            Employee current  = iterator.next();
            if (current.getDepartment() == department) {
                iterator.remove();
                recentlyRemoved.record(current);
                removed++;
            }
        }
        if(removed > 0) {
            log("Removed " + removed + " employee(s) from " + department.getDisplayName());
        }
        return removed;
    }

    public boolean addSkill(int id, String skill) throws EmployeeNotFoundException {
        Employee employee = findById(id);
        boolean added = employee.addSkill(skill);
        if(added) {
            log("Added skill " + skill.trim() + " to " + describe(employee));
        }
        return added;
    }

    public Set<Employee> findBySkills(Set<String> required) {

        Set<Employee> matches = new LinkedHashSet<>();
        for(Employee employee : employees.values()) {

            if(employee.getSkills().containsAll(required)) {
                matches.add(employee);
            }
        }

        return Collections.unmodifiableSet(matches);

    }




    public Set<String> allSkills() {
       Set<String> all = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
       for(Employee employee : employees.values()) {
           all.addAll(employee.getSkills());
       }
       return Collections.unmodifiableSet(all);
    }


    public List<Employee> recentlyRemoved() {
        return recentlyRemoved.latestFirst();
    }

    public List<ActivityEntry> history() {
        return activity.latestFirst();
    }

    private static String describe(Employee employee) {
        return employee.designation() + " " + employee.getId() + " (" + employee.getName() + ")";
    }

    public Map<Department, Integer> headCountByDepartment() {
        Map<Department, Integer> headcount = new TreeMap<>();
        for(Employee employee : employees.values()) {
            Integer soFar = headcount.get(employee.getDepartment()); // null for first time
            headcount.put(employee.getDepartment(), soFar == null ? 1 : soFar + 1);
        }

        return Collections.unmodifiableMap(headcount);
    }

    public boolean changeStatus(int id, EmployeeStatus status) throws EmployeeNotFoundException {
        Employee employee = findById(id);
        EmployeeStatus before = employee.getStatus();
        if(before == status) {
            return false;
        }

        employee.setStatus(status);
        log(
                "Status of" + describe(employee) + " changes from" + before.getDisplayName() + " to " +
                        status.getDisplayName());
        return true;
    }

    public Set<Department> departmentWithNoEmployees() {
        Set<Department> empty =
                EnumSet.complementOf(EnumSet.of(Department.UNASSIGNED));
        for(Employee employee : employees.values()) {
            empty.remove(employee.getDepartment());
        }
        return Collections.unmodifiableSet(empty);
    }

    private void log(String message) {
        activity.record(new ActivityEntry(LocalDateTime.now(), message));
    }


}
