package com.bloodlink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bloodlink.entity.BloodGroup;
import com.bloodlink.entity.Donor;

public interface DonorRepository extends JpaRepository<Donor, Long> {

	@Override
	@EntityGraph(attributePaths = "bloodGroup")
	Optional<Donor> findById(Long id);

	@Override
	@EntityGraph(attributePaths = "bloodGroup")
	List<Donor> findAll();

	@EntityGraph(attributePaths = "bloodGroup")
	List<Donor> findByBloodGroup(BloodGroup bloodGroup);

	@EntityGraph(attributePaths = "bloodGroup")
	List<Donor> findByBloodGroupAndCity(BloodGroup bloodGroup, String city);

	long countByBloodGroup(BloodGroup bloodGroup);
}