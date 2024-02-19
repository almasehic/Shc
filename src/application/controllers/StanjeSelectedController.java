package application.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StanjeSelectedController {

	@FXML
	TextField stanje_model, stanje_tip;
	@FXML
	DatePicker od_datuma, do_datuma;

	private Stage stage;
	private Scene scene;

	private void switchToView(String viewPath, ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(viewPath));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public void switchBackToMain(ActionEvent event) throws IOException {
		switchToView("/resources/view/FXMLHomePage.fxml", event);
	}

	public void switchToTransakcije(ActionEvent event) throws IOException {
		switchToView("/resources/view/Transakcije.fxml", event);
	}

}
