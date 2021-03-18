package com.developsoft.comtainer.service;

import org.springframework.stereotype.Service;

import com.developsoft.comtainer.rest.dto.ComtainerRequestDto;
import com.developsoft.comtainer.rest.dto.ContainerLoadPlanDto;
import com.developsoft.comtainer.util.CargoUtil;

@Service
public class ComtainerService {
	public ContainerLoadPlanDto createLoadPlan (final ComtainerRequestDto request) {
		return CargoUtil.createLoadPlan(request);
	}
}
