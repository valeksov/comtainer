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
public class CargoItemStepRuntime implements Comparable<CargoItemStepRuntime>{

	private final CargoItemRuntime item;
	private final int itemOrientation;
	private int stepOrientation;
	private final int quantity;
	private boolean placed;
	private Integer startX;
	private Integer startY;
	private Integer startZ;
	private final List<CargoItemStepRuntime> topSteps;
	private boolean skipInNextStep = false;
	private boolean placedInContainer;
	
	public CargoItemStepRuntime (final CargoItemRuntime item, final int quantity, final boolean rotate) {
		super();
		this.item = item;
		this.quantity = quantity;
		final int correction = rotate ?  correction(item.getOrientation(), false) : 0;
		this.itemOrientation = item.getOrientation() + correction;
		this.stepOrientation = 0;
		this.item.addRuntimeStep(this);
		this.topSteps = new ArrayList<CargoItemStepRuntime>();
	}
	
	public int getItemOrientation() {
		return stepOrientation * correction(this.itemOrientation, false) + this.itemOrientation;
	}
	
	private static int correction(int val, final boolean inverse) {
		final int remainder = inverse ? 0 : 1;
		return val % 2 == remainder ? 1 : -1;
	}
	
	public void rotate() {
		this.stepOrientation += correction(this.stepOrientation, true);
		topSteps.forEach(step -> step.rotate());
	}
	
	public int getHeight() {
		return this.item.getHeight();
	}
	
	public int getLength() {
		return (this.stepOrientation == 0 ? quantity : 1) * getPieceLength();
	}
	
	public int getPieceLength() {
		return (getItemOrientation() % 2 == 1 ? this.item.getLength() : this.item.getWidth());
	}

	public int getPieceWidth() {
		return (getItemOrientation() % 2 == 1 ? this.item.getWidth() : this.item.getLength());
	}

