package application.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.service.DatabaseUtil;
import application.service.NotificationHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class UlazSelectedController {

	@FXML
	private TextField modelTextField;

	@FXML
	private TextField tipTextField;

	@FXML
	private TextField kolicinaTextField;

	@FXML
	private Pane top_pane;

	@FXML
	private StackPane notificationPane;

	@FXML
	private void handleOkButton() {
		String model = modelTextField.getText();
		String tip = tipTextField.getText();
		String kolicinaText = kolicinaTextField.getText();

		if (model.isEmpty() || tip.isEmpty() || kolicinaText.isEmpty()) {
			NotificationHandler.showNotification(notificationPane, "Error", "Please fill in all fields.");
			return;
		}

		int kolicina;
		try {
			kolicina = Integer.parseInt(kolicinaText);
		} catch (NumberFormatException e) {
			NotificationHandler.showNotification(notificationPane, "Error",
					"Invalid quantity input. Please enter a valid number.");
			return;
		}

		if (kolicina <= 0) {
			NotificationHandler.showNotification(notificationPane, "Error",
					"Quantity cannot be zero or less. Please enter a valid quantity.");
			return;
		}

		try (Connection connection = DatabaseUtil.getConnection()) {
			String selectSql = "SELECT id FROM Product WHERE product_collection = ? AND product_type = ?";
			try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
				selectStatement.setString(1, model);
				selectStatement.setString(2, tip.toUpperCase());

				try (ResultSet resultSet = selectStatement.executeQuery()) {
					if (resultSet.next()) {
						int product_id = resultSet.getInt("id");

						String insertSql = "INSERT INTO Transaction (product_id, quantity, status) VALUES (?, ?, ?)";
						try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
							insertStatement.setInt(1, product_id);
							insertStatement.setInt(2, kolicina);
							insertStatement.setString(3, "ADDED");
							insertStatement.executeUpdate();

						} catch (SQLException e) {
							e.printStackTrace();
							NotificationHandler.showNotification(notificationPane, "Error",
									"Failed to create transaction: " + e.getMessage());
						}
					} else {
						NotificationHandler.showNotification(notificationPane, "Error",
								"Product not found in inventory!");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					NotificationHandler.showNotification(notificationPane, "Error",
							"Database error while checking product: " + e.getMessage());
				}
			} catch (SQLException e) {
				e.printStackTrace();
				NotificationHandler.showNotification(notificationPane, "Error",
						"Database error while querying product: " + e.getMessage());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			NotificationHandler.showNotification(notificationPane, "Error",
					"Database connection error: " + e.getMessage());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			NotificationHandler.showNotification(notificationPane, "Error",
					"Invalid quantity input. Please enter a valid number.");
		}
	}

	@FXML
	private void switchToMainView() throws IOException {
		Node source = (Node) top_pane;
		Stage stage = (Stage) source.getScene().getWindow();
		stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/resources/view/FXMLHomePage.fxml"))));
		stage.show();
	}
}
