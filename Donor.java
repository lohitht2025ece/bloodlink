package com.bloodlink.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "donors")
public class Donor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@NotBlank
	@Column(nullable = false)
	private String phone;

	@NotBlank
	@Column(nullable = false)
	private String city;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "blood_group_id", nullable = false)
	private BloodGroup bloodGroup;

	@Column(name = "last_donation_date")
	private LocalDate lastDonationDate;

	@OneToMany(mappedBy = "donor")
	private List<DonationRecord> donationRecords = new ArrayList<>();

	protected Donor() {
	}

	public Donor(String name, String phone, String city, BloodGroup bloodGroup, LocalDate lastDonationDate) {
		this.name = name;
		this.phone = phone;
		this.city = city;
		this.bloodGroup = bloodGroup;
		this.lastDonationDate = lastDonationDate;
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

	public BloodGroup getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(BloodGroup bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public LocalDate getLastDonationDate() {
		return lastDonationDate;
	}

	public void setLastDonationDate(LocalDate lastDonationDate) {
		this.lastDonationDate = lastDonationDate;
	}

	public List<DonationRecord> getDonationRecords() {
		return donationRecords;
	}

	public void setDonationRecords(List<DonationRecord> donationRecords) {
		this.donationRecords = donationRecords;
	}
}