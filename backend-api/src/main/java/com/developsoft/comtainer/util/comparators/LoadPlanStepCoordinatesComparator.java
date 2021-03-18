package com.developsoft.comtainer.util.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.rest.dto.LoadPlanStepDto;

public class LoadPlanStepCoordinatesComparator implements Comparator<LoadPlanStepDto>{

	@Override
	public int compare(final LoadPlanStepDto o1, final LoadPlanStepDto o2) {
		int compareX = Integer.compare(o1.getStartX(), o2.getStartX());
		if (compareX != 0) {
			return compareX;
		}
		
		int compareY = Integer.compare(o1.getStartY(), o2.getStartY());
		if (compareY != 0) {
			return compareY;
		}
		return Integer.compare(o1.getStartZ(), o2.getStartZ());
	}

}
