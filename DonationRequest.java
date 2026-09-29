package com.bloodlink.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DonationRequest {

	@NotNull
	@Positive
	private Long donorId;

	@NotNull
	private LocalDate donationDate;

	public DonationRequest() {
	}

	public DonationRequest(Long donorId, LocalDate donationDate) {
		this.donorId = donorId;
		this.donationDate = donationDate;
	}

	public Long getDonorId() {
		return donorId;
	}

	public void setDonorId(Long donorId) {
		this.donorId = donorId;
	}

	public LocalDate getDonationDate() {
		return donationDate;
	}

	public void setDonationDate(LocalDate donationDate) {
		this.donationDate = donationDate;
	}
}