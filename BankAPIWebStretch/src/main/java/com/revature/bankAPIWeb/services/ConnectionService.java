package com.revature.bankAPIWeb.services;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionService {
	private static Connection connection;

	public static Connection getConnection() {

		if (connection == null) {
			try {
				//The current path for properties was "C:\Users\pswov\scoop\apps\sts\current\connection.properties"
				Class.forName("org.postgresql.Driver");
				String relPath = "connection.properties";
				FileInputStream fis = new FileInputStream(relPath);
				Properties prop = new Properties();
				prop.load(fis);

				connection = DriverManager.getConnection(prop.getProperty("url"), prop.getProperty("username"),
						prop.getProperty("password"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return connection;
	}

	public static void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
