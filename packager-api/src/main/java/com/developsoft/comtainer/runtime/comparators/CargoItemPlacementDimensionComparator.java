package com.developsoft.comtainer.runtime.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.runtime.model.CargoItemPlacementRuntime;

public class CargoItemPlacementDimensionComparator implements Comparator<CargoItemPlacementRuntime>{

	private final int dimension;
	public CargoItemPlacementDimensionComparator (final int dimension) {
		super();
		this.dimension = dimension;
	}
	
	@Override
	public int compare(final CargoItemPlacementRuntime o1, final CargoItemPlacementRuntime o2) {
		return (-1) * Integer.compare(o1.getDimensionValue(dimension), o2.getDimensionValue(dimension));
	}
}
