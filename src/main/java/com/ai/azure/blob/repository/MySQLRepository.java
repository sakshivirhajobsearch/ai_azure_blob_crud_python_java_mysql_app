package com.ai.azure.blob.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ai.azure.blob.db.connection.DatabaseConnector;

public class MySQLRepository {

	// Insert a new blob record
	public void insertBlob(String blobName) {
		String sql = "INSERT INTO blobs (name) VALUES (?)";

		try (Connection conn = new DatabaseConnector().connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, blobName);
			stmt.executeUpdate();
			System.out.println("✅ Blob inserted into MySQL: " + blobName);

		} catch (SQLException e) {
			System.err.println("❌ Failed to insert blob into MySQL: " + e.getMessage());
		}
	}

	// Fetch all blob names
	public List<String> getAllBlobs() {
		List<String> blobs = new ArrayList<>();
		String sql = "SELECT name FROM blobs";

		try (Connection conn = new DatabaseConnector().connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				blobs.add(rs.getString("name"));
			}

		} catch (SQLException e) {
			System.err.println("❌ Failed to fetch blobs from MySQL: " + e.getMessage());
		}

		return blobs;
	}

	// Delete a blob record by name
	public void deleteBlob(String blobName) {
		String sql = "DELETE FROM blobs WHERE name = ?";

		try (Connection conn = new DatabaseConnector().connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, blobName);
			stmt.executeUpdate();
			System.out.println("✅ Blob deleted from MySQL: " + blobName);

		} catch (SQLException e) {
			System.err.println("❌ Failed to delete blob from MySQL: " + e.getMessage());
		}
	}
}
