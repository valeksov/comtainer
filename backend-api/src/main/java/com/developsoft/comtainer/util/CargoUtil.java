package com.developsoft.comtainer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import com.developsoft.comtainer.rest.dto.CargoGroupDto;
import com.developsoft.comtainer.rest.dto.CargoItemPlacementDto;
import com.developsoft.comtainer.rest.dto.ComtainerRequestDto;
import com.developsoft.comtainer.rest.dto.ContainerDto;
import com.developsoft.comtainer.rest.dto.ContainerLoadPlanDto;
import com.developsoft.comtainer.rest.dto.LoadPlanStepDto;
import com.developsoft.comtainer.runtime.model.BasePackage;
import com.developsoft.comtainer.runtime.model.CargoGroupRuntime;
import com.developsoft.comtainer.runtime.model.CargoItemPlacementRow;
import com.developsoft.comtainer.runtime.model.CargoItemPlacementRuntime;
import com.developsoft.comtainer.runtime.model.CargoItemRuntime;
import com.developsoft.comtainer.runtime.model.CargoItemStepRuntime;
import com.developsoft.comtainer.runtime.model.ComboPackage;
import com.developsoft.comtainer.runtime.model.CompositeKey;
import com.developsoft.comtainer.runtime.model.MatrixRow;
import com.developsoft.comtainer.runtime.model.PackageGroup;
import com.developsoft.comtainer.runtime.model.RuntimeListElement;
import com.developsoft.comtainer.util.comparators.BasePackageAreaComparator;
import com.developsoft.comtainer.util.comparators.ItemsNumberComparator;
import com.developsoft.comtainer.util.comparators.LoadPlanStepCoordinatesComparator;
import com.developsoft.comtainer.util.comparators.LoadPlanStepWeightComparator;
import com.developsoft.comtainer.util.comparators.RuntimeStepAreaComparator;
import com.developsoft.comtainer.util.comparators.RuntimeStepCoordinatesComparator;

public class CargoUtil {

	public static List<ContainerLoadPlanDto> createLoadPlan (final ComtainerRequestDto request) {
		final List<ContainerLoadPlanDto> result = new ArrayList<ContainerLoadPlanDto>();
		final ContainerDto container = request.getContainers().get(0);
		final int maxLength = container.getLength() / 2;
		final int maxChain = 10;
		final int maxHeight = container.getHeight();
		final List<CargoItemRuntime> allItems = createRuntimeObjects(request.getGroups());
		final List<CargoItemStepRuntime> allSteps = createRuntimeSteps(allItems, maxLength, maxChain);
		Collections.sort(allSteps);
		final List<CargoItemStepRuntime> bottomSteps = new ArrayList<CargoItemStepRuntime>();
		for (final CargoItemStepRuntime nextStep : allSteps) {
			if (!nextStep.isPlaced() && nextStep.hasAvailableQuantity(0)) {
				nextStep.place(0, 0, 0);
				nextStep.pickTopSteps(allSteps, maxHeight);
				bottomSteps.add(nextStep);
			}
		}
		final ContainerLoadPlanDto loadPlan = new ContainerLoadPlanDto();
		loadPlan.setId("Successfully Placed");
		final List<LoadPlanStepDto> steps = new ArrayList<LoadPlanStepDto>();
		loadPlan.setLoadPlanSteps(steps);
		result.add(loadPlan);
		Collections.sort(bottomSteps, new RuntimeStepAreaComparator());
		final List<BasePackage> failedSteps = placeStepsInContainer(bottomSteps, container);
		Collections.sort(bottomSteps, new RuntimeStepCoordinatesComparator());

		final ContainerLoadPlanDto failedPlan = new ContainerLoadPlanDto();
		failedPlan.setId("Failed Steps");
		final List<LoadPlanStepDto> failedPlanSteps = new ArrayList<LoadPlanStepDto>();
		failedPlan.setLoadPlanSteps(failedPlanSteps);
		if (failedSteps.size() > 0) {
			result.add(failedPlan);
		}
		long volumeUsed = 0;
		int areaUsed = 0;
		final int totalArea = container.getLength() * container.getWidth();
		for (final CargoItemStepRuntime step : bottomSteps) {
			final StringBuilder strB = new StringBuilder();
			if (step.isPlacedInContainer()) {
				steps.addAll(step.toDto());
				volumeUsed += step.getVolume();
				areaUsed+= step.getArea();
				strB.append("Success ");
			} else {
				failedPlanSteps.addAll(step.toDto());
				strB.append("Failure ");
			}
			step.print(strB);
			System.out.println(strB.toString());
		}
		final int volumePercent = (int) (100.0 * volumeUsed / container.getMaxAllowedVolume());
		System.out.println ("Volume used: "+volumeUsed+"/"+ container.getMaxAllowedVolume()+" "+volumePercent+"%");
		System.out.println ("Floor area used: "+areaUsed+"/"+ totalArea);
		
		return result;
	}

