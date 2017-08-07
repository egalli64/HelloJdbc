/**
 * http://thisthread.blogspot.com/2017/08/connecting-to-oracle-via-jdbc.html
 */
package dd;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

public class OracleDataSourceConnector extends Connector {
    private OracleDataSource ods;

    /**
     * Create a OracleDataSourceConnector
     * 
     * @param url
     * @param user
     * @param password
     * @throws SQLException
     *             if the underlying OracleDataSource can't be created.
     */
    public OracleDataSourceConnector(String url, String user, String password) throws SQLException {
        ods = new OracleDataSource();
        ods.setURL(url);
        ods.setUser(user);
        ods.setPassword(password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ods.getConnection();
    }
}
