package com.example.planning.persistence.repository;

import com.example.planning.persistence.model.Shift;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface representing repository methods on Shift table
 */
public interface ShiftRepository extends PagingAndSortingRepository<Shift, Integer> {
    Optional<List<Shift>> findByWorkdayId(Integer id);
}