	public int getWidth() {
		return (this.stepOrientation == 1 ? quantity : 1) * getPieceWidth();
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
	
	public void updateCoordinates(final int startX, final int startY, final int startZ) {
		this.startX += startX;
		this.startY += startY;
		this.startZ += startZ;
		this.topSteps.forEach(step -> step.updateCoordinates(startX, startY, startZ));
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
		pickTopStepsForArea(steps, getStartX(), getStartY(), getStartZ() + getHeight(), getLength(), getWidth(), maxHeight - getHeight(), getWeight());
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
		if(step.getWeight() > maxWeightPerArea) {//if (step.getWeightPerArea() > maxWeightPerArea) {
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
			final float minWeightPerArea = items.get(0).getWeight();//getMinWeightPerArea(items);
			pickTopStepsForArea(allSteps, startX, startY, startZ + height, totalLength, totalWidth, maxHeight - height, minWeightPerArea);
		}
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
						placeMatchItems(steps, widthMatch, startX, startY, startZ, false, maxHeight, maxWeightPerArea);
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
	
	public static void findSubsets1(final Set<List<CargoItemStepRuntime>> allSubsets, final List<CargoItemStepRuntime> allSteps, final int sum, final boolean sameLength) {
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
			findSubsets1(allSubsets, subset, sum, sameLength);
		}
	}
	public static void findSubsets(final Set<List<CargoItemStepRuntime>> allSubsets, final List<CargoItemStepRuntime> arr, final int sum, final boolean sameLength) {
		final List<CargoItemStepRuntime> arrLocal = new ArrayList<CargoItemStepRuntime>();
		final int maxIndex = Math.min (arr.size(), 25);
		arrLocal.addAll(arr.subList(0, maxIndex));
		findSubsets(allSubsets, arrLocal, sum, 0, "", sameLength);
    }

    public static void findSubsets(final Set<List<CargoItemStepRuntime>> allSubsets, final List<CargoItemStepRuntime> arr, 
    									final int sum, final int i, final String accIn, final boolean sameLength) 
    {
    	String acc = accIn;
        if (sum == 0 && !"".equals(acc)) {
            final String[] accArr = acc.split(",");
            if (accArr != null && accArr.length > 0) {
            	final List<CargoItemStepRuntime> subset = new ArrayList<CargoItemStepRuntime>();
            	allSubsets.add(subset);
            	for (int k = 0; k < accArr.length; k++) {
            		int index = Integer.parseInt(accArr[k]);
            		subset.add(arr.get(index));
            	}
            }
            acc = "";
        }
        if (i == arr.size()) {
            return;
        }
        findSubsets(allSubsets, arr, sum, i + 1, acc, sameLength);
        if(!"".equals(acc)) {
        	acc = acc+",";
        }
        final CargoItemStepRuntime step = arr.get(i);
        final int val = sameLength ? step.getWidth() : step.getLength(); 
        findSubsets(allSubsets, arr, sum - val, i + 1, acc+i, sameLength);
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
		int curStartX = getStartX() != null ? getStartX() : 0;
		int curStartY = getStartY() != null ? getStartY() : 0;
		for (int i = 0; i < getQuantity(); i++) {
			final CargoItemPlacementDto nextItemDto = new CargoItemPlacementDto();
			dtoItems.add(nextItemDto);
			nextItemDto.setCargo(getItem().getSource());
			nextItemDto.setOrientation(getItemOrientation());
			nextItemDto.setStartX(curStartX);
			nextItemDto.setStartY(curStartY);
			if (this.stepOrientation == 0) {
				curStartX += getPieceLength();
			} else {
				curStartY += getPieceWidth();
			}
//			nextItemDto.setStartX(getStartX() + i * getItem().getLength());
//			nextItemDto.setStartY(getStartY());
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

	@Override
	public int compareTo(final CargoItemStepRuntime o) {
		final int compareWeight = Float.compare(getWeight(), o.getWeight());
		if (compareWeight != 0) {
			return (-1) * compareWeight;
		}
/*		final int compareDensity = Float.compare(getWeightPerArea(), o.getWeightPerArea());
		if (compareDensity != 0) {
			return (-1) * compareDensity;
		}
*/
		return (-1) * Integer.compare(getArea(), o.getArea());
	}
	public void print (final StringBuilder strB) {
		strB.append("[ (");
		strB.append(this.stepOrientation);
		strB.append(", ");
		strB.append(getItemOrientation());
		strB.append(", ");
		strB.append(item.getOrientation());
		strB.append(", ");
		strB.append(isSkipInNextStep());
		strB.append(") - (");
		strB.append(getStartX());
		strB.append(", ");
		strB.append(getStartY());
		strB.append(", ");
		strB.append(getStartZ());
		strB.append(") - ");
		if (getQuantity() > 1) {
			strB.append(getQuantity());
			strB.append(" * ");
		}
		strB.append(getLength()+" x "+getWidth()+" x "+getHeight());
		strB.append(" W:");
		strB.append(getWeight());
		if (this.topSteps.size() > 0) {
			final List<CargoItemStepRuntime> firstLevel = topSteps.stream().filter(step -> step.isSkipInNextStep()).collect(Collectors.toList());
			final List<CargoItemStepRuntime> secondLevel = topSteps.stream().filter(step -> !step.isSkipInNextStep()).collect(Collectors.toList());
			printSteps(strB, firstLevel);
			printSteps(strB, secondLevel);
		}
		strB.append("]");

	}
	protected void printSteps(final StringBuilder strB, final List<CargoItemStepRuntime> steps) {
		if (steps.size() > 0) {
			boolean isFirst = true;
			strB.append(" -> [ ");
			for (final CargoItemStepRuntime step : steps) {
				if (!isFirst) {
					strB.append(", ");
				}
				step.print(strB);
				isFirst = false;
			}
			strB.append("]");
		}
	}
}
