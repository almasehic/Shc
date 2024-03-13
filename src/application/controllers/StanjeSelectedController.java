package application.controllers;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/view/Transakcije.fxml"));
		Parent root = loader.load();
		TransakcijeController controller = loader.getController();

		if (od_datuma.getValue() != null && do_datuma.getValue() != null) {
			controller.setDates(Date.valueOf(od_datuma.getValue()), Date.valueOf(do_datuma.getValue()));
		} else if (od_datuma.getValue() != null) {
			controller.setDates(Date.valueOf(od_datuma.getValue()), Date.valueOf(LocalDate.now()));
		} else if (do_datuma.getValue() != null) {
			controller.setDates(Date.valueOf(LocalDate.of(1900, 1, 1)), Date.valueOf(do_datuma.getValue()));
		}

		controller.setStanjeModel(stanje_model.getText());
		controller.setStanjeTip(stanje_tip.getText());

		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public void switchToDisplayStanje(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/view/DisplayStanje.fxml"));
		Parent root = loader.load();
		DisplayStanjeController controller = loader.getController();

		if (od_datuma.getValue() != null && do_datuma.getValue() != null) {
			controller.setDates(Date.valueOf(od_datuma.getValue()), Date.valueOf(do_datuma.getValue()));
		} else if (od_datuma.getValue() != null) {
			controller.setDates(Date.valueOf(od_datuma.getValue()), Date.valueOf(LocalDate.now()));
		} else if (do_datuma.getValue() != null) {
			controller.setDates(Date.valueOf(LocalDate.of(1900, 1, 1)), Date.valueOf(do_datuma.getValue()));
		}

		controller.setStanjeModel(stanje_model.getText());
		controller.setStanjeTip(stanje_tip.getText());

		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

}
