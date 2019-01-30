package dd;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Main {
	static private final String URL = "jdbc:oracle:thin:@localhost:1521/orclpdb";
	static private final String USER = "me";
	static private final String PASSWORD = "password";

	public static void main(String[] args) {
		plainDataSource();

		Connector mds = null;
		try {
			mds = new OracleDataSourceConnector(URL, USER, PASSWORD);
			selectAll(mds);
			callMe(mds);
			rollingBack(mds);
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	private static void selectAll(Connector mds) throws SQLException {
		try (Connection conn = mds.getConnection(); //
				Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT first_name FROM coders ORDER BY 1");

			List<String> results = new ArrayList<String>();
			while (rs.next()) {
				results.add(rs.getString(1));
			}

			System.out.println("Select all coders: " + results);
		}
	}

	/**
	 * This method assumes a procedure on the Oracle "ME" schema:
	 * 
	 * <pre>
	 * create or replace PROCEDURE get_coder_name(
	 *     p_coder_id IN coders.coder_id%type,
	 *     p_first_name OUT coders.first_name%TYPE,
	 *     p_last_name OUT coders.last_name%TYPE
	 * )
	 * IS BEGIN
	 *     SELECT first_name, last_name
	 *     INTO p_first_name, p_last_name
	 *     FROM coders WHERE coder_id = p_coder_id;
	 * END get_coder_name;
	 * </pre>
	 */
	private static void callMe(Connector mds) throws SQLException {
		try (Connection conn = mds.getConnection(); //
				CallableStatement stmt = conn.prepareCall("{call get_coder_name(?, ?, ?)}")) {
			int id = 103;
			stmt.setInt(1, id);
			stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			stmt.execute();

			String first = stmt.getString(2);
			String last = stmt.getString(3);
			System.out.println(String.format("Coder %d is: %s %s", id, first, last));
		}
	}

	private static void rollingBack(Connector mds) {
		try (Connection conn = mds.getConnection()) {
			System.out.print("By default, autocommit is " + conn.getAutoCommit());
			conn.setAutoCommit(false);
			System.out.println(". Here is set it to " + conn.getAutoCommit() + ".");

			try (Statement stmt = conn.createStatement()) {
				stmt.executeUpdate("INSERT INTO coders VALUES(301, 'John', 'Coltrane', SYSDATE, 6000)");

				selectAllAndPrint(stmt);
				conn.rollback();
				selectAllAndPrint(stmt);
			} catch (SQLException se) {
				conn.rollback();
				se.printStackTrace();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public static void selectAllAndPrint(Statement stmt) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT coder_id, first_name, last_name FROM coders");

		System.out.print("[");
		while (rs.next()) {
			System.out.print(String.format("(%d: %s %s)", //
					rs.getInt("coder_id"), //
					rs.getString("first_name"), //
					rs.getString("last_name")));
		}
		System.out.println("]");
	}

	private static void plainDataSource() {
		try (Connection conn = new PlainConnector(URL, USER, PASSWORD).getConnection(); //
				Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT last_name FROM coders ORDER BY 1");

			List<String> results = new ArrayList<String>();
			while (rs.next()) {
				results.add(rs.getString(1));
			}

			System.out.println("Select from plain DS: " + results);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
