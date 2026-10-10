package com.newgen.ems.app;

import com.newgen.ems.model.*;
import com.newgen.ems.repository.EmployeeNotFoundException;
import com.newgen.ems.repository.EmployeeRepository;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        System.out.printf("[====Employee Management System Application====]\n");

        Manager manager = new Manager(101, "Asha Rao", "Engineering", 90_000, 6);
        Developer developer = new Developer(102, "Rahul Verma", "Engineering", 65_000, "Java");
        Intern inter = new Intern(103, "Priya Singh", "Engineering", 15_000, "Asha Rao");
        Developer secondDeveloper = new Developer(104, "Virat Kohli", "Engineering", 60_000, "Python");

        EmployeeRepository employeeRepository = new EmployeeRepository();
        employeeRepository.add(manager);
        employeeRepository.add(developer);
        employeeRepository.add(inter);
        employeeRepository.add(secondDeveloper);

        //print salary of every employee
        for (Employee employee : employeeRepository.findAll()) {
            printPayslip(employee);
        }

        // Linear search by id.
        int searchId = 103;

        try {
            Employee found = employeeRepository.findById(searchId);
            System.out.println("Found employee " + searchId + ": " + found.designation());
        } catch (EmployeeNotFoundException e) {
            System.out.println(e.getMessage());
        }

        char performanceGrade = 'A';
        double bonusMultiplier = switch (performanceGrade) {
            case 'A' -> 0.20;
            case 'B' -> 0.10;
            default -> 0.0;
        };
        System.out.printf("%nBonus multiplier for grade %c: %.0f%%%n", performanceGrade, bonusMultiplier * 100);

        if (developer.getBaseSalary() > 50_000 && developer.isActive()) {
            System.out.println(developer.getName() + " is eligible for the annual stock grant.");
        }

        List<Promotable> promotables = List.of(manager, developer, secondDeveloper);

        for (Promotable promotable : promotables) {
            promotable.promote();
        }

        double yearToDate = 0;
        for (int month = 1; month <= 12; month++) {
            yearToDate += manager.calculateMonthlySalary();
        }
        System.out.printf("%n%s year-to-date earnings after 12 months: %.2f%n", manager.getName(), yearToDate);

        demonstrateObjectMethods(manager, developer, employeeRepository);
        demonstrateWrapperClasses(employeeRepository);
        demonstrateLists(employeeRepository);

        System.out.println();
        System.out.println("=== Section 13/14: over to you (Scanner input, now with exception handling) ===");
        runConsoleMenu(employeeRepository);



