package application.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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
			// Set a default icon and title for unknown status
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

		if (notification != null) {
			closeNotification(notification);
		} else {
			System.out.println("Notification is null");
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
