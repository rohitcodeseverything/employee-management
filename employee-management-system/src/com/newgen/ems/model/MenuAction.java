package com.newgen.ems.model;

import com.newgen.ems.repository.EmployeeRepository;

import java.util.Scanner;

@FunctionalInterface
public interface MenuAction {

    void run(Scanner scanner, EmployeeRepository employeeRepository);
}
