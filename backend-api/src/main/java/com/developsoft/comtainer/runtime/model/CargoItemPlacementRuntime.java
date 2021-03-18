package com.developsoft.comtainer.runtime.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemPlacementRuntime {

	private final CargoItemRuntime item;
	private int orientation;
	private Integer startX;
	private Integer startY;
	private Integer startZ;
	private boolean rotated;
	
	public CargoItemPlacementRuntime(final CargoItemRuntime item) {
		super();
		this.item = item;
		this.orientation = item.getOrientation();
	}

	public CargoItemPlacementRuntime(final CargoItemRuntime item, final boolean rotate) {
		this(item);
		if (rotate) {
			rotate();
		}
	}
	public void rotate() {
		final int correction = this.orientation % 2 == 1 ? 1 : -1;
		this.orientation += correction;
		this.rotated = !this.rotated;
	}
	
	public int getHeight() {
		return this.item.getHeight();
	}
	
	public int getLength() {
		return this.orientation % 2 == 1 ? this.item.getLength() : this.item.getWidth();
	}
	
	public int getWidth() {
		return this.orientation % 2 == 1 ? this.item.getWidth() : this.item.getLength();
	}
	
	public CargoItemPlacementRuntime clone() {
		final CargoItemPlacementRuntime newPlacement = new CargoItemPlacementRuntime(item);
		newPlacement.setOrientation(this.getOrientation());
		newPlacement.setRotated(this.isRotated());
		return newPlacement;
	}
	
}
