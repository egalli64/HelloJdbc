/**
 * http://thisthread.blogspot.com/2017/08/connecting-to-oracle-via-jdbc.html
 */
package dd;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;

public class PlainConnector4OracleTest {
    // private static final String KLASS = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/xe";
    private static final String USER = "hr";
    private static final String PASSWORD = "hr";

    private static PlainConnector connector;
    private static Connection connection;

    @BeforeClass
    public static void setUp() {
        connector = new PlainConnector(URL, USER, PASSWORD);

        try {
            connection = connector.getConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBadUser() {
        PlainConnector connector = null;
        connector = new PlainConnector(URL, "Unknown", PASSWORD);

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
//            String expected = "Oracle Database 12c Enterprise Edition Release 12.2.0.1.0 - 64bit Production";
            String expected = "Oracle Database 11g Express Edition Release 11.2.0.2.0 - 64bit Production";
            String actual = connector.getDatabaseVersion(connection);
            assertEquals(expected, actual);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }
}
