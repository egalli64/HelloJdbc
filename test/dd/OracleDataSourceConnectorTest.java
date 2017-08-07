/**
 * http://thisthread.blogspot.com/2017/08/connecting-to-oracle-via-jdbc.html
 */
package dd;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;

public class OracleDataSourceConnectorTest {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/orclpdb";
    private static final String USER = "hr";
    private static final String PASSWORD = "hr";

    private static OracleDataSourceConnector ods;
    private static Connection conn;

    @BeforeClass
    public static void setUp() {
        try {
            ods = new OracleDataSourceConnector(URL, USER, PASSWORD);
            conn = ods.getConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBadUser() {
        OracleDataSourceConnector connector = null;
        try {
            connector = new OracleDataSourceConnector(URL, "Unknown", PASSWORD);
        } catch (SQLException e) {
            fail(e.getMessage());
        }

        try {
            connector.getConnection();
            fail("No connection expected for unknown user");
        } catch (SQLException e) {
            String expectedState = "72000"; // SQL execute phase errors
            assertEquals(expectedState, e.getSQLState());

            int expectedCode = 1017; // ORA-01017
            assertEquals(expectedCode, e.getErrorCode());
        }
    }

    @Test
    public void testGetDatabaseNameVersion() {
        try {
            String expected = "Oracle Database 12c Enterprise Edition Release 12.2.0.1.0 - 64bit Production";
            String actual = ods.getDatabaseVersion(conn);
            assertEquals(expected, actual);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }
}
