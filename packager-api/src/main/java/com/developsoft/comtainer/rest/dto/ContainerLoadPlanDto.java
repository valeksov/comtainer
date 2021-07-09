package com.developsoft.comtainer.rest.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContainerLoadPlanDto {

	private String id;
	private long volumeUsed;
	private long volumeFree;
	private float volumeUsedInPercent;

	private long floorAreaUsed;
	private long floorAreaFree;
	private float floorAreaUsedInPercent;

	private int lengthUsed;
	private int lengthFree;
	private float lengthUsedInPercent;

	private int widthUsed;
	private int widthFree;
	private float widthUsedInPercent;

	private int heightUsed;
	private int heightFree;
	private float heightUsedInPercent;

	private float weightUsed;
	private float weightFree;
	private float weightUsedInPercent;

	private int numberOfPieces;

	private List<LoadPlanStepDto> loadPlanSteps;

	private List<CargoItemDto> items;

}
