package com.developsoft.comtainer.service;

import static com.developsoft.comtainer.runtime.util.RuntimeUtil.confirmStep;
import static com.developsoft.comtainer.runtime.util.RuntimeUtil.randomColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.developsoft.comtainer.rest.dto.CargoGroupDto;
import com.developsoft.comtainer.rest.dto.CargoItemDto;
import com.developsoft.comtainer.rest.dto.ComtainerRequestDto;
import com.developsoft.comtainer.rest.dto.ComtainerResponseDto;
import com.developsoft.comtainer.rest.dto.ConfigDto;
import com.developsoft.comtainer.rest.dto.ContainerDto;
import com.developsoft.comtainer.rest.dto.ContainerLoadPlanDto;
import com.developsoft.comtainer.runtime.comparators.ContainerVolumeComparator;
import com.developsoft.comtainer.runtime.comparators.GroupTotalVolumeComparator;
import com.developsoft.comtainer.runtime.model.CargoGroupRuntime;
import com.developsoft.comtainer.runtime.model.CargoItemRuntime;
import com.developsoft.comtainer.runtime.model.ContainerAreaRuntime;
import com.developsoft.comtainer.runtime.model.LoadPlanStepRuntime;
import com.developsoft.comtainer.runtime.util.ContainerUtil;
import com.developsoft.comtainer.runtime.util.MatrixUtil;
import com.developsoft.comtainer.runtime.util.RuntimeUtil;

@Service
public class PackagerService {

	private CargoGroupDto findOneCandidateGroup(final List<CargoGroupDto> candidateGroups, final List<CargoGroupDto> placedGroups, final List<CargoGroupDto> newGroups,
										final Map<String, CargoGroupDto> newPlacedGroupsMap, final Map<String, CargoGroupDto> failedGroupsMap) {
		for (final CargoGroupDto nextPlaced : placedGroups) {
			if (!newPlacedGroupsMap.containsKey(nextPlaced.getId()) && !failedGroupsMap.containsKey(nextPlaced.getId()) && !alreadyCandidate(candidateGroups, nextPlaced)) {
				return nextPlaced;
			}
		}
		for (final CargoGroupDto nextNew : newGroups) {
			if (!newPlacedGroupsMap.containsKey(nextNew.getId()) && !failedGroupsMap.containsKey(nextNew.getId()) && !alreadyCandidate(candidateGroups, nextNew)) {
				return nextNew;
			}
		}
			
		return null;	
	}
	
