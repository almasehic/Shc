package application.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.componentHandler.NotificationHandler;
import application.componentHandler.PopUpHandler;
import application.models.Product;
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
	private TextField productCollectionField;

	@FXML
	private TextField productTypeField;

	@FXML
	private TextField goldField;

	@FXML
	private TextField silverField;

	@FXML
	private TextField priceField;

	@FXML
	private StackPane notificationPane;

	@FXML
	private StackPane popUpPane;

	private int product_id;

	public void initData(Product product) {
		productCollectionField.setText(product.getProductCollection());
		productTypeField.setText(product.getProductType());
		goldField.setText(String.valueOf(product.getGold()));
		silverField.setText(String.valueOf(product.getSilver()));
		priceField.setText(String.valueOf(product.getPrice()));

		product_id = product.getId();
	}

	private void switchToView(String viewPath, ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(viewPath));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	private void switchToMainViewThenShowNotification(ActionEvent event, String notificationType,
			String notificationMessage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/view/FXMLHomePage.fxml"));
		Parent root = loader.load();
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

		if (loader.getController() instanceof MainController) {
			MainController destinationController = loader.getController();
			StackPane notificationPane = destinationController.getNotificationPane();
			NotificationHandler.showNotification(notificationPane, notificationType, notificationMessage);
		}
	}

	public void switchToProizvodi(ActionEvent event) throws IOException {
		switchToView("/resources/view/DisplayProducts.fxml", event);
	}

	public void switchToMain(ActionEvent event) throws IOException {
		switchToView("/resources/view/FXMLHomePage.fxml", event);
	}

	public void updateProduct(ActionEvent event) {
		String model = productCollectionField.getText();
		String tip = productTypeField.getText();
		String gramiAu = goldField.getText();
		String gramiAg = silverField.getText();
		String price = priceField.getText();

		if (model.isEmpty() || tip.isEmpty() || gramiAu.isEmpty() || gramiAg.isEmpty() || price.isEmpty()) {
			NotificationHandler.showNotification(notificationPane, "Error", "All fields must be filled in!");
			return;
		}

		double Pr, Au, Ag;
		try {
			Au = Double.parseDouble(gramiAu);
			Ag = Double.parseDouble(gramiAg);
			Pr = Double.parseDouble(price);
		} catch (NumberFormatException e) {
			NotificationHandler.showNotification(notificationPane, "Error",
					"Invalid input! Au grams, Ag grams, and Price must be integers.");
			return;
		}

		PopUpHandler.showPopup(popUpPane, "Da li je ovo novi proizvog koji zelite dodati:",
				model + "     " + tip + "     " + Au + "     " + Ag + "     " + Pr, (isConfirmed) -> {
					if (isConfirmed) {
						try (Connection connection = DatabaseUtil.getConnection()) {
							String updateSql = "UPDATE Product SET gold = ?, silver = ?, price = ?, product_collection = ?, product_type = ? WHERE id = ?";
							try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
								updateStatement.setDouble(1, Au);
								updateStatement.setDouble(2, Ag);
								updateStatement.setDouble(3, Pr);
								updateStatement.setString(4, model);
								updateStatement.setString(5, tip.toUpperCase());
								updateStatement.setInt(6, product_id);

								int rowsAffected = updateStatement.executeUpdate();
								if (rowsAffected > 0) {
									try {
										switchToMainViewThenShowNotification(event, "Success",
												"Product updated successfully!");
									} catch (IOException e) {
										e.printStackTrace();
									}
								} else {
									NotificationHandler.showNotification(notificationPane, "Error",
											"No product found for updating!");
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
				});

	}

	public void addNewProduct(ActionEvent event) {
		String model = productCollectionField.getText();
		String tip = productTypeField.getText();
		String gramiAu = goldField.getText();
		String gramiAg = silverField.getText();
		String price = priceField.getText();

		if (model.isEmpty() || tip.isEmpty() || gramiAu.isEmpty() || gramiAg.isEmpty() || price.isEmpty()) {
			NotificationHandler.showNotification(notificationPane, "Error", "Sva polja moraju biti popunjena!");
			return;
		}
		double Pr, Au, Ag;
		try {
			Au = Double.parseDouble(gramiAu);
			Ag = Double.parseDouble(gramiAg);
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
												insertStatement.setDouble(3, Au);
												insertStatement.setDouble(4, Ag);
												insertStatement.setDouble(5, Pr);
												insertStatement.setInt(6, 0); // Default value for is_deleted

												insertStatement.executeUpdate();

												NotificationHandler.showNotification(notificationPane, "Success",
														"Novi proizvod je uspješno dodan!");

												try {
													switchToMainViewThenShowNotification(event, "Success",
															"Product updated successfully!");
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
