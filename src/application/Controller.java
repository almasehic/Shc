package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Controller {

	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void siwtchToUlazSelected(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Ulaz_selected.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void siwtchToIzlazSelected(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Izlaz_selected.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void siwtchToStanjeSelected(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Stanje_selected.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void siwtchToIzmjenaSelected(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Izmjena_selected.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void siwtchToDodajNoviProizvodSelected(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Dodaj_proizvod_selected.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void siwtchToIzmjenaNazivaModelaSelected(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Izmjena_naziva_selected.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void siwtchToIzmjenaCijeneSelected(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Izmjena_cijene.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void siwtchToIzmjenaUnosaSelected(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Izmjena_unosa_selected.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void siwtchBackToMain(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("FXMLHomePage.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
