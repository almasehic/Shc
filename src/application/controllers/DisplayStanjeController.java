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
import application.models.Product;
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

public class DisplayStanjeController implements Initializable {

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
	private TableColumn<Product, String> quantityColumn;

	@FXML
	private Pane top_pane;

	@FXML
	private Label totalItemsLabel;
	@FXML
	private Label totalGoldLabel;
	@FXML
	private Label totalSilverLabel;
	@FXML
	private Label totalPriceLabel;

	@FXML
	private StackPane notificationPane;

	@FXML
	private TextField searchField;

	private FilteredList<Product> filteredData;

	private String stanjeModel, stanjeTip;
	private Date odDatuma, doDatuma;
	private Boolean noZero = false;
	private Boolean minus = false;

	public void setDates(Date odDatuma, Date doDatuma) {
		this.odDatuma = odDatuma;
		this.doDatuma = doDatuma;

		loadProductsInStoreAndUpdateTotalItemsCount();
	}

	public void loadProductsInStoreAndUpdateTotalItemsCount() {
		loadProductsInStore();

		int totalItems = prikazivanje_table.getItems().size();
		totalItemsLabel.setText("Total items: " + totalItems);
	
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		kolekcijaColumn.setCellValueFactory(new PropertyValueFactory<>("productCollection"));
		tipColumn.setCellValueFactory(new PropertyValueFactory<>("productType"));
		auColumn.setCellValueFactory(new PropertyValueFactory<>("gold"));
		agColumn.setCellValueFactory(new PropertyValueFactory<>("silver"));
		cijenaColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		cijenaColumn.setCellValueFactory(cellData -> {
			Double cijena = cellData.getValue().getPrice();
			String formattedCijena = formatCijena(cijena);
			return new SimpleStringProperty(formattedCijena);
		});

		createdAtColumn.setCellValueFactory(cellData -> {
			Timestamp timestamp = cellData.getValue().getCreatedAt();
			return new SimpleStringProperty(formatTimestamp(timestamp));
		});

		loadProductsInStoreAndUpdateTotalItemsCount();

		filteredData = new FilteredList<>(prikazivanje_table.getItems(), p -> true);

		prikazivanje_table.setItems(filteredData);

		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			search(newValue);
			prikazivanje_table.setItems(filteredData);

			double totalGold = 0.0;
	        double totalSilver = 0.0;
	        double totalPrice = 0.0;
	        
	        for (Product product : filteredData) {
	            totalGold += product.getGold()*product.getQuantity();
	            totalSilver += product.getSilver()*product.getQuantity();
	            totalPrice += product.getPrice()*product.getQuantity();
	        }
	        
	        totalGoldLabel.setText("Total au:   " + String.valueOf(totalGold) + " g");
	        totalSilverLabel.setText("Total ag:   " + String.valueOf(totalSilver) + " g");
	        totalPriceLabel.setText("Total price:   " + String.valueOf(totalPrice) + " KM");
	        
			int totalItems = prikazivanje_table.getItems().size();
			totalItemsLabel.setText("Total items: " + totalItems);
			
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
						|| String.valueOf(product.getQuantity()).contains(keyword)) {
					return true;
				}
			}
			return false;
		});
	}

	private void loadProductsInStore() {
		double totalGold = 0.0;
		double totalSilver = 0.0;
		double totalPrice = 0.0;
		ObservableList<Product> products = FXCollections.observableArrayList();
		String selectSql = "SELECT p.id, p.product_collection, p.product_type, p.gold, p.silver, p.price, p.is_deleted, p.created_at, p.updated_at, "
	            + "(SELECT SUM(CASE WHEN t.status = 'ADDED' OR t.status = 'REFUNDED' OR t.status = 'FIX ADD' THEN t.quantity "
	            + "WHEN t.status = 'SOLD' OR t.status = 'FIX SOLD' THEN -t.quantity ELSE 0 END) "
	            + "FROM Transaction t WHERE p.id = t.product_id) AS total_quantity "
	            + "FROM Product p LEFT JOIN Transaction t ON p.id = t.product_id WHERE p.is_deleted = FALSE ";

		String operation = null;
		if (noZero) {
			operation = ">";
		} else if (minus) {
			operation = "<";
		}
		
	    if (noZero || minus) {
	    	selectSql += "AND (SELECT SUM(CASE WHEN t.status = 'ADDED' OR t.status = 'REFUNDED' OR t.status = 'FIX ADD' THEN t.quantity "
                    + "WHEN t.status = 'SOLD' OR t.status = 'FIX SOLD' THEN -t.quantity ELSE 0 END) "
                    + "FROM Transaction t WHERE p.id = t.product_id) " + operation + " 0 ";
	    }
		
		if (odDatuma != null && doDatuma != null) {
			selectSql += "AND p.created_at BETWEEN ? AND ? ";
		}

		if (stanjeModel != null && !stanjeModel.isEmpty()) {
			selectSql += "AND p.product_collection = ? ";
		}
		if (stanjeTip != null && !stanjeTip.isEmpty()) {
			selectSql += "AND p.product_type = ? ";
		}
	

		selectSql += "GROUP BY p.id, p.product_collection, p.product_type, p.gold, p.silver, p.price, p.is_deleted, p.created_at, p.updated_at";

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			int parameterIndex = 1;
			if (odDatuma != null && doDatuma != null) {
				selectStatement.setDate(parameterIndex++, odDatuma);
				selectStatement.setDate(parameterIndex++, doDatuma);
			} else if (odDatuma != null) {
				selectStatement.setDate(parameterIndex++, odDatuma);
			} else if (doDatuma != null) {
				selectStatement.setDate(parameterIndex++, doDatuma);
			}

			if (stanjeModel != null && !stanjeModel.isEmpty()) {
				selectStatement.setString(parameterIndex++, stanjeModel);
			}
			if (stanjeTip != null && !stanjeTip.isEmpty()) {
				selectStatement.setString(parameterIndex++, stanjeTip);
			}

			ResultSet resultSet = selectStatement.executeQuery();
			
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
				int quantity = resultSet.getInt("total_quantity");

				totalGold += (gold*quantity);
				totalSilver += (silver*quantity);
				totalPrice += (price*quantity);
				
				Product product = new Product(id, productCollection, productType, gold, silver, price, isDeleted,
						createdAt, updatedAt);
				product.setQuantity(quantity);

				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		prikazivanje_table.setItems(products);
		totalGoldLabel.setText("Total au:   " + String.valueOf(totalGold) + " g");
        totalSilverLabel.setText("Total ag:   " + String.valueOf(totalSilver) + " g");
        totalPriceLabel.setText("Total price:   " + String.valueOf(totalPrice) + " KM");
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
	
	//private float[] sumGoldSilverPriceSQL() {
		float totalGold = 0;
		float totalSilver = 0;
		float totalPrice = 0;
		
		//String searchSql = "SELECT SUM(gold * quantity), SUM(silver * quantity), SUM(price * quantity) FROM "
	
	
	private double[] sumGoldSilverPrice() {
	    double totalGold = 0.0;
	    double totalSilver = 0.0;
	    double totalPrice = 0.0;
	    TableColumn<Product, ?> goldColumn = null;
	    TableColumn<Product, ?> silverColumn = null;
	    TableColumn<Product, ?> priceColumn = null;
	    TableColumn<Product, ?> kolicinaColumn = null;
	    
	    for (TableColumn<Product, ?> column : prikazivanje_table.getColumns()) {
	    	//System.out.println(column.getText());
	        if (column.getText().equals("Au")) {
	            goldColumn = column;
	        } else if (column.getText().equals("Ag")) {
	            silverColumn = column;
	        }else if (column.getText().equals("Cijena")) {
	        	priceColumn = column;
	        } else if (column.getText().equals("Quantity")) {
	        	kolicinaColumn = column;
	        }
	    }
	    
	    if (goldColumn != null) {
	        for (Product product : prikazivanje_table.getItems()) {
	            Object goldValue = goldColumn.getCellData(product);
	            Object kolicinaValue = kolicinaColumn.getCellData(product);
	                totalGold += (Double) goldValue;
	        }
	    }
	    
	    if (silverColumn != null) {
	        for (Product product : prikazivanje_table.getItems()) {
	            Object silverValue = silverColumn.getCellData(product);
	                totalSilver += (Double) silverValue;
	        }
	    }
	    
	    if (priceColumn != null) {
	        for (Product product : prikazivanje_table.getItems()) {
	            String priceData = (String) priceColumn.getCellData(product);
	            if (priceData != null && priceData.length() >= 2) {
	            	String trimmedPriceData = priceData.substring(0, priceData.length() - 2);
	            	try {
	                    Double priceValue = Double.parseDouble(trimmedPriceData);
	                    totalPrice += priceValue;
	                } catch (NumberFormatException e) {
	                    // Handle parsing errors, if necessary
	                    System.err.println("Error parsing price value: " + trimmedPriceData);
	                }
	            } 
	        }
	    }
	    
	    return new double[]{totalGold, totalSilver, totalPrice};
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
		loadProductsInStoreAndUpdateTotalItemsCount();
	}

	public String getStanjeModel() {
		return stanjeModel;
	}

	public void setStanjeModel(String stanjeModel) {
		this.stanjeModel = stanjeModel;
		loadProductsInStoreAndUpdateTotalItemsCount();
	}
	
	public void setBooleanZero(Boolean noZero) {
		this.noZero = noZero;
	}
	
	public Boolean getBooleanZero() {
		return noZero;
	}
	
	public void setBooleanMinus(Boolean minus) {
		this.minus = minus;
	}
	
	public Boolean getBooleanMinus() {
		return minus;
	}
	
	public void setBoolean(Boolean noZero) {
		this.noZero = noZero;
	}
	
	public Boolean getBoolean() {
		return noZero;
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
