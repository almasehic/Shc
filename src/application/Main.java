package application;

import application.service.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/resources/view/FXMLHomePage.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);

			primaryStage.setResizable(false); // made it so it's not re-sizable

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		DatabaseInitializer.createDatabaseAndTables();

		launch(args);
	}
}
