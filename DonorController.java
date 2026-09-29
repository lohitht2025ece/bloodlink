package com.bloodlink.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bloodlink.dto.DonorRequest;
import com.bloodlink.dto.DonorResponse;
import com.bloodlink.entity.BloodGroup;
import com.bloodlink.entity.Donor;
import com.bloodlink.service.DonorService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/donors")
@Tag(name = "Donors", description = "Register, manage, search, and view donor statistics.")
public class DonorController {

	private final DonorService donorService;

	public DonorController(DonorService donorService) {
		this.donorService = donorService;
	}

	@PostMapping
	@Operation(summary = "Register a donor", description = "Registers a donor with a supported blood group.")
	public ResponseEntity<DonorResponse> registerDonor(@Valid @RequestBody DonorRequest request) {
		Donor donor = new Donor(
				request.getName(),
				request.getPhone(),
				request.getCity(),
				new BloodGroup(request.getBloodGroup()),
				null);
		Donor savedDonor = donorService.registerDonor(donor);
		return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedDonor));
	}

	@GetMapping
	@Operation(summary = "Get all donors", description = "Returns all registered donors with current eligibility status.")
	public ResponseEntity<List<DonorResponse>> getAllDonors() {
		List<DonorResponse> donors = donorService.getAllDonors().stream()
				.map(this::toResponse)
				.toList();
		return ResponseEntity.ok(donors);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get donor by ID", description = "Returns one registered donor by ID.")
	public ResponseEntity<DonorResponse> getDonorById(@PathVariable Long id) {
		return ResponseEntity.ok(toResponse(donorService.findDonorById(id)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update donor details", description = "Updates a donor's contact details, city, or blood group.")
	public ResponseEntity<DonorResponse> updateDonor(
			@PathVariable Long id,
			@Valid @RequestBody DonorRequest request) {
		Donor existingDonor = donorService.findDonorById(id);
		Donor donorDetails = new Donor(
				request.getName(),
				request.getPhone(),
				request.getCity(),
				new BloodGroup(request.getBloodGroup()),
				existingDonor.getLastDonationDate());
		return ResponseEntity.ok(toResponse(donorService.updateDonor(id, donorDetails)));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a donor", description = "Deletes a registered donor by ID.")
	public ResponseEntity<Void> deleteDonor(@PathVariable Long id) {
		donorService.deleteDonor(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/search")
	@Operation(summary = "Search eligible donors", description = "Searches eligible donors by blood group and optionally by city.")
	public ResponseEntity<List<DonorResponse>> searchDonors(
			@Parameter(description = "Supported blood group, such as O+") @RequestParam String bloodGroup,
			@Parameter(description = "Optional city filter") @RequestParam(required = false) String city) {
		List<Donor> matchingDonors = city == null
				? donorService.searchDonorsByBloodGroup(bloodGroup)
				: donorService.searchDonorsByBloodGroupAndCity(bloodGroup, city);
		List<DonorResponse> responses = matchingDonors.stream()
				.map(this::toResponse)
				.toList();
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/statistics")
	@Operation(summary = "Get donor statistics", description = "Returns the registered donor count for each supported blood group.")
	public ResponseEntity<Map<String, Long>> getDonorStatistics() {
		return ResponseEntity.ok(donorService.getDonorCountsByBloodGroup());
	}

	private DonorResponse toResponse(Donor donor) {
		return new DonorResponse(
				donor.getId(),
				donor.getName(),
				donor.getPhone(),
				donor.getCity(),
				donor.getBloodGroup().getName(),
				donor.getLastDonationDate(),
				donorService.isDonorEligible(donor));
	}
}