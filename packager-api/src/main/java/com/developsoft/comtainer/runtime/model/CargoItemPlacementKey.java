package com.developsoft.comtainer.runtime.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemPlacementKey {

	
	private final int height;
	private final int secondDimensionValue;
	
	public CargoItemPlacementKey (final CargoItemPlacementRuntime placement, int dimension) {
		super();
		this.height = placement.getHeight();
		this.secondDimensionValue = placement.getDimensionValue(dimension);
	}
	

	@Override
	public boolean equals(final Object o) {
		if (o != null && o.getClass().equals(this.getClass())) {
			final CargoItemPlacementKey other = (CargoItemPlacementKey) o;
			return Integer.compare(this.getHeight(), other.getHeight()) == 0 && Integer.compare(this.getSecondDimensionValue(), other.getSecondDimensionValue()) == 0;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final Long l = ((long)getHeight())*1000000000L + getSecondDimensionValue();
		return l.hashCode();
	}
	
}
