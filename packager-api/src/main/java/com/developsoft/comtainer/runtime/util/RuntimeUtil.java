package com.developsoft.comtainer.runtime.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.developsoft.comtainer.rest.dto.CargoGroupDto;
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
	
	public static LoadPlanStepRuntime createStep (final List<LoadPlanStepRuntime> steps, final CargoItemRuntime item, final ContainerAreaRuntime source) {
		final List<CargoItemPlacementRuntime> itemPlacements = 	item.createPlacements(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false, false);
		for (final CargoItemPlacementRuntime placement : itemPlacements) {
			final int length = placement.getLength();
			final int width = placement.getWidth();
			final int height = placement.getHeight();
			final boolean skipZ = !placement.getItem().getSource().isStackable();
			final ContainerAreaRuntime area = MatrixUtil.getFreeArea(steps, source, item.getWeight(), 1.08f, 0, 0, 0, length, width, height, skipZ, false);
			if (area != null) {
				final List<CargoItemPlacementRuntime> placements = new ArrayList<CargoItemPlacementRuntime>();
				placements.add(placement);
				final LoadPlanStepRuntime step = new LoadPlanStepRuntime(placements, 0, 0, 0, 2);
				step.confirm();
				step.updateCoordinates(area.getStartX(), area.getStartY(), area.getStartZ());
				System.out.println("Placing single item on X=" + area.getStartX() + ", Y=" + area.getStartY() + ", Z="+area.getStartZ());
				placement.print();
				return step;
			}
		}
		return null;
	}
	
	public static LoadPlanStepRuntime createStep (final List<CargoItemRuntime> items, final ContainerAreaRuntime area, final int targetSum) {
		final int targetDimension = area.getTargetDimension();
//		final int otherDimension = area.getTargetDimension() % 2 + 1;
		//Step 1: Filter Items heavier than maxWeight
		final List<CargoItemRuntime> availableItems = items.stream()
													.filter(item -> area.getMaxWeight() == 0 || item.getWeight() <= area.getMaxWeight())
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
		items.forEach(item -> result.addAll(item.createPlacements(maxLength, maxWidth, maxHeight, fixedLength, fixedWidth, fixedHeight)));
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

}
