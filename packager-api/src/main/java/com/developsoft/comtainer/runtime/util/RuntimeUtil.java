package com.developsoft.comtainer.runtime.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.developsoft.comtainer.runtime.comparators.CargoItemPlacementDimensionComparator;
import com.developsoft.comtainer.runtime.model.CargoItemPlacementKey;
import com.developsoft.comtainer.runtime.model.CargoItemPlacementListElement;
import com.developsoft.comtainer.runtime.model.CargoItemPlacementRuntime;

public class RuntimeUtil {

	public static Map<CargoItemPlacementKey, CargoItemPlacementListElement> mapItems (final List<CargoItemPlacementRuntime> placements, final int dimension) {
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
	
	public static List<CargoItemPlacementRuntime> getMaxChain (final List<CargoItemPlacementRuntime> placements, final int targetSum, final int dimension) {
		Collections.sort(placements, new CargoItemPlacementDimensionComparator(dimension));
		final List<CargoItemPlacementRuntime> result = new ArrayList<CargoItemPlacementRuntime>();
		final List<Integer> indexes = getMaxChain(placements, 0, placements.size(), targetSum, dimension);
		if (indexes != null && indexes.size() > 0) {
			indexes.forEach(index -> result.add(placements.get(index))); 
		}
		return result;
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
