package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Controller {

	private Stage stage;
	private Scene scene;
	private Parent root;
	private Button okButton;
	
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
	
	
	
	//Taking data from Stanje_selected.fxml and sending it to Display.fxml
	
	
	
	
	
	
	@FXML
	TextField stanje_model, stanje_tip;
	@FXML
	DatePicker od_datuma, do_datuma;
	
	
	//method to print the sent data on the top of the Display.fxml 
	@FXML
	Label Prikazivanje_text;
	
	public void display_arguments(String display) {
		Prikazivanje_text.setText(display); 
	}
	
	public void siwtchToDisplay(ActionEvent event) throws IOException {
		
		
		String display = stanje_model.getText()+" "+stanje_tip.getText()+" "+od_datuma.getValue().toString()+" "+do_datuma.getValue().toString();
		Parent root = FXMLLoader.load(getClass().getResource("Display.fxml"));
		System.out.println(display);
		display_arguments(display);
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void onOKButtonPress() {
		
	}
	
}
