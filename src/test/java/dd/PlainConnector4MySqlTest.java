/**
 * http://thisthread.blogspot.com/2017/08/connecting-to-oracle-via-jdbc.html
 */
package dd;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;

public class PlainConnector4MySqlTest {
    // private static final String KLASS = "com.mysql.jdbc.Driver";
    /**
     * When required, explicitly use no SSL connection (useSSL=false) to avoid
     * warning:
     * 
     * WARN: Establishing SSL connection without server's identity verification is
     * not recommended. According to MySQL 5.5.45+, 5.6.26+ and 5.7.6+ requirements
     * SSL connection must be established by default if explicit option isn't set.
     * For compliance with existing applications not using SSL the
     * verifyServerCertificate property is set to 'false'. You need either to
     * explicitly disable SSL by setting useSSL=false, or set useSSL=true and
     * provide truststore for server certificate verification.
     */
    private static final String URL = "jdbc:mysql://localhost:3306/sakila?serverTimezone=Europe/Rome";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

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
            String expectedState = "28000";
            assertEquals(expectedState, e.getSQLState());

            int expectedCode = 1045;
            assertEquals(expectedCode, e.getErrorCode());
        }
    }

    @Test
    public void testGetDatabaseNameVersion() {
        try {
//            String expected = "5.7.19-log";
            String expected = "8.0.17";
            String actual = connector.getDatabaseVersion(connection);
            assertEquals(expected, actual);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }
}
