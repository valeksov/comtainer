package com.developsoft.comtainer.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContainerDto {
	private String id; 
	private String name;
	private Integer length;
	private Integer width; 
	private Integer height; 
	private Long maxAllowedVolume; //in cubic centimeters
	private Double maxAllowedWeight; 
	private ContainerLoadPlanDto loadPlan;

}
