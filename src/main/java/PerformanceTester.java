import java.sql.*;
import java.util.concurrent.TimeUnit;

public class PerformanceTester {
    private static final String QUERY = "SELECT * FROM employees " +
            "WHERE gender = 'Male' AND split_part(full_name, ' ', 1) LIKE 'F%'";

    public static void measureQueryPerformance(boolean optimized) {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            long startTime = System.nanoTime();
            try (ResultSet rs = stmt.executeQuery(QUERY)) {
                int count = 0;
                while (rs.next()) count++;

                long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

                if (optimized) {
                    System.out.println("Optimized query time: " + duration + " ms | Records: " + count);
                } else {
                    System.out.println("Original query time: " + duration + " ms | Records: " + count);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query failed", e);
        }
    }
}