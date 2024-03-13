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

import application.componentHandler.NotificationHandler;
import application.componentHandler.PopUpHandler;
import application.models.Transaction;
import application.service.DatabaseUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
	private TableColumn<Transaction, Void> actionsColumn;

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

	private String stanjeModel, stanjeTip;
	private Date odDatuma, doDatuma;

	public void setDates(Date odDatuma, Date doDatuma) {
		this.odDatuma = odDatuma;
		this.doDatuma = doDatuma;

		loadTransactionsAndUpdateTotalItemsCount();
	}

	public void loadTransactionsAndUpdateTotalItemsCount() {
		loadTransactions();

		int totalItems = prikazivanje_table.getItems().size();
		totalItemsLabel.setText("Total items: " + totalItems);
	}

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

		loadTransactionsAndUpdateTotalItemsCount();

		Image deleteIcon = new Image(getClass().getResourceAsStream("/resources/images/trash.png"));

		actionsColumn.setCellFactory(param -> new TableCell<>() {
			private final Button deleteButton = createIconButton(deleteIcon);
			{
				deleteButton.setOnAction(event -> {
					Transaction transaction = getTableView().getItems().get(getIndex());
					showDeleteConfirmation(transaction);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					HBox buttonsBox = new HBox(deleteButton);
					buttonsBox.setSpacing(20);
					buttonsBox.setAlignment(Pos.CENTER);
					setGraphic(buttonsBox);
				}
			}
		});

		filteredData = new FilteredList<>(prikazivanje_table.getItems(), p -> true);

		prikazivanje_table.setItems(filteredData);

		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			search(newValue);
		});
	}

	private Button createIconButton(Image icon) {
		ImageView imageView = new ImageView(icon);
		imageView.setFitWidth(20);
		imageView.setFitHeight(20);

		Button button = new Button();
		button.setGraphic(imageView);
		button.setStyle("-fx-background-color: transparent;");
		button.setMaxSize(40, 40);

		button.setOnMouseEntered(e -> button.setCursor(Cursor.HAND));
		button.setOnMouseExited(e -> button.setCursor(Cursor.DEFAULT));

		return button;
	}

	private void showDeleteConfirmation(Transaction transaction) {
		PopUpHandler.showPopup(popUpPane, "Confirm Deletion", "Are you sure you want to delete this transaction?",
				confirmation -> {
					if (confirmation) {
						deleteTransaction(transaction);
					}
				});
	}

	private void deleteTransaction(Transaction transaction) {
		String updateSql = "DELETE FROM transaction WHERE id = ?";

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
			updateStatement.setInt(1, transaction.getId());
			int rowsAffected = updateStatement.executeUpdate();

			if (rowsAffected > 0) {
				NotificationHandler.showNotification(notificationPane, "Success",
						"Transaction marked as deleted successfully.");

				loadTransactions();

				filteredData = new FilteredList<>(prikazivanje_table.getItems(), p -> true);

				prikazivanje_table.setItems(filteredData);
			} else {
				NotificationHandler.showNotification(notificationPane, "Error",
						"Failed to mark transaction as deleted.");
			}
		} catch (SQLException e) {
			NotificationHandler.showNotification(notificationPane, "Error",
					"An error occurred while marking the Transaction as deleted.");
			e.printStackTrace();
		}
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
		ObservableList<Transaction> transactions = FXCollections.observableArrayList();
		String selectSql = "SELECT t.*, p.product_collection, p.product_type " + "FROM Transaction t "
				+ "INNER JOIN Product p ON t.product_id = p.id ";

		if (stanjeModel != null && !stanjeModel.isEmpty()) {
			selectSql += " AND p.product_collection = ?";
		}
		if (stanjeTip != null && !stanjeTip.isEmpty()) {
			selectSql += " AND p.product_type = ?";
		}
		if (odDatuma != null && doDatuma != null) {
			selectSql += " AND t.created_at BETWEEN ? AND ?";
		}

		selectSql += " ORDER BY t.created_at DESC";

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {

			int parameterIndex = 1;
			if (stanjeModel != null && !stanjeModel.isEmpty()) {
				selectStatement.setString(parameterIndex++, stanjeModel);
			}
			if (stanjeTip != null && !stanjeTip.isEmpty()) {
				selectStatement.setString(parameterIndex++, stanjeTip);
			}
			if (odDatuma != null && doDatuma != null) {
				selectStatement.setDate(parameterIndex++, odDatuma);
				selectStatement.setDate(parameterIndex++, doDatuma);
			}

			ResultSet resultSet = selectStatement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				int productId = resultSet.getInt("product_id");
				int quantity = resultSet.getInt("quantity");
				String status = resultSet.getString("status");
				Timestamp createdAt = resultSet.getTimestamp("created_at");

				Transaction transaction = new Transaction(id, productId, quantity, status, createdAt);
				transactions.add(transaction);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		prikazivanje_table.setItems(transactions);
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
	private void switchBackToStanjeSelected() throws IOException {
		Node source = (Node) top_pane;
		Stage stage = (Stage) source.getScene().getWindow();
		stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/resources/view/Stanje_selected.fxml"))));
		stage.show();
	}

	public String getStanjeTip() {
		return stanjeTip;
	}

	public void setStanjeTip(String stanjeTip) {
		this.stanjeTip = stanjeTip;
		loadTransactionsAndUpdateTotalItemsCount();
	}

	public String getStanjeModel() {
		return stanjeModel;
	}

	public void setStanjeModel(String stanjeModel) {
		this.stanjeModel = stanjeModel;
		loadTransactionsAndUpdateTotalItemsCount();
	}

	public Date getOdDatuma() {
		return odDatuma;
	}

	public void setOdDatuma(Date odDatuma) {
		this.odDatuma = odDatuma;
	}

	public Date getDoDatuma() {
		return doDatuma;
	}

	public void setDoDatuma(Date doDatuma) {
		this.doDatuma = doDatuma;
	}

}
