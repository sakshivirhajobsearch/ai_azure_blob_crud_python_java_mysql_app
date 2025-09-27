package com.ai.azure.blob.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;

import io.github.cdimascio.dotenv.Dotenv;

public class BlobStorageService {

	private final BlobContainerClient containerClient;

	public BlobStorageService() {
		// Load from .env or environment
		Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMissing().load();

		String sasUrl = System.getenv("AZURE_BLOB_SAS_URL");
		if (sasUrl == null)
			sasUrl = dotenv.get("AZURE_BLOB_SAS_URL");

		if (sasUrl == null || !sasUrl.contains("https://")) {
			throw new IllegalArgumentException("❌ Missing or invalid AZURE_BLOB_SAS_URL.");
		}

		// Create container client using SAS URL
		containerClient = new BlobContainerClientBuilder().endpoint(sasUrl).buildClient();

		testConnection();
	}

	public void testConnection() {
		try {
			System.out.println("🔍 Testing Azure Blob Storage connection using SAS...");
			for (BlobItem blob : containerClient.listBlobs()) {
				System.out.println("📦 Blob: " + blob.getName());
			}
			System.out.println("✅ Azure SAS connection test complete.");
		} catch (Exception e) {
			System.err.println("❌ Connection test failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void uploadBlob() {
		try {
			File file = new File("test_upload.txt");
			try (PrintWriter writer = new PrintWriter(file)) {
				writer.println("Hello from Java with SAS!");
			}

			BlobClient blobClient = containerClient.getBlobClient("test_upload.txt");
			blobClient.uploadFromFile(file.getAbsolutePath(), true);
			System.out.println("✅ Upload successful.");
		} catch (Exception e) {
			System.err.println("❌ Upload failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void listBlobs() {
		try {
			System.out.println("📄 Listing blobs:");
			for (BlobItem blob : containerClient.listBlobs()) {
				System.out.println("📦 " + blob.getName());
			}
		} catch (Exception e) {
			System.err.println("❌ Listing failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void deleteBlob() {
		try {
			String blobName = "test_upload.txt";
			BlobClient blobClient = containerClient.getBlobClient(blobName);
			blobClient.delete();
			System.out.println("🗑 Deleted: " + blobName);
		} catch (Exception e) {
			System.err.println("❌ Delete failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void downloadBlob() {
		try {
			String blobName = "test_upload.txt";
			BlobClient blobClient = containerClient.getBlobClient(blobName);
			blobClient.downloadToFile("downloaded_" + blobName, true);
			System.out.println("⬇ Downloaded: downloaded_" + blobName);
		} catch (Exception e) {
			System.err.println("❌ Download failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void runPythonScript() {
		try {
			Process process = Runtime.getRuntime().exec("python backend-python/azure_blob_ai_connector.py");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			System.out.println("🧠 AI Script Output:");
			while ((line = reader.readLine()) != null) {
				System.out.println("📘 " + line);
			}
			process.waitFor();
		} catch (Exception e) {
			System.err.println("❌ Failed to run AI script: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