	private static void print(final BasePackage bp) {
		System.out.println("Number in Package "+ bp.getCount());
		System.out.println("L "+ bp.getLength()+ " W "+bp.getWidth());
	}
	
	private static List<BasePackage> placeStepsInContainer (final List<CargoItemStepRuntime> bottomSteps, final ContainerDto container) {
		final List<BasePackage> packages = bottomSteps.stream().map(step -> new BasePackage(step)).collect(Collectors.toList());
		final List<BasePackage> result = PackageGroup.matchPackages(packages, container.getLength(), container.getWidth());
		List<BasePackage> failedSteps = placeStepsInContainerOnce(result, bottomSteps, container);
		int iteration = 1;
		while(failedSteps.size() > 0 && splitPackage(result)) {
			System.out.println("Attempt iteration #"+ iteration++);
			for (final BasePackage nextPackage : result) {
				nextPackage.updateContainerCoordinates(0, 0);
				nextPackage.setPlacedInContainer(false);
			}			
			failedSteps = placeStepsInContainerOnce(result, bottomSteps, container);
		}
		for (final BasePackage nextPackage : result) {
			if (nextPackage.isPlacedInContainer()) {
				nextPackage.placeInContainer();
			}
		}
		return failedSteps;
		
	}

	private static List<BasePackage> placeStepsInContainerOnce(final List<BasePackage> result, final List<CargoItemStepRuntime> bottomSteps, final ContainerDto container) {
		final List<BasePackage> failedSteps = new ArrayList<BasePackage>();
		Collections.sort(result, new BasePackageAreaComparator());
		result.forEach(bp -> print(bp));
		final List<MatrixRow> matrix = ContainerUtil.init2DMatrix(container);
		for (final BasePackage nextStep : result) {
			if(!ContainerUtil.placeRuntimeStep(matrix, nextStep)) {
				failedSteps.add(nextStep);
			}
		}
		return failedSteps;
	}
	
	private static boolean splitPackage(final List<BasePackage> list) {
		int index = 0;
		for (final BasePackage next : list) {
			if (next.getCount() > 1) {
				final ComboPackage cp = (ComboPackage)list.remove(index);
				list.addAll(cp.split());
				return true;
			}
			index++;
		}
		return false;
	}
	private static Map<Integer, List<CargoItemStepRuntime>> mapByLength(final List<CargoItemStepRuntime> bottomSteps) {
		final Map<Integer, List<CargoItemStepRuntime>> result = new HashMap<Integer, List<CargoItemStepRuntime>>();
		final Map<Integer, List<CargoItemStepRuntime>> tempMap = new HashMap<Integer, List<CargoItemStepRuntime>>();
		for (final CargoItemStepRuntime step : bottomSteps) {
			List<CargoItemStepRuntime> list = tempMap.get(step.getLength());
			if (list == null) {
				list = new ArrayList<CargoItemStepRuntime>();
				tempMap.put(step.getLength(), list);
			}
			list.add(step);
		}
		for (final Entry<Integer, List<CargoItemStepRuntime>> nextEntry : tempMap.entrySet()) {
			if (nextEntry.getValue().size() > 1) {
				result.put(nextEntry.getKey(), nextEntry.getValue());
			}
		}
		return result;
	}
	
