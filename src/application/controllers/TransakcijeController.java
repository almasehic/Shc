package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import application.models.Transaction;
import application.service.DatabaseUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TransakcijeController implements Initializable {

	@FXML
	private TableView<Transaction> prikazivanje_table;

	@FXML
	private TableColumn<Transaction, String> kolekcijaColumn;

	@FXML
	private TableColumn<Transaction, String> tipColumn;

	@FXML
	private TableColumn<Transaction, Double> quantityColumn;

	@FXML
	private TableColumn<Transaction, String> statusColumn;

	@FXML
	private TableColumn<Transaction, String> createdAtColumn;

	@FXML
	private Pane top_pane;

	@FXML
	private Label totalItemsLabel;

	@FXML
	private StackPane popUpPane;

	@FXML
	private StackPane notificationPane;

	@FXML
	private TextField searchField;

	private FilteredList<Transaction> filteredData;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		kolekcijaColumn.setCellValueFactory(new PropertyValueFactory<>("productCollection"));
		tipColumn.setCellValueFactory(new PropertyValueFactory<>("productType"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

		createdAtColumn.setCellValueFactory(cellData -> {
			Timestamp timestamp = cellData.getValue().getCreatedAt();
			return new SimpleStringProperty(formatTimestamp(timestamp));
		});

		loadTransactions();

		int totalItems = prikazivanje_table.getItems().size();
		totalItemsLabel.setText("Total items: " + totalItems);

		filteredData = new FilteredList<>(prikazivanje_table.getItems(), p -> true);

		prikazivanje_table.setItems(filteredData);

		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			search(newValue);
		});
	}

	private void search(String keywords) {
		String[] keywordArray = keywords.toLowerCase().split("\\s+");

		filteredData.setPredicate(Transaction -> {
			if (keywords == null || keywords.isEmpty()) {
				return true;
			}

			for (String keyword : keywordArray) {
				if (Transaction.getProductCollection().toLowerCase().contains(keyword)
						|| Transaction.getProductType().toLowerCase().contains(keyword)
						|| String.valueOf(Transaction.getQuantity()).toLowerCase().contains(keyword)
						|| String.valueOf(Transaction.getStatus()).toLowerCase().contains(keyword)
						|| Transaction.getCreatedAt().toString().contains(keyword)) {
					return true;
				}
			}
			return false;
		});
	}

	private void loadTransactions() {
		ObservableList<Transaction> Transactions = FXCollections.observableArrayList();

		String selectSql = "SELECT * FROM Transaction ORDER BY created_at DESC";
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement selectStatement = connection.prepareStatement(selectSql);
				ResultSet resultSet = selectStatement.executeQuery()) {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				int productId = resultSet.getInt("product_id");
				int quantity = resultSet.getInt("quantity");
				String status = resultSet.getString("status");
				Timestamp createdAt = resultSet.getTimestamp("created_at");

				Transaction Transaction = new Transaction(id, productId, quantity, status, createdAt);

				Transactions.add(Transaction);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		prikazivanje_table.setItems(Transactions);
	}

	private String formatTimestamp(Timestamp timestamp) {
		if (timestamp == null) {
			return "";
		}
		Date date = new Date(timestamp.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	@FXML
	private void switchToMainView() throws IOException {
		Node source = (Node) top_pane;
		Stage stage = (Stage) source.getScene().getWindow();
		stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/resources/view/FXMLHomePage.fxml"))));
		stage.show();
	}

}
