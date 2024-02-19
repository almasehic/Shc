package application.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import application.service.DatabaseUtil;

public class Transaction {
	private int id;
	private int productId;
	private String productType;
	private String productCollection;
	private int quantity;
	private String status;
	private Timestamp createdAt;

	public Transaction(int id, int productId, int quantity, String status, Timestamp createdAt) {
		super();
		this.id = id;
		this.productId = productId;
		this.quantity = quantity;
		this.status = status;
		this.createdAt = createdAt;

		retrieveProductDetailsFromDatabase(productId);
	}

	private void retrieveProductDetailsFromDatabase(int productId) {
		String selectSql = "SELECT product_type, product_collection FROM product WHERE id = ?";

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			selectStatement.setInt(1, productId);
			try (ResultSet resultSet = selectStatement.executeQuery()) {
				if (resultSet.next()) {
					this.productCollection = resultSet.getString("product_collection");
					this.productType = resultSet.getString("product_type");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getProductType() {
		return productType;
	}

	public String getProductCollection() {
		return productCollection;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

}