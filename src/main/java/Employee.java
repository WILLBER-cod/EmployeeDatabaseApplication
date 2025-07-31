import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Employee {
    private static final Random random = new Random();
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String[] GENDERS = {"Male", "Female"};

    private String fullName;
    private LocalDate birthDate;
    private String gender;

    public Employee(String fullName, LocalDate birthDate, String gender) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public static Employee generateRandom(boolean special) {
        String surname = special ? "F" + randomString(5, 10) : randomString(6, 12);
        String name = randomString(5, 10);
        String patronymic = randomString(6, 12);

        String fullName = capitalize(surname) + " " + capitalize(name) + " " + capitalize(patronymic);

        LocalDate birthDate = LocalDate.now()
                .minusYears(20 + random.nextInt(40))
                .minusDays(random.nextInt(365));

        String gender = special ? "Male" : GENDERS[random.nextInt(GENDERS.length)];

        return new Employee(fullName, birthDate, gender);
    }

    public void save() throws SQLException {
        String sql = "INSERT INTO employees (full_name, birth_date, gender) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setDate(2, Date.valueOf(birthDate));
            stmt.setString(3, gender);
            stmt.executeUpdate();
        }
    }

    public static void saveBatch(List<Employee> employees) throws SQLException {
        String sql = "INSERT INTO employees (full_name, birth_date, gender) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int counter = 0;
            for (Employee emp : employees) {
                stmt.setString(1, emp.fullName);
                stmt.setDate(2, Date.valueOf(emp.birthDate));
                stmt.setString(3, emp.gender);
                stmt.addBatch();

                if (++counter % 1000 == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
        }
    }

    public int calculateAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private static String randomString(int minLen, int maxLen) {
        int len = minLen + random.nextInt(maxLen - minLen + 1);
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %d years",
                fullName, birthDate, gender, calculateAge());
    }
}