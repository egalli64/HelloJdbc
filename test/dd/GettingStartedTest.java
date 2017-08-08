package dd;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class GettingStartedTest {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/orclpdb";
    private static final String USER = "hr";
    private static final String PASSWORD = "hr";

    private static OracleDataSourceConnector ods;

    @BeforeClass
    public static void setUp() {
        try {
            ods = new OracleDataSourceConnector(URL, USER, PASSWORD);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSelect() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ods.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT first_name FROM employees");
            
            List<String> results = new ArrayList<String>();
            while (rs.next()) {
                results.add(rs.getString(1));
            }
            
            assertEquals(107, results.size());
            Collections.sort(results);
            assertEquals("Adam", results.get(0));
            assertEquals("Winston", results.get(106));
        } catch (SQLException e) {
            fail(e.getMessage());
        } finally {
            try {
                assertNotNull(rs);
                rs.close();

                assertNotNull(stmt);
                stmt.close();
                
                assertNotNull(conn);
                conn.close();
            } catch (SQLException e) {
                fail(e.getMessage());
            }
        }
    }
}
