import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.time.LocalDate;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private Properties properties;

    private DatabaseManager() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) throw new RuntimeException("config.properties not found");
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS employees (" +
                "id SERIAL PRIMARY KEY, " +
                "full_name VARCHAR(255) NOT NULL, " +
                "birth_date DATE NOT NULL, " +
                "gender VARCHAR(10) NOT NULL)";
        executeUpdate(sql);
        System.out.println("Table 'employees' created successfully");
    }

    public void createIndex() throws SQLException {
        dropIndex();
        String sql = "CREATE INDEX idx_gender_surname ON employees " +
                "(gender, (split_part(full_name, ' ', 1)))";
        executeUpdate(sql);
        System.out.println("Optimization index created");
    }

    public void dropIndex() throws SQLException {
        String sql = "DROP INDEX IF EXISTS idx_gender_surname";
        executeUpdate(sql);
    }

    public void generateEmployees(int count, int specialCount) throws SQLException {
        List<Employee> batch = new ArrayList<>();
        int batchSize = 10000;
        System.out.println("Generating " + count + " random employees...");

        for (int i = 1; i <= count; i++) {
            batch.add(Employee.generateRandom(false));
            if (i % batchSize == 0) {
                Employee.saveBatch(batch);
                batch.clear();
                System.out.printf("Processed: %d/%d (%.1f%%)%n",
                        i, count, (i * 100.0 / count));
            }
        }

        System.out.println("Generating " + specialCount + " special employees...");
        for (int i = 0; i < specialCount; i++) {
            batch.add(Employee.generateRandom(true));
        }

        if (!batch.isEmpty()) {
            Employee.saveBatch(batch);
        }
        System.out.println("Data generation completed");
    }

    public void printUniqueEmployees() throws SQLException {
        String sql = "SELECT DISTINCT full_name, birth_date, gender " +
                "FROM employees ORDER BY full_name";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("\n%-40s %-12s %-6s %s%n",
                    "Full Name", "Birth Date", "Gender", "Age");
            System.out.println("---------------------------------------------------------------");

            while (rs.next()) {
                String fullName = rs.getString("full_name");
                LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
                String gender = rs.getString("gender");
                Employee emp = new Employee(fullName, birthDate, gender);

                System.out.printf("%-40s %-12s %-6s %d%n",
                        fullName, birthDate, gender, emp.calculateAge());
            }
            System.out.println();
        }
    }

    private void executeUpdate(String sql) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}