package com.developsoft.comtainer.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developsoft.comtainer.rest.dto.ComtainerRequestDto;
import com.developsoft.comtainer.rest.dto.ContainerLoadPlanDto;
import com.developsoft.comtainer.service.ComtainerService;

@RestController
@RequestMapping("/comtainer/api/v0.1/")
public class ComtainerRestController {

	@Autowired
	private ComtainerService comtainerService;
	
	@CrossOrigin
	@PostMapping("test1")
	public ResponseEntity<List<ContainerLoadPlanDto>> test1(@RequestBody final ComtainerRequestDto request) {
		return ResponseEntity.ok(this.comtainerService.createLoadPlan(request));
	}
	
}
