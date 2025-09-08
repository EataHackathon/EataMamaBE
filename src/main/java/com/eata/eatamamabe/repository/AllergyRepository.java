package com.eata.eatamamabe.repository;

import com.eata.eatamamabe.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {
    List<Allergy> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
