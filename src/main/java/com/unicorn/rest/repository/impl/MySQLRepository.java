package com.unicorn.rest.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLRepository {
    public static void main () {

        Connection connection = null;
        Statement insertStmt = null;
        Statement selectStmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://mydbinstance-1.culifdindshc.us-west-2.rds.amazonaws.com:3306/JDBCDemo", "awsrootuser", "awsrootpassword");
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}