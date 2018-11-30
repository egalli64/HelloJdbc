/**
 * http://thisthread.blogspot.com/2017/08/connecting-to-oracle-via-jdbc.html
 */
package dd;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Connector {
    /**
     * Get a connection to the underlying database
     * 
     * @return a connection
     * @throws SQLException
     */
    public abstract Connection getConnection() throws SQLException;

    /**
     * Underlying database version
     * 
     * @param conn
     * @return database version
     * @throws SQLException
     */
    public String getDatabaseVersion(Connection conn) throws SQLException {
        return conn.getMetaData().getDatabaseProductVersion();
    }
}
