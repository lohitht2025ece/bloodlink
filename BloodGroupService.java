package com.bloodlink.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.bloodlink.entity.BloodGroup;
import com.bloodlink.exception.BusinessRuleException;
import com.bloodlink.exception.ResourceNotFoundException;
import com.bloodlink.repository.BloodGroupRepository;

@Service
public class BloodGroupService {

	public static final List<String> SUPPORTED_NAMES = List.of(
			"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

	private final BloodGroupRepository bloodGroupRepository;

	public BloodGroupService(BloodGroupRepository bloodGroupRepository) {
		this.bloodGroupRepository = bloodGroupRepository;
	}

	public List<BloodGroup> getSupportedBloodGroups() {
		return bloodGroupRepository.findAll().stream()
				.filter(bloodGroup -> SUPPORTED_NAMES.contains(bloodGroup.getName()))
				.toList();
	}

	public BloodGroup findByName(String name) {
		String normalizedName = normalizeSupportedName(name);
		return bloodGroupRepository.findByName(normalizedName)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Blood group not found: " + normalizedName));
	}

	public BloodGroup registerBloodGroup(String name) {
		String normalizedName = normalizeSupportedName(name);
		if (bloodGroupRepository.findByName(normalizedName).isPresent()) {
			throw new BusinessRuleException("Blood group already exists: " + normalizedName);
		}
		return bloodGroupRepository.save(new BloodGroup(normalizedName));
	}

	private String normalizeSupportedName(String name) {
		if (name == null || name.isBlank()) {
			throw new BusinessRuleException("Blood group name is required.");
		}

		String normalizedName = name.trim().toUpperCase(Locale.ROOT);
		if (!SUPPORTED_NAMES.contains(normalizedName)) {
			throw new BusinessRuleException("Unsupported blood group: " + normalizedName);
		}
		return normalizedName;
	}
}