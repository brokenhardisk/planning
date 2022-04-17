package com.example.planning.persistence.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Shift")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "workday_id", nullable = false)
    Integer workdayId;

    @Column(name = "start_time", nullable = false)
    LocalTime shiftStart;

    @Column(name = "end_time", nullable = false)
    LocalTime shiftEnd;

    @Column(name = "last_update")
    LocalDateTime lastUpdate;
}
