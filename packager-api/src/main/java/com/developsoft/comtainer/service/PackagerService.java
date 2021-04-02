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
				final List<LoadPlanStepRuntime> steps = new ArrayList<LoadPlanStepRuntime>();
				for (final CargoItemRuntime item : initialItems) {
					for (int i = 0; i < item.getSource().getQuantity(); i++) {
						final LoadPlanStepRuntime step = RuntimeUtil.createStep(steps, item, initialArea);
						if (step != null) {
							steps.add(step);
						}
					}
				}
				if (steps.size() > 0) {
					loadPlan.setLoadPlanSteps(steps.stream().map(step -> step.toDto()).collect(Collectors.toList()));
				}
			}
		}
		return result;
	}
}
