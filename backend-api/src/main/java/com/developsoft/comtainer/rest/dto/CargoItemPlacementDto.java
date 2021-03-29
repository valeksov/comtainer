package com.developsoft.comtainer.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemPlacementDto {
	private CargoItemDto cargo;
	private Integer startX;
	private Integer startY;
	private Integer startZ;
	private Integer orientation; //1-6
	private Integer length;	
	private Integer width; 
	private Integer height; 

}
