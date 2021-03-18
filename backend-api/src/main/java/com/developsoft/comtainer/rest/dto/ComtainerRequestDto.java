package com.developsoft.comtainer.rest.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ComtainerRequestDto {
	private List<ContainerDto> containers; 
	private ConfigDto config;
	private List<CargoGroupDto> groups;

}
