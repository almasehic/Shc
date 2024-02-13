package application.models;

import java.sql.Timestamp;

public class Product {
	private int id;
	private String productCollection;
	private String productType;
	private double gold;
	private double silver;
	private double price;
	private boolean isDeleted;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	public Product(int id, String productCollection, String productType, double gold, double silver, double price,
			boolean isDeleted, Timestamp createdAt, Timestamp updatedAt) {
		super();
		this.id = id;
		this.productCollection = productCollection;
		this.productType = productType;
		this.gold = gold;
		this.silver = silver;
		this.price = price;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProductCollection() {
		return productCollection;
	}

	public void setProductCollection(String productCollection) {
		this.productCollection = productCollection;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public double getGold() {
		return gold;
	}

	public void setGold(double gold) {
		this.gold = gold;
	}

	public double getSilver() {
		return silver;
	}

	public void setSilver(double silver) {
		this.silver = silver;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "Product{" + "id=" + id + ", productCollection='" + productCollection + '\'' + ", productType='"
				+ productType + '\'' + ", gold=" + gold + ", silver=" + silver + ", price=" + price + ", isDeleted="
				+ isDeleted + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
	}

}