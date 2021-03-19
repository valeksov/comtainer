package com.developsoft.comtainer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.developsoft.comtainer.rest.dto.CargoGroupDto;
import com.developsoft.comtainer.rest.dto.CargoItemPlacementDto;
import com.developsoft.comtainer.rest.dto.ComtainerRequestDto;
import com.developsoft.comtainer.rest.dto.ContainerLoadPlanDto;
import com.developsoft.comtainer.rest.dto.LoadPlanStepDto;
import com.developsoft.comtainer.runtime.model.CargoGroupRuntime;
import com.developsoft.comtainer.runtime.model.CargoItemPlacementRow;
import com.developsoft.comtainer.runtime.model.CargoItemPlacementRuntime;
import com.developsoft.comtainer.runtime.model.CargoItemRuntime;
import com.developsoft.comtainer.runtime.model.CargoItemStepRuntime;
import com.developsoft.comtainer.runtime.model.CompositeKey;
import com.developsoft.comtainer.runtime.model.RuntimeListElement;
import com.developsoft.comtainer.util.comparators.ItemsNumberComparator;
import com.developsoft.comtainer.util.comparators.LoadPlanStepCoordinatesComparator;
import com.developsoft.comtainer.util.comparators.LoadPlanStepWeightComparator;
import com.developsoft.comtainer.util.comparators.RuntimeStepAreaComparator;

public class CargoUtil {

	public static List<ContainerLoadPlanDto> createLoadPlan (final ComtainerRequestDto request) {
		final List<ContainerLoadPlanDto> result = new ArrayList<ContainerLoadPlanDto>();
		final int maxLength = request.getContainers().get(0).getLength() / 2;
		final int maxHeight = request.getContainers().get(0).getHeight();
		final List<CargoItemRuntime> allItems = createRuntimeObjects(request.getGroups());
		final List<CargoItemStepRuntime> allSteps = createRuntimeSteps(allItems, maxLength);
		Collections.sort(allSteps, new RuntimeStepAreaComparator());
		final List<CargoItemStepRuntime> bottomSteps = new ArrayList<CargoItemStepRuntime>();
		for (final CargoItemStepRuntime nextStep : allSteps) {
			if (!nextStep.isPlaced() && nextStep.hasAvailableQuantity(0)) {
				nextStep.place(0, 0, 0);
				nextStep.pickTopSteps(allSteps, maxHeight);
				bottomSteps.add(nextStep);
			}
		}
		
		for (final CargoItemStepRuntime nextBottomStep : bottomSteps) {
			final ContainerLoadPlanDto loadPlan = new ContainerLoadPlanDto();
			loadPlan.setId(nextBottomStep.getItem().getSource().getId());
			loadPlan.setLoadPlanSteps(nextBottomStep.toDto());
			result.add(loadPlan);
		}
		
		return result;
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
	
	private static List<CargoItemStepRuntime> createRuntimeSteps (final List<CargoItemRuntime> allItems, final int maxLength) {
		final List<CargoItemStepRuntime> result = new ArrayList<CargoItemStepRuntime>();
		for (final CargoItemRuntime item : allItems) {
			createRuntimeSteps(result, item, maxLength);
		}
		return result;
	}
	private static void createRuntimeSteps (final List<CargoItemStepRuntime> steps, final CargoItemRuntime item, 
										final int totalLength, final int itemLength, final int maxLength, final boolean rotate) {
		if (totalLength <= maxLength) {
			steps.addAll(createStepCombinations(item, item.getRemainingQuantity(), rotate));
		} else if (totalLength <= maxLength * 2) {
			final int halfQuantity = (item.getRemainingQuantity()+1) /2;
			steps.addAll(createStepCombinations(item, halfQuantity, rotate));
			steps.addAll(createStepCombinations(item, item.getRemainingQuantity() - halfQuantity, rotate));
		} else if (totalLength <= maxLength * 3 && item.getRemainingQuantity() % 3 == 0) {
			createRuntimeSteps(steps, item, 3, item.getRemainingQuantity() / 3, rotate);
		} else if (totalLength <= maxLength * 4 && item.getRemainingQuantity() % 4 == 0) {
			createRuntimeSteps(steps, item, 4, item.getRemainingQuantity() / 4, rotate);
		} else if (totalLength >= maxLength * 4 && totalLength <= maxLength * 5 && item.getRemainingQuantity() % 5 == 0) {
			createRuntimeSteps(steps, item, 5, item.getRemainingQuantity() / 5, rotate);
		} else {
			int currentQuantity = 0;
			for (int i = 0; i < item.getRemainingQuantity(); i++) {
				if ((currentQuantity + 1)* itemLength > maxLength) {
					steps.addAll(createStepCombinations(item, currentQuantity, rotate));
					currentQuantity = 0;
				}
				currentQuantity++;
			}
			if (currentQuantity > 0) {
				steps.addAll(createStepCombinations(item, currentQuantity, rotate));
			}
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
	private static void createRuntimeSteps(final List<CargoItemStepRuntime> steps, final CargoItemRuntime item, final int numSteps, final int quantity,	final boolean rotate) {
		for (int i = 0; i < numSteps; i++) {
			steps.addAll(createStepCombinations(item, quantity, rotate));
		}
	}
	
	private static void createRuntimeSteps (final List<CargoItemStepRuntime> steps, final CargoItemRuntime item, final int maxLength) {
		final int totalLength = item.getLength() * item.getRemainingQuantity();
		createRuntimeSteps(steps, item, totalLength, item.getLength(), maxLength, false);
		if (item.getLength() != item.getWidth()) {
			final int totalWidth = item.getWidth() * item.getRemainingQuantity();
			createRuntimeSteps(steps, item, totalWidth, item.getWidth(), maxLength, true);
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
