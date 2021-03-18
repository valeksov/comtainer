package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemPlacementRow {

	private final List<CargoItemPlacementRuntime> placements;
	
	public CargoItemPlacementRow() {
		super();
		this.placements = new ArrayList<CargoItemPlacementRuntime>();
	}
	
	public void add(final CargoItemPlacementRuntime placement) {
		final int startZ = 0;
		final int startY = 0;
		final int startX = this.placements.size() == 0 ? 0 : getLength();
		placement.setStartX(startX);
		placement.setStartY(startY);
		placement.setStartZ(startZ);
		final int width = getWidth();
		if (width > 0 && width != placement.getWidth()) {
			placement.rotate();
		}
		this.placements.add(placement);
	}
	
	public int getLength() {
		int result = 0;
		for (final CargoItemPlacementRuntime next : this.placements) {
			result += next.getLength();
		}
		return result;
	}
	
	public int getHeight() {
		return this.placements.size() == 0 ? 0 : this.placements.get(0).getHeight(); 
	}

	public int getWidth() {
		return this.placements.size() == 0 ? 0 : this.placements.get(0).getWidth(); 
	}
	
}
