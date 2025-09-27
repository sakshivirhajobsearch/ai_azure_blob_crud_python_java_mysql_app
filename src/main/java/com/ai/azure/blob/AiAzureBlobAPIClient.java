package com.ai.azure.blob;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public class AiAzureBlobAPIClient {

	private final String JDBC_URL;
	private final String JDBC_USER;
	private final String JDBC_PASSWORD;

	public AiAzureBlobAPIClient() {
		// Load DB credentials from .env
		Dotenv dotenv = Dotenv.configure().directory(System.getProperty("user.dir")).filename(".env").load();

		this.JDBC_URL = dotenv.get("DB_URL");
		this.JDBC_USER = dotenv.get("DB_USER");
		this.JDBC_PASSWORD = dotenv.get("DB_PASSWORD");

		if (JDBC_URL == null || JDBC_USER == null || JDBC_PASSWORD == null) {
			throw new RuntimeException("❌ Missing DB_URL, DB_USER, or DB_PASSWORD in .env");
		}
	}

	// Executes the Python AI script
	public void runAIScript() {
		try {
			String pythonScriptPath = "backend-python/azure_blob_ai_connector.py";
			ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath);
			pb.directory(new File(System.getProperty("user.dir")));
			Process process = pb.start();

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String s;
			System.out.println("📜 AI Script Output:");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			System.out.println("⚠️ AI Script Errors (if any):");
			while ((s = stdError.readLine()) != null) {
				System.err.println(s);
			}

			int exitCode = process.waitFor();
			if (exitCode == 0) {
				System.out.println("✅ AI metadata extraction completed.");
			} else {
				System.err.println("❌ AI script exited with code: " + exitCode);
			}
		} catch (Exception e) {
			System.err.println("❌ Error running AI script: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Fetches the updated blob metadata after AI analysis
	public List<String> getAllBlobsWithMetadata() {
		List<String> results = new ArrayList<>();

		String query = "SELECT name, ai_metadata FROM blobs";

		try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String blobName = rs.getString("name");
				String metadata = rs.getString("ai_metadata");
				results.add("Blob: " + blobName + " | AI Metadata: " + metadata);
			}

		} catch (SQLException e) {
			System.err.println("❌ Error fetching blob metadata: " + e.getMessage());
		}

		return results;
	}
}
