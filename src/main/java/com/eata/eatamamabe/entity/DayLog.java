package com.eata.eatamamabe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dayLog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dayLogId; // 내부 PK

    @Column(nullable = false, length = 50)
    private String logDate; // 하루 기준 날짜

    @Column(length = 512)
    private String dailyAdvice; // ai 하루 조언

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private User user;
}