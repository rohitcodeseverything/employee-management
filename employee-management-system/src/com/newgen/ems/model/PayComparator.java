package com.newgen.ems.model;

import java.util.Comparator;

public class PayComparator implements Comparator<Payable> {

    @Override
    public int compare(Payable first, Payable second) {
        // The arguments are swapped on purpose: that is what makes it descending.
        return Double.compare(second.calculateMonthlySalary(), first.calculateMonthlySalary());
    }
}
