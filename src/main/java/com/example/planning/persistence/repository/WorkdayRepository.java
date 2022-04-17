package com.example.planning.persistence.repository;

import com.example.planning.persistence.model.Workday;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Interface representing repository methods on Workday table
 */
public interface WorkdayRepository extends PagingAndSortingRepository<Workday, Integer> {
    /**
     *
     * @param date
     * @return
     */
    Optional<Workday> findByDate(LocalDate date);
}
