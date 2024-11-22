package com.est.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

    public static class MariaDbConnectionConstant {
        public static final String URL = "jdbc:mariadb://localhost:3306/est_jdbc";
        public static final String USER = "";
        public static final String PASSWORD = "";
    }

    public static class H2ConnectionConstant {
        public static final String URL = "jdbc:h2:./est";
        public static final String USER = "sa";
        public static final String PASSWORD = "";
    }


    public static Connection getConnection() {

        try {
            Connection connection = DriverManager.getConnection(
                        MariaDbConnectionConstant.URL,
                        MariaDbConnectionConstant.USER,
                        MariaDbConnectionConstant.PASSWORD
                );

//            Connection connection = DriverManager.getConnection(
//                    H2ConnectionConstant.URL,
//                    H2ConnectionConstant.USER,
//                    H2ConnectionConstant.PASSWORD
//            );

            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch ( Exception e ) {
            throw new RuntimeException(e);
        }

    }

}
