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
	private Float maxAllowedWeight; 
	private ContainerLoadPlanDto loadPlan;

	public ContainerDto() {
		super();
	}
	
	public ContainerDto(final ContainerDto source, final int index) {
		this();
		copyProperties(source, index);
	}
	
	private void copyProperties(final ContainerDto source, final int index) {
		setId(source.getId() + " " + index);
		setName(source.getName() + " " + index);
		setLength(source.getLength());
		setWidth(source.getWidth());
		setHeight(source.getHeight());
		setMaxAllowedVolume(source.getMaxAllowedVolume());
		setMaxAllowedWeight(source.getMaxAllowedWeight());
	}
}
