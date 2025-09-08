package com.eata.eatamamabe.repository;

import com.eata.eatamamabe.entity.Condition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
    List<Condition> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
