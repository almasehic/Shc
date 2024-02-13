package application.models;

import java.sql.Timestamp;

public class PriceArchive {
	private int id;
	private int productId;
	private float oldPrice;
	private float newPrice;
	private Timestamp createdAt;

	public PriceArchive(int id, int productId, float oldPrice, float newPrice, Timestamp createdAt) {
		super();
		this.id = id;
		this.productId = productId;
		this.oldPrice = oldPrice;
		this.newPrice = newPrice;
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

	public float getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(float oldPrice) {
		this.oldPrice = oldPrice;
	}

	public float getNewPrice() {
		return newPrice;
	}

	public void setNewPrice(float newPrice) {
		this.newPrice = newPrice;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

}