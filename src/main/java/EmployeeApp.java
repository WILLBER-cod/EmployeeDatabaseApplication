import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EmployeeApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String mode = args[0];
        try {
            switch (mode) {
                case "1":
                    DatabaseManager.getInstance().createTable();
                    break;
                case "2":
                    handleInsertMode(args);
                    break;
                case "3":
                    DatabaseManager.getInstance().printUniqueEmployees();
                    break;
                case "4":
                    DatabaseManager.getInstance().generateEmployees(1000000, 100);
                    break;
                case "5":
                    DatabaseManager.getInstance().dropIndex();
                    PerformanceTester.measureQueryPerformance(false);
                    break;
                case "6":
                    DatabaseManager.getInstance().createIndex();
                    PerformanceTester.measureQueryPerformance(true);
                    break;
                default:
                    System.out.println("Invalid mode: " + mode);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleInsertMode(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java EmployeeApp 2 \"Full Name\" yyyy-MM-dd gender");
            return;
        }
        try {
            LocalDate birthDate = LocalDate.parse(args[2]);
            Employee employee = new Employee(args[1], birthDate, args[3]);
            employee.save();
            System.out.println("Employee added: " + employee);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format. Use yyyy-MM-dd.");
        } catch (Exception e) {
            System.err.println("Failed to add employee: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java EmployeeApp <mode> [arguments]");
        System.out.println("Modes:");
        System.out.println("  1 - Create table");
        System.out.println("  2 \"Full Name\" yyyy-MM-dd gender - Add employee");
        System.out.println("  3 - List unique employees (distinct by full name and birth date, sorted by full name)");
        System.out.println("  4 - Generate 1,000,000 random employees + 100 male with last name starting with 'F'");
        System.out.println("  5 - Measure query performance without optimization");
        System.out.println("  6 - Measure query performance with optimization");
    }
}