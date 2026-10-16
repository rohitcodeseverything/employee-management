package com.newgen.ems.repository;

import com.newgen.ems.model.Employee;

import java.util.*;

public class EmployeeRepository {

    private static final int ACTIVITY_LIMIT = 10;
    private static final int REMOVED_LIMIT = 5;


    private static final int HISTORY_LIMIT = 10;
    private final Map<Integer, Employee> employees = new LinkedHashMap<>();
    private final BoundedHistory<String> activity = new BoundedHistory<>(ACTIVITY_LIMIT);
    private final BoundedHistory<Employee> recentlyRemoved = new BoundedHistory<>(REMOVED_LIMIT);

    public void add(Employee employee) {
        if (employees.get(employee.getId()) != null)  {
            throw new DuplicateEmployeeIdException("Employee id " + employee.getId() + " already exists");
        }
        employees.put(employee.getId(), employee);
        activity.record("Added  " + describe(employee));
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

    public List<Employee> findByDepartment(String department) {
        List<Employee> matches = new ArrayList<>();

        for(Employee employee : employees.values()) {
            if(employee.getDepartment().equalsIgnoreCase(department)) {
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
        activity.record("Removed  " + describe(employee));
        recentlyRemoved.record(employee);
        return employee;
    }

    public int removeDepartment(String department) {
        int removed = 0;
        Iterator<Employee> iterator = employees.values().iterator();
        while (iterator.hasNext()) {
            Employee current  = iterator.next();
            if (current.getDepartment().equalsIgnoreCase(department)) {
                iterator.remove();
                recentlyRemoved.record(current);
                removed++;
            }
        }
        if(removed > 0) {
            activity.record("Removed " + removed + " employee(s) from " + department);
        }
        return removed;
    }

    public boolean addSkill(int id, String skill) throws EmployeeNotFoundException {
        Employee employee = findById(id);
        boolean added = employee.addSkill(skill);
        if(added) {
            activity.record("Added skill " + skill.trim() + " to " + describe(employee));
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

    public List<String> history() {
        return activity.latestFirst();
    }

    private static String describe(Employee employee) {
        return employee.designation() + " " + employee.getId() + " (" + employee.getName() + ")";
    }

    public Map<String, Integer> headCountByDepartment() {
        Map<String, Integer> headcount = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for(Employee employee : employees.values()) {
            Integer soFar = headcount.get(employee.getDepartment()); // null for first time
            headcount.put(employee.getDepartment(), soFar == null ? 1 : soFar + 1);
        }

        return Collections.unmodifiableMap(headcount);
    }
}
