package com.newgen.ems.app;

import com.newgen.ems.model.*;
import com.newgen.ems.repository.ActivityEntry;
import com.newgen.ems.repository.BoundedHistory;
import com.newgen.ems.repository.EmployeeNotFoundException;
import com.newgen.ems.repository.EmployeeRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    private static final LocalDate DEMO_HIRE_DATE = LocalDate.of(2026, 1, 5);

    private static final Comparator<Employee> BY_NAME =
            (first, second) -> first.getName().compareToIgnoreCase(second.getName());

    private static final Comparator<Payable> BY_PAY_HIGHEST_FIRST =
            (first, second) ->
                    Double.compare(second.calculateMonthlySalary(), first.calculateMonthlySalary());



    public static void main(String[] args) {

        System.out.printf("[====Employee Management System Application====]\n");

        Manager manager = new Manager(101, "Asha Rao", Department.ENGINEERING, 90_000, LocalDate.of(2016, 4, 4), 6);
        Developer developer = new Developer(102, "Rahul Verma", Department.ENGINEERING, 65_000, LocalDate.of(2019, 8, 19),"Java");
        Intern inter = new Intern(103, "Priya Singh", Department.ENGINEERING, 15_000, LocalDate.of(2026, 6, 1), "Asha Rao");
        Developer secondDeveloper = new Developer(104, "Virat Kohli", Department.ENGINEERING, 60_000, LocalDate.of(2022, 1, 10), "Python");

        manager.addSkill("Leadership");
        manager.addSkill("Java");
        developer.addSkill("Java");
        developer.addSkill("SQL");
        developer.addSkill("Docker");
        developer.addSkill("java");     // already there (ignoring case): ignored
        secondDeveloper.addSkill("Python");
        secondDeveloper.addSkill("SQL");
        inter.addSkill("Java");


        EmployeeRepository employeeRepository = new EmployeeRepository();
        employeeRepository.add(manager);
        employeeRepository.add(developer);
        employeeRepository.add(inter);
        employeeRepository.add(secondDeveloper);

        printPayslips(employeeRepository.findAll());

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

        promotables.forEach(promotable -> promotable.promote());

        double yearToDate = 0;
        for (int month = 1; month <= 12; month++) {
            yearToDate += manager.calculateMonthlySalary();
        }
        System.out.printf("%n%s year-to-date earnings after 12 months: %.2f%n", manager.getName(), yearToDate);

        demonstrateObjectMethods(manager, developer, employeeRepository);
        demonstrateWrapperClasses(employeeRepository);
        demonstrateLists(employeeRepository);
        demonstrateLinkedList(employeeRepository);
        demonstrateGenerics(employeeRepository, developer, secondDeveloper);
        demonstrateMap(employeeRepository);
        demonstrateSets(employeeRepository, developer);
        demonstrateEnums(employeeRepository, developer);
        demonstrateDates(manager);

        System.out.println();
        System.out.println("=== Section 13/14: over to you (Scanner input, now with exception handling) ===");
        runConsoleMenu(employeeRepository);



//        // Section 15 teaser: Object's default toString() isn't useful yet --
//        // we'll override it when we cover java.lang.Object.
//        System.out.println();
//        System.out.println("Default Object#toString() (we'll fix this in Section 15): " + manager);

    }

    private static void demonstrateDates(Manager manager) {
        System.out.println();
        System.out.println("=== Section 23: date and time ===");

        LocalDate hired = manager.getHireDate();
        System.out.printf("%-34s: %s%n", "hire date (ISO text)", hired);
        System.out.printf("%-34s: %d, %s, %d%n", "getYear/getMonth/getDayOfMonth",
                hired.getYear(), hired.getMonth(), hired.getDayOfMonth());
        System.out.printf("%-34s: %s%n", "getDayOfWeek()", hired.getDayOfWeek());
        System.out.printf("%-34s: %s%n", "format(\"dd MMM yyyy\")", DATE_FORMAT.format(hired));
        System.out.printf("%-34s: %s (hired is still %s)%n", "plusYears(5) is a new LocalDate", hired.plusYears(5), hired);

        LocalDate reportDate = LocalDate.of(2026, 1, 1);
        Period tenure = manager.tenure(reportDate);
        System.out.printf("%-34s: %b%n", "hired.isBefore(2026-01-01)", hired.isBefore(reportDate));
        System.out.printf("%-34s: %s%n", "tenure as of 2026-01-01", tenure);
        System.out.printf("%-34s: %d, %d, %d%n", "getYears/getMonths/getDays",
                tenure.getYears(), tenure.getMonths(), tenure.getDays());
        System.out.printf("%-34s: %s%n", "formatted for people", formatTenure(tenure));
        System.out.printf("%-34s: %s%n", "hire date plus that Period", hired.plus(tenure));
        System.out.printf("%-34s: %s%n", "Jan 31 to Mar 1",
                Period.between(LocalDate.of(2026, 1, 31), LocalDate.of(2026, 3, 1)));

        System.out.printf("%-34s: %s%n", "parse(\"2024-02-29\")", LocalDate.parse("2024-02-29"));

        try {
            LocalDate.parse("2025-02-30");
        } catch (DateTimeParseException ex) {
            System.out.printf("%-34s: %s%n", "parse(\"2025-02-30\")", ex.getClass().getSimpleName());
        }

        DateTimeFormatter slashes = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.printf("%-34s: %s%n", "ofPattern parse(\"30/02/2025\")", LocalDate.parse("30/02/2025", slashes));

        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 9, 30);
        LocalDateTime stop = start.plusHours(2).plusMinutes(15);
        System.out.printf("%-34s: %s%n", "Duration.between(09:30, 11:45)", Duration.between(start, stop));

        System.out.printf("%-34s: %s%n", "an ActivityEntry, as displayed",
                new ActivityEntry(start, "Added Manager 101 (Asha Rao)"));

    }

    private static String formatTenure(Period tenure) {
        if (tenure.getYears() > 0) {
            return plural(tenure.getYears(), "year") + ", " + plural(tenure.getMonths(), "month");
        }
        if (tenure.getMonths() > 0) {
            return plural(tenure.getMonths(), "month");
        }
        return plural(tenure.getDays(), "day");
    }

    private static String plural(int count, String unit) {
        return count + " " + unit + (count == 1 ? "" : "s");
    }




    private static void demonstrateEnums(EmployeeRepository employeeRepository, Developer developer) {
        System.out.println();
        System.out.println("=== Section 22: enums ===");

        System.out.printf("%-34s: %s%n", "Department.values()", Arrays.toString(Department.values()));

        Department hr = Department.HUMAN_RESOURCES;
        System.out.printf("%-34s: %s, %d%n", "HUMAN_RESOURCES name(), ordinal()", hr.name(), hr.ordinal());
        System.out.printf("%-34s: %s%n", "HUMAN_RESOURCES getDisplayName()", hr.getDisplayName());
        System.out.printf("%-34s: %s%n", "values()[2]", Department.values()[2]);
        System.out.printf("%-34s: %s%n", "valueOf(\"SALES\")", Department.valueOf("SALES"));

        try {
            Department.valueOf("Sales");
        } catch (IllegalArgumentException ex) {
            System.out.printf("%-34s: %s%n", "valueOf(\"Sales\") -- wrong case", ex.getClass().getSimpleName());
        }

        System.out.printf("%-34s: %b%n", "getDepartment() == ENGINEERING",
                developer.getDepartment() == Department.ENGINEERING);

        for (EmployeeStatus status : EmployeeStatus.values()) {
            String note = switch (status) {
                case ACTIVE -> "eligible for the annual stock grant";
                case ON_LEAVE -> "on the payroll, back soon; no stock grant yet";
                case ON_NOTICE -> "on the payroll until the last day; no stock grant";
            };
            System.out.printf("%-34s: %s%n", status.getDisplayName(), note);
        }

        EnumSet<Department> all = EnumSet.allOf(Department.class);
        EnumSet<Department> middle = EnumSet.range(Department.HUMAN_RESOURCES, Department.FINANCE);
        EnumSet<Department> real = EnumSet.complementOf(EnumSet.of(Department.UNASSIGNED));
        System.out.printf("%-34s: %s%n", "EnumSet.allOf(Department.class)", all);
        System.out.printf("%-34s: %s%n", "EnumSet.range(HR, FINANCE)", middle);
        System.out.printf("%-34s: %s%n", "complementOf(of(UNASSIGNED))", real);
        System.out.printf("%-34s: %s%n", "Nobody works in", employeeRepository.departmentWithNoEmployees());

        EmployeeRepository scratch = new EmployeeRepository();
        scratch.add(new Intern(900, "Temp A", Department.SALES, 1_000, DEMO_HIRE_DATE, "Nobody"));
        scratch.add(new Intern(901, "Temp B", Department.FINANCE, 1_000, DEMO_HIRE_DATE, "Nobody"));
        scratch.add(new Intern(902, "Temp C", Department.ENGINEERING, 1_000,DEMO_HIRE_DATE,  "Nobody"));
        scratch.add(new Intern(903, "Temp D", Department.SALES, 1_000, DEMO_HIRE_DATE, "Nobody"));
        System.out.printf("%-34s: %s%n", "headcount (declaration order)", scratch.headCountByDepartment());

        try {
            boolean first = scratch.changeStatus(900, EmployeeStatus.ON_LEAVE);
            boolean again = scratch.changeStatus(900, EmployeeStatus.ON_LEAVE);
            System.out.printf("%-34s: %b, then %b%n", "changeStatus(ON_LEAVE) twice", first, again);
            System.out.printf("%-34s: %s%n", "latest activity", scratch.history().get(0));
            System.out.printf("%-34s: %b%n", "isActive() once on leave", scratch.findById(900).isActive());
        } catch (EmployeeNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private static void demonstrateSets(EmployeeRepository employeeRepository, Developer developer) {

        System.out.println();
        System.out.println("=== Section 21: sets ===");

        List<String> typed = List.of("Java", "SQL", "Docker", "Python", "Leadership", "Java");

        Set<String> hashed = new HashSet<>();
        Set<String> sorted = new TreeSet<>();
        LinkedHashSet<String> insertion = new LinkedHashSet<>();

        for (String skill : typed) {
            hashed.add(skill);
            sorted.add(skill);
            insertion.add(skill);
        }

        System.out.printf("%-34s: %s%n", "HashSet (no promised order)", hashed);
        System.out.printf("%-34s: %s%n", "TreeSet (sorted)", sorted);
        System.out.printf("%-34s: %s%n", "LinkedHashSet (insertion order)", insertion);

        SequencedSet<String> newestFirst = insertion.reversed();
        System.out.printf("%-34s: %s%n", "LinkedHashSet reversed()", newestFirst);
        System.out.printf("%-34s: %d typed, %d kept%n", "Duplicates are ignored", typed.size(), insertion.size());
        System.out.printf("%-34s: %b%n", "add(\"Java\") again says new?", insertion.add("Java"));


        Set<Employee> team = new HashSet<>();
        team.add(developer);
        var rahulFromHr = new Developer(102, "Rahul V.", Department.ENGINEERING, 70_000, LocalDate.of(2020, 1, 6),
                "Kotlin");
        System.out.printf("%-34s: %b%n", "team.add(same id, other object)", team.add(rahulFromHr));
        var temp = new Intern(900, "Temp", Department.ENGINEERING, 1_000, DEMO_HIRE_DATE, "Nobody");
        team.add(temp);
        temp.addSkill("Mentoring");
        System.out.printf("%-34s: %b%n", "contains(temp) after addSkill", team.contains(temp));

        Set<Employee> javaPeople = new LinkedHashSet<>(employeeRepository.findBySkills(Set.of("Java")));
        Set<Employee> sqlPeople = new LinkedHashSet<>(employeeRepository.findBySkills(Set.of("SQL")));
        Set<Employee> both = new LinkedHashSet<>(javaPeople);
        both.retainAll(sqlPeople);
        Set<Employee> either = new LinkedHashSet<>(javaPeople);
        either.addAll(sqlPeople);
        Set<Employee> javaOnly = new LinkedHashSet<>(javaPeople);
        javaOnly.removeAll(sqlPeople);
        System.out.printf("%-34s: %s%n", "knows Java", names(javaPeople));
        System.out.printf("%-34s: %s%n", "knows SQL", names(sqlPeople));
        System.out.printf("%-34s: %s%n", "Java AND SQL (retainAll)", names(both));
        System.out.printf("%-34s: %s%n", "Java OR SQL (addAll)", names(either));
        System.out.printf("%-34s: %s%n", "Java but NOT SQL (removeAll)", names(javaOnly));
        System.out.printf("%-34s: %b%n", "Java people contain the AND set", javaPeople.containsAll(both));

        Set<Employee> byPay = new TreeSet<>(BY_PAY_HIGHEST_FIRST);
        byPay.add(new Intern(901, "Sam", Department.ENGINEERING, 15_000, DEMO_HIRE_DATE, "Nobody"));
        byPay.add(new Intern(902, "Alex", Department.ENGINEERING, 15_000, DEMO_HIRE_DATE, "Nobody"));
        System.out.printf("%-34s: %s%n", "byPay", byPay);
        System.out.printf("%-34s: %d%n", "TreeSet by pay, 2 on the same pay", byPay.size());

        try {
            developer.getSkills().add("Sneaky");
        } catch (UnsupportedOperationException ex) {
            System.out.printf("%-34s: %s%n", "getSkills().add(...)", ex.getClass().getSimpleName());
        }

        try {
            Employee copy = (Employee) developer.clone();
            copy.addSkill("Rust");
            System.out.printf("%-34s: %b%n", "clone has the new skill", copy.getSkills().contains("Rust"));
            System.out.printf("%-34s: %b%n", "original has it too", developer.getSkills().contains("Rust"));
        } catch (CloneNotSupportedException ex) {
            System.out.println("Can't clone: " + ex.getMessage());
        }

    }

    private static List<String> names(Collection<? extends Employee> employees) {
        List<String> names = new ArrayList<>();
        for (Employee employee : employees) {
            names.add(employee.getName());
        }
        return names;
    }

    private static void demonstrateMap(EmployeeRepository employeeRepository) {
        System.out.println();
        System.out.println("=== Section 20: maps ===");

        Map<String, Double> hashed = new HashMap<>();
        Map<String, Double> sorted = new TreeMap<>();
        LinkedHashMap<String, Double> insertion = new LinkedHashMap<>();

        for(Employee employee : employeeRepository.findAll()) {

            double pay = employee.calculateMonthlySalary();

            hashed.put(employee.getName(), pay);
            sorted.put(employee.getName(), pay);
            insertion.put(employee.getName(), pay);

        }

        System.out.printf("%-34s: %s%n", "HashMap (no promised order)", hashed.keySet());
        System.out.printf("%-34s: %s%n", "TreeMap (keys sorted)", sorted.keySet());
        System.out.printf("%-34s: %s%n", "LinkedHashMap (insertion order)", insertion.keySet());


        SequencedMap<String, Double> newestFirst = insertion.reversed();

        System.out.printf("%-34s: %s%n", "LinkedHashMap reversed()", newestFirst.keySet());

        System.out.printf("%-34s: %s%n", "get(\"Rahul Verma\")", sorted.get("Rahul Verma"));
        System.out.printf("%-34s: %s%n", "get(\"Nobody\") -- not a key", sorted.get("Nobody"));

        double total = 0;

        for(double pay : sorted.values()) {
            total += pay;
        }

        System.out.printf("%-34s: %.2f%n", "Total monthly payroll (values())", total);

        for (Map.Entry<String, Double> entry : sorted.entrySet()) {
            System.out.printf("  %-32s %,.2f%n", entry.getKey(), entry.getValue());
        }

        // Map.of builds an immutable map, like List.of and Set.of: put() fails.
        Map<String, Double> rates = Map.of("java", 0.10, "python", 0.10);

        try {
            rates.put("kotlin", 0.10);

        } catch (UnsupportedOperationException e) {
            System.out.printf("%-34s: %s%n", "Map.of(...).put(...)", e.getClass().getSimpleName());
        }



    }

    private static void demonstrateGenerics(EmployeeRepository employeeRepository, Developer developer, Developer secondDeveloper) {

        System.out.println();
        System.out.println();
        System.out.println("=== Section 19: generics ===");


        BoundedHistory<String> notes = new BoundedHistory<>(2);
        notes.record("first");
        notes.record("second");
        notes.record("third");

        BoundedHistory<Integer> ids = new BoundedHistory<>(3);
        for (int id : employeeRepository.findAllIds()) {
            ids.record(id);
        }

        BoundedHistory<Employee> team = new BoundedHistory<>(2);
        for (Employee employee : employeeRepository.findAll()) {
            team.record(employee);
        }

        Employee mostRecent = team.latestFirst().get(0);
        System.out.printf("%-34s: %s%n", "BoundedHistory<String>", notes.latestFirst());
        System.out.printf("%-34s: %s%n", "BoundedHistory<Integer>", ids.latestFirst());
        System.out.printf("%-34s: %s%n", "BoundedHistory<Employee> newest", mostRecent.getName());


        List<Developer> developers = List.of(developer, secondDeveloper);
        printPayslips(developers);

        Comparator<Payable> byPay = BY_PAY_HIGHEST_FIRST;

        List<String> highestPaidFirst = new ArrayList<>();
        for (Employee employee : employeeRepository.findAllSorted(byPay)) {
            highestPaidFirst.add(employee.getName());
        }

        System.out.printf("%-34s: %s%n", "Highest paid first", String.join(", ", highestPaidFirst));

        Employee firstEmployee = firstOf(employeeRepository.findAll());
        ActivityEntry newestActivity = firstOf(employeeRepository.history());
        System.out.printf("%-34s: %s%n", "firstOf(findAll())", firstEmployee.getName());
        System.out.printf("%-34s: %s%n", "firstOf(history())", newestActivity);

    }

    private static <T> T firstOf(List<T> items) {
        return items.get(0);
    }

    private static void printPayslips(Collection<? extends Employee> employees) {
        employees.forEach(employee -> printPayslip(employee));
    }

    private static void demonstrateLinkedList(EmployeeRepository employeeRepository) {

        System.out.println();
        System.out.println("=== Section 18 L LinkedList ===");

        printNumbered("Recent activity (newest first): ", employeeRepository.history());

        BoundedHistory<String> smallLog = new BoundedHistory<>(3);

        for(int i=1 ;i<=5;i++) {
            smallLog.record("entry " + i);
        }
        printNumbered("Capacity-3 log after 5 entries (newest first):", smallLog.latestFirst());

    }

    private static void printNumbered(String title, List<?> history) {
        System.out.println(title);

        if (history.isEmpty()) {
            System.out.println(" (none) ");
        }

        int number = 1;

        for( Object item : history) {
            System.out.print("  " + number++ + ". " + item);
        }
    }

    private static void demonstrateLists(EmployeeRepository employeeRepository) {
        System.out.println();
        System.out.println("=== Section 17: lists ===");

        List<Employee> everyone =  employeeRepository.findAll();

         try {
             everyone.add(new Intern(999, "Komal", Department.ENGINEERING, 1, DEMO_HIRE_DATE, "Nobody"));
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
        employeeRepository.findAll().forEach(employee -> System.out.println("employee = " + employee));

        // getClass(): the runtime type behind a reference. getName() is the
        // text the default toString() used to print before the "@".
        System.out.printf("%n%-34s: %s%n", "manager.getClass().getName()", manager.getClass().getName());

        // equals()/hashCode(): same id means same employee, even when the
        // details differ -- but it's still a different object in memory.
        var rahulFromHr = new Developer(102, "Rahul V.", Department.ENGINEERING, 70_000, DEMO_HIRE_DATE, "Kotlin");
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

        Map<Integer, MenuItem> menu = buildMenu();

        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;

             while (running) {
                 System.out.println();

                 for(Map.Entry<Integer, MenuItem> entry: menu.entrySet()) {
                     System.out.println(entry.getKey() + ". " + entry.getValue().label());
                 }

                 System.out.println("0. Exit");
                 System.out.print("Choose an option: ");


                 int choice = readChoice(sc);

                 MenuItem selected = menu.get(choice);

                 if(choice == 0) {
                     running = false;
                 } else if(selected == null ) {
                     System.out.println("PLease choose between 0-" + menu.size() + ". ");

                 } else {
                     selected.action().run(sc, employeeRepository);
                 }


                 System.out.println("Goodbye!");
             }

        }

    }

    private static void findEmployee(Scanner scanner, EmployeeRepository repository) {
        System.out.print("Enter employee id: ");
        // Same idiom BufferedReader uses -- read the line as text, then parse
        // it.
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            printPayslip(repository.findById(id));
        } catch (NumberFormatException | EmployeeNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private record MenuItem(String label, MenuAction action){}


    private static Map<Integer, MenuItem> buildMenu() {

        Map<Integer, MenuItem> menu = new LinkedHashMap<>();

        menu.put(1 , new MenuItem("Add new Employee",
                (scanner, repository) -> addEmployeeFromConsole(scanner, repository)));

        menu.put(2, new MenuItem("find Employee by id",
                ((scanner, repository) -> findEmployee(scanner, repository))));

        menu.put(3, new MenuItem("List all employees",
                (scanner, repository) -> printPayslips(repository.findAll())));
        menu.put(4, new MenuItem("List employees in a department",
                (scanner, repository) -> listDepartment(scanner, repository)));
        menu.put(5, new MenuItem("List employees sorted",
                (scanner, repository) -> listSorted(scanner, repository)));
        menu.put(6, new MenuItem("Remove an employee by id",
                (scanner, repository) -> removeEmployee(scanner, repository)));
        menu.put(7, new MenuItem("Remove a whole department",
                (scanner, repository) -> removeDepartment(scanner, repository)));
        menu.put(8, new MenuItem("Show recent activity",
                (scanner, repository) -> printNumbered("Recent activity (newest first):", repository.history())));
        menu.put(9, new MenuItem("Show recently removed employees",
                (scanner, repository) -> printNumbered("Recently removed (newest first):", repository.recentlyRemoved())));
        menu.put(10, new MenuItem("Show headcount by department",
                (scanner, repository) -> showHeadCount(repository)));
        menu.put(11, new MenuItem("Add a skill to an employee",
                (scanner, repository) -> addSkill(scanner, repository)));
        menu.put(12, new MenuItem("Find employees by skills",
                (scanner, repository) -> findBySkills(scanner, repository)));
        menu.put(13, new MenuItem("Change an employee's status",
                (scanner, repository) -> changeStatus(scanner, repository)));
        menu.put(14, new MenuItem("Show tenure of every employee",
                (scanner, repository) -> showTenure(repository)));

        return menu;


    }

    private static void showTenure(EmployeeRepository repository) {
        LocalDate today = LocalDate.now();
        System.out.println("Tenure as of " + DATE_FORMAT.format(today) + ":");
        List<Employee> everyone = repository.findAll();
        if (everyone.isEmpty()) {
            System.out.println("  (none)");
        }
        for (Employee employee : everyone) {
            System.out.printf("  %-15s joined %s -- %s%n", employee.getName(),
                    DATE_FORMAT.format(employee.getHireDate()), formatTenure(employee.tenure(today)));
        }
    }


    private static void addSkill(Scanner scanner, EmployeeRepository repository) {
        System.out.println("Skills on file: " + repository.allSkills());
        System.out.print("Employee id: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Employee employee = repository.findById(id);
            System.out.print("Skill to add to " + employee.getName() + ": ");
            String skill = scanner.nextLine();
            if (repository.addSkill(id, skill)) {
                System.out.println("Added " + skill.trim() + " to " + employee.getName());
            } else {
                System.out.println("Nothing added: that skill is blank, or " + employee.getName() + " already has it.");
            }
        } catch (NumberFormatException | EmployeeNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void findBySkills(Scanner scanner, EmployeeRepository repository) {
        System.out.println("Skills on file: " + repository.allSkills());
        System.out.print("Skills they must ALL have (comma separated): ");
        Set<String> required = new LinkedHashSet<>();
        for (String skill : scanner.nextLine().split(",")) {
            String trimmed = skill.trim();
            if (!trimmed.isEmpty()) {
                required.add(trimmed);
            }
        }
        if (required.isEmpty()) {
            System.out.println("Please enter at least one skill.");
            return;
        }
        Set<Employee> matches = repository.findBySkills(required);
        if (matches.isEmpty()) {
            System.out.println("No employee has all of: " + required);
        }
        printPayslips(matches);
    }



    private static void showHeadCount(EmployeeRepository employeeRepository) {
            System.out.println("Headcount By Department:");

            Map<Department, Integer> headcount = employeeRepository.headCountByDepartment();

            if(headcount.isEmpty()) {
                System.out.println("No employees found");
            }

            for( Map.Entry<Department, Integer> entry : headcount.entrySet()) {
                System.out.println(" " + entry.getKey() + ": " + entry.getValue());
            }
    }


    private static void removeDepartment(Scanner scanner, EmployeeRepository repository) {
        System.out.print("Department to remove: ");
        Department department = readDepartment(scanner);
        int removed = repository.removeDepartment(department);
        System.out.println("Removed " + removed + " employee(s) from " + department.getDisplayName());
    }

    private static void changeStatus(Scanner scanner, EmployeeRepository repository) {

        System.out.println("Employee id: ");
        try{
            int id = Integer.parseInt(scanner.nextLine().trim());
            Employee employee = repository.findById(id);
            System.out.println(employee.getName() + " is currently: " + employee.getStatus().getDisplayName());
            EmployeeStatus status = readStatus(scanner);
            if (status == null) {
                System.out.println("Please choose 1-" + EmployeeStatus.values().length + ".");
                return;
            }
            if (repository.changeStatus(id, status)) {
                System.out.println(employee.getName() + " is now: " + status.getDisplayName());
            } else {
                System.out.println("Nothing changed: " + employee.getName() + " is already " + status.getDisplayName() + ".");
            }

        } catch(NumberFormatException | EmployeeNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private static EmployeeStatus readStatus(Scanner scanner) {
        EmployeeStatus[] statuses = EmployeeStatus.values();
        System.out.println("Statuses:");
        for (int i = 0; i < statuses.length; i++) {
            System.out.println("  " + (i + 1) + ". " + statuses[i].getDisplayName());
        }
        System.out.print("Status number: ");
        int choice = readChoice(scanner);
        if (choice < 1 || choice > statuses.length) {
            return null;
        }
        return statuses[choice - 1];
    }

    private static Department readDepartment(Scanner scanner) {
        Department[] departments = Department.values();
        System.out.println("Departments:");
        for (int i = 0; i < departments.length; i++) {
            System.out.println("  " + (i + 1) + ". " + departments[i].getDisplayName());
        }
        System.out.print("Department number: ");
        int choice = readChoice(scanner);
        if (choice < 1 || choice > departments.length) {
            return null;
        }
        return departments[choice - 1];
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
        Department department = readDepartment(sc);
        if(department == null) {
            System.out.println("Please choose 1-" + Department.values().length + ".");
            return;
        }

        List<Employee> matches = employeeRepository.findByDepartment(department);
        if(matches.isEmpty()) {
            System.out.println("No employees found in Department " + department.getDisplayName());
        }

        printPayslips(matches);
    }


    private static void listSorted(Scanner scanner, EmployeeRepository repository) {
        System.out.println("1. By id  2. By name  3. By monthly pay (highest first)");
        System.out.print("Sort by: ");
        Comparator<? super Employee> order = switch (readChoice(scanner)) {
            case 1 -> Comparator.naturalOrder();
            case 2 -> BY_NAME;
            case 3 -> BY_PAY_HIGHEST_FIRST;
            default -> null;
        };
        if (order == null) {
            System.out.println("Please choose 1-3.");
            return;
        }

        printPayslips(repository.findAllSorted(order));

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
        Department department = readDepartment(sc);

        System.out.print("Hire date (yyyy-MM-dd): ");
        LocalDate hireDate = LocalDate.parse(sc.nextLine().trim());

        if(hireDate.isAfter(LocalDate.now())) {
            System.out.println("A hire date can't be in the future -- employee not added.");
            return;
        }

        System.out.print("Base salary: ");
        double baseSalary = Double.parseDouble(sc.nextLine().trim());

        System.out.println("1. Manager  2. Developer  3. Intern");
        System.out.print("Employee type: ");
        int type = Integer.parseInt(sc.nextLine().trim());

        Employee newEmployee = switch (type) {

            case 1 -> {
                System.out.print("Team size: ");
                int teamSize = Integer.parseInt(sc.nextLine().trim());
                yield new Manager(id, name, department, baseSalary, hireDate, teamSize);
            }

            case 2 -> {
                System.out.print("Primary language: ");
                String language = sc.nextLine().trim();
                yield new Developer(id, name, department, baseSalary,hireDate , language);
            }

            default ->  {
                System.out.print("Mentor name: ");
                String mentorName = sc.nextLine().trim();
                yield new Intern(id, name, department, baseSalary, hireDate, mentorName);
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
                employee.getDepartment().getDisplayName(), employee.calculateMonthlySalary());
        System.out.println(payslip);
    }



}