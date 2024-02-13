package application.models;

import java.sql.Timestamp;

public class Transaction {
	private int id;
	private int productId;
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