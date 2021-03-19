package com.developsoft.comtainer.util.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.rest.dto.LoadPlanStepDto;

public class LoadPlanStepCoordinatesComparator implements Comparator<LoadPlanStepDto>{

	@Override
	public int compare(final LoadPlanStepDto o1, final LoadPlanStepDto o2) {
		int o1val = o1.getStartX() * 15 + o1.getStartY() * 30 + o1.getStartZ();
		int o2val = o2.getStartX() * 15 + o2.getStartY() * 30 + o2.getStartZ();
		
		int compareTotal = Integer.compare(o1val, o2val);
		if (compareTotal != 0) {
			return compareTotal;
		}
		
		int compareY = Integer.compare(o1.getStartY(), o2.getStartY());
		if (compareY != 0) {
			return compareY;
		}
		
		int compareX = Integer.compare(o1.getStartX(), o2.getStartX());
		if (compareX != 0) {
			return compareX;
		}
		
		return Integer.compare(o1.getStartZ(), o2.getStartZ());
	}

}
