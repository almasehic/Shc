package application.controllers;

import application.componentHandler.NotificationHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class NotificationController {

	@FXML
	private ImageView iconImageView;

	@FXML
	private Label titleLabel;

	@FXML
	private Label bodyLabel;

	@FXML
	private ImageView closeImageView;

	@FXML
	private StackPane closeButton;

	@FXML
	private void initialize() {
		closeButton.setOnMouseClicked(event -> handleCloseButtonClicked());

		closeButton.setOnMouseEntered(e -> closeButton.getScene().setCursor(Cursor.HAND));
		closeButton.setOnMouseExited(e -> closeButton.getScene().setCursor(Cursor.DEFAULT));
	}

	public void setNotification(String status, String body) {
		switch (status.toLowerCase()) {
		case "success":
			iconImageView.setImage(new Image("/resources/images/face_smile.png"));
			titleLabel.setText("Success");
			break;
		case "warning":
			iconImageView.setImage(new Image("/resources/images/exclamation.png"));
			titleLabel.setText("Warning");
			break;
		case "error":
			iconImageView.setImage(new Image("/resources/images/face_cry.png"));
			titleLabel.setText("Error");
			break;
		default:
			// Set a default icon and title for unknown status ( idk what )
			iconImageView.setImage(new Image("/resources/images/face_cry.png"));
			titleLabel.setText("Unknown Status");
			break;
		}

		bodyLabel.setText(body);
		closeImageView.setOnMouseClicked(event -> handleCloseButtonClicked());
	}

	@FXML
	private void handleCloseButtonClicked() {
		VBox notification = (VBox) closeButton.getParent().getParent();
		StackPane notificationPane = (StackPane) notification.getParent();

		if (notification != null && notificationPane != null) {
			NotificationHandler.closeNotification(notificationPane, notification);
		} else {
			System.out.println("Notification or Notification Pane is null");
		}
	}
}
