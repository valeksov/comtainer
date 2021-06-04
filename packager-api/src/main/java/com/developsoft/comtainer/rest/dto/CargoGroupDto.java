package com.developsoft.comtainer.rest.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoGroupDto {
	private String id; 
	private String name;
	private List<CargoItemDto> items;
	private String color;
	private boolean stackGroupOnly; //derived from: H)	 3rd  sub property for unstackable goods Group of unstackable goods can be put on each other only but other goods cannot put on this group of this goods
	private boolean alreadyLoaded;
	private List<CargoGroupDto> groups;
}
