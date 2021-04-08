package com.developsoft.comtainer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.developsoft.comtainer.rest.dto.CargoGroupDto;
import com.developsoft.comtainer.rest.dto.ComtainerRequestDto;
import com.developsoft.comtainer.rest.dto.ComtainerResponseDto;
import com.developsoft.comtainer.rest.dto.ContainerDto;
import com.developsoft.comtainer.rest.dto.ContainerLoadPlanDto;
import com.developsoft.comtainer.runtime.model.CargoItemRuntime;
import com.developsoft.comtainer.runtime.model.ContainerAreaRuntime;
import com.developsoft.comtainer.runtime.model.LoadPlanStepRuntime;
import com.developsoft.comtainer.runtime.util.ContainerUtil;
import com.developsoft.comtainer.runtime.util.MatrixUtil;
import com.developsoft.comtainer.runtime.util.RuntimeUtil;

@Service
public class PackagerService {

	public ComtainerResponseDto run(final ComtainerRequestDto request) {
		final ComtainerResponseDto result = new ComtainerResponseDto();
		result.setConfig(request.getConfig());
		result.setGroups(request.getGroups());
		boolean success = false;
		result.setContainers(request.getContainers());
		if (request.getContainers() != null && request.getContainers().size() > 0) {
			final ContainerDto container = request.getContainers().get(0);
			final ContainerLoadPlanDto loadPlan = new ContainerLoadPlanDto();
			loadPlan.setId(UUID.randomUUID().toString());
			container.setLoadPlan(loadPlan);
			if (request.getGroups() != null && request.getGroups().size() > 0) {
				List<LoadPlanStepRuntime> placedSteps = new ArrayList<LoadPlanStepRuntime>();
				final ContainerAreaRuntime initialArea = ContainerUtil.createContainerArea(container, request.getConfig());
				final List<CargoGroupDto> newGroups = request.getGroups().stream().filter(groupDto -> !groupDto.isAlreadyLoaded()).collect(Collectors.toList());
				final List<CargoGroupDto> placedGroups = request.getGroups().stream().filter(groupDto -> groupDto.isAlreadyLoaded()).collect(Collectors.toList());
				//At first we will take the old plan (if any), identify the new groups and try to add the new items to the old plan without full rebuild
				if (container.getLoadPlan() != null && container.getLoadPlan().getLoadPlanSteps() != null && container.getLoadPlan().getLoadPlanSteps().size() > 0) {
					System.out.println ("Check with the old plan first");
					placedSteps.addAll(container.getLoadPlan().getLoadPlanSteps().stream().map(stepDto -> new LoadPlanStepRuntime(stepDto)).collect(Collectors.toList()));
					final List<CargoItemRuntime> newItems = RuntimeUtil.createRuntimeItems(newGroups);
					success = placeSteps(newItems, initialArea, placedSteps);
				}
				//We will start the packaging from the start
				if (!success) {
					placedSteps.clear();
					final List<CargoItemRuntime> initialItems = RuntimeUtil.createRuntimeItems(request.getGroups());
					System.out.println ("Total Number of Items: " + initialItems.size());
					final float averageWeight = RuntimeUtil.getAverageWeight(initialItems);
					//1. Try the heaviest packages first (weight 50% higher than the average weight)
					final List<CargoItemRuntime> mostHeavyItems = RuntimeUtil.filterByWeight(initialItems, averageWeight * 1.5f);
					System.out.println ("Heaviest Items: " + mostHeavyItems.size());
					placeSteps(mostHeavyItems, initialArea, placedSteps);
					//2. Try the next heavy packages (weight 80% or more compared to the the average weight)
					final List<CargoItemRuntime> nextHeavyItems = RuntimeUtil.filterByWeight(initialItems, averageWeight * 0.8f);
					System.out.println ("Next Heavy Items: " + nextHeavyItems.size());
					placeSteps(nextHeavyItems, initialArea, placedSteps);
					//3.Try all the remaining packages (not placed heavy packages and the light ones)
					success = placeSteps(initialItems, initialArea, placedSteps);
				}
				
				
				if (placedSteps.size() > 0) {
					printStats(container, placedSteps);
					loadPlan.setLoadPlanSteps(placedSteps.stream().map(step -> step.toDto()).collect(Collectors.toList()));
				}
			}
		}
		result.setStatus(success ? 0 : 1);
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
		}
		
	}
	
	private boolean placeSteps (final List<CargoItemRuntime> items, final ContainerAreaRuntime source, final List<LoadPlanStepRuntime> placedSteps) {
		boolean result = true;
		while (findNextStep(items, source, placedSteps) != null);
		final List<CargoItemRuntime> remainingItems = items.stream().filter(item -> !item.isPlaced()).collect(Collectors.toList());
		if (remainingItems.size() > 0) {
			for (final CargoItemRuntime item : remainingItems) {
				final int numRemainingItems = item.getRemainingQuantity();
				for (int i = 0; i < numRemainingItems; i++) {
					final LoadPlanStepRuntime step = RuntimeUtil.createStep(placedSteps, item, source);
					if (step != null) {
						placedSteps.add(step);
					} else {
						item.print("FAILED");
						result = false;
					}
				}
			}
		}
		return result;
	}
	
	private LoadPlanStepRuntime findNextStep (final List<CargoItemRuntime> items, final ContainerAreaRuntime source, final List<LoadPlanStepRuntime> placedSteps) {
		final int initialTarget = source.getDimensionValue(source.getTargetDimension());
		final int targetDeduction = Math.round(((float)initialTarget) * 0.15f);
		return findNextStep(items, source, placedSteps, initialTarget, targetDeduction);
	}
	
	private LoadPlanStepRuntime confirmStep(final LoadPlanStepRuntime step, final ContainerAreaRuntime area, final List<LoadPlanStepRuntime> placedSteps) {
		step.confirm();
		step.updateCoordinates(area.getStartX(), area.getStartY(), area.getStartZ());
		placedSteps.add(step);
		return step;
	}
	
	private LoadPlanStepRuntime findNextStep (final List<CargoItemRuntime> items, final ContainerAreaRuntime source, final List<LoadPlanStepRuntime> placedSteps,
												final int targetSum, final int targetDeduction) {
		if (targetSum < targetDeduction) {
			return null;
		}
		final LoadPlanStepRuntime step = RuntimeUtil.createStep(items, source, targetSum);
		if (step != null) {
			System.out.println ("Searching for free area for step");
			step.print("New Candidate");
			ContainerAreaRuntime area = MatrixUtil.getFreeArea(placedSteps, source, step.getMinItemWeight(), 1.08f, 0, 0, 0, step.getLength(), step.getWidth(), step.getHeight(), false);
			if (area != null) {
				System.out.println ("Found Area: X=" + area.getStartX() + ", Y=" + area.getStartY() + ", Z=" + area.getStartZ());
				return confirmStep(step, area, placedSteps);
			} else {
				//We will try to find place for rotated step (length becomes width and vise versa)
				area = MatrixUtil.getFreeArea(placedSteps, source, step.getMinItemWeight(), 1.08f, 0, 0, 0, step.getWidth(), step.getLength(), step.getHeight(), false);
				if (area != null) {
					final LoadPlanStepRuntime rotatedStep = step.createRotatedCopy();
					System.out.println ("Found Area for Rotated step: X=" + area.getStartX() + ", Y=" + area.getStartY() + ", Z=" + area.getStartZ());
					rotatedStep.print("Rotated");
					return confirmStep(rotatedStep, area, placedSteps);
				}
			}
		}
		return findNextStep(items, source, placedSteps, targetSum - targetDeduction, targetDeduction);
	}
	
}
