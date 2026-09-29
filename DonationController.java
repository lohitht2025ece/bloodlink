package com.bloodlink.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bloodlink.dto.DonationRequest;
import com.bloodlink.dto.DonationResponse;
import com.bloodlink.entity.DonationRecord;
import com.bloodlink.service.DonationService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/donations")
@Tag(name = "Donations", description = "Record donations and view donation history.")
public class DonationController {

	private final DonationService donationService;

	public DonationController(DonationService donationService) {
		this.donationService = donationService;
	}

	@PostMapping
	@Operation(summary = "Record a donation", description = "Records a donation for a donor and updates their last donation date.")
	public ResponseEntity<DonationResponse> recordDonation(@Valid @RequestBody DonationRequest request) {
		DonationRecord donationRecord = donationService.recordDonation(
				request.getDonorId(), request.getDonationDate());
		return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(donationRecord));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a donation record", description = "Returns one donation record by ID.")
	public ResponseEntity<DonationResponse> getDonationById(@PathVariable Long id) {
		return ResponseEntity.ok(toResponse(donationService.findDonationRecordById(id)));
	}

	@GetMapping("/donor/{donorId}")
	@Operation(summary = "Get donor donation history", description = "Returns a donor's donation records ordered by donation date.")
	public ResponseEntity<List<DonationResponse>> getDonationHistory(@PathVariable Long donorId) {
		List<DonationResponse> history = donationService.getDonationHistory(donorId).stream()
				.map(this::toResponse)
				.toList();
		return ResponseEntity.ok(history);
	}

	private DonationResponse toResponse(DonationRecord donationRecord) {
		return new DonationResponse(
				donationRecord.getId(),
				donationRecord.getDonor().getId(),
				donationRecord.getDonor().getName(),
				donationRecord.getDonationDate());
	}
}