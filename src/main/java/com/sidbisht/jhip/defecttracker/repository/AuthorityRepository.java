package com.sidbisht.jhip.defecttracker.repository;

import com.sidbisht.jhip.defecttracker.domain.Authority;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
