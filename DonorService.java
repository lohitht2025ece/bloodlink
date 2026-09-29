package com.bloodlink.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bloodlink.entity.BloodGroup;
import com.bloodlink.entity.Donor;
import com.bloodlink.exception.BusinessRuleException;
import com.bloodlink.exception.ResourceNotFoundException;
import com.bloodlink.repository.BloodGroupRepository;
import com.bloodlink.repository.DonationRecordRepository;
import com.bloodlink.repository.DonorRepository;

@Service
public class DonorService {

	private static final long COOLDOWN_DAYS = 90;

	private final DonorRepository donorRepository;
	private final BloodGroupRepository bloodGroupRepository;
	private final DonationRecordRepository donationRecordRepository;
	private final BloodGroupService bloodGroupService;

	public DonorService(
			DonorRepository donorRepository,
			BloodGroupRepository bloodGroupRepository,
			DonationRecordRepository donationRecordRepository,
			BloodGroupService bloodGroupService) {
		this.donorRepository = donorRepository;
		this.bloodGroupRepository = bloodGroupRepository;
		this.donationRecordRepository = donationRecordRepository;
		this.bloodGroupService = bloodGroupService;
	}

	public Donor registerDonor(Donor donor) {
		if (donor == null || donor.getBloodGroup() == null) {
			throw new BusinessRuleException("Donor and blood group are required.");
		}
		donor.setBloodGroup(bloodGroupService.findByName(donor.getBloodGroup().getName()));
		return donorRepository.save(donor);
	}

	public Donor findDonorById(Long id) {
		return donorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Donor not found: " + id));
	}

	public List<Donor> getAllDonors() {
		return donorRepository.findAll();
	}

	public Donor updateDonor(Long id, Donor donorDetails) {
		Donor existingDonor = findDonorById(id);
		if (donorDetails == null || donorDetails.getBloodGroup() == null) {
			throw new BusinessRuleException("Donor details and blood group are required.");
		}

		BloodGroup bloodGroup = bloodGroupService.findByName(donorDetails.getBloodGroup().getName());
		existingDonor.setName(donorDetails.getName());
		existingDonor.setPhone(donorDetails.getPhone());
		existingDonor.setCity(donorDetails.getCity());
		existingDonor.setBloodGroup(bloodGroup);
		existingDonor.setLastDonationDate(donorDetails.getLastDonationDate());
		return donorRepository.save(existingDonor);
	}

	public void deleteDonor(Long id) {
		Donor donor = findDonorById(id);
		if (donationRecordRepository.existsByDonor_Id(id)) {
			throw new BusinessRuleException("Cannot delete a donor with donation history.");
		}
		donorRepository.delete(donor);
	}

	public List<Donor> searchDonorsByBloodGroup(String bloodGroupName) {
		BloodGroup bloodGroup = bloodGroupService.findByName(bloodGroupName);
		return eligibleDonors(donorRepository.findByBloodGroup(bloodGroup));
	}

	public List<Donor> searchDonorsByBloodGroupAndCity(String bloodGroupName, String city) {
		BloodGroup bloodGroup = bloodGroupService.findByName(bloodGroupName);
		return eligibleDonors(donorRepository.findByBloodGroupAndCity(bloodGroup, city));
	}

	public long countDonorsByBloodGroup(String bloodGroupName) {
		BloodGroup bloodGroup = bloodGroupService.findByName(bloodGroupName);
		return donorRepository.countByBloodGroup(bloodGroup);
	}

	public Map<String, Long> getDonorCountsByBloodGroup() {
		Map<String, Long> donorCounts = new LinkedHashMap<>();
		for (String bloodGroupName : BloodGroupService.SUPPORTED_NAMES) {
			long count = bloodGroupRepository.findByName(bloodGroupName)
					.map(donorRepository::countByBloodGroup)
					.orElse(0L);
			donorCounts.put(bloodGroupName, count);
		}
		return donorCounts;
	}

	public boolean isDonorEligible(Donor donor) {
		if (donor == null) {
			throw new BusinessRuleException("Donor is required to check eligibility.");
		}

		LocalDate lastDonationDate = donor.getLastDonationDate();
		return lastDonationDate == null
				|| ChronoUnit.DAYS.between(lastDonationDate, LocalDate.now()) >= COOLDOWN_DAYS;
	}

	private List<Donor> eligibleDonors(List<Donor> donors) {
		return donors.stream()
				.filter(this::isDonorEligible)
				.collect(Collectors.toList());
	}
}