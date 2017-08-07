/**
 * http://thisthread.blogspot.com/2017/08/connecting-to-oracle-via-jdbc.html
 */
package dd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PlainConnector extends Connector {
    private String url;
    private String user;
    private String password;

    /**
     * 
     * @param url
     * @param user
     * @param password
     */
    public PlainConnector(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