	private boolean alreadyCandidate(final List<CargoGroupDto> candidateGroups, final CargoGroupDto newCandidate) {
		for (final CargoGroupDto nextCandidate : candidateGroups) {
			if (nextCandidate.getId().equals(newCandidate.getId())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean allGroupsPlaced (final List<CargoGroupDto> placedGroups, final List<CargoGroupDto> newGroups, final Map<String, CargoGroupDto> newPlacedGroupsMap, final boolean skipNew) {
		for (final CargoGroupDto nextPlaced : placedGroups) {
			if (!newPlacedGroupsMap.containsKey(nextPlaced.getId())) {
				return false;
			}
		}
		if (!skipNew) {
			for (final CargoGroupDto nextNew : newGroups) {
				if (!newPlacedGroupsMap.containsKey(nextNew.getId())) {
					return false;
				}
			}
		}
		return true;
	}
	
	private int countPlacedNewGroups (final List<CargoGroupDto> newGroups, final Map<String, CargoGroupDto> newPlacedGroupsMap) {
		int result = 0;
		for (final CargoGroupDto nextNew : newGroups) {
			if (newPlacedGroupsMap.containsKey(nextNew.getId())) {
				result++;
			}
		}
		return result;
	}
	
	private List<CargoItemRuntime> cloneItems(final List<CargoItemRuntime> items) {
		return items.stream().map(sourceItem -> new CargoItemRuntime(sourceItem)).collect(Collectors.toList());
	}
	
	public ComtainerResponseDto runLoadPlanner(final ComtainerRequestDto request) {
		final ComtainerResponseDto result = new ComtainerResponseDto();
		result.setConfig(request.getConfig());
		result.setGroups(request.getGroups());
		final List<ContainerDto> loadPlans = new ArrayList<ContainerDto>();
		result.setContainers(loadPlans);
		result.setStatus(0);
		if (request.getContainers() != null && request.getContainers().size() > 0 && request.getGroups() != null && request.getGroups().size() > 0) {
			final List<CargoGroupDto> newGroups = request.getGroups();
			Collections.sort(newGroups, new GroupTotalVolumeComparator());
			final List<CargoGroupDto> placedGroups =  new ArrayList<CargoGroupDto>();
			final Map<String, CargoGroupDto> newPlacedGroupsMap = new HashMap<String, CargoGroupDto>();
			final List<ContainerDto> availableContainers = new ArrayList<ContainerDto>(request.getContainers());
			Collections.sort(availableContainers, new ContainerVolumeComparator());
			int index = 1;
			final List<CargoItemRuntime> initialItems = RuntimeUtil.createRuntimeItems(RuntimeUtil.createRuntimeGroups(newGroups));
			List<CargoItemRuntime> curRemainingItems = initialItems;
			final boolean keepGroupsTogether = request.getConfig().isKeepGroupsTogether();
			while (true) {
				final List<CargoItemRuntime> remainingItems = !keepGroupsTogether ? cloneItems(curRemainingItems) : null;
				final List<CargoGroupDto> placedCandidateGroups = new ArrayList<CargoGroupDto>();
				final ContainerAreaRuntime containerArea = ContainerUtil.createContainerArea(availableContainers.get(0), request.getConfig());
				List<LoadPlanStepRuntime> placedSteps = new ArrayList<LoadPlanStepRuntime>();
				boolean success = false;
				if (keepGroupsTogether) {
					placedSteps = placeGroups(placedGroups, newGroups, newPlacedGroupsMap, containerArea, request.getConfig(), placedCandidateGroups);
				} else {
					success = placeSteps(remainingItems, containerArea, placedSteps, request.getConfig());
				}
				if (placedSteps.size() > 0) {
					if (keepGroupsTogether) {
						for (final CargoGroupDto nextPlacedGroup : placedCandidateGroups) {
							newPlacedGroupsMap.put(nextPlacedGroup.getId(), nextPlacedGroup);
							nextPlacedGroup.setAlreadyLoaded(true);
						}
					}
					if (success || (keepGroupsTogether && allGroupsPlaced(placedGroups, newGroups, newPlacedGroupsMap, false))) {
						ContainerDto source = availableContainers.get(0);
						List<LoadPlanStepRuntime> lastPlacedSteps = placedSteps;
						if (availableContainers.size() > 1) {
							for (int i = 1; i < availableContainers.size(); i++) {
								final ContainerDto newContainerSource = availableContainers.get(i);
								final ContainerAreaRuntime newContainerArea = ContainerUtil.createContainerArea(newContainerSource, request.getConfig());
								final List<LoadPlanStepRuntime> newSteps = new ArrayList<LoadPlanStepRuntime>();
								final List<CargoGroupRuntime> candidateRuntimeGroups = keepGroupsTogether ? RuntimeUtil.createRuntimeGroups(placedCandidateGroups) : null;
								final List<CargoItemRuntime> candidateItems = keepGroupsTogether ? RuntimeUtil.createRuntimeItems(candidateRuntimeGroups) : cloneItems(curRemainingItems);
								final boolean containerSuccess = placeSteps(candidateItems, newContainerArea, newSteps, request.getConfig());
								if (containerSuccess) {
									source = newContainerSource;
									lastPlacedSteps = newSteps;
								} else {
									//No need to attempt with Smaller Containers
									break;
								}
							}
						}
						final ContainerDto resultContainer = createContainerWithLoadPlan(source, index, lastPlacedSteps);
						printStats(resultContainer, lastPlacedSteps);
						loadPlans.add(resultContainer);
						break;
					} else {
						final ContainerDto nextResultContainer = createContainerWithLoadPlan(availableContainers.get(0), index, placedSteps);
						printStats(nextResultContainer, placedSteps);
						loadPlans.add(nextResultContainer);
						index++;
					}
					if (!keepGroupsTogether) {
						curRemainingItems = remainingItems;
					}
				} else {
					//In Theory we should get here ONLY if there is a group which alone doesn't fit in any container
					result.setStatus(1);
					break;
				}
				
			}
			if (!keepGroupsTogether) {
				for (final CargoGroupDto nextGroup : newGroups) {
					nextGroup.setAlreadyLoaded(true);
				}
				
			}
		}
		return result;
	}
	
	private ContainerDto createContainerWithLoadPlan (final ContainerDto source, final int index, final List<LoadPlanStepRuntime> placedSteps) {
		final ContainerDto newContainer = new ContainerDto(source, index);
		final ContainerLoadPlanDto newLoadPlan = new ContainerLoadPlanDto();
		newLoadPlan.setId(UUID.randomUUID().toString());
		newLoadPlan.setLoadPlanSteps(placedSteps.stream().map(step -> step.toDto()).collect(Collectors.toList()));
		newContainer.setLoadPlan(newLoadPlan);
		return newContainer;
	}
	
	public ComtainerResponseDto run(final ComtainerRequestDto request) {
		final ComtainerResponseDto result = new ComtainerResponseDto();
		result.setConfig(request.getConfig());
		result.setGroups(request.getGroups());
		boolean success = false;
		result.setContainers(request.getContainers());
		if (request.getContainers() != null && request.getContainers().size() > 0 && request.getGroups() != null && request.getGroups().size() > 0) {
			final int numContainers = request.getContainers().size();
			final List<CargoGroupDto> newGroups = request.getGroups().stream().filter(groupDto -> !groupDto.isAlreadyLoaded()).collect(Collectors.toList());
			final List<CargoGroupDto> placedGroups = request.getGroups().stream().filter(groupDto -> groupDto.isAlreadyLoaded()).collect(Collectors.toList());
			if (request.getContainers().size() == 1) {
				final ContainerDto container = request.getContainers().get(0);
				final ContainerAreaRuntime initialArea = ContainerUtil.createContainerArea(container, request.getConfig());
				if (container.getLoadPlan() != null && container.getLoadPlan().getLoadPlanSteps() != null && container.getLoadPlan().getLoadPlanSteps().size() > 0) {
					System.out.println ("Check with the old plan first");
					final List<LoadPlanStepRuntime> placedSteps = new ArrayList<LoadPlanStepRuntime>();
					placedSteps.addAll(container.getLoadPlan().getLoadPlanSteps().stream().map(stepDto -> new LoadPlanStepRuntime(stepDto)).collect(Collectors.toList()));
					final List<CargoItemRuntime> newItems = RuntimeUtil.createRuntimeItems(RuntimeUtil.createRuntimeGroups(newGroups));
					success = placeSteps(newItems, initialArea, placedSteps, request.getConfig());
					System.out.println ("Old plan " + (success ? "" : "NOT ") + "successful");
					if (success) {
						final ContainerLoadPlanDto newLoadPlan = new ContainerLoadPlanDto();
						newLoadPlan.setId(UUID.randomUUID().toString());
						result.setStatus(0);
						printStats(container, placedSteps);
						newLoadPlan.setLoadPlanSteps(placedSteps.stream().map(step -> step.toDto()).collect(Collectors.toList()));
						container.setLoadPlan(newLoadPlan);
						for (final CargoGroupDto nextNewGroup : newGroups) {
							nextNewGroup.setAlreadyLoaded(true);
						}
					}
				}
			}
			if (!success) {
				if (request.getConfig().isKeepGroupsTogether()) {
					final List<ContainerLoadPlanDto> newLoadPlans = new ArrayList<ContainerLoadPlanDto>();
					final Map<String, CargoGroupDto> newPlacedGroupsMap = new HashMap<String, CargoGroupDto>();
					for (int contIndex = 0; contIndex < numContainers; contIndex++) {
						if (allGroupsPlaced(placedGroups, newGroups, newPlacedGroupsMap, false)) {
							break;
						}
						final ContainerDto nextContainer = request.getContainers().get(contIndex);
						final ContainerAreaRuntime containerArea = ContainerUtil.createContainerArea(nextContainer, request.getConfig());
						final ContainerLoadPlanDto newLoadPlan = new ContainerLoadPlanDto();
						newLoadPlan.setId(UUID.randomUUID().toString());
						final List<CargoGroupDto> placedCandidateGroups = new ArrayList<CargoGroupDto>(); 
						final List<LoadPlanStepRuntime> placedSteps = placeGroups(placedGroups, newGroups, newPlacedGroupsMap, containerArea, request.getConfig(), placedCandidateGroups);
					
/*					final List<CargoGroupDto> candidateGroups = new ArrayList<CargoGroupDto>(); 
					final Map<String, CargoGroupDto> failedGroupsMap = new HashMap<String, CargoGroupDto>();
					CargoGroupDto lastGroup = findOneCandidateGroup(candidateGroups, placedGroups, newGroups, newPlacedGroupsMap, failedGroupsMap);
					while (lastGroup != null) {
						final List<LoadPlanStepRuntime> newSteps = new ArrayList<LoadPlanStepRuntime>();
						candidateGroups.add(lastGroup);
						final List<CargoGroupRuntime> candidateRuntimeGroups = RuntimeUtil.createRuntimeGroups(candidateGroups);
						final List<CargoItemRuntime> candidateItems = RuntimeUtil.createRuntimeItems(candidateRuntimeGroups);
						final boolean containerSuccess = placeSteps(candidateItems, nextArea, newSteps, request.getConfig());
						if (containerSuccess) {
							placedSteps = newSteps;
							placedCandidateGroups.clear();
							placedCandidateGroups.addAll(candidateGroups);
						} else {
							failedGroupsMap.put(lastGroup.getId(), lastGroup);
							candidateGroups.remove(candidateGroups.size() - 1);
						}
						lastGroup = findOneCandidateGroup(candidateGroups, placedGroups, newGroups, newPlacedGroupsMap, failedGroupsMap);
					}
*/					
						if (placedSteps.size() > 0) {
							printStats(nextContainer, placedSteps);
							newLoadPlan.setLoadPlanSteps(placedSteps.stream().map(step -> step.toDto()).collect(Collectors.toList()));
							newLoadPlans.add(newLoadPlan);
							for (final CargoGroupDto nextPlacedGroup : placedCandidateGroups) {
								newPlacedGroupsMap.put(nextPlacedGroup.getId(), nextPlacedGroup);
							}
						}
					}
					final int newPlacedGroups = countPlacedNewGroups(newGroups, newPlacedGroupsMap);
					if (allGroupsPlaced(placedGroups, newGroups, newPlacedGroupsMap, true) && newPlacedGroups > 0) {
						result.setStatus(allGroupsPlaced(placedGroups, newGroups, newPlacedGroupsMap, false) ? 0 : 1);
						for (int i = 0; i < newLoadPlans.size(); i++) {
							final ContainerDto container = request.getContainers().get(i);
							final ContainerLoadPlanDto loadPlan = newLoadPlans.get(i);
							container.setLoadPlan(loadPlan);
						}
						if (numContainers > newLoadPlans.size()) {
							for (int j = newLoadPlans.size(); j < numContainers; j++) {
								final ContainerDto container = request.getContainers().get(j);
								container.setLoadPlan(null);
							}
						}
						for (final CargoGroupDto nextNewGroup : newGroups) {
							if (newPlacedGroupsMap.containsKey(nextNewGroup.getId())) {
								nextNewGroup.setAlreadyLoaded(true);
							}
						}
					} else {
						result.setStatus(1);
					}
				} else {
					final boolean containersSuccess = placeContainers(request);
					result.setStatus(containersSuccess ? 0 :1);
				}
			}
		}
		return result;
	}
	private boolean placeContainers (final ComtainerRequestDto request) {
		final List<CargoGroupRuntime> groupRuntimes = RuntimeUtil.createRuntimeGroups(request.getGroups());
		final List<CargoItemRuntime> initialItems = RuntimeUtil.createRuntimeItems(groupRuntimes);
		boolean success = false;
		for (final ContainerDto nextContainer : request.getContainers()) {
			nextContainer.setLoadPlan(null);
		}
		for (final ContainerDto nextContainer : request.getContainers()) {
			final ContainerAreaRuntime containerArea = ContainerUtil.createContainerArea(nextContainer, request.getConfig());
			final List<LoadPlanStepRuntime> newSteps = new ArrayList<LoadPlanStepRuntime>();
			final boolean containerSuccess = placeSteps(initialItems, containerArea, newSteps, request.getConfig());
			if (newSteps.size() > 0) {
				printStats(nextContainer, newSteps);
				final ContainerLoadPlanDto newLoadPlan = new ContainerLoadPlanDto();
				newLoadPlan.setId(UUID.randomUUID().toString());
				newLoadPlan.setLoadPlanSteps(newSteps.stream().map(step -> step.toDto()).collect(Collectors.toList()));
				nextContainer.setLoadPlan(newLoadPlan);
			}
			if (containerSuccess) {
				success = true;
				break;
			}
		}
		for (final CargoGroupRuntime nextGroupRuntime : groupRuntimes) {
			nextGroupRuntime.getSource().setAlreadyLoaded(nextGroupRuntime.isPlaced());
		}
		return success;
	}
	private List<LoadPlanStepRuntime> placeGroups(final List<CargoGroupDto> placedGroups, final List<CargoGroupDto> newGroups, final Map<String, CargoGroupDto> newPlacedGroupsMap,
											final ContainerAreaRuntime containerArea, final ConfigDto config, final List<CargoGroupDto> placedCandidateGroups) {
		List<LoadPlanStepRuntime> result = new ArrayList<LoadPlanStepRuntime>();
		final List<CargoGroupDto> candidateGroups = new ArrayList<CargoGroupDto>(); 
		final Map<String, CargoGroupDto> failedGroupsMap = new HashMap<String, CargoGroupDto>();
		CargoGroupDto lastGroup = findOneCandidateGroup(candidateGroups, placedGroups, newGroups, newPlacedGroupsMap, failedGroupsMap);
		while (lastGroup != null) {
			candidateGroups.add(lastGroup);
			final List<LoadPlanStepRuntime> newSteps = new ArrayList<LoadPlanStepRuntime>();
			final List<CargoGroupRuntime> candidateRuntimeGroups = RuntimeUtil.createRuntimeGroups(candidateGroups);
			final List<CargoItemRuntime> candidateItems = RuntimeUtil.createRuntimeItems(candidateRuntimeGroups);
			final boolean containerSuccess = placeSteps(candidateItems, containerArea, newSteps, config);
			if (containerSuccess) {
				result = newSteps;
				placedCandidateGroups.clear();
				placedCandidateGroups.addAll(candidateGroups);
			} else {
				failedGroupsMap.put(lastGroup.getId(), lastGroup);
				candidateGroups.remove(candidateGroups.size() - 1);
			}
			lastGroup = findOneCandidateGroup(candidateGroups, placedGroups, newGroups, newPlacedGroupsMap, failedGroupsMap);
		}
		return result;
	}
	
	private static void printStats(final ContainerDto container, final List<LoadPlanStepRuntime> placedSteps) {
		final long containerFloorArea = ((long)container.getLength()) * ((long)container.getWidth()); 
		final long containerVolume = containerFloorArea * ((long)container.getHeight());
		long usedVolume = 0;
		long usedFloorArea = 0;
		for (final LoadPlanStepRuntime step: placedSteps) {
			usedVolume += ((long)step.getLength()) * ((long)step.getWidth()) * ((long)step.getHeight());
			if (step.getStartZ() == 0) {
				usedFloorArea += ((long)step.getLength()) * ((long)step.getWidth());
			}
		}
		if (containerFloorArea > 0 && containerVolume > 0) {
			final int volumePercent = (int) (100.0 * usedVolume / containerVolume);
			final int floorPercent = (int) (100.0 * usedFloorArea / containerFloorArea);
			System.out.println ("Volume used: " + usedVolume + "/" + containerVolume + " - " + volumePercent + "%");
			System.out.println ("Floor area used: " + usedFloorArea + "/" + containerFloorArea + " - " + floorPercent + "%");
			if (container.getLoadPlan() != null) {
				container.getLoadPlan().setFloorAreaUsed(floorPercent);
				container.getLoadPlan().setVolumeUsed(volumePercent);
			}
		}
		
	}
	
	private boolean placeSteps (final List<CargoItemRuntime> items, final ContainerAreaRuntime source, final List<LoadPlanStepRuntime> placedSteps, final ConfigDto config) {
		boolean result = true;
		while (findNextStep(items, source, placedSteps, config) != null);
		final List<CargoItemRuntime> remainingItems = items.stream().filter(item -> !item.isPlaced()).collect(Collectors.toList());
		if (remainingItems.size() > 0) {
			for (final CargoItemRuntime item : remainingItems) {
				if (!item.getSource().isStackable() && item.getSource().isSelfStackable()) {
					RuntimeUtil.createSelfStackableStep(placedSteps, item, source, config);
				}
				final int numRemainingItems = item.getRemainingQuantity();
				for (int i = 0; i < numRemainingItems; i++) {
					final LoadPlanStepRuntime step = RuntimeUtil.createStep(placedSteps, item, source, config);
					if (step != null) {
						placedSteps.add(step);
					} else {
//						item.print("FAILED");
						result = false;
					}
				}
			}
		}
		return result;
	}
	
	private LoadPlanStepRuntime findNextStep (final List<CargoItemRuntime> items, final ContainerAreaRuntime source, final List<LoadPlanStepRuntime> placedSteps, final ConfigDto config) {
		final int initialTarget = source.getDimensionValue(source.getTargetDimension());
		final int targetDeduction = Math.round(((float)initialTarget) * 0.15f);
		return findNextStep(items, source, placedSteps, config, initialTarget, targetDeduction);
	}
	
	private LoadPlanStepRuntime findNextStep (final List<CargoItemRuntime> items, final ContainerAreaRuntime source, final List<LoadPlanStepRuntime> placedSteps,
												final ConfigDto config, final int targetSum, final int targetDeduction) {
		if (targetSum < targetDeduction) {
			return null;
		}
		final LoadPlanStepRuntime step = RuntimeUtil.createStep(items, source, targetSum);
		if (step != null) {
//			System.out.println ("Searching for free area for step");
//			step.print("New Candidate");
			final boolean skipCargoSupport = step.getPlacements().size() > 1;
			final float minWeight = step.getMinItemWeight();
			final int length = step.getLength();
			final int width = step.getWidth();
			final int height = step.getHeight();
			ContainerAreaRuntime area = MatrixUtil.getFreeArea(placedSteps, source, minWeight, 1.08f, step.getWeight(), config.isAllowHeavierCargoOnTop(), config.getMaxHeavierCargoOnTop(), 
																			step.getMaxLayer(), 0, 0, 0, length, width, height, false, skipCargoSupport, false, null);
			if (area != null) {
//				System.out.println ("Found Area: X=" + area.getStartX() + ", Y=" + area.getStartY() + ", Z=" + area.getStartZ());
				return confirmStep(step, area, placedSteps);
			} else {
				//We will try to find place for rotated step (length becomes width and vise versa)
				area = MatrixUtil.getFreeArea(placedSteps, source, minWeight, 1.08f, step.getWeight(), config.isAllowHeavierCargoOnTop(), config.getMaxHeavierCargoOnTop(), 
																			step.getMaxLayer(), 0, 0, 0, width, length, height, false, skipCargoSupport, false, null);
				if (area != null) {
					final LoadPlanStepRuntime rotatedStep = step.createRotatedCopy();
//					System.out.println ("Found Area for Rotated step: X=" + area.getStartX() + ", Y=" + area.getStartY() + ", Z=" + area.getStartZ());
//					rotatedStep.print("Rotated");
					return confirmStep(rotatedStep, area, placedSteps);
				}
			}
		}
		return findNextStep(items, source, placedSteps, config, targetSum - targetDeduction, targetDeduction);
	}
	
	public String buildColorPallete() {
		final StringBuilder strB = new StringBuilder();
		strB.append("<html><body><table style=\"width:100%\">");
		for (int row = 0; row < 25; row++) {
			strB.append("<tr>");
			for (int i = 0; i < 15; i++) {
				strB.append("<td style=\"border: 1px solid orange;\">");
				strB.append("<table style=\"width:100%\"><tr><td style=\"width:33%\">&nbsp;</td><td style=\"border: 1px solid black; width:33%; background-color:#");
				final String color = randomColor();
				strB.append(color);
				strB.append("\">&nbsp</td><td style=\"width:33%\">&nbsp;</td></tr><tr><td colspan=3 style=\"text-align:center\">");
				strB.append(color);
				strB.append("</td></tr></table></td>");
			}
			strB.append("</tr>");
		}
		strB.append("</table></body></html>");
		return strB.toString();
	}
	
	public List<CargoGroupDto> generateRandomGroups(final String idPreffix, final int numGroups) {
		final List<CargoGroupDto> result = new ArrayList<CargoGroupDto>();
		final Random rnd = new Random();
		for (int i = 0; i < numGroups; i++) {
			final CargoGroupDto group = new CargoGroupDto();
			result.add(group);
			group.setId(idPreffix + "-" + i);
			group.setName(group.getId());
			group.setColor(randomColor());
			group.setAlreadyLoaded(false);
			group.setStackGroupOnly(false);
			final List<CargoItemDto> items = new ArrayList<CargoItemDto>();
			group.setItems(items);
			final int numItems = 1 + rnd.nextInt(10);
			for (int j = 0; j < numItems; j++) {
				final CargoItemDto item = new CargoItemDto();
				items.add(item);
				item.setId(group.getId() + "-" + j);
				item.setName(item.getId());
				item.setCargoStyle(0);
				final int length = 200 + rnd.nextInt(1800);
				item.setLength(length);
				final int width = 200 + rnd.nextInt(1800);
				item.setWidth(width);
				final int height = 200 + rnd.nextInt(1800);
				item.setHeight(height);
				final float weight = 20 + rnd.nextInt(1460);
				item.setWeight(weight);
				final int quantity = 1 + rnd.nextInt(10);
				item.setQuantity(quantity);
				final int r1 = 1 + rnd.nextInt(10);
				item.setRotatable(r1 % 2 == 0);
				final int r2 = 1 + rnd.nextInt(10);
				final boolean stackable = r2 % 2 == 0;
				item.setStackable(stackable);
				final boolean selfStackable = !stackable && quantity > 1;
				item.setSelfStackable(selfStackable);
			}
		}
		return result;
	}
}
