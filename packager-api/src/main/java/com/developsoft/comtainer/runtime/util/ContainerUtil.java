package com.developsoft.comtainer.runtime.util;

import com.developsoft.comtainer.rest.dto.ConfigDto;
import com.developsoft.comtainer.rest.dto.ContainerDto;
import com.developsoft.comtainer.runtime.model.ContainerAreaRuntime;

public class ContainerUtil {

	
	public static void main(final String[] args) {
	}
	
	public static ContainerAreaRuntime createContainerArea (final ContainerDto container, final ConfigDto config) {
		final int cargoSupport = config != null && config.getCargoSupport() != null ? config.getCargoSupport() : 100;
		return createContainerArea(container.getLength(), container.getWidth(), container.getHeight(), cargoSupport, container.getMaxAllowedWeight());
	}	
	
	public static ContainerAreaRuntime createContainerArea (final int length, final int width, final int height, final int cargoSupport, final Float maxWeight) {
		final ContainerAreaRuntime area = new ContainerAreaRuntime();
		area.setCargoSupport(cargoSupport);
		area.setCheckAllArea(true);
		area.setFixedHeight(false);
		area.setFixedLength(false);
		area.setFixedWidth(false);
		area.setMaxHeight(height);
		area.setMaxLength(length);
		area.setMaxWidth(width);
		area.setMaxWeight(maxWeight);
		area.setStartX(0);
		area.setStartY(0);
		area.setStartZ(0);
		area.setTargetDimension(2);
		return area;
	}
/*	
	public static void fillArea (final List<CargoItemRuntime> items, final ContainerAreaRuntime source, final boolean fillFullLength) {
		final LoadPlanStepRuntime mainStep = RuntimeUtil.createStep(items, source);
		final int sumDimension = source.getTargetDimension() % 2 + 1;
		if (mainStep != null) {
			mainStep.print("Main");
			mainStep.confirm();
			source.addStep(mainStep);
			if (isTerminalDimensionValue(mainStep.getDimensionValue(source.getTargetDimension()), source.getDimensionValue(source.getTargetDimension()))) {
				if (!fillFullLength || isTerminalDimensionValue(mainStep.getDimensionValue(sumDimension), source.getDimensionValue(sumDimension))) {
					return;
				}
			}
			int usedLength = mainStep.getDimensionValue(sumDimension);
			if (!source.isFixedLength()) {
				if (fillFullLength) {
					usedLength = attempNextSteps(items, source, source, mainStep, usedLength);
				}
				if (source.getDimensionValue(source.getTargetDimension()) > mainStep.getDimensionValue(source.getTargetDimension())) {
					final int remainingWidth = source.getDimensionValue(source.getTargetDimension()) - mainStep.getDimensionValue(source.getTargetDimension());
					//Step with remainingWidth and length <= mainStep.length with cargo support offset
					final int maxLength = Math.max(usedLength, getCorrectedWithCargoSupportLength(mainStep.getDimensionValue(sumDimension), source.getCargoSupport()));
					System.out.println ("Corrected Max Length "+maxLength);
					final ContainerAreaRuntime nextArea = source.clone();
					final int nextAreaStartX = source.getTargetDimension() == 2 ? source.getStartX() : source.getStartX() + mainStep.getLength();
					final int nextAreaStartY = source.getTargetDimension() == 1 ? source.getStartY() : source.getStartY() + mainStep.getWidth();
					final int nextAreaMaxWidth = source.getTargetDimension() == 2 ? remainingWidth : maxLength;
					final int nextAreaMaxLength = source.getTargetDimension() == 1 ? remainingWidth : maxLength;
					nextArea.setStartX(nextAreaStartX);
					nextArea.setStartY(nextAreaStartY);
					nextArea.setMaxWidth(nextAreaMaxWidth);
					nextArea.setMaxLength(nextAreaMaxLength);
					nextArea.setFixedHeight(true);
					nextArea.setMaxHeight(mainStep.getHeight());
					final LoadPlanStepRuntime nextStep = RuntimeUtil.createStep(items, nextArea);
					if (nextStep != null && isTerminalDimensionValue(nextStep.getDimensionValue(nextArea.getTargetDimension()), remainingWidth) &&
											isTerminalDimensionValue(nextStep.getDimensionValue(sumDimension), usedLength)) {
							nextStep.print("Next Terminal");
							nextStep.confirm();
							source.addStep(nextStep);
							return;
					}
					
					final ContainerAreaRuntime nextAreaReversed = nextArea.clone();
					nextAreaReversed.setTargetDimension(sumDimension);
					final int nextAreaReversedMaxWidth = source.getTargetDimension() == 2 ? remainingWidth : maxLength;
					final int nextAreaReversedMaxLength = source.getTargetDimension() == 1 ? remainingWidth : maxLength;
					nextAreaReversed.setMaxWidth(nextAreaReversedMaxWidth);
					nextAreaReversed.setMaxLength(nextAreaReversedMaxLength);
					final LoadPlanStepRuntime nextStepReversed = RuntimeUtil.createStep(items, nextAreaReversed);
					if (nextStepReversed != null && isTerminalDimensionValue(nextStepReversed.getDimensionValue(source.getTargetDimension()), remainingWidth) &&
													isTerminalDimensionValue(nextStepReversed.getDimensionValue(sumDimension), usedLength)) {
						nextStepReversed.print("Next Reversed Terminal");
						nextStepReversed.confirm();
						source.addStep(nextStepReversed);
						return;
					}
					if (!fillFullLength) {
						final LoadPlanStepRuntime selectedStep = selectBetterStep(nextStep, nextStepReversed);
						if (selectedStep != null) {
							selectedStep.print("Selected");
							selectedStep.confirm();
							source.addStep(selectedStep);
						}
					} else {
						if (nextStep != null) {
							nextStep.print("Next");
							nextStep.confirm();
							source.addStep(nextStep);
							usedLength = nextStep.getDimensionValue(sumDimension);
							attempNextSteps(items, source, nextArea, nextStep, usedLength);
						}
					}
					
				}
			
			}
		}
	}
	private static LoadPlanStepRuntime selectBetterStep (final LoadPlanStepRuntime step1, final LoadPlanStepRuntime step2) {
		if (step1 != null && step2 == null) {
			return step1;
		}
		if (step2 != null && step1 == null) {
			return step2;
		}
		if (step1 != null && step2 != null) {
			return step2.getArea() > step1.getArea() ? step2 : step1;
		}
		return null;
	}
	
	private static int attempNextSteps(final List<CargoItemRuntime> items, final ContainerAreaRuntime source, final ContainerAreaRuntime initialArea, 
																			final LoadPlanStepRuntime mainStep, final int usedLength) {
		final int sumDimension = source.getTargetDimension() % 2 + 1;
		int result = usedLength;
		int remainingLength = initialArea.getDimensionValue(sumDimension) - mainStep.getDimensionValue(sumDimension);
		ContainerAreaRuntime prevArea = initialArea;
		while (remainingLength > 0) {
			final ContainerAreaRuntime nextArea = findNextStep(items, prevArea, mainStep, remainingLength);
			if (nextArea == null) {
				break;
			}
			final LoadPlanStepRuntime nextStep = nextArea.getSteps().get(0);
			nextStep.confirm();
			source.addStep(nextStep);
			prevArea = nextArea;
			remainingLength -= nextStep.getDimensionValue(sumDimension);
			result += nextStep.getDimensionValue(sumDimension);
		}
		return result;
	}
	
	private static boolean isTerminalDimensionValue (final int value, final int total) {
		final int correctedTotal = Math.round(DEFAULT_DIMENSION_OFFSET * total);
		return value >= correctedTotal;
	}
	
	private static int getCorrectedWithCargoSupportLength (final int length, final int cargoSupport) {
		int result = length;
		if (cargoSupport > 0) {
			result += Math.round((100.0f - cargoSupport) * length / 100.0f);
		}
		return result;
	}
		
	private static void correctAreaProperties(final ContainerAreaRuntime nextArea, final int dimension, final int remainingValue, final int startX, final int startY,
																					final int length, final int width) {
		switch (dimension) {
			case 1:
				nextArea.setMaxWidth(remainingValue);
				nextArea.setMaxLength(length);
				nextArea.setStartY(startY + width);
			case 2:
				nextArea.setMaxLength(remainingValue);
				nextArea.setMaxWidth(width);
				nextArea.setStartX(startX + length);
			default:	
		}
	}

	private static ContainerAreaRuntime findNextStep (final List<CargoItemRuntime> items, final ContainerAreaRuntime source, final LoadPlanStepRuntime mainStep, final int remainingValue) {
		final int sumDimension = source.getTargetDimension() % 2 + 1;
		final ContainerAreaRuntime nextArea = source.clone();
		correctAreaProperties(nextArea, source.getTargetDimension(), remainingValue, source.getStartX(), source.getStartY(), mainStep.getLength(), mainStep.getWidth());
		nextArea.setCheckAllArea(false);
		//Try to find step with same height
		final ContainerAreaRuntime sameHeightArea = nextArea.clone();
		sameHeightArea.setFixedHeight(true);
		sameHeightArea.setMaxHeight(mainStep.getHeight());
		final LoadPlanStepRuntime sameHeightStep = RuntimeUtil.createStep(items, sameHeightArea);
		if (sameHeightStep != null) {
			if (sameHeightStep.getDimensionValue(sumDimension) == mainStep.getDimensionValue(sumDimension)) {
				nextArea.addStep(sameHeightStep);
				return nextArea;
			}
		}
		//We will check if there is step with different height that would be a better match
		final LoadPlanStepRuntime otherHeightStep = RuntimeUtil.createStep(items, nextArea);
		if (otherHeightStep != null) {
			if (otherHeightStep.getDimensionValue(sumDimension) == mainStep.getDimensionValue(sumDimension)) {
				return null;
			}
			if (sameHeightStep != null && (((float)mainStep.getDimensionValue(sumDimension)) * DEFAULT_DIMENSION_OFFSET < sameHeightStep.getDimensionValue(sumDimension) || 
												sameHeightStep.getDimensionValue(sumDimension) >= otherHeightStep.getDimensionValue(sumDimension))) {
				nextArea.addStep(sameHeightStep);
				return nextArea;
			}
		}
		return null;
	}
*/	
}
