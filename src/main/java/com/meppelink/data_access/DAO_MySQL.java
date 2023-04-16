package com.meppelink.data_access;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public interface DAO_MySQL<T> {
    default Connection getConnection() throws SQLException {
        Dotenv dotenv = Dotenv.load();
        String db_full_driver = dotenv.get("DB_FULL_DRIVER");
        String db_driver = dotenv.get("DB_DRIVER");
        String db_server_name = dotenv.get("DB_SERVER_NAME");
        String db_host = dotenv.get("DB_HOST");
        String db_port = dotenv.get("DB_PORT");
        String db_schema = dotenv.get("DB_SCHEMA");
        String db_properties = dotenv.get("DB_PROPERTIES");
        String db_user = dotenv.get("DB_USER");
        String db_password = dotenv.get("DB_PASSWORD");
        try {
            Class.forName(db_full_driver);
        } catch(ClassNotFoundException e) {
            // what to do if the driver is not found
        }
        String url = String.format("jdbc:%s://%s.%s:%s/%s?%s",
                db_driver, db_server_name, db_host, db_port, db_schema, db_properties);
        Connection myDbConn = DriverManager.getConnection(url, db_user, db_password);
        return myDbConn;
    }
}