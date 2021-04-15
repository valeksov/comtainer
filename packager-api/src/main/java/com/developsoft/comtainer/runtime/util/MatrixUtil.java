package com.developsoft.comtainer.runtime.util;

import java.util.ArrayList;
import java.util.List;

import com.developsoft.comtainer.runtime.model.ContainerAreaRuntime;
import com.developsoft.comtainer.runtime.model.LoadPlanStepRuntime;

public class MatrixUtil {
	public static void main(final String[] args) {
	}
	

	public static ContainerAreaRuntime getFreeArea (final List<LoadPlanStepRuntime> steps, final ContainerAreaRuntime source, final float weight, final float supportWeight, 
			final int x, final int y, final int z, final int length, final int width, final int height, final boolean skipZDImension, final boolean skipCargoSupport, final boolean skipStackable) {
		
		final int endX = x + length;
		final int endY = y + width;
		final int endZ = z + height;
		//Check if we reached one of the ends of the initial source area (Container)
		if (endX > source.getMaxLength() || endY > source.getMaxWidth() || endZ > source.getMaxHeight()) {
			return null;
		}
		
		final LoadPlanStepRuntime intersectingStep = findIntersection(steps, x, y, z, endX, endY, endZ);
		if (intersectingStep == null) {
			//The area is Free, but we need to check if bottom can support it. This check is relevant only if z > 0 (free area is not on the floor)
			if (z > 0) {
				if (!checkCargoSupport(steps, source.getCargoSupport(), weight, supportWeight, x, y, z-1, length, width, skipCargoSupport, skipStackable)) {
					return null;
				}
			}
			final ContainerAreaRuntime area = new ContainerAreaRuntime();
			area.setStartX(x);
			area.setStartY(y);
			area.setStartZ(z);
			return area;
		} else {
			ContainerAreaRuntime nextArea;
			if (!skipZDImension) {
				//1. Search for free area on top of the package
				nextArea = getFreeArea(steps, source, weight, supportWeight, x, y, intersectingStep.getEndZ(), length, width, height, skipZDImension, skipCargoSupport, skipStackable);
				if (nextArea != null) {
					return nextArea;
				}
			}
			//2.Search next following the width
			nextArea = getFreeArea(steps, source, weight, supportWeight, x, intersectingStep.getEndY(), z, length, width, height, skipZDImension, skipCargoSupport, skipStackable);
			if (nextArea != null) {
				return nextArea;
			}
			//3.Go next following the length
			nextArea = getFreeArea(steps, source, weight, supportWeight, intersectingStep.getEndX(), y,  z, length, width, height, skipZDImension, skipCargoSupport, skipStackable);
			if (nextArea != null) {
				return nextArea;
			}
		}
		return null;
	}
	
	private static boolean checkStepsSupport (final List<LoadPlanStepRuntime> intersectingSteps, final int cargoSupport, final float weight, final float supportWeight,
								final int x, final int y, final int length, final int width, final boolean skipCargoSupport, final boolean skipStackable) {
		final int targetSupportArea = skipCargoSupport ? length * width : Math.round(((float)cargoSupport) * ((float)length) * ((float)width) / 100);
		int currentSupportArea = 0;
		for (final LoadPlanStepRuntime step : intersectingSteps) {
			if (step.getMaxSupportingWeight(supportWeight) < weight) {
				return false;
			}
			if (!skipStackable && step.isNotStackable()) {
				return false;
			}
			final int startX = Math.max(step.getStartX(), x);
			final int startY = Math.max(step.getStartY(), y);
			final int endX = Math.min(step.getEndX(), x + length);
			final int endY = Math.min(step.getEndY(), y + width);
			currentSupportArea += (endX - startX) * (endY - startY);
		}
		return currentSupportArea >= targetSupportArea;
	}
	
	private static boolean checkCargoSupport (final List<LoadPlanStepRuntime> steps, final int cargoSupport, final float weight, final float supportWeight, 
													final int x, final int y, final int z, final int length, final int width, final boolean skipCargoSupport, final boolean skipStackable) {
		
		final List<LoadPlanStepRuntime> intersectingSteps = findAllIntersections(steps, x, y, z-1, x + length, y + width, z);
		return checkStepsSupport(intersectingSteps, cargoSupport, weight, supportWeight, x, y, length, width, skipCargoSupport, skipStackable);
	}
	
	private static List<LoadPlanStepRuntime> findAllIntersections(final List<LoadPlanStepRuntime> steps, final int x, final int y, final int z, final int endX, final int endY, final int endZ) {
		final List<LoadPlanStepRuntime> result = new ArrayList<LoadPlanStepRuntime>();
		if (steps != null && steps.size() > 0) {
			for (final LoadPlanStepRuntime step : steps) {
				if (stepIntersects(step, x, y, z, endX, endY, endZ)) {
					result.add(step);
				}
			}
		}
		return result;
	}
	
	public static LoadPlanStepRuntime findIntersection(final List<LoadPlanStepRuntime> steps, final int x, final int y, final int z, final int endX, final int endY, final int endZ) {
		final List<LoadPlanStepRuntime> result = findAllIntersections(steps, x, y, z, endX, endY, endZ);
		return result.size() > 0 ? result.get(0) : null;
	}
	
	private static boolean stepIntersects (final LoadPlanStepRuntime step, final int startX, final int startY, final int startZ, final int endX, final int endY, final int endZ) {
		if (startX >= step.getEndX() || step.getStartX() >= endX) {
			return false;
		}
		if (startY >= step.getEndY() || step.getStartY() >= endY) {
			return false;
		}
		if (startZ >= step.getEndZ() || step.getStartZ() >= endZ) {
			return false;
		}
		return true;

	}

	
	
	
}
