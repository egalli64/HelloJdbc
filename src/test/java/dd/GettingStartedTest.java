/**
 * http://thisthread.blogspot.com/2017/08/selecting-on-oracle-via-jdbc.html
 * http://thisthread.blogspot.com/2017/08/calling-stored-function-from-jdbc.html
 */
package dd;

import static org.junit.Assert.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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

	@Test
	public void select() {
		try (Connection conn = ods.getConnection(); //
				Statement stmt = conn.createStatement(); //
				ResultSet rs = stmt.executeQuery("SELECT first_name FROM employees ORDER BY 1"); //
		) {
			List<String> results = new ArrayList<String>();
			while (rs.next()) {
				results.add(rs.getString(1));
			}
			
			assertEquals(107, results.size());
			assertEquals("Adam", results.get(0));
			assertEquals("Winston", results.get(106));
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * The foo function should be created in the HR schema:
	 * 
	 * <pre>
	 * create or replace FUNCTION foo (val CHAR)
	 * RETURN CHAR AS
	 * BEGIN
	 *     RETURN val || 'suffix';
	 * END;
	 * </pre>
	 * 
	 * @param call to the foo function either in JDBC or PL/SQL format
	 */
	private void callFoo(String call) {
		try (Connection conn = ods.getConnection(); //
				CallableStatement cs = conn.prepareCall(call);) {
			cs.registerOutParameter(1, Types.CHAR);
			cs.setString(2, "aa");
			cs.execute();
			String result = cs.getString(1);
			assertEquals("aasuffix", result);
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFunctionCallJdbcEscape() {
		// JDBC escape syntax
		callFoo("{? = call foo(?)");
	}

	@Test
	public void testFunctionCallPlSqlBlock() {
		// PL/SQL block syntax
		callFoo("begin ? := foo(?); end;");
	}
}
