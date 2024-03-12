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
import java.util.List;
import java.util.ResourceBundle;

import application.componentHandler.NotificationHandler;
import application.componentHandler.PopUpHandler;
import application.models.Product;
import application.service.DatabaseUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DisplayProductsController implements Initializable {

	@FXML
	private TableView<Product> prikazivanje_table;

	@FXML
	private TableColumn<Product, String> kolekcijaColumn;

	@FXML
	private TableColumn<Product, String> tipColumn;

	@FXML
	private TableColumn<Product, Double> auColumn;

	@FXML
	private TableColumn<Product, Double> agColumn;

	@FXML
	private TableColumn<Product, String> cijenaColumn;

	@FXML
	private TableColumn<Product, String> createdAtColumn;

	@FXML
	private TableColumn<Product, String> updatedAtColumn;

	@FXML
	private TableColumn<Product, Void> actionsColumn;

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

	private FilteredList<Product> filteredData;

	private static final int ROWS_PER_PAGE = 10;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		kolekcijaColumn.setCellValueFactory(new PropertyValueFactory<>("productCollection"));
		tipColumn.setCellValueFactory(new PropertyValueFactory<>("productType"));
		auColumn.setCellValueFactory(new PropertyValueFactory<>("gold"));
		agColumn.setCellValueFactory(new PropertyValueFactory<>("silver"));
		cijenaColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
		updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

		cijenaColumn.setCellValueFactory(cellData -> {
			Double cijena = cellData.getValue().getPrice();
			String formattedCijena = formatCijena(cijena);
			return new SimpleStringProperty(formattedCijena);
		});

		createdAtColumn.setCellValueFactory(cellData -> {
			Timestamp timestamp = cellData.getValue().getCreatedAt();
			return new SimpleStringProperty(formatTimestamp(timestamp));
		});

		updatedAtColumn.setCellValueFactory(cellData -> {
			Timestamp timestamp = cellData.getValue().getUpdatedAt();
			return new SimpleStringProperty(formatTimestamp(timestamp));
		});

		loadProducts();

		int totalItems = prikazivanje_table.getItems().size();
		totalItemsLabel.setText("Total items: " + totalItems);

		Image deleteIcon = new Image(getClass().getResourceAsStream("/resources/images/trash.png"));
		Image updateIcon = new Image(getClass().getResourceAsStream("/resources/images/pen_to_square.png"));

		actionsColumn.setCellFactory(param -> new TableCell<>() {
			private final Button deleteButton = createIconButton(deleteIcon);
			private final Button updateButton = createIconButton(updateIcon);

			{
				deleteButton.setOnAction(event -> {
					Product product = getTableView().getItems().get(getIndex());
					showDeleteConfirmation(product);
				});

				updateButton.setOnAction(event -> {
					Product product = getTableView().getItems().get(getIndex());
					updateProduct(product);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					HBox buttonsBox = new HBox(updateButton, deleteButton);
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

	private void search(String keywords) {
		String[] keywordArray = keywords.toLowerCase().split("\\s+");

		filteredData.setPredicate(product -> {
			if (keywords == null || keywords.isEmpty()) {
				return true;
			}

			for (String keyword : keywordArray) {
				if (product.getProductCollection().toLowerCase().contains(keyword)
						|| product.getProductType().toLowerCase().contains(keyword)
						|| String.valueOf(product.getGold()).toLowerCase().contains(keyword)
						|| String.valueOf(product.getSilver()).toLowerCase().contains(keyword)
						|| String.valueOf(product.getPrice()).toLowerCase().contains(keyword)
						|| product.getCreatedAt().toString().contains(keyword)
						|| product.getUpdatedAt().toString().contains(keyword)) {
					return true;
				}
			}
			return false;
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

	private void showDeleteConfirmation(Product product) {
		PopUpHandler.showPopup(popUpPane, "Confirm Deletion", "Are you sure you want to delete this product?",
				confirmation -> {
					if (confirmation) {
						deleteProduct(product);
					}
				});
	}

	private void deleteProduct(Product product) {
		String updateSql = "UPDATE product SET is_deleted = TRUE WHERE id = ?";

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
			updateStatement.setInt(1, product.getId());
			int rowsAffected = updateStatement.executeUpdate();

			if (rowsAffected > 0) {
				NotificationHandler.showNotification(notificationPane, "Success",
						"Product marked as deleted successfully.");
				product.setDeleted(true);

				loadProducts();

				filteredData = new FilteredList<>(prikazivanje_table.getItems(), p -> true);

				prikazivanje_table.setItems(filteredData);
			} else {
				NotificationHandler.showNotification(notificationPane, "Error", "Failed to mark product as deleted.");
			}
		} catch (SQLException e) {
			NotificationHandler.showNotification(notificationPane, "Error",
					"An error occurred while marking the product as deleted.");
			e.printStackTrace();
		}
	}

	private void updateProduct(Product product) {
		// Implement logic to update the product
	}

	private void loadProducts() {
		ObservableList<Product> products = FXCollections.observableArrayList();

		String selectSql = "SELECT * FROM product WHERE is_deleted = FALSE";
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement selectStatement = connection.prepareStatement(selectSql);
				ResultSet resultSet = selectStatement.executeQuery()) {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String productCollection = resultSet.getString("product_collection");
				String productType = resultSet.getString("product_type");
				double gold = resultSet.getDouble("gold");
				double silver = resultSet.getDouble("silver");
				double price = resultSet.getDouble("price");
				boolean isDeleted = resultSet.getBoolean("is_deleted");
				Timestamp createdAt = resultSet.getTimestamp("created_at");
				Timestamp updatedAt = resultSet.getTimestamp("updated_at");

				Product product = new Product(id, productCollection, productType, gold, silver, price, isDeleted,
						createdAt, updatedAt);

				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		prikazivanje_table.setItems(products);
	}

	private String formatCijena(Double cijena) {
		if (cijena == null) {
			return "";
		}
		return String.format("%.2f KM", cijena);
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

	@FXML
	private void switchToDisplayProductDeleted() throws IOException {
		Node source = (Node) top_pane;
		Stage stage = (Stage) source.getScene().getWindow();
		stage.setScene(
				new Scene(FXMLLoader.load(getClass().getResource("/resources/view/DisplayProductsDeleted.fxml"))));
		stage.show();
	}

	@FXML
	private void switchToAddNewProduct() throws IOException {
		Node source = (Node) top_pane;
		Stage stage = (Stage) source.getScene().getWindow();
		stage.setScene(
				new Scene(FXMLLoader.load(getClass().getResource("/resources/view/Dodaj_proizvod_selected.fxml"))));
		stage.show();
	}

	private AnchorPane loadPrintLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/components/printFormat.fxml"));
		return loader.load();
	}

	@FXML
	private void handlePrintWithLayout() {
		try {
			AnchorPane printLayout = loadPrintLayout();
			populatePrintLayout(printLayout);

			// Create a dialog to display the print layout
			Dialog<Void> printDialog = new Dialog<>();
			DialogPane dialogPane = printDialog.getDialogPane();
			dialogPane.setContent(printLayout);
			dialogPane.getButtonTypes().add(ButtonType.CLOSE); // Add a close button

			// Create and add a print button
			Button printButton = new Button("Print");
			printButton.setOnAction(event -> {
				printCombinedPages(printLayout);
			});

			VBox vbox = new VBox(printLayout, printButton);
			dialogPane.setContent(vbox);

			// Set the dialog style
			Stage stage = (Stage) dialogPane.getScene().getWindow();
			stage.initStyle(StageStyle.UTILITY);

			// Show the dialog
			printDialog.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printCombinedPages(Node node) {
		PrinterJob job = PrinterJob.createPrinterJob();
		if (job != null) {
			TableView<Product> printTableView = (TableView<Product>) node.lookup("#printTableView");
			Pagination pagination = (Pagination) node.lookup("#pagination");
			int pageCount = pagination.getPageCount();

			try {
				for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
					pagination.setCurrentPageIndex(pageIndex);
					int fromIndex = pageIndex * ROWS_PER_PAGE;
					int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, printTableView.getItems().size());
					printTableView.setItems(
							FXCollections.observableArrayList(printTableView.getItems().subList(fromIndex, toIndex)));

					// Create a new scene for each page
					Scene pageScene = new Scene(new StackPane(printTableView));

					// Create a group containing the root node of the scene
					Group root = new Group();
					root.getChildren().add(pageScene.getRoot());

					// Print the group
					boolean success = job.printPage(root);
					if (!success) {
						System.out.println("Printing failed for page " + (pageIndex + 1));
						break;
					}
				}

				job.endJob();
			} catch (Exception e) {
				System.out.println("An error occurred during printing: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.out.println("Printer job is null. Cannot initiate printing.");
		}
	}

	private void populatePrintLayout(AnchorPane printLayout) {
		try {
			TableView<Product> printTableView = (TableView<Product>) printLayout.lookup("#printTableView");
			Pagination pagination = (Pagination) printLayout.lookup("#pagination");

			ObservableList<Product> products = prikazivanje_table.getItems();
			int pageCount = (int) Math.ceil((double) products.size() / ROWS_PER_PAGE);

			pagination.setPageCount(pageCount);
			pagination.setPageFactory(pageIndex -> {
				int fromIndex = pageIndex * ROWS_PER_PAGE;
				int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, products.size());
				List<Product> currentPageData = products.subList(fromIndex, toIndex);
				printTableView.setItems(FXCollections.observableArrayList(currentPageData));

				double totalColumnWidth = 0.0;
				for (TableColumn<Product, ?> column : printTableView.getColumns()) {
					totalColumnWidth += column.getWidth();
				}
				printTableView.setPrefWidth(totalColumnWidth);

				return new AnchorPane(printTableView);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
