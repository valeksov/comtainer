package com.developsoft.comtainer.util.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.rest.dto.CargoItemPlacementDto;
import com.developsoft.comtainer.rest.dto.LoadPlanStepDto;

public class LoadPlanStepWeightComparator implements Comparator<LoadPlanStepDto> {

	@Override
	public int compare(final LoadPlanStepDto o1, final LoadPlanStepDto o2) {
		return (-1) * Float.compare(calculateWeight(o1), calculateWeight(o2));
	}

	private float calculateWeight(final LoadPlanStepDto step) {
		float result = 0.0f;
		for (final CargoItemPlacementDto nextDto : step.getItems()) {
			result += nextDto.getCargo().getWeigth();
		}
		return result;
	}
}
