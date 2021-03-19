package com.developsoft.comtainer.util.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.runtime.model.CargoItemStepRuntime;

public class RuntimeStepAreaComparator implements Comparator<CargoItemStepRuntime>{

	@Override
	public int compare(final CargoItemStepRuntime o1, final CargoItemStepRuntime o2) {
		return (-1) * Integer.compare(o1.getArea(), o2.getArea());
	}

}