	private static boolean placeSteps (final List<MatrixRow> matrix, final int startX, final List<CargoItemStepRuntime> allSteps) {
		int width = matrix.get(startX).getNumColumns();
		int minWidth = Math.round (0.8f * width);
		while (width >= minWidth) {
			final List<CargoItemStepRuntime> stepsLocal = allSteps.stream().filter(step -> !step.isPlacedInContainer()).collect(Collectors.toList());
			while (stepsLocal.size() > 0) {
				final List<CargoItemStepRuntime> steps = new ArrayList<CargoItemStepRuntime>();
				int maxIndex = Math.min(stepsLocal.size(), 10);
				for (int i = 0; i < maxIndex; i++) {
					steps.add(stepsLocal.remove(stepsLocal.size()-1));
				}
				final Set<List<CargoItemStepRuntime>> allSubsets = new HashSet<List<CargoItemStepRuntime>>();
				CargoItemStepRuntime.findSubsets(allSubsets, steps, width , true);
				if (allSubsets.size() > 0) {
					final List<CargoItemStepRuntime> matchingSteps = allSubsets.iterator().next();
					int startY = 0;
					for (final CargoItemStepRuntime step : matchingSteps) {
						step.updateCoordinates(startX, startY, 0);
						step.setPlacedInContainer(true);
						ContainerUtil.markedPlaced(matrix, startX, startY, step.getLength(), step.getWidth());
						startY += step.getWidth();
					}
					return true;
				}
			}
			width--;
		}
		return false;
	}
	@SuppressWarnings("unused")
	private static List<CargoItemStepRuntime> placeStepsInContainerOld (final List<CargoItemStepRuntime> bottomSteps, final ContainerDto container) {
		final List<CargoItemStepRuntime> failedSteps = new ArrayList<CargoItemStepRuntime>();
		final List<MatrixRow> matrix = ContainerUtil.init2DMatrix(container);
		final Map<Integer, List<CargoItemStepRuntime>> mapByLength = mapByLength(bottomSteps);
		for (final Integer key : mapByLength.keySet()) {
			System.out.println("Len ["+key+"], size "+mapByLength.get(key).size());
		}
		int startX = 0;
		if (mapByLength.size() > 0) {
			final List<Integer> lengths = mapByLength.keySet().stream().collect(Collectors.toList());
			Collections.sort(lengths);
			Collections.reverse(lengths);
			for (final Integer nextLen : lengths) {
				if (startX + nextLen <= matrix.size()) {
					final List<CargoItemStepRuntime> steps = mapByLength.get(nextLen);
					final boolean placeSteps = placeSteps(matrix, startX, steps);
					if (placeSteps) {
						startX +=nextLen;
					}
				}
			}
		}
		final List<CargoItemStepRuntime> filteredSteps = bottomSteps.stream().filter(nextStep -> !nextStep.isPlacedInContainer()).collect(Collectors.toList());
		for (final CargoItemStepRuntime nextStep : filteredSteps) {
			if (!nextStep.isPlacedInContainer()) {
				if(!ContainerUtil.placeRuntimeStep(matrix, nextStep)) {
					failedSteps.add(nextStep);
				} else {
					nextStep.setPlacedInContainer(true);
				}
			}
		}
		return failedSteps;
	}
	
