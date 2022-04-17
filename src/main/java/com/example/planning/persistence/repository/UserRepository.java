package com.example.planning.persistence.repository;

import com.example.planning.persistence.model.Shift;
import com.example.planning.persistence.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Interface representing repository methods on Shift table
 */
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    Optional<User> findByName(String name);

    Optional<List<User>> findByShiftIdIsNull();

    Optional<List<User>> findByShiftIdIsNotNull();

    @Modifying
    @Query("update User user set user.shiftId = :status where user.shiftId = :id")
    int setStatusForShiftId(@Param("status") Integer status, @Param("id") Integer id);
}
