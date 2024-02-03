package application;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
	
	public void checkDisplayInput(){
		
	}
	
	@FXML
	TextField stanje_model, stanje_tip;
	@FXML
	DatePicker od_datuma, do_datuma;


	public void siwtchToDisplay(ActionEvent event) throws IOException {
		
		//no fields specified: show all items in store currently available
		//only model specified: all typed of the model currently available
		//only type specified: all models of type currently available
		//only date: on that date all available
		//date range: in that range all available... follow this logic for rest haha
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		String doDatuma;
		
		if(stanje_model.getText().trim().isEmpty()) {
			stanje_model.setText("Svi modeli");
		}
		if(stanje_tip.getText().trim().isEmpty()) {
			stanje_tip.setText("svi tipovi");
		}
		if(od_datuma.getValue()==null) {
			LocalDate currentDate = LocalDate.now();
			od_datuma.setValue(currentDate);
		}
		if(do_datuma.getValue()==null) {
			doDatuma = " ";
		}else {
			doDatuma = " - " +do_datuma.getValue().format(dateFormatter);
		}
		

		String display = stanje_model.getText() + ", " + stanje_tip.getText() + ", " +
		        od_datuma.getValue().format(dateFormatter) + doDatuma;

	    System.out.println(display);
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("Display.fxml"));
	    root = loader.load();

	    DisplayController displayController = loader.getController();
	    displayController.displayArguments(display);

	    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    Scene scene = new Scene(root);
	    stage.setScene(scene);
	    stage.show();
	}

	
	public void onOKButtonPress() {
		
	}
	
}