	public static ContainerLoadPlanDto createLoadPlanOld (final ComtainerRequestDto request) {
		final ContainerLoadPlanDto result = new ContainerLoadPlanDto();
		final List<LoadPlanStepDto> loadPlanSteps = new ArrayList<LoadPlanStepDto>();
		result.setLoadPlanSteps(loadPlanSteps);
		final int maxLength = request.getContainers().get(0).getLength() / 2;
		final List<CargoItemRuntime> allItems = createRuntimeObjects(request.getGroups());
		final Map<CompositeKey, RuntimeListElement> mapItems = mapRuntimeItems(allItems, maxLength);
		final List<RuntimeListElement> listElements = mapItems.values().stream().collect(Collectors.toList());
		Collections.sort(listElements, new ItemsNumberComparator());
		for (final RuntimeListElement nextEntry : listElements) {
			final List<CargoItemPlacementRow> placementRows = getPlacementRows(nextEntry);
			for (final CargoItemPlacementRow nextRow : placementRows) {
				loadPlanSteps.add(createStepDto(nextRow));
			}
		}
		Collections.sort(loadPlanSteps, new LoadPlanStepWeightComparator());
		ContainerUtil.build(loadPlanSteps, request.getContainers().get(0));
		Collections.sort(loadPlanSteps, new LoadPlanStepCoordinatesComparator());
		return result;
	}
	
	private static List<CargoItemStepRuntime> createRuntimeSteps (final List<CargoItemRuntime> allItems, final int maxLength, final int maxChain) {
		final List<CargoItemStepRuntime> result = new ArrayList<CargoItemStepRuntime>();
		for (final CargoItemRuntime item : allItems) {
			createRuntimeSteps(result, item, maxLength, maxChain);
		}
		return result;
	}
	private static void createRuntimeSteps (final List<CargoItemStepRuntime> steps, final CargoItemRuntime item, 
										final int totalLength, final int itemLength, final int maxLength, final boolean rotate, final int maxChain) {
		int currentQuantity = 0;
		for (int i = 0; i < item.getRemainingQuantity(); i++) {
			if ((currentQuantity + 1)* itemLength > maxLength || currentQuantity == maxChain) {
				steps.addAll(createStepCombinations(item, currentQuantity, rotate));
				currentQuantity = 0;
			}
			currentQuantity++;
		}
		if (currentQuantity > 0) {
			steps.addAll(createStepCombinations(item, currentQuantity, rotate));
		}
	}
	
	private static List<CargoItemStepRuntime> createStepCombinations(final CargoItemRuntime item, final int quantity, final boolean rotate) {
		final List<CargoItemStepRuntime> result = new ArrayList<CargoItemStepRuntime>();
		for (int i = 1; i <= quantity; i++) {
			for (int j = 1; j <= i; j++) {
				result.add(new CargoItemStepRuntime(item, j, rotate));
			}
		}
		return result;
	}
	
	private static void createRuntimeSteps (final List<CargoItemStepRuntime> steps, final CargoItemRuntime item, final int maxLength, final int maxChain) {
		final int totalLength = item.getLength() * item.getRemainingQuantity();
		createRuntimeSteps(steps, item, totalLength, item.getLength(), maxLength, false, maxChain);
		if (item.getLength() != item.getWidth()) {
			final int totalWidth = item.getWidth() * item.getRemainingQuantity();
			createRuntimeSteps(steps, item, totalWidth, item.getWidth(), maxLength, true, maxChain);
		}
	}
	
	private static LoadPlanStepDto createStepDto (final CargoItemPlacementRow placementRow) {
		final LoadPlanStepDto dto = new LoadPlanStepDto();
		dto.setId(UUID.randomUUID().toString());
		dto.setStartX(0);
		dto.setStartY(0);
		dto.setStartZ(0);
		dto.setLength(placementRow.getLength());
		dto.setWidth(placementRow.getWidth());
		dto.setHeight(placementRow.getHeight());
		final List<CargoItemPlacementDto> items = placementRow.getPlacements().stream().map(next -> createCargoItemPlacementDto(next)).collect(Collectors.toList());
		dto.setItems(items);
		return dto;
	}
	
	private static CargoItemPlacementDto createCargoItemPlacementDto (final CargoItemPlacementRuntime runtime) {
		final CargoItemPlacementDto dto = new CargoItemPlacementDto();
		dto.setCargo(runtime.getItem().getSource());
		dto.setOrientation(runtime.getOrientation());
		dto.setStartX(runtime.getStartX());
		dto.setStartY(runtime.getStartY());
		dto.setStartZ(runtime.getStartZ());
		return dto;
	}
	
