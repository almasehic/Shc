package application.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.componentHandler.NotificationHandler;
import application.componentHandler.PopUpHandler;
import application.service.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DodajProizvodController {

	private Stage stage;
	private Scene scene;

	@FXML
	private TextField textModel;

	@FXML
	private TextField textTip;

	@FXML
	private TextField textAu;

	@FXML
	private TextField textAg;

	@FXML
	private TextField textPrice;

	@FXML
	private StackPane notificationPane;

	@FXML
	private StackPane popUpPane;

	private void switchToView(String viewPath, ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(viewPath));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public void switchToProizvodi(ActionEvent event) throws IOException {
		switchToView("/resources/view/DisplayProducts.fxml", event);
	}

	public void switchToMain(ActionEvent event) throws IOException {
		switchToView("/resources/view/FXMLHomePage.fxml", event);
	}

	public void addNewProduct(ActionEvent event) {
		String model = textModel.getText();
		String tip = textTip.getText();
		String gramiAu = textAu.getText();
		String gramiAg = textAg.getText();
		String price = textPrice.getText();

		if (model.isEmpty() || tip.isEmpty() || gramiAu.isEmpty() || gramiAg.isEmpty() || price.isEmpty()) {
			NotificationHandler.showNotification(notificationPane, "Error", "Sva polja moraju biti popunjena!");
			return;
		}
		int Au, Ag;
		double Pr;
		try {
			Au = Integer.parseInt(gramiAu);
			Ag = Integer.parseInt(gramiAg);
			Pr = Double.parseDouble(price);
		} catch (NumberFormatException e) {
			NotificationHandler.showNotification(notificationPane, "Error",
					"Nevažeći unos! Au grami, Ag grami i Cijena moraju biti cijeli brojevi.");
			return;
		}

		try (Connection connection = DatabaseUtil.getConnection()) {
			String selectIDSql = "SELECT id FROM Product WHERE product_collection = ? AND product_type = ?";
			try (PreparedStatement selectStatement = connection.prepareStatement(selectIDSql)) {
				selectStatement.setString(1, model);
				selectStatement.setString(2, tip.toUpperCase());

				try (ResultSet resultSet = selectStatement.executeQuery()) {
					if (!resultSet.next()) { // if no matches are found, add new row

						PopUpHandler.showPopup(popUpPane, "Da li je ovo novi proizvod koji želite dodati:",
								model + "     " + tip + "     " + gramiAu + "     " + gramiAg + "     " + price,
								(isConfirmed) -> {
									if (isConfirmed) {
										try (Connection connectionInner = DatabaseUtil.getConnection()) {
											String insertSql = "INSERT INTO Product (product_collection, product_type, gold, silver, price, is_deleted) VALUES (?, ?, ?, ?, ?, ?)";

											try (PreparedStatement insertStatement = connectionInner
													.prepareStatement(insertSql)) {
												insertStatement.setString(1, model);
												insertStatement.setString(2, tip.toUpperCase());
												insertStatement.setInt(3, Au);
												insertStatement.setInt(4, Ag);
												insertStatement.setDouble(5, Pr);
												insertStatement.setInt(6, 0); // Default value for is_deleted

												insertStatement.executeUpdate();

												NotificationHandler.showNotification(notificationPane, "Success",
														"Novi proizvod je uspješno dodan!");

												// Switch to the main view
												try {
													switchToMain(event);
												} catch (IOException e) {
													e.printStackTrace();
												}
											} catch (SQLException e) {
												e.printStackTrace();
												NotificationHandler.showNotification(notificationPane, "Error",
														"Failed to create product: " + e.getMessage());
											}
										} catch (SQLException e) {
											e.printStackTrace();
											NotificationHandler.showNotification(notificationPane, "Error",
													"Database connection error: " + e.getMessage());
										}
									}
								});
					} else {
						NotificationHandler.showNotification(notificationPane, "Error", "Ovaj proizvod već postoji!");
					}
				}
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
}
