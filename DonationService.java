package com.bloodlink.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bloodlink.entity.DonationRecord;
import com.bloodlink.entity.Donor;
import com.bloodlink.exception.BusinessRuleException;
import com.bloodlink.exception.ResourceNotFoundException;
import com.bloodlink.repository.DonationRecordRepository;
import com.bloodlink.repository.DonorRepository;

@Service
public class DonationService {

	private final DonationRecordRepository donationRecordRepository;
	private final DonorRepository donorRepository;
	private final DonorService donorService;

	public DonationService(
			DonationRecordRepository donationRecordRepository,
			DonorRepository donorRepository,
			DonorService donorService) {
		this.donationRecordRepository = donationRecordRepository;
		this.donorRepository = donorRepository;
		this.donorService = donorService;
	}

	@Transactional
	public DonationRecord recordDonation(Long donorId, LocalDate donationDate) {
		Donor donor = donorService.findDonorById(donorId);
		if (donationDate == null) {
			throw new BusinessRuleException("Donation date is required.");
		}
		if (donationDate.isAfter(LocalDate.now())) {
			throw new BusinessRuleException("Donation date cannot be in the future.");
		}
		if (!donorService.isDonorEligible(donor)) {
			throw new BusinessRuleException("Donor is still within the 90-day cooldown.");
		}

		DonationRecord donationRecord = donationRecordRepository.save(new DonationRecord(donor, donationDate));
		donor.setLastDonationDate(donationDate);
		donorRepository.save(donor);
		return donationRecord;
	}

	public DonationRecord findDonationRecordById(Long id) {
		return donationRecordRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Donation record not found: " + id));
	}

	public List<DonationRecord> getDonationHistory(Long donorId) {
		Donor donor = donorService.findDonorById(donorId);
		return donationRecordRepository.findByDonorOrderByDonationDateDesc(donor);
	}
}