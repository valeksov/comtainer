package com.developsoft.comtainer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

	public ComtainerResponseDto run( final ComtainerRequestDto request) {
		final ComtainerResponseDto result = new ComtainerResponseDto();
		result.setConfig(request.getConfig());
		result.setGroups(request.getGroups());
		result.setStatus(0);
		result.setContainers(request.getContainers());
		if (request.getContainers() != null && request.getContainers().size() > 0) {
			final ContainerDto container = request.getContainers().get(0);
			final ContainerLoadPlanDto loadPlan = new ContainerLoadPlanDto();
			loadPlan.setId(UUID.randomUUID().toString());
			container.setLoadPlan(loadPlan);
			if (request.getGroups() != null && request.getGroups().size() > 0) {
				final List<CargoItemRuntime> initialItems = RuntimeUtil.createRuntimeItems(request.getGroups());
				final ContainerAreaRuntime initialArea = ContainerUtil.createContainerArea(container, request.getConfig());
				final List<LoadPlanStepRuntime> placedSteps = new ArrayList<LoadPlanStepRuntime>();
				while (findNextStep(initialItems, initialArea, placedSteps) != null);
				final List<CargoItemRuntime> remainingItems = initialItems.stream().filter(item -> !item.isPlaced()).collect(Collectors.toList());
				if (remainingItems.size() > 0) {
					for (final CargoItemRuntime item : remainingItems) {
						final int numRemainingItems = item.getRemainingQuantity();
						for (int i = 0; i < numRemainingItems; i++) {
							final LoadPlanStepRuntime step = RuntimeUtil.createStep(placedSteps, item, initialArea);
							if (step != null) {
								placedSteps.add(step);
							} else {
								item.print("FAILED");
								result.setStatus(1);
							}
						}
					}
				}
				if (placedSteps.size() > 0) {
					loadPlan.setLoadPlanSteps(placedSteps.stream().map(step -> step.toDto()).collect(Collectors.toList()));
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
	
	private LoadPlanStepRuntime findNextStep (final List<CargoItemRuntime> items, final ContainerAreaRuntime source, final List<LoadPlanStepRuntime> placedSteps,
												final int targetSum, final int targetDeduction) {
		if (targetSum < targetDeduction) {
			return null;
		}
		final LoadPlanStepRuntime step = RuntimeUtil.createStep(items, source, targetSum);
		if (step != null) {
			final ContainerAreaRuntime area = MatrixUtil.getFreeArea(placedSteps, source, step.getMinItemWeight(), 1.08f, 0, 0, 0, step.getLength(), step.getWidth(), step.getHeight(), false);
			if (area != null) {
				step.confirm();
				step.updateCoordinates(area.getStartX(), area.getStartY(), area.getStartZ());
				placedSteps.add(step);
				return step;
			}
		}
		return findNextStep(items, source, placedSteps, targetSum - targetDeduction, targetDeduction);
	}
	
}
