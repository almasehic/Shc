package application.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.service.DatabaseUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

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
		int kolicina = Integer.parseInt(kolicinaTextField.getText());

		try (Connection connection = DatabaseUtil.getConnection()) {
			String selectSql = "SELECT id FROM Product WHERE product_collection = ? AND product_type = ?";
			try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
				selectStatement.setString(1, model);
				selectStatement.setString(2, tip);

				try (ResultSet resultSet = selectStatement.executeQuery()) {
					if (resultSet.next()) {
						int product_id = resultSet.getInt("id");

						String insertSql = "INSERT INTO Transaction (product_id, quantity, status) VALUES (?, ?, ?)";
						try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
							insertStatement.setInt(1, product_id);
							insertStatement.setInt(2, kolicina);
							insertStatement.setString(3, "ADDED");
							insertStatement.executeUpdate();

							showNotification("Success", "Data saved to the database!");
						}
					} else {
						showNotification("Error", "Data not saved to the database!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void switchToMainView() throws IOException {
		Node source = (Node) top_pane;
		Stage stage = (Stage) source.getScene().getWindow();
		stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/resources/view/FXMLHomePage.fxml"))));
		stage.show();
	}

	private void showNotification(String status, String message) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/components/Notification.fxml"));
			Parent notification = loader.load();

			NotificationController notificationController = loader.getController();
			notificationController.setNotification(status, message);

			double spaceBetweenNotifications = 5;

			// Calculate the total height of all notifications, including spacing
			double totalHeight = notificationPane.getChildren().stream()
					.mapToDouble(node -> node.getBoundsInParent().getHeight()).sum()
					+ (notificationPane.getChildren().size() * spaceBetweenNotifications);

			notification.setTranslateY(totalHeight);

			notificationPane.getChildren().add(notification);

			notificationPane.toFront();

			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					closeNotification(notification);
				}
			}));
			timeline.play();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeNotification(Node notification) {
		StackPane parentPane = (StackPane) notification.getParent();
		if (parentPane != null) {
			int index = parentPane.getChildren().indexOf(notification);

			parentPane.getChildren().remove(notification);

			for (int i = index; i < parentPane.getChildren().size(); i++) {
				Node currentNotification = parentPane.getChildren().get(i);
				double currentTranslateY = currentNotification.getTranslateY();

				double closingNotificationHeight = notification.getBoundsInParent().getHeight() + 5;
				animateTranslation(currentNotification, currentTranslateY - closingNotificationHeight);
			}
		}
	}

	private void animateTranslation(Node node, double targetY) {
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), node);
		transition.setToY(targetY);
		transition.play();
	}

}
