package com.newgen.ems.app;

import com.newgen.ems.model.*;
import com.newgen.ems.repository.EmployeeRepository;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.printf("[====Employee Management System Application====]\n");

        Manager manager = new Manager(101, "Asha Rao", "Engineering", 90_000, 6);
        Developer developer = new Developer(102, "Rahul Verma", "Engineering", 65_000, "Java");
        Intern inter = new Intern(103, "Priya Singh", "Engineering", 15_000, "Asha Rao");
        Developer secondDeveloper = new Developer(104, "Virat Kohli", "Engineering", 60_000, "Python");

        EmployeeRepository employeeRepository = new EmployeeRepository(2);
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
        Employee found = employeeRepository.findById(searchId);
        if (found != null) {
            System.out.println("Found employee " + searchId + ": " + found.designation());
        } else {
            System.out.println("No employee with id " + searchId);
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

        Promotable[] promotables = {manager, developer, secondDeveloper};

        for (Promotable promotable : promotables) {
            promotable.promote();
        }

        double yearToDate = 0;
        for (int month = 1; month <= 12; month++) {
            yearToDate += manager.calculateMonthlySalary();
        }
        System.out.printf("%n%s year-to-date earnings after 12 months: %.2f%n", manager.getName(), yearToDate);

        runConsoleMenu(employeeRepository);



//        // Section 15 teaser: Object's default toString() isn't useful yet --
//        // we'll override it when we cover java.lang.Object.
//        System.out.println();
//        System.out.println("Default Object#toString() (we'll fix this in Section 15): " + manager);

    }

    private static void runConsoleMenu(EmployeeRepository employeeRepository) {


        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;

             while (running) {
                 System.out.println();
                 System.out.println("1. Add new Employee");
                 System.out.println("2. find Employee by id");
                 System.out.println("3. list All employees");
                 System.out.println("4. exit");
                 System.out.print("Choose an option: ");

                 int choice = sc.nextInt();
                 sc.nextLine();

                 switch (choice) {
                     case 1 -> addEmployeeFromConsole(sc, employeeRepository);
                     case 2 -> {
                         System.out.println("Enter employee id: ");
                         int id = Integer.parseInt(sc.nextLine().trim());
                         printPayslip(employeeRepository.findById(id));
                     }
                     case 3 -> {
                         for (Employee employee : employeeRepository.findAll()) {
                             printPayslip(employee);
                         }

                     }
                     case 4 -> {
                         running = false;
                     }
                     default -> {
                         System.out.println("Invalid option Choose betwen 1 to 4");
                     }
                 }
             }

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