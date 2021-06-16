package com.developsoft.comtainer.runtime.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.developsoft.comtainer.rest.dto.CargoGroupDto;
import com.developsoft.comtainer.rest.dto.ConfigDto;
import com.developsoft.comtainer.runtime.comparators.CargoItemPlacementDimensionComparator;
import com.developsoft.comtainer.runtime.model.CargoGroupRuntime;
import com.developsoft.comtainer.runtime.model.CargoItemPlacementKey;
import com.developsoft.comtainer.runtime.model.CargoItemPlacementListElement;
import com.developsoft.comtainer.runtime.model.CargoItemPlacementRuntime;
import com.developsoft.comtainer.runtime.model.CargoItemRuntime;
import com.developsoft.comtainer.runtime.model.ContainerAreaRuntime;
import com.developsoft.comtainer.runtime.model.LoadPlanStepRuntime;

public class RuntimeUtil {

	public static List<CargoGroupRuntime> createRuntimeGroups (final List<CargoGroupDto> groups) {
		return groups.stream().map(source -> new CargoGroupRuntime(source)).collect(Collectors.toList());
	
	}
	public static List<CargoItemRuntime> createRuntimeItems (final List<CargoGroupRuntime> groups) {
		final List<CargoItemRuntime> result = new ArrayList<CargoItemRuntime>();
		groups.forEach(source -> result.addAll(source.getItems()));
		return result;
	}
	
	public static boolean calculateSkipZ (final CargoItemRuntime item, final float lightUnstackableWeightLimit) {
		if (item.getGroup().getSource().isStackGroupOnly()) {
			return true;
		}
		if (!item.getSource().isStackable()) {
			if (item.getSource().isSelfStackable()) {
				return true;
			}
			if (item.getWeight() > lightUnstackableWeightLimit) {
				return true;
			}
		}
		return false;
	}
	private static LoadPlanStepRuntime confirmNewStep (final CargoItemPlacementRuntime placement, final int startX, final int startY, final int startZ, final int layer) {
		final List<CargoItemPlacementRuntime> placements = new ArrayList<CargoItemPlacementRuntime>();
		placements.add(placement);
		final LoadPlanStepRuntime step = new LoadPlanStepRuntime(placements, 0, 0, 0, 2);
		step.confirm(layer);
		step.updateCoordinates(startX, startY, startZ);
//		System.out.println("Placing single item on X=" + startX + ", Y=" + startY + ", Z="+startZ);
//		placement.print();
		return step;
	}
	
	private static LoadPlanStepRuntime createSelfStackableStepInternal (final List<LoadPlanStepRuntime> placedSteps, final CargoItemRuntime item, 
											final ContainerAreaRuntime source, final ConfigDto config) {
		final List<CargoItemPlacementRuntime> itemPlacements = 	item.createPlacements(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false, false, 1);
		final int maxLayer = item.getSource().getMaxLayer() != null ? item.getSource().getMaxLayer() : 0;
		for (final CargoItemPlacementRuntime placement : itemPlacements) {
			final List<CargoItemPlacementRuntime> stepPlacements = new ArrayList<CargoItemPlacementRuntime>();
			int itemsNum = 0;
			int curHeight = 0;
			final int maxStackItems = maxLayer > 0 ? Math.min(maxLayer, item.getRemainingQuantity()) : item.getRemainingQuantity();
			while (itemsNum < maxStackItems && curHeight + placement.getHeight() < source.getMaxHeight()) {
				final CargoItemPlacementRuntime stepPlacement = new CargoItemPlacementRuntime(placement.getItem(), placement.getOrientation());
				stepPlacements.add(stepPlacement);
				itemsNum++;
				curHeight += placement.getHeight();
			}
			final LoadPlanStepRuntime step = new LoadPlanStepRuntime(stepPlacements, 0, 0, 0, 3);
			final float minWeight = placement.getItem().getWeight();
			final int length = step.getLength();
			final int width = step.getWidth();
			final int height = step.getHeight();
			final ContainerAreaRuntime area = MatrixUtil.getFreeArea(placedSteps, source, minWeight , 1.08f, step.getWeight(), config.isAllowHeavierCargoOnTop(), 
																	config.getMaxHeavierCargoOnTop(), maxLayer, 0, 0, 0, length, width, height, true, true, true, item.getGroup());
			if (area != null) {
//				System.out.println ("Found Area: X=" + area.getStartX() + ", Y=" + area.getStartY() + ", Z=" + area.getStartZ());
				return confirmStep(step, area, placedSteps);
			}
		}
		return null;
	}
	
