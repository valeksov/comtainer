package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.developsoft.comtainer.rest.dto.LoadPlanStepDto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoadPlanStepRuntime {

	private final List<CargoItemPlacementRuntime> placements;
	private Integer startX;
	private Integer startY;
	private Integer startZ;
	private final int dimension;
	
	public LoadPlanStepRuntime(final LoadPlanStepDto dto) {
		super();
		this.startX = dto.getStartX();
		this.startY = dto.getStartY();
		this.startZ = dto.getStartZ();
		this.dimension = 2;
		this.placements = dto.getItems() != null ? dto.getItems().stream().map(itemDto -> new CargoItemPlacementRuntime(itemDto)).collect(Collectors.toList()) 
												: new ArrayList<CargoItemPlacementRuntime>();
	}
	
	public LoadPlanStepRuntime(final List<CargoItemPlacementRuntime> placements, final Integer startX, final Integer startY, final Integer startZ, final int dimension) {
		super();
		this.placements = placements;
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.dimension = dimension;
		init();
	}
	
	public boolean isNotStackable() {
		for (final CargoItemPlacementRuntime placement : getPlacements()) {
			if (!placement.getItem().getSource().isStackable()) {
				return true;
			}
			if (placement.getItem().getGroup().getSource().isStackGroupOnly()) {
				return true;
			}
		}
		return false;
	}
	
	public LoadPlanStepRuntime createRotatedCopy() {
		final int reverseDimension = getDimension() % 2 + 1;
		final List<CargoItemPlacementRuntime> newPlacements = getPlacements().stream().map(next -> next.createRotatedCopy()).collect(Collectors.toList());
		return new LoadPlanStepRuntime(newPlacements, getStartX(), getStartY(), getStartZ(), reverseDimension);
	}
	
	private void init() {
		int curStartX = getStartX();
		int curStartY = getStartY();
		int curStartZ = getStartZ();
		for (final CargoItemPlacementRuntime nextPlacement : getPlacements()) {
			nextPlacement.updateCoordinates(curStartX, curStartY, curStartZ);
			switch (dimension) {
				case 1: curStartX += nextPlacement.getLength(); break;
				case 2: curStartY += nextPlacement.getWidth(); break;
				default: curStartZ += nextPlacement.getHeight();
			}
		}
	}
	
	public boolean isSameStep(final int dimension, final int dimensionSum, final int height, final int otherDimensionvalue) {
		if (getDimension() != dimension) {
			return false;
		}
		final int otherDimension = getDimension() % 2 + 1;
		if (getDimensionValue(dimension) != dimensionSum || getDimensionValue(otherDimension) != otherDimensionvalue || getHeight() != height) {
			return false;
		}
		return true;
	}
	
	public void print(final String preffix) {
		final StringBuilder strB = new StringBuilder();
		if (preffix != null) {
			strB.append(preffix);
			strB.append(" ");
		}
		strB.append("Step(");
		strB.append(getDimension());
		strB.append("), L:");
		strB.append(getLength());
		strB.append(", W:");
		strB.append(getWidth());
		strB.append(", H:");
		strB.append(getHeight());
		strB.append(". Starts At (");
		strB.append(getStartX());
		strB.append(", ");
		strB.append(getStartY());
		strB.append(", ");
		strB.append(getStartZ());
		strB.append("), Number of Items: ");
		strB.append(getPlacements().size());
		System.out.println(strB.toString());
	}
	
	public void confirm() {
		for (final CargoItemPlacementRuntime nextPlacement : getPlacements()) {
			nextPlacement.place();
		}
	}

	public void updateCoordinates(final int x, final int y, final int z) {
		setStartX(x);
		setStartY(y);
		setStartZ(z);
	}
	
	public int getArea() {
		return getLength() * getWidth();
	}
	
	public int getLength() {
		return getDimension() == 1 ? getDimensionSum() : getPlacements().get(0).getLength();
	}
	
	public int getWidth() {
		return getDimension() == 2 ? getDimensionSum() : getPlacements().get(0).getWidth();
	}
	
	public int getHeight() {
		return getDimension() == 3 ? getDimensionSum() : getPlacements().get(0).getHeight();
	}
	
	public int getDimensionValue(final int dimension) {
		return (dimension < 2) ? getLength() : (dimension < 3) ? getWidth() : getHeight();
	}
	
	
	public int getEndX() {
		return getStartX() + getLength();
	}
	
	public int getEndY() {
		return getStartY() + getWidth();
	}
	
	public int getEndZ() {
		return getStartZ() + getHeight();
	}
	
	private int getDimensionSum() {
		int result = 0;
		for (final CargoItemPlacementRuntime nextPlacement : getPlacements()) {
			result += nextPlacement.getDimensionValue(getDimension());
		}
		return result;
	}
	
	public LoadPlanStepDto toDto() {
		final LoadPlanStepDto result = new LoadPlanStepDto();
		result.setId(UUID.randomUUID().toString());
		result.setLength(getLength());
		result.setWidth(getWidth());
		result.setHeight(getHeight());
		result.setStartX(getStartX());
		result.setStartY(getStartY());
		result.setStartZ(getStartZ());
		result.setItems(getPlacements().stream().map(next -> next.toDto(this)).collect(Collectors.toList()));
		return result;
	}
	
	public float getMaxSupportingWeight(final float supportWeight) {
		final float minWeigth = getMinItemWeight();
		return minWeigth * supportWeight;
	}
	
	public float getMinItemWeight() {
		float minWeigth = Float.MAX_VALUE;
		for (final CargoItemPlacementRuntime placement : getPlacements()) {
			if (placement.getItem().getWeight() < minWeigth) {
				minWeigth = placement.getItem().getWeight();
			}
		}
		return (minWeigth < Float.MAX_VALUE) ? minWeigth : 10.0f;
	}
}