//        // Section 15 teaser: Object's default toString() isn't useful yet --
//        // we'll override it when we cover java.lang.Object.
//        System.out.println();
//        System.out.println("Default Object#toString() (we'll fix this in Section 15): " + manager);

    }

    private static void demonstrateLists(EmployeeRepository employeeRepository) {
        System.out.println();
        System.out.println("=== Section 17: lists ===");

        List<Employee> everyone =  employeeRepository.findAll();

         try {
             everyone.add(new Intern(999, "Komal", "Engineering", 1, "Nobody"));
         } catch (UnsupportedOperationException ex) {
             System.out.printf("%-34s: %s%n", "findAll().add(...)", ex.getClass().getSimpleName());
         }

         List<String> newestFirst = new ArrayList<>();
         ListIterator<Employee> backward =  everyone.listIterator(everyone.size());

         while (backward.hasPrevious()) {
             System.out.println("Previous ::: " + backward.previous());
             newestFirst.add(backward.previous().getName());
         }

        System.out.printf("%-34s: %s%n", "Newest first (ListIterator)", String.join(", ", newestFirst));


         var ids = new ArrayList<>(employeeRepository.findAllIds());
        ids.remove(1);
        ids.remove(Integer.valueOf(104));
        System.out.printf("%-34s: %s%n", "ids after remove(1), remove(104)", ids);

        var copy = new ArrayList<>(everyone);

        try {

            for(Employee employee : copy) {
                if(employee.getId() == 101) {
                    copy.remove(employee);
                }
            }

        } catch (ConcurrentModificationException ex) {
            System.out.printf("%-34s: %s%n", "remove inside for-each", ex.getClass().getSimpleName());
        }

    }

    private static void demonstrateWrapperClasses(EmployeeRepository employeeRepository) {
        System.out.println();
        System.out.println("=== Section 16: wrapper classes ===");

        List<Integer> ids = employeeRepository.findAllIds();
        System.out.printf("%-34s: %s%n", "Employee ids on file", ids);
        //Auto-Unboxing
         int firstId =  ids.get(0);

         //Boxing
        Integer boxed = Integer.valueOf(firstId);
        //manual unboxing
        int unboxed = boxed.intValue();
        System.out.printf("%-34s: %d%n", "valueOf(...).intValue()", unboxed);

        // Parsing: text -> primitive. This is what the console menu has been
        // doing with every number you type since Section 13.
        System.out.printf("%-34s: %d%n", "Integer.parseInt(\"104\") + 1", Integer.parseInt("104") + 1);
        System.out.printf("%-34s: %s%n", "Double.parseDouble(\"65000.50\")", Double.parseDouble("65000.50"));

        // Caching: autoboxing goes through Integer.valueOf(), which reuses one
        // object per value from -128 to 127. So == "works" for a small id by
        // accident and fails for a bigger one -- Section 15's rule applies:
        // == compares references, equals() compares values.
        Integer smallA = 102;
        Integer smallB = 102;
        Integer bigA = 1002;
        Integer bigB = 1002;
        System.out.printf("%-34s: %b%n", "102 == 102 (cached object)", smallA == smallB);
        System.out.printf("%-34s: %b%n", "1002 == 1002 (two objects)", bigA == bigB);
        System.out.printf("%-34s: %b%n", "1002 equals 1002", bigA.equals(bigB));

        // A wrapper can be null; a primitive can't. Unboxing null is where the
        // NullPointerException comes from.
        Integer noId = null;
        try {
            int unboxedNull = noId;
            System.out.println(unboxedNull);
        } catch (NullPointerException ex) {
            System.out.printf("%-34s: %s%n", "Unboxing a null Integer", ex.getClass().getSimpleName());
        }

    }

    private static void demonstrateObjectMethods(Manager manager, Developer developer, EmployeeRepository employeeRepository) {

        System.out.println();
        System.out.println("=== Section 15: java.lang.Object ===");

        // toString(): each of these used to print like com.newgen.ems.model.Manager@a09ee92.
        for (var employee : employeeRepository.findAll()) {
            System.out.println(employee);
        }

        // getClass(): the runtime type behind a reference. getName() is the
        // text the default toString() used to print before the "@".
        System.out.printf("%n%-34s: %s%n", "manager.getClass().getName()", manager.getClass().getName());

        // equals()/hashCode(): same id means same employee, even when the
        // details differ -- but it's still a different object in memory.
        var rahulFromHr = new Developer(102, "Rahul V.", "Engineering", 70_000, "Kotlin");
        System.out.printf("%-34s: %b%n", "developer == rahulFromHr", developer == rahulFromHr);
        System.out.printf("%-34s: %b%n", "developer.equals(rahulFromHr)", developer.equals(rahulFromHr));
        System.out.printf("%-34s: %b%n", "developer.equals(manager)", developer.equals(manager));
        System.out.printf("%-34s: %b%n", "hashCodes match", developer.hashCode() == rahulFromHr.hashCode());

        // clone(): an independent copy. Plain assignment only copies the reference.
        Employee alias = developer;
        System.out.printf("%-34s: %b%n", "alias == developer", alias == developer);
        previewRaise(developer, 80_000);

        // Records: Payslip is an immutable value type -- there's no
        // setMonthlyPay(), so once issued a payslip can't change. Its
        // generated toString()/equals() use every component, unlike Employee
        // (id only).
        var payslip = Payslip.of(manager);
        var sameAgain = Payslip.of(manager);

        System.out.println();
        System.out.println(payslip);
        System.out.printf("%-34s: %b%n", "payslip == sameAgain", payslip == sameAgain);
        System.out.printf("%-34s: %b%n", "payslip.equals(sameAgain)", payslip.equals(sameAgain));

    }


    // clone() lets HR try a raise on a copy and leave the real record alone.
    // CloneNotSupportedException is checked (Section 14), but Employee
    // implements Cloneable, so it can't actually happen here.
    private static void previewRaise(Employee employee, double newBaseSalary) {
        try {
            Employee preview = (Employee) employee.clone();
            preview.setBaseSalary(newBaseSalary);
            System.out.printf("Raise preview for %s: %.2f now, %.2f if base pay were %.2f%n",
                    employee.getName(), employee.calculateMonthlySalary(),
                    preview.calculateMonthlySalary(), newBaseSalary);
            System.out.printf("%-34s: %b%n", "preview == employee", preview == employee);
            System.out.printf("%-34s: %b%n", "preview.equals(employee)", preview.equals(employee));
        } catch (CloneNotSupportedException e) {
            System.out.println("Can't preview a raise for " + employee.getName() + ": " + e.getMessage());
        }

    }

    private static void runConsoleMenu(EmployeeRepository employeeRepository) {


        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;

             while (running) {
                 System.out.println();
                 System.out.println("1. Add new Employee");
                 System.out.println("2. find Employee by id");
                 System.out.println("3. list All employees");
                 System.out.println("4. List employees in a department");
                 System.out.println("5. List employees sorted");
                 System.out.println("6. Remove an employee by id");
                 System.out.println("7. Remove a whole department");
                 System.out.println("0. Exit");
                 System.out.print("Choose an option: ");


                 int choice = readChoice(sc);

                 sc.nextLine();

                 switch (choice) {
                     case 1 -> addEmployeeFromConsole(sc, employeeRepository);
                     case 2 -> {
                         System.out.println("Enter employee id: ");
                         try {
                             int id = Integer.parseInt(sc.nextLine().trim());
                             printPayslip(employeeRepository.findById(id));
                         } catch (EmployeeNotFoundException e) {
                             System.out.println(e.getMessage());
                         }
                     }
                     case 3 -> {
                         for (Employee employee : employeeRepository.findAll()) {
                             printPayslip(employee);
                         }

                     }
                     case 4 -> listDepartment(sc, employeeRepository);
                     case 5 -> listSorted(sc, employeeRepository);
                     case 6 -> removeEmployee(sc, employeeRepository);
                     case 7 -> removeDepartment(sc, employeeRepository);
                     case 0 -> running = false;
                     default -> {
                         System.out.println("Invalid option Choose betwen 0 to 7");
                     }
                 }
                 System.out.println("Goodbye!");
             }

        }

    }

    private static void removeDepartment(Scanner scanner, EmployeeRepository repository) {
        System.out.print("Department to remove: ");
        String department = scanner.nextLine().trim();
        int removed = repository.removeDepartment(department);
        System.out.println("Removed " + removed + " employee(s) from " + department);
    }

    private static void removeEmployee(Scanner scanner, EmployeeRepository repository) {
        System.out.print("Enter Employeeee id to remove: ");
        try {
            int id = readChoice(scanner);
            Employee removed = repository.remove(id);
            System.out.println("Removed " + removed.getName() + " (" + removed.designation() + ")");
        } catch (NumberFormatException | EmployeeNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void listDepartment(Scanner sc, EmployeeRepository employeeRepository) {
        System.out.print("Department: ");
        String department = sc.nextLine().trim();
        List<Employee> matches = employeeRepository.findByDepartment(department);
        if(matches.isEmpty()) {
            System.out.println("No employees found in Department " + department);
        }

        for(Employee employee : matches) {
            System.out.println(Payslip.of(employee));
        }
    }


    private static void listSorted(Scanner scanner, EmployeeRepository repository) {
        System.out.println("1. By id  2. By name  3. By monthly pay (highest first)");
        System.out.print("Sort by: ");
        Comparator<Employee> order = switch (readChoice(scanner)) {
            case 1 -> Comparator.naturalOrder();
            case 2 -> new EmployeeNameComparator();
            case 3 -> new EmployeeSalaryComparator();
            default -> null;
        };
        if (order == null) {
            System.out.println("Please choose 1-3.");
            return;
        }
        for (Employee employee : repository.findAllSorted(order)) {
            System.out.println(Payslip.of(employee));
        }

    }



    private static int readChoice(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static void addEmployeeFromConsole(Scanner sc, EmployeeRepository employeeRepository) {

        System.out.print("Employee id: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Department: ");
        String department = sc.nextLine().trim();

        System.out.print("Base salary: ");
        double baseSalary = Double.parseDouble(sc.nextLine().trim());

        System.out.println("1. Manager  2. Developer  3. Intern");
        System.out.print("Employee type: ");
        int type = Integer.parseInt(sc.nextLine().trim());

        Employee newEmployee = switch (type) {

            case 1 -> {
                System.out.print("Team size: ");
                int teamSize = Integer.parseInt(sc.nextLine().trim());
                yield new Manager(id, name, department, baseSalary, teamSize);
            }

            case 2 -> {
                System.out.print("Primary language: ");
                String language = sc.nextLine().trim();
                yield new Developer(id, name, department, baseSalary, language);
            }

            default ->  {
                System.out.print("Mentor name: ");
                String mentorName = sc.nextLine().trim();
                yield new Intern(id, name, department, baseSalary, mentorName);
            }

        };

        employeeRepository.add(newEmployee);
        System.out.println("Employee " + id + " has been added.");
        printPayslip(newEmployee);
    }

    private static void printPayslip(Employee employee) {
        String payslip = String.format("""
                --------------------------------
                Payslip for %-15s
                Designation : %s
                Department  : %s
                Monthly Pay : %.2f
                --------------------------------
                """, employee.getName(), employee.designation(),
                employee.getDepartment(), employee.calculateMonthlySalary());
        System.out.println(payslip);
    }

}