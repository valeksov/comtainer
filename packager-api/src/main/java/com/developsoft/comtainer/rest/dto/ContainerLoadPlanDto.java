package com.developsoft.comtainer.rest.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContainerLoadPlanDto {

	private String id;
	private List<LoadPlanStepDto> loadPlanSteps;

}
