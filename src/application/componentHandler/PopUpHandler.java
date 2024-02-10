package application.componentHandler;

import java.io.IOException;
import java.util.function.Consumer;

import application.controllers.PopUpController;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class PopUpHandler {

	private static void applyScalingAnimation(Parent popupRoot) {
		ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), popupRoot);
		scaleTransition.setFromX(0.1);
		scaleTransition.setFromY(0.1);
		scaleTransition.setToX(1);
		scaleTransition.setToY(1);
		scaleTransition.play();
	}

	private static void applyRotatingAnimation(Parent popupRoot) {
		RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), popupRoot);
		rotateTransition.setFromAngle(0);
		rotateTransition.setToAngle(360);
		rotateTransition.play();
	}

	public static void showPopup(StackPane popUpPane, String title, String body,
			Consumer<Boolean> confirmationHandler) {
		try {
			FXMLLoader loader = new FXMLLoader(PopUpHandler.class.getResource("/resources/components/PopUp.fxml"));
			Parent popupRoot = loader.load();

			PopUpController popupController = loader.getController();
			popupController.setPopUp(title, body);
			popupController.setOnConfirmation(confirmationHandler);

			popUpPane.getChildren().add(popupRoot);
			popUpPane.toFront();

			applyScalingAnimation(popupRoot);
			applyRotatingAnimation(popupRoot);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