	public static void createSelfStackableStep (final List<LoadPlanStepRuntime> placedSteps, final CargoItemRuntime item, 
											final ContainerAreaRuntime source, final ConfigDto config) {
		while (item.getRemainingQuantity() > 1) {
			final LoadPlanStepRuntime newStep = createSelfStackableStepInternal(placedSteps, item, source, config);
			if (newStep == null) {
					break;
			}
		}
	}

	public static LoadPlanStepRuntime confirmStep(final LoadPlanStepRuntime step, final ContainerAreaRuntime area, final List<LoadPlanStepRuntime> placedSteps) {
		step.confirm(area.getLayer());
		step.updateCoordinates(area.getStartX(), area.getStartY(), area.getStartZ());
		placedSteps.add(step);
		return step;
	}
	
	public static LoadPlanStepRuntime createStep (final List<LoadPlanStepRuntime> steps, final CargoItemRuntime item, final ContainerAreaRuntime source, final ConfigDto config) {
		final List<CargoItemPlacementRuntime> itemPlacements = 	item.createPlacements(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false, false, item.getRemainingQuantity());
		final int maxLayer = item.getSource().getMaxLayer() != null ? item.getSource().getMaxLayer() : 0;
		for (final CargoItemPlacementRuntime placement : itemPlacements) {
			final int length = placement.getLength();
			final int width = placement.getWidth();
			final int height = placement.getHeight();
			//final boolean skipZ = calculateSkipZ(item, config.getLightUnstackableWeightLimit());
			final boolean skipStackable = item.getWeight() <= config.getLightUnstackableWeightLimit();
			final ContainerAreaRuntime area = MatrixUtil.getFreeArea(steps, source, item.getWeight(), 1.08f, placement.getItem().getWeight(), config.isAllowHeavierCargoOnTop(),  
														config.getMaxHeavierCargoOnTop(), maxLayer, 0, 0, 0, length, width, height, false, false, skipStackable, item.getGroup());
			if (area != null) {
				return confirmNewStep(placement, area.getStartX(), area.getStartY(), area.getStartZ(), area.getLayer());
			}
		}
	
		if (!item.isPlaced()) {
			if (!item.getSource().isStackable()) {
				if (item.getSource().isSelfStackable()) {
					for (final CargoItemPlacementRuntime placement : itemPlacements) {
						final List<LoadPlanStepRuntime> samePlacementSteps = findSamePlacements(steps, placement);
						if (samePlacementSteps.size() > 0) {
							for (final LoadPlanStepRuntime nextStep : samePlacementSteps) {
								for (final CargoItemPlacementRuntime nextSamePlacement : nextStep.getPlacements()) {
									if (maxLayer > 0 && nextSamePlacement.getLayer() < maxLayer &&
										placement.getItem().getSource().getId().equals(nextSamePlacement.getItem().getSource().getId())) {
										if (placement.getOrientation() == nextSamePlacement.getOrientation()) {
											final int startX = nextStep.getStartX() + nextSamePlacement.getStartX();
											final int startY = nextStep.getStartY() + nextSamePlacement.getStartY();
											final int startZ = nextStep.getStartZ() + nextSamePlacement.getStartZ() + nextSamePlacement.getHeight();
											final int endX = startX + placement.getLength();
											final int endY = startY + placement.getWidth();
											final int endZ = startZ + placement.getHeight();
											final float usedWeight = MatrixUtil.getUsedWeight(steps);
											final Float containerMaxWeight = source.getMaxWeight();
											if (containerMaxWeight != null && containerMaxWeight > 0 && placement.getItem().getWeight() + usedWeight > containerMaxWeight) {
												return null;
											}

											if (endZ < source.getMaxHeight() && MatrixUtil.findIntersection(steps, startX, startY, startZ, endX, endY, endZ) == null) {
												return confirmNewStep(placement, startX, startY, startZ, nextSamePlacement.getLayer() + 1);
											}
										}
									}
								}
							}
						}
					}
				}
			} else if (item.getGroup().getSource().isStackGroupOnly()) {
				for (final CargoItemPlacementRuntime placement : itemPlacements) {
					final List<LoadPlanStepRuntime> sameGroupPlacementSteps = findSameGroupPlacements(steps, placement);
					if (sameGroupPlacementSteps.size() > 0) {
						for (final LoadPlanStepRuntime nextStep : sameGroupPlacementSteps) {
							for (final CargoItemPlacementRuntime nextSamePlacement : nextStep.getPlacements()) {
								if (placement.getItem().getGroup().getSource().getId().equals(nextSamePlacement.getItem().getGroup().getSource().getId())) {
									if (placement.getLength() <= nextSamePlacement.getLength() && placement.getWidth() <= nextSamePlacement.getWidth()) {
										final int startX = nextStep.getStartX() + nextSamePlacement.getStartX();
										final int startY = nextStep.getStartY() + nextSamePlacement.getStartY();
										final int startZ = nextStep.getStartZ() + nextSamePlacement.getStartZ() + nextSamePlacement.getHeight();
										final int length = placement.getLength();
										final int width = placement.getWidth();
										final int height = placement.getHeight();
										final ContainerAreaRuntime area = 
											MatrixUtil.getFreeArea(steps, source, item.getWeight(), 1.08f, placement.getItem().getWeight(), config.isAllowHeavierCargoOnTop(), 
																			config.getMaxHeavierCargoOnTop(), maxLayer, startX, startY, startZ, length, width, height, true, true, true, null);
										if (area != null) {
											return confirmNewStep(placement, area.getStartX(), area.getStartY(), area.getStartZ(), area.getLayer());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	
	private static List<LoadPlanStepRuntime> findSamePlacements (final List<LoadPlanStepRuntime> steps, final CargoItemPlacementRuntime placement) {
		final List<LoadPlanStepRuntime> result = new ArrayList<LoadPlanStepRuntime>();
		for (final LoadPlanStepRuntime step : steps) {
			for (final CargoItemPlacementRuntime stepPlacement : step.getPlacements()) {
				if (placement.getItem().getSource().getId().equals(stepPlacement.getItem().getSource().getId())) {
					if (placement.getOrientation() == stepPlacement.getOrientation()) {
						result.add(step);
						break;
					}
				}
			}
		}
		return result;
	}
	
	private static List<LoadPlanStepRuntime> findSameGroupPlacements (final List<LoadPlanStepRuntime> steps, final CargoItemPlacementRuntime placement) {
		final List<LoadPlanStepRuntime> result = new ArrayList<LoadPlanStepRuntime>();
		for (final LoadPlanStepRuntime step : steps) {
			for (final CargoItemPlacementRuntime stepPlacement : step.getPlacements()) {
				if (placement.getItem().getGroup().getSource().getId().equals(stepPlacement.getItem().getGroup().getSource().getId())) {
					result.add(step);
					break;
				}
			}
		}
		return result;
	}
	
	private static boolean itemQualifyForMatch (final CargoItemRuntime item, final ContainerAreaRuntime area) {
		if (!item.getSource().isStackable() && item.getSource().isSelfStackable()) {
			return false;
		}
		return area.getMaxWeight() ==null || area.getMaxWeight() == 0 || item.getWeight() <= area.getMaxWeight();
	}
	
	public static LoadPlanStepRuntime createStep (final List<CargoItemRuntime> items, final ContainerAreaRuntime area, final int targetSum) {
		final int targetDimension = area.getTargetDimension();
//		final int otherDimension = area.getTargetDimension() % 2 + 1;
		//Step 1: Filter Items heavier than maxWeight
		final List<CargoItemRuntime> availableItems = items.stream()
													.filter(item -> itemQualifyForMatch(item, area))
													.collect(Collectors.toList());
//		System.out.println("Searching Placements For Target ("+targetDimension + ") - "+area.getDimensionValue(targetDimension) + "x" + area.getDimensionValue(otherDimension));
//		availableItems.forEach(item -> item.print(null));
		//Step 2: Create Placement Runtimes with all available rotations and dimensions not exceeding maxLength, maxWidth and maxHeight
		final List<CargoItemPlacementRuntime> placements = 
				createAvailablePlacements(availableItems, area.getMaxLength(), area.getMaxWidth(), area.getMaxHeight(), area.isFixedLength(), area.isFixedWidth(), area.isFixedHeight());
//		placements.forEach(pl -> pl.print());
		//Step 3: Map items by Height and Length
		final Map<CargoItemPlacementKey, CargoItemPlacementListElement> mapPlacements = mapPlacements(placements, targetDimension % 2 + 1);
//		mapPlacements.values().forEach(el -> el.print());
		
		//Step 4: Get List with placements with max surface area
		final List<CargoItemPlacementRuntime> newStepPlacements = getMaxChain(mapPlacements, targetSum, targetDimension, area.isCheckAllArea());
/*		if (newStepPlacements.size() > 0) {
			System.out.println("Max Chain Found ");
			newStepPlacements.forEach(pl -> pl.print());
			System.out.println("--------------------------------------");
		}
*/		
		return newStepPlacements.size() > 0 ? new LoadPlanStepRuntime(newStepPlacements, area.getStartX(), area.getStartY(), area.getStartZ(), targetDimension) : null;
	}

	public static float getAverageWeight (final List<CargoItemRuntime> items) {
		float totalWeight = 0.0f;
		int totalNumber = 0;
		for (final CargoItemRuntime item : items) {
			totalWeight += item.getWeight()*item.getRemainingQuantity();
			totalNumber += item.getRemainingQuantity();
		}
		return (totalNumber >0) ? totalWeight / totalNumber : 0;
	}

	public static List<CargoItemRuntime> filterByWeight (final List<CargoItemRuntime> items, final float targetWeight) {
		return items.stream().filter(item -> item.getWeight() > targetWeight).collect(Collectors.toList());
	}
	
	private static List<CargoItemPlacementRuntime> createAvailablePlacements(final List<CargoItemRuntime> items, final int maxLength, final int maxWidth, final int maxHeight, 
																		final boolean fixedLength, final boolean fixedWidth, final boolean fixedHeight) {
		final List<CargoItemPlacementRuntime> result = new ArrayList<CargoItemPlacementRuntime>();
		items.forEach(item -> result.addAll(item.createPlacements(maxLength, maxWidth, maxHeight, fixedLength, fixedWidth, fixedHeight, item.getRemainingQuantity())));
		return result;
	}
	
	private static Map<CargoItemPlacementKey, CargoItemPlacementListElement> mapPlacements (final List<CargoItemPlacementRuntime> placements, final int dimension) {
		final Map<CargoItemPlacementKey, CargoItemPlacementListElement> result = new HashMap<CargoItemPlacementKey, CargoItemPlacementListElement>();
		for (final CargoItemPlacementRuntime nextPlacement : placements) {
			final CargoItemPlacementKey nextPlacementKey = new CargoItemPlacementKey(nextPlacement, dimension);
			CargoItemPlacementListElement listElement = result.get(nextPlacementKey);
			if (listElement == null) {
				listElement = new CargoItemPlacementListElement(nextPlacementKey);
				result.put(nextPlacementKey, listElement);
			}
			listElement.getElements().add(nextPlacement);
		}
		return result;
	}

	private static List<CargoItemPlacementRuntime> getMaxChain (final Map<CargoItemPlacementKey, CargoItemPlacementListElement> mapPlacements, 
																final int targetSum, final int dimension, final boolean checkAllArea) {
		List<CargoItemPlacementRuntime> result = new ArrayList<CargoItemPlacementRuntime>();
		int maxArea = 0;
		int maxSum = 0;
		for (final Entry<CargoItemPlacementKey, CargoItemPlacementListElement> nextEntry : mapPlacements.entrySet()) {
			final List<CargoItemPlacementRuntime> entryChain = getMaxChain(nextEntry.getValue().getAvailableElements(), targetSum, dimension);
			final int entrySum = getSum(entryChain, dimension);
			final int entryArea = checkAllArea ? entrySum * nextEntry.getKey().getSecondDimensionValue() : entrySum;
			if (checkAllArea && entryArea == maxArea) {
				//We will choose the chain with bigger value on the target dimension
				if (entrySum > maxSum) {
					maxSum = entrySum;
					result = entryChain;
				}
			} else if (entryArea > maxArea) {
				maxArea = entryArea;
				maxSum = entrySum;
				result = entryChain;
			}
		}
		return result;
	}
	
	private static List<CargoItemPlacementRuntime> getMaxChain (final List<CargoItemPlacementRuntime> placements, final int targetSum, final int dimension) {
		Collections.sort(placements, new CargoItemPlacementDimensionComparator(dimension));
		final List<CargoItemPlacementRuntime> result = new ArrayList<CargoItemPlacementRuntime>();
		final List<Integer> indexes = getMaxChain(placements, 0, placements.size(), targetSum, dimension);
		if (indexes != null && indexes.size() > 0) {
			indexes.forEach(index -> result.add(placements.get(index))); 
		}
		return result;
	}
	
	private static int getSum(final List<CargoItemPlacementRuntime> placements, final int dimension) {
		int sum = 0;
		for (final CargoItemPlacementRuntime nextPlacement : placements) {
			sum += nextPlacement.getDimensionValue(dimension);
		}
		return sum;
	}
	private static int getSum(final List<CargoItemPlacementRuntime> placements, final List<Integer> indexes, final int dimension) {
		int sum = 0;
		for (final Integer nextIndex : indexes) {
			sum += placements.get(nextIndex).getDimensionValue(dimension);
		}
		return sum;
	}
	
	private static List<Integer> getMaxChain (final List<CargoItemPlacementRuntime> placements, final int startIndex, final int size, final int targetSum, final int dimension) {
		List<Integer> result = new ArrayList<Integer>();
		if (startIndex < size){
			int curValue = 0;
			int maxSum = 0;
			List<Integer> maxChain = null;
			for (int i = startIndex; i < size; i++) {
				int nextValue = placements.get(i).getDimensionValue(dimension);
				if (nextValue <= targetSum) {
					if (curValue == 0 || nextValue < curValue) {
						curValue = nextValue;
						final List<Integer> nextChain = getMaxChain(placements, i+1, size, targetSum-nextValue, dimension);
						final int nextSum = getSum(placements, nextChain, dimension);
						if (nextValue+nextSum == targetSum) {
							result.add(i);
							result.addAll(nextChain);
							return result;
						}
						if (nextValue+nextSum > maxSum) {
							maxSum = nextValue+nextSum;
							maxChain = new ArrayList<Integer>();
							maxChain.add(i);
							maxChain.addAll(nextChain);
						}
					}
				}
			}
			if (maxChain != null) {
				result = maxChain;
			}
		}
		return result;
	}

	public static String randomColor() {
		final Random rnd = new Random();
		final int r = 20 + rnd.nextInt(230);
		final int g = 20 + rnd.nextInt(230);
		final int b = 20 + rnd.nextInt(230);
		return Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
	}
		
	
}
