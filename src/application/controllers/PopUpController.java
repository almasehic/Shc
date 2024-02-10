package application.controllers;

import java.util.function.Consumer;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class PopUpController {

	@FXML
	private StackPane rootStackPane;

	@FXML
	private Label titleLabel;

	@FXML
	private Label bodyLabel;

	@FXML
	private Button neButton;

	@FXML
	private Button daButton;

	private Consumer<Boolean> confirmationHandler;

	@FXML
	private void initialize() {
		neButton.setOnAction(event -> handleCancellation());
		daButton.setOnAction(event -> handleConfirmation());
	}

	public void setPopUp(String title, String body) {
		titleLabel.setText(title);
		bodyLabel.setText(body);
	}

	public void setOnConfirmation(Consumer<Boolean> confirmationHandler) {
		this.confirmationHandler = confirmationHandler;
	}

	@FXML
	private void handleConfirmation() {
		if (confirmationHandler != null) {
			confirmationHandler.accept(true);
		}
		closePopup(rootStackPane);
	}

	@FXML
	private void handleCancellation() {
		if (confirmationHandler != null) {
			confirmationHandler.accept(false);
		}
		closePopup(rootStackPane);
	}

	private void closePopup(Node node) {
		StackPane parent = (StackPane) node.getParent();
		if (parent != null) {
			// Apply reverse animations to close the popup
			applyReverseAnimations(node, new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// Remove the node from the parent after animations complete
					parent.getChildren().remove(node);
					parent.toBack();
				}
			});
		}
	}

	private void applyReverseAnimations(Node node, EventHandler<ActionEvent> onFinish) {
		// Create scaling animation
		ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), node);
		scaleTransition.setFromX(1);
		scaleTransition.setFromY(1);
		scaleTransition.setToX(0.1);
		scaleTransition.setToY(0.1);

		// Create rotating animation
		RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), node);
		rotateTransition.setFromAngle(360);
		rotateTransition.setToAngle(0);

		ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, rotateTransition);
		parallelTransition.setOnFinished(onFinish);

		parallelTransition.play();
	}

}
