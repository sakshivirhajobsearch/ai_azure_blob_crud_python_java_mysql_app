package com.ai.azure.blob.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.ai.azure.blob.service.BlobStorageService;

public class UnifiedGUI {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(UnifiedGUI::createAndShowGUI);
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("☁ Azure Blob Storage GUI");
		frame.setSize(320, 400);
		frame.setLayout(null);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JButton upload = new JButton("⬆ Upload Blob");
		JButton list = new JButton("📄 List Blobs");
		JButton delete = new JButton("🗑 Delete Blob");
		JButton download = new JButton("⬇ Download Blob");
		JButton refreshAI = new JButton("🧠 Refresh AI Metadata");

		upload.setBounds(50, 50, 200, 30);
		list.setBounds(50, 100, 200, 30);
		delete.setBounds(50, 150, 200, 30);
		download.setBounds(50, 200, 200, 30);
		refreshAI.setBounds(50, 250, 200, 30);

		frame.add(upload);
		frame.add(list);
		frame.add(delete);
		frame.add(download);
		frame.add(refreshAI);

		BlobStorageService blobService;
		try {
			blobService = new BlobStorageService();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "❌ Failed to connect to Azure Blob:\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}

		upload.addActionListener(e -> new Thread(() -> blobService.uploadBlob()).start());
		list.addActionListener(e -> new Thread(() -> blobService.listBlobs()).start());
		delete.addActionListener(e -> new Thread(() -> blobService.deleteBlob()).start());
		download.addActionListener(e -> new Thread(() -> blobService.downloadBlob()).start());
		refreshAI.addActionListener(e -> new Thread(() -> blobService.runPythonScript()).start());

		frame.setVisible(true);
	}
}
