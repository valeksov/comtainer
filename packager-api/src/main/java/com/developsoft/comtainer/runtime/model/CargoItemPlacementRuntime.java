package com.developsoft.comtainer.runtime.model;

import com.developsoft.comtainer.rest.dto.CargoItemPlacementDto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemPlacementRuntime {
	private final CargoItemRuntime item;
	private final int orientation;
	
	private int startX;
	private int startY;
	private int startZ;
	private boolean placed;
	
	private int layer;
	
	public CargoItemPlacementRuntime(final CargoItemPlacementDto dto) {
		super();
		this.orientation = dto.getOrientation();
		this.startX = dto.getStartX();
		this.startY = dto.getStartY();
		this.startZ = dto.getStartZ();
		this.placed = true;
		this.item = new CargoItemRuntime(dto.getCargo(), null);
	}
	
	public CargoItemPlacementRuntime(final CargoItemRuntime item, final int orientation) {
		super();
		this.item = item;
		this.orientation = orientation;
		this.startX = 0;
		this.startY = 0;
		this.startZ = 0;
		this.placed = false;
	}
	
	public CargoItemPlacementRuntime createRotatedCopy() {
		final int correction = getOrientation() % 2 == 1 ? 1 : -1;
		final int newOrientation = getOrientation() + correction;
		return new CargoItemPlacementRuntime(getItem(), newOrientation);
	}
	
	public void print() {
		final StringBuilder strB = new StringBuilder();
		strB.append("Placement for ");
		strB.append(getItem().getSource().getId());
		strB.append(", Orientation: ");
		strB.append(getOrientation());
		strB.append(", L:");
		strB.append(getLength());
		strB.append(", W:");
		strB.append(getWidth());
		strB.append(", H:");
		strB.append(getHeight());
		strB.append(" on coordinates X=");
		strB.append(getStartX());
		strB.append(", Y=");
		strB.append(getStartY());
		strB.append(", Z=");
		strB.append(getStartZ());
		System.out.println(strB.toString());
	}
	
	public boolean place(final int layer) {
		setLayer(layer);
		boolean result = getItem().markUsed(1);
		if (result) {
			this.placed = true;
		}
		return result;
	}

	public void resetCoordinates() {
		setStartX(0);
		setStartY(0);
		setStartZ(0);
	}
	
	public void updateCoordinates(final int x, final int y, final int z) {
		setStartX(getStartX() + x);
		setStartY(getStartY() + y);
		setStartZ(getStartZ() + z);
	}
	
	//1 - length; 2 - width; 3 - height
	public int getDimensionValue(final int dimension) {
		return (dimension < 2) ? getLength() : (dimension < 3) ? getWidth() : getHeight();
	}
	
	public int getLength() {
		return (getOrientation() <= 3 && getOrientation() % 2 == 1) ? getItem().getLength() 
									: (getOrientation() > 3 && getOrientation() % 2 == 0) ? getItem().getHeight() : getItem().getWidth();
	}
	
	public int getWidth() {
		return (getOrientation() < 6 && getOrientation() % 2 == 0) ? getItem().getLength() 
									: (getOrientation() > 1 && getOrientation() % 2 == 1) ? getItem().getHeight() : getItem().getWidth();
	}
	
	public int getHeight() {
		return (getOrientation() < 3) ? getItem().getHeight() : (getOrientation() < 5) ? getItem().getWidth() : getItem().getLength();
	}
	
	public CargoItemPlacementDto toDto(final LoadPlanStepRuntime parentStep) {
		final CargoItemPlacementDto result = new CargoItemPlacementDto();
		result.setCargo(getItem().getSource());
		result.setLength(getLength());
		result.setWidth(getWidth());
		result.setHeight(getHeight());
		result.setOrientation(getOrientation());
		result.setStartX(parentStep.getStartX() + getStartX());
		result.setStartY(parentStep.getStartY() + getStartY());
		result.setStartZ(parentStep.getStartZ() + getStartZ());
		//TBD Color
		if (getItem().getGroup() != null) {
			result.setColor(getItem().getGroup().getSource().getColor());
		}
		return result;
	}
}
