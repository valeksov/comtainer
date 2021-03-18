package com.developsoft.comtainer.rest.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoadPlanStepDto {
	private String id;
	private List<CargoItemPlacementDto> items; //if one item has quantity>0, we will have one instance for each piece, since each piece would have different coordinates
	private Integer startX; //the coordinate of the first item in the step
	private Integer startY;
	private Integer startZ;
	private Integer length;	//total length
	private Integer width; //total width
	private Integer height; //total height

}
