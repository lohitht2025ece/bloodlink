package com.bloodlink.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bloodlink.dto.BloodGroupResponse;
import com.bloodlink.entity.BloodGroup;
import com.bloodlink.service.BloodGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/blood-groups")
@Tag(name = "Blood Groups", description = "View the supported blood groups.")
public class BloodGroupController {

	private final BloodGroupService bloodGroupService;

	public BloodGroupController(BloodGroupService bloodGroupService) {
		this.bloodGroupService = bloodGroupService;
	}

	@GetMapping
	@Operation(summary = "Get supported blood groups", description = "Returns the supported blood group names and their IDs when registered.")
	public ResponseEntity<List<BloodGroupResponse>> getSupportedBloodGroups() {
		Map<String, Long> idsByName = bloodGroupService.getSupportedBloodGroups().stream()
				.collect(Collectors.toMap(BloodGroup::getName, BloodGroup::getId));
		List<BloodGroupResponse> responses = BloodGroupService.SUPPORTED_NAMES.stream()
				.map(name -> new BloodGroupResponse(idsByName.get(name), name))
				.toList();
		return ResponseEntity.ok(responses);
	}
}