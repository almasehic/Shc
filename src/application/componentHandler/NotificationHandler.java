package application.componentHandler;

import java.io.IOException;

import application.controllers.NotificationController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class NotificationHandler {

	public static void showNotification(StackPane notificationPane, String status, String message) {
		try {
			FXMLLoader loader = new FXMLLoader(
					NotificationHandler.class.getResource("/resources/components/Notification.fxml"));
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
					closeNotification(notificationPane, notification);
				}
			}));
			timeline.play();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeNotification(StackPane notificationPane, Node notification) {
		if (notificationPane != null) {
			int index = notificationPane.getChildren().indexOf(notification);

			notificationPane.getChildren().remove(notification);

			for (int i = index; i < notificationPane.getChildren().size(); i++) {
				Node currentNotification = notificationPane.getChildren().get(i);
				double currentTranslateY = currentNotification.getTranslateY();

				double closingNotificationHeight = notification.getBoundsInParent().getHeight() + 5;
				animateTranslation(currentNotification, currentTranslateY - closingNotificationHeight);
			}

			if (notificationPane.getChildren().isEmpty()) {
				notificationPane.toBack();
			}
		}
	}

	public static void animateTranslation(Node node, double targetY) {
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), node);
		transition.setToY(targetY);
		transition.play();
	}
}
