package edu.sru.cpsc.webshopping.domain.products;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;
@Column(name = "name")
private String name;

@Column(name = "category")
private String category;

@Column(name = "product_price")
private Long productPrice;

@Column(name = "image_name")
private String imageName;
@Column(name = "view_count")
private int viewCount = 0;

public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getCategory() {
	return category;
}
public void setCategory(String category) {
	this.category = category;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getImageName() {
	return imageName;
}
public void setImageName(String imageName) {
	this.imageName = imageName;
}
public Long getProductPrice() {
	return productPrice;
}
public void setProductPrice(Long productPrice) {
	this.productPrice = productPrice;
}
public int getViewCount() {
	return viewCount;
}
public void setViewCount(int viewCount) {
	this.viewCount = viewCount;
}




}
