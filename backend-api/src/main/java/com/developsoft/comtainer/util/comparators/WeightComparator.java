package com.developsoft.comtainer.util.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.runtime.model.CargoItemRuntime;

public class WeightComparator implements Comparator<CargoItemRuntime>{

	@Override
	public int compare(final CargoItemRuntime o1, final CargoItemRuntime o2) {
		return (-1) * Double.compare(o1.getSource().getWeigth(), o2.getSource().getWeigth());
	}

}
