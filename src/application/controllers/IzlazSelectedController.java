package application.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.componentHandler.NotificationHandler;
import application.componentHandler.PopUpHandler;
import application.service.DatabaseUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class IzlazSelectedController {

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
	private StackPane popUpPane;
	
	@FXML
    private CheckBox checkbox_fix;

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
		
		String status;
		
		if (checkbox_fix.isSelected()) {
			status = "FIX SOLD";
		} else {
			status = "SOLD";
		}

		try (Connection connection = DatabaseUtil.getConnection()) {
			String selectSql = "SELECT id, silver, gold, price FROM Product WHERE product_collection = ? AND product_type = ?";
			try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
				selectStatement.setString(1, model);
				selectStatement.setString(2, tip.toUpperCase());

				try (ResultSet resultSet = selectStatement.executeQuery()) {
					if (resultSet.next()) {
						int product_id = resultSet.getInt("id");
						double silver = resultSet.getDouble("silver");
						double gold = resultSet.getDouble("gold");
						double price = resultSet.getDouble("price");

						String checkSql = "SELECT SUM(CASE " 
								+ "WHEN status = 'ADDED' OR status = 'FIX ADD' OR status = 'REFUNDED' THEN quantity "
								+ "WHEN status = 'SOLD' OR status = 'FIX SOLD' THEN -1 * quantity " 
								+ "ELSE 0 " + "END) AS current "
								+ "FROM Transaction " + "WHERE product_id = ?";

						try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
							checkStatement.setInt(1, product_id);
							try (ResultSet checkResultSet = checkStatement.executeQuery()) {
								if (checkResultSet.next()) {
									int currentQuantity = checkResultSet.getInt("current"); 
									
									String title, body;
									
									if(currentQuantity >= kolicina) {
										title = "Potvrdi prodaju\nDa li ste sigurni da zelite prodati proizvod?";
										body = model + "     " + tip + "     " + gold + "     " + silver + "     " + price;
									} else {
										title = "Stanje ovog proizvoda ce uci u minus!\nDa li ste sigurni da zelite prodati proizvod?";
										body = model + "     " + tip + "     " + gold + "     " + silver + "     " + price;
									}
									PopUpHandler.showPopup(popUpPane, title, body, (isConfirmed) -> {
												if (isConfirmed) {
													try {
														Connection insertConnection = DatabaseUtil.getConnection();
														String insertSql = "INSERT INTO Transaction (product_id, quantity, status) VALUES (?, ?, ?)";
														try (PreparedStatement insertStatement = insertConnection
																.prepareStatement(insertSql)) {
															insertStatement.setInt(1, product_id);
															insertStatement.setInt(2, kolicina);
															insertStatement.setString(3, status);
															insertStatement.executeUpdate();

															NotificationHandler.showNotification(notificationPane,
																	"Success", "Data saved to the database!");
														}
													} catch (SQLException e) {
														e.printStackTrace();
														NotificationHandler.showNotification(notificationPane,
																"Error",
																"Failed to create transaction: " + e.getMessage());
													}
												}
											});
									
								} else {
									NotificationHandler.showNotification(notificationPane, "Error",
											"Failed to check current quantity!");
								}
							}
						} catch (SQLException e) {
							e.printStackTrace();
							NotificationHandler.showNotification(notificationPane, "Error",
									"Database error while checking current quantity: " + e.getMessage());
						}
					} else {
						NotificationHandler.showNotification(notificationPane, "Error",
								"Product not found in inventory!");
					}
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
