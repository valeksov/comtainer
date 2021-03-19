package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.developsoft.comtainer.rest.dto.CargoItemPlacementDto;
import com.developsoft.comtainer.rest.dto.LoadPlanStepDto;
import com.developsoft.comtainer.util.comparators.RuntimeStepAreaComparator;
import com.developsoft.comtainer.util.comparators.RuntimeStepCoordinatesComparator;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemStepRuntime {

	private final CargoItemRuntime item;
	private final int orientation;
	private final int quantity;
	private boolean placed;
	private Integer startX;
	private Integer startY;
	private Integer startZ;
	private final List<CargoItemStepRuntime> topSteps;
	private boolean skipInNextStep = false;
	
	public CargoItemStepRuntime (final CargoItemRuntime item, final int quantity, final boolean rotate) {
		super();
		this.item = item;
		this.quantity = quantity;
		final int correction = rotate ?  item.getOrientation() % 2 == 1 ? 1 : -1 :0;
		this.orientation = item.getOrientation() + correction;
		this.item.addRuntimeStep(this);
		this.topSteps = new ArrayList<CargoItemStepRuntime>();
	}

	public int getHeight() {
		return this.item.getHeight();
	}
	
	public int getLength() {
		return (this.orientation % 2 == 1 ? this.item.getLength() : this.item.getWidth()) * quantity;
	}
	
	public int getWidth() {
		return this.orientation % 2 == 1 ? this.item.getWidth() : this.item.getLength();
	}
	
	public int getArea() {
		return getLength() * getWidth();
	}
	
	public float getWeight() {
		return item.getSource().getWeigth() * this.quantity;
	}
	
	public float getWeightPerArea() {
		return this.getWeight() / this.getArea();
	}
	
	public boolean hasAvailableQuantity(final int reservedQuantity) {
		final int availableQuantity = item.getAvailableQuantity() - reservedQuantity;
		return availableQuantity > 0 && this.quantity <= availableQuantity;
	}
	
	public boolean place(final int startX, final int startY, final int startZ) {
		final boolean result = hasAvailableQuantity(0);
		if (result) {
			this.placed = true;
			this.startX = startX;
			this.startY = startY;
			this.startZ = startZ;
		}
		return result;
	}
	
	public Long getVolume() {
		long result =((long) getArea()) * getHeight();
		for (final CargoItemStepRuntime topStep : this.topSteps) {
			result += topStep.getVolume();
		}
		return result;
	}

	private void pickTopSteps(final CargoItemStepRuntime step, final List<CargoItemStepRuntime> steps, final int maxHeight) {
		if (!step.isSkipInNextStep()) {
			step.pickTopSteps(steps, maxHeight);
		}
	}
	
	public void pickTopSteps(final List<CargoItemStepRuntime> steps, final int maxHeight) {
		pickTopStepsForArea(steps, getStartX(), getStartY(), getStartZ() + getHeight(), getLength(), getWidth(), maxHeight - getHeight(), getWeightPerArea());
		this.topSteps.forEach(topStep -> pickTopSteps(topStep, steps, maxHeight - getHeight()));
	}

	private boolean isTopPickCandidate(final CargoItemStepRuntime step, final int maxLength, final int maxWidth, final int maxHeight, final float maxWeightPerArea) {
		if (step == this) {
			return false;
		}
		if (step.isPlaced() || !step.hasAvailableQuantity(0)) {
			return false;
		}
		if (step.getLength() > maxLength || step.getWidth() > maxWidth || step.getHeight() > maxHeight) {
			return false;
		}
		if (step.getWeightPerArea() > maxWeightPerArea) {
			return false;
		}
		return true;
	}
	
	private void placeMatchItems (final List<CargoItemStepRuntime> allSteps, final List<CargoItemStepRuntime> items, final int startX, final int startY, final int startZ, 
									final boolean sameLength, final int maxHeight, final float maxWeightPerArea) {
		int curStartX = startX;
		int curStartY = startY;
		int totalWidth = 0;
		int totalLength = 0;
		final boolean sameHeight = isSameHeight(items);
		for (final CargoItemStepRuntime nextStep : items) {
			nextStep.setSkipInNextStep(sameHeight);
			nextStep.place(curStartX, curStartY, startZ);
			this.topSteps.add(nextStep);
			if (sameLength) {
				curStartY += nextStep.getWidth();
				totalWidth += nextStep.getWidth();
				totalLength = nextStep.getLength();
			} else {
				curStartX += nextStep.getLength();
				totalLength += nextStep.getLength();
				totalWidth = nextStep.getWidth();
			}
		}
		//We will try another layer on top of the current one
		if (sameHeight) {
			final int height = items.get(0).getHeight();
			final float minWeightPerArea = getMinWeightPerArea(items);
			pickTopStepsForArea(allSteps, startX, startY, startZ + height, totalLength, totalWidth, maxHeight - height, minWeightPerArea);
		}
	}
	
	private float getMinWeightPerArea (final List<CargoItemStepRuntime> items) {
		float minWeightPerArea = Float.MAX_VALUE;
		for (final CargoItemStepRuntime step : items) {
			if (minWeightPerArea > step.getWeightPerArea()) {
				minWeightPerArea = step.getWeightPerArea();
			}
		}
		return minWeightPerArea;
	}
	
	private void pickTopStepsForArea(final List<CargoItemStepRuntime> steps, final int startX, final int startY, final int startZ, 
																final int length, final int width, final int maxHeight, final float maxWeightPerArea) {
		final List<CargoItemStepRuntime> areaCandidates = steps.stream()
																.filter(step -> isTopPickCandidate(step, length, width, maxHeight, maxWeightPerArea))
																.collect(Collectors.toList());
		Collections.sort(areaCandidates, new RuntimeStepAreaComparator());

		for (int curLen = length; curLen > 0; curLen--) {
			for (int curWid = width; curWid > 0; curWid--) {
				final List<CargoItemStepRuntime> lengthMatch = findExactMatch(areaCandidates, curLen, curWid, true);
				if (lengthMatch.size() > 0) {
					placeMatchItems(steps, lengthMatch, startX, startY, startZ, true, maxHeight, maxWeightPerArea);
					return;
				} else {
					final List<CargoItemStepRuntime> widthMatch = findExactMatch(areaCandidates, curLen, curWid, false);
					if (widthMatch.size() > 0) {
						placeMatchItems(steps, lengthMatch, startX, startY, startZ, false, maxHeight, maxWeightPerArea);
						return;
					}
				}
			}
		}
	}
	
	private static int getMaxSize (final Set<List<CargoItemStepRuntime>> allSubsets) {
		int maxSize = 0;
		for (final List<CargoItemStepRuntime> nextList : allSubsets) {
			if (nextList.size() > maxSize) {
				maxSize = nextList.size();
			}
		}
		return maxSize;
	}
	
	private static List<CargoItemStepRuntime> findMatchingSubset(final Set<List<CargoItemStepRuntime>> allSubsets) {
		List<CargoItemStepRuntime> firstMatch = null;
		final Map<Integer, List<CargoItemStepRuntime>> matchesWithSameHeight = new HashMap<Integer, List<CargoItemStepRuntime>>();
		if (allSubsets.size() > 0) {
			final int maxSize = getMaxSize(allSubsets);
			if (maxSize > 0) {
				for (int i = 1; i <= maxSize; i++) {
					final Set<List<CargoItemStepRuntime>> subsetsWithGivenSize = allSubsets.stream().filter(nextList -> nextList.size() == maxSize).collect(Collectors.toSet());
					for (final List<CargoItemStepRuntime> nextList : subsetsWithGivenSize) {
						final Map<String, Integer> reservedQuantities = new HashMap<String, Integer>();
						boolean isMatch = true;
						for (final CargoItemStepRuntime nextStep : nextList) {
							Integer itemReservedQuantity = reservedQuantities.get(nextStep.getItem().getUid());
							if (itemReservedQuantity == null) {
								itemReservedQuantity = new Integer(0);
							}
							if (!nextStep.hasAvailableQuantity(itemReservedQuantity)) {
								isMatch = false;
								break;
							}
							int newReservedQuantity = itemReservedQuantity + nextStep.getQuantity();
							reservedQuantities.put(nextStep.getItem().getUid(), new Integer(newReservedQuantity));
						}
						if (isMatch) {
							if (firstMatch == null) {
								firstMatch = nextList;
							}
							if (!matchesWithSameHeight.containsKey(i) && isSameHeight(nextList)) {
								matchesWithSameHeight.put(i, nextList);
							}
						}
					}
				}
				if (matchesWithSameHeight.size() > 0) {
					for (int i = 1; i <= maxSize; i++) {
						final List<CargoItemStepRuntime> nextList = matchesWithSameHeight.get(i);
						if (nextList != null) {
							return nextList;
						}
					}
				}	
				
			}
		}
		return firstMatch != null ? firstMatch : new ArrayList<CargoItemStepRuntime>();
	}
	
	private static boolean isSameHeight(final List<CargoItemStepRuntime> nextList) {
		int height = 0;
		for (final CargoItemStepRuntime nextStep : nextList) {
			if (height == 0) {
				height = nextStep.getHeight();
			} else {
				if (nextStep.getHeight() != height) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static List<CargoItemStepRuntime> findExactMatch (final List<CargoItemStepRuntime> allSteps, final int length, final int width, final boolean sameLength) {
		final List<CargoItemStepRuntime> candidateSteps = allSteps.stream().filter(step -> step.getLength() == length).collect(Collectors.toList());
		final Set<List<CargoItemStepRuntime>> allSubsets = new HashSet<List<CargoItemStepRuntime>>();
		findSubsets(allSubsets, candidateSteps, width, sameLength);
		return findMatchingSubset(allSubsets);
	}
	
	public static void findSubsets(final Set<List<CargoItemStepRuntime>> allSubsets, final List<CargoItemStepRuntime> allSteps, final int sum, final boolean sameLength) {
		if (allSteps.size() == 0) {
			return;
		}
		int currentSum = 0;
		for (CargoItemStepRuntime step : allSteps) {
			final int val = sameLength ? step.getWidth() : step.getLength(); 
			currentSum += val;
		}
		// does the current list add up to the needed sum?
		if (currentSum == sum) {
			allSubsets.add(new ArrayList<>(allSteps));
		}
		for (int i = 0; i < allSteps.size(); i++) {
			final List<CargoItemStepRuntime> subset = new ArrayList<>(allSteps);
			subset.remove(i);
			findSubsets(allSubsets, subset, sum, sameLength);
		}
	}

	public List<LoadPlanStepDto> toDto() {
		final List<LoadPlanStepDto> result = new ArrayList<LoadPlanStepDto>();
		final LoadPlanStepDto dto = new LoadPlanStepDto();
		result.add(dto);
		dto.setId(UUID.randomUUID().toString());
		dto.setStartX(getStartX());
		dto.setStartY(getStartY());
		dto.setStartZ(getStartZ());
		dto.setLength(getLength());
		dto.setWidth(getWidth());
		dto.setHeight(getHeight());
		final List<CargoItemPlacementDto> dtoItems = new ArrayList<CargoItemPlacementDto>();
		dto.setItems(dtoItems);
		for (int i = 0; i < getQuantity(); i++) {
			final CargoItemPlacementDto nextItemDto = new CargoItemPlacementDto();
			dtoItems.add(nextItemDto);
			nextItemDto.setCargo(getItem().getSource());
			nextItemDto.setOrientation(getOrientation());
			nextItemDto.setStartX(getStartX() + i * getItem().getLength());
			nextItemDto.setStartY(getStartY());
			nextItemDto.setStartZ(getStartZ());
		}
		if (!isSkipInNextStep()) {
			Collections.sort(topSteps, new RuntimeStepCoordinatesComparator());
			for (final CargoItemStepRuntime nextTopStep : topSteps) {
				result.addAll(nextTopStep.toDto());
			}
		}
		return result;
	}
}
