package application.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

	private Stage stage;
	private Scene scene;

	private void switchToView(String viewPath, ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(viewPath));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public void switchToUlazSelected(ActionEvent event) throws IOException {
		switchToView("/resources/view/Ulaz_selected.fxml", event);
	}

	public void switchToIzlazSelected(ActionEvent event) throws IOException {
		switchToView("/resources/view/Izlaz_selected.fxml", event);
	}

	public void switchToStanjeSelected(ActionEvent event) throws IOException {
		switchToView("/resources/view/Stanje_selected.fxml", event);
	}

	public void switchToIzmjenaSelected(ActionEvent event) throws IOException {
		switchToView("/resources/view/Izmjena_selected.fxml", event);
	}

	public void switchToDodajNoviProizvodSelected(ActionEvent event) throws IOException {
		switchToView("/resources/view/Dodaj_proizvod_selected.fxml", event);
	}

	public void switchToIzmjenaNazivaModelaSelected(ActionEvent event) throws IOException {
		switchToView("/resources/view/Izmjena_naziva_selected.fxml", event);
	}

	public void switchToIzmjenaCijeneSelected(ActionEvent event) throws IOException {
		switchToView("/resources/view/Izmjena_cijene.fxml", event);
	}

	public void switchToIzmjenaUnosaSelected(ActionEvent event) throws IOException {
		switchToView("/resources/view/Izmjena_unosa_selected.fxml", event);
	}

	public void switchBackToMain(ActionEvent event) throws IOException {
		switchToView("/resources/view/FXMLHomePage.fxml", event);
	}

	public void switchToDisplay(ActionEvent event) throws IOException {
		switchToView("/resources/view/Display.fxml", event);
	}
}
