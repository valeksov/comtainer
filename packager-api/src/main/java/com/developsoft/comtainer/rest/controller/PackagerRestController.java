package com.developsoft.comtainer.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developsoft.comtainer.rest.dto.ComtainerRequestDto;
import com.developsoft.comtainer.rest.dto.ComtainerResponseDto;
import com.developsoft.comtainer.service.PackagerService;

@RestController
@RequestMapping("/comtainer/packager/api/v0.1/")
public class PackagerRestController {

	@Autowired
	private PackagerService packagerService;
	
	@CrossOrigin
	@PostMapping("run")
	public ResponseEntity<ComtainerResponseDto> run(@RequestBody final ComtainerRequestDto request) {
		return ResponseEntity.ok(this.packagerService.run(request));
	}
}
