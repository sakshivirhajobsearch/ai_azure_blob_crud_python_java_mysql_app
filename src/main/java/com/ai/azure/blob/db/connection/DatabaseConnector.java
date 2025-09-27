package com.ai.azure.blob.db.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnector {

	private final String url;
	private final String user;
	private final String password;

	public DatabaseConnector() {
		// Load from .env file in project root
		Dotenv dotenv = Dotenv.configure().directory(System.getProperty("user.dir")).filename(".env").load();

		this.url = dotenv.get("DB_URL");
		this.user = dotenv.get("DB_USER");
		this.password = dotenv.get("DB_PASSWORD");

		if (url == null || user == null || password == null) {
			throw new RuntimeException(
					"❌ Missing DB connection info in .env file. Check DB_URL, DB_USER, DB_PASSWORD.");
		}
	}

	public Connection connect() {
		try {
			Connection conn = DriverManager.getConnection(url, user, password);
			System.out.println("✅ Connected to MySQL successfully!");
			return conn;
		} catch (SQLException e) {
			System.err.println("❌ MySQL Connection Error: " + e.getMessage());
			return null;
		}
	}
}
