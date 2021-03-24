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
		int compareX = Integer.compare(o1.getStartX(), o2.getStartX());
		if (compareX != 0) {
			return compareX;
		}
		return Integer.compare(o1.getStartY(), o2.getStartY());
	}

}
