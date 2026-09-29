package com.bloodlink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bloodlink.entity.DonationRecord;
import com.bloodlink.entity.Donor;

public interface DonationRecordRepository extends JpaRepository<DonationRecord, Long> {

	@Override
	@EntityGraph(attributePaths = "donor")
	Optional<DonationRecord> findById(Long id);

	boolean existsByDonor_Id(Long donorId);

	@EntityGraph(attributePaths = "donor")
	List<DonationRecord> findByDonorOrderByDonationDateDesc(Donor donor);
}