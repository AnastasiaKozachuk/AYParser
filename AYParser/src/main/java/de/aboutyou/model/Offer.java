package de.aboutyou.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Offer {
	
	private String name;
	private String brand;
	private String color;
	private String shop_size;
	private String vendor_size;
	private String price;
	private String initialPrice;
	private String description;
	private String articleID;
	private final String shippingCosts = "0";
	
}