	private static List<CargoItemRuntime> createRuntimeObjects(final List<CargoGroupDto> groups) {
		final List<CargoItemRuntime> result = new ArrayList<CargoItemRuntime>();
		if (groups != null) {
			for (final CargoGroupDto nextGroup : groups) {
				final CargoGroupRuntime groupRuntime = new CargoGroupRuntime(nextGroup);
				result.addAll(groupRuntime.getItems());
			}
		}
		return result;
	}
	
	public static Map<CompositeKey, RuntimeListElement> mapRuntimeItems (final List<CargoItemRuntime> allItems, final int maxLength) {
		final Map<CompositeKey, RuntimeListElement> result = new HashMap<CompositeKey, RuntimeListElement>();
		//Step 1: Map runtime items into Lists by Height and Width/Length (2 equal sides)
		for (final CargoItemRuntime nextItem : allItems) {
			if (!nextItem.isPlaced()) {
				final CompositeKey key1 = new CompositeKey(nextItem.getHeight(), nextItem.getWidth());
				addToMap(result, key1, nextItem);
				if (nextItem.getWidth() != nextItem.getLength()) {
					final CompositeKey key2 = new CompositeKey(nextItem.getHeight(), nextItem.getLength());
					addToMap(result, key2, nextItem);
				}
			}
		}
		//Step 2: For Each List - make all possible combinations taking in mind single pieces (if quantity=5 then we may use in the chain 1,2,3,4 or 5 of these pieces)
		for (final CompositeKey nextKey : result.keySet()) {
			final RuntimeListElement nextListEl = result.get(nextKey);
			final List<List<CargoItemPlacementRuntime>> itemCombinations = new ArrayList<List<CargoItemPlacementRuntime>>();
			for (final CargoItemRuntime nextRuntime : nextListEl.getList()) {
				final boolean rotateItem = nextKey.getWidth() != nextRuntime.getWidth();
				makeCombinations(itemCombinations, nextRuntime, rotateItem);
			}
			//Step 3 We will use only these combinations, which total length < maxLength
			for (final List<CargoItemPlacementRuntime> nextCombination : itemCombinations) {
				final Integer length = getCombinationLength(nextCombination);
				if (length > 0 && length <= maxLength) {
					nextListEl.getAllCombinations().add(nextCombination);
				}
			}
		}		
		return result;
	}
	
	private static void makeCombinations (final List<List<CargoItemPlacementRuntime>> allCombinations, final CargoItemRuntime newItem, final boolean rotateItem) {
		final List<List<CargoItemPlacementRuntime>> newItemCombinations = makeItemCombinationsBasedOnQuantity(newItem, rotateItem);
		final List<List<CargoItemPlacementRuntime>> newCombinations = makeNewCombinations(allCombinations, newItemCombinations);
		allCombinations.addAll(newItemCombinations);
		allCombinations.addAll(newCombinations);
	}
	
	private static List<List<CargoItemPlacementRuntime>> makeNewCombinations (final List<List<CargoItemPlacementRuntime>> allCombinations, 
																				final List<List<CargoItemPlacementRuntime>> newCombinations) {
		final List<List<CargoItemPlacementRuntime>> result = new ArrayList<List<CargoItemPlacementRuntime>>();
		for (final List<CargoItemPlacementRuntime> source : allCombinations) {
			for (final List<CargoItemPlacementRuntime> next : newCombinations) {
				final List<CargoItemPlacementRuntime> newList = new ArrayList<CargoItemPlacementRuntime>();
				source.forEach(placement -> newList.add(placement.clone()));
				next.forEach(placement -> newList.add(placement.clone()));
				result.add(newList);
			}
		}
		return result;
	}
	
	public static List<CargoItemPlacementRuntime> getMaxCombinationByLength (final List<List<CargoItemPlacementRuntime>> allCombinations) {
		List<CargoItemPlacementRuntime> result = new ArrayList<CargoItemPlacementRuntime>();
		int maxLength = 0;
		for (final List<CargoItemPlacementRuntime> nextCombination : allCombinations) {
			printCombination(nextCombination, null);
			if (isAvailable(nextCombination)) { 
				final Integer length = getCombinationLength(nextCombination);
				if (length > maxLength) {
					maxLength = length;
					result = nextCombination;
				}
			}
		}
		printCombination(result, "MAX ->");
		return result;
	}
	
