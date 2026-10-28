package com.newgen.ems.service;

import com.newgen.ems.model.Department;
import com.newgen.ems.model.Employee;
import com.newgen.ems.model.Payslip;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PayrollReport {

    private static final Comparator<Payslip> HIGHEST_PAY_FIRST =
            Comparator.comparing(Payslip::salary, Comparator.reverseOrder());

    private final List<Payslip> payslips;


    public PayrollReport(Collection<? extends Employee> employees) {
            this.payslips =
                    employees.stream()
                            .map(Payslip::of)
                            .collect(readOnlyList());
    }

    public static <T> Collector<T, ? , List<T>> readOnlyList() {
        return Collectors.collectingAndThen(

                Collectors.toList(),
                Collections::unmodifiableList
        );
    }

    public List<Payslip> payslips() {
        return payslips;
    }

    public int headCount() {
        return payslips.size();
    }

    public double totalMonthlyPay() {

        return total(payslips);
    }


    public double averageMonthlySalary() {
        return  payslips.isEmpty() ? 0 : totalMonthlyPay()/payslips.size();
    }



    private static double total(Collection<Payslip> payslips) {
        return
                payslips.stream()
                        .mapToDouble(Payslip::salary)
                        .reduce(0.0, Double::sum);
    }


    public List<Payslip> topEarners(int count) {
        if(count < 0) {

            throw new IllegalArgumentException("count cant be negative : " + count);
        }

        return payslips.stream()
                .sorted(HIGHEST_PAY_FIRST)
                .limit(count)
                .collect(readOnlyList());
    }

    public Map<Department, List<Payslip>> byDepartment() {

        Map<Department, List<Payslip>> map =
                payslips.stream()
                        .collect(Collectors.groupingBy(
                                Payslip::department,
                                TreeMap::new,
                                readOnlyList()
                        ));

        return  Collections.unmodifiableMap(map);
    }

    public Map<Department, Double> totalByDepartment() {

        Map<Department , Double> totals =

                payslips.stream()
                        .collect(
                                Collectors.groupingBy(

                                        Payslip::department,

                                        Collectors.collectingAndThen(
                                                Collectors.toList(),
                                                PayrollReport::total
                                        )
                                )
                        );

        return Collections.unmodifiableMap(totals);
    }

    public Map<Boolean, List<Payslip>> splitAbove(double threshold) {


        Map<Boolean, List<Payslip>> parts =

                payslips.stream()
                        .collect(
                                Collectors.partitioningBy(

                                        payslip -> payslip.salary() > threshold,
                                        readOnlyList()
                                )
                        );

        return  Collections.unmodifiableMap(parts);

    }








}
