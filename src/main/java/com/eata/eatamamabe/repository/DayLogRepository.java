package com.eata.eatamamabe.repository;

import com.eata.eatamamabe.entity.DayLog;
import com.eata.eatamamabe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DayLogRepository extends JpaRepository<DayLog, Long> {
    Optional<DayLog> findByUserAndLogDate(User user, String logDate);
}
