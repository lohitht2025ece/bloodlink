package com.bloodlink.dto;

import java.time.LocalDate;

public class DonorResponse {

	private Long id;
	private String name;
	private String phone;
	private String city;
	private String bloodGroup;
	private LocalDate lastDonationDate;
	private boolean eligible;

	public DonorResponse() {
	}

	public DonorResponse(
			Long id,
			String name,
			String phone,
			String city,
			String bloodGroup,
			LocalDate lastDonationDate,
			boolean eligible) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.city = city;
		this.bloodGroup = bloodGroup;
		this.lastDonationDate = lastDonationDate;
		this.eligible = eligible;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public LocalDate getLastDonationDate() {
		return lastDonationDate;
	}

	public void setLastDonationDate(LocalDate lastDonationDate) {
		this.lastDonationDate = lastDonationDate;
	}

	public boolean isEligible() {
		return eligible;
	}

	public void setEligible(boolean eligible) {
		this.eligible = eligible;
	}
}