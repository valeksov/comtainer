package com.developsoft.comtainer.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemDto {
	private String id;
	private String name;
	private Integer length;	//in centimeters
	private Integer width; //in centimeters
	private Integer height; //in centimeters
	private Float weigth; //in killograms
	private Integer quantity; 
	private Integer cargoStyle; //A number to present the style of the cargo. Enum: 0 = Carton; 1 = Pallet
	private boolean rotatable; //if true, allows all orientations, if false, it will allow on his axis only
	private boolean stackable; 
	private boolean selfStackable; 

}
