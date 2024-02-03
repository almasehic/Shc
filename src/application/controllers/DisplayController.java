package application.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DisplayController {

	private Stage stage;
	private Scene scene;
	private Parent root;

	@FXML
	Label Prikazivanje_text;

	public void displayArguments(String display) {
		Prikazivanje_text.setText(display);
	}

	public void switchBackToMain(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("FXMLHomePage.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
