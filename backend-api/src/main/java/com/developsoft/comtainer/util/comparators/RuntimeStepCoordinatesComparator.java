package com.developsoft.comtainer.util.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.runtime.model.CargoItemStepRuntime;

public class RuntimeStepCoordinatesComparator implements Comparator<CargoItemStepRuntime>{

	@Override
	public int compare(final CargoItemStepRuntime o1, final CargoItemStepRuntime o2) {
		int compareZ = Integer.compare(o1.getStartZ(), o2.getStartZ());
		if (compareZ != 0) {
			return compareZ;
		}
		int compareY = Integer.compare(o1.getStartY(), o2.getStartY());
		if (compareY != 0) {
			return compareY;
		}
		return Integer.compare(o1.getStartX(), o2.getStartX());
	}

}