	private static void printCombination (final List<CargoItemPlacementRuntime> combination, final String preffix) {
		final StringBuffer strBuf = new StringBuffer();
		if (preffix != null) {
			strBuf.append(preffix);
		}
		boolean isFirst = true;
		for (final CargoItemPlacementRuntime next : combination) {
			if (!isFirst) {
				strBuf.append(",");
			}
			isFirst = false;
			strBuf.append(next.getItem().getSource().getId());
			strBuf.append("(");
			strBuf.append(next.getOrientation());
			strBuf.append(",");
			strBuf.append(next.getItem().getRemainingQuantity());
			strBuf.append(")");
		}
		System.out.println(strBuf.toString());
	}
	
	public static List<CargoItemPlacementRow> getPlacementRows (final RuntimeListElement element) {
		final List<CargoItemPlacementRow> result = new ArrayList<CargoItemPlacementRow>();
		List<CargoItemPlacementRuntime> nextCombination = getMaxCombinationByLength(element.getAllCombinations());
		while (nextCombination.size() > 0) {
			result.add(createPlacementRow(nextCombination));
			nextCombination = getMaxCombinationByLength(element.getAllCombinations());
		}
		return result;
	}
	
	private static CargoItemPlacementRow createPlacementRow (final List<CargoItemPlacementRuntime> combination) {
		final CargoItemPlacementRow result = new CargoItemPlacementRow();
		for (final CargoItemPlacementRuntime nextPlacement : combination) {
			nextPlacement.getItem().markUsed(1);
			result.add(nextPlacement);
		}
		return result;
	}
	
	private static boolean isAvailable(final List<CargoItemPlacementRuntime> combination) {
		String currItemId = null;
		CargoItemPlacementRuntime prevRuntime = null;
		int numItems = 0;
		for (final CargoItemPlacementRuntime next : combination) {
			if (currItemId == null) {
				currItemId = next.getItem().getSource().getId();
				numItems = 1;
				prevRuntime = next;
			} else {
				if (currItemId.equals(next.getItem().getSource().getId())) {
					numItems++;
				} else {
					if (numItems > prevRuntime.getItem().getRemainingQuantity()) {
						return false;
					}
					currItemId = next.getItem().getSource().getId();
					numItems = 1;
					prevRuntime = next;
				}
			}
		}
		return prevRuntime != null && numItems <= prevRuntime.getItem().getRemainingQuantity();
	}
	
	private static Integer getCombinationLength(final List<CargoItemPlacementRuntime> combination) {
		int result = 0;
		for (final CargoItemPlacementRuntime next : combination) {
			result += next.getLength();
		}
		return new Integer(result);
	}
	
	private static List<List<CargoItemPlacementRuntime>> makeItemCombinationsBasedOnQuantity(final CargoItemRuntime newItem, final boolean rotateItem) {
		final List<List<CargoItemPlacementRuntime>> result = new ArrayList<List<CargoItemPlacementRuntime>>();
		for (int i=1; i<= newItem.getRemainingQuantity(); i++) {
			final List<CargoItemPlacementRuntime> nextList = new ArrayList<CargoItemPlacementRuntime>();
			for (int j = 0; j < i; j++) {
				final CargoItemPlacementRuntime placement = new CargoItemPlacementRuntime(newItem, rotateItem);
				nextList.add(placement);
			}
			result.add(nextList);
		}
		return result;
	}
	
	private static void addToMap (final Map<CompositeKey, RuntimeListElement> mapItems, final CompositeKey key, final CargoItemRuntime item) {
		RuntimeListElement mappedEl = mapItems.get(key);
		 if (mappedEl == null) {
			 mappedEl = new RuntimeListElement(key);
			 mapItems.put(key, mappedEl);
		 }
		 mappedEl.getList().add(item);
	}
	
}
