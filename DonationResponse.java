package com.bloodlink.dto;

import java.time.LocalDate;

public class DonationResponse {

	private Long id;
	private Long donorId;
	private String donorName;
	private LocalDate donationDate;

	public DonationResponse() {
	}

	public DonationResponse(Long id, Long donorId, String donorName, LocalDate donationDate) {
		this.id = id;
		this.donorId = donorId;
		this.donorName = donorName;
		this.donationDate = donationDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDonorId() {
		return donorId;
	}

	public void setDonorId(Long donorId) {
		this.donorId = donorId;
	}

	public String getDonorName() {
		return donorName;
	}

	public void setDonorName(String donorName) {
		this.donorName = donorName;
	}

	public LocalDate getDonationDate() {
		return donationDate;
	}

	public void setDonationDate(LocalDate donationDate) {
		this.donationDate = donationDate;
	}
}