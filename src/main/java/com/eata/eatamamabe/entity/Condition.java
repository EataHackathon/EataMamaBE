package com.eata.eatamamabe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conditions") // "condition"은 예약어 가능성 → 복수형으로 회피
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Condition extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conditionId; // 내부 PK

    @Column(length = 50)
    private String diseaseName; // 질환 이름 (nullable)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private User user;          // 유저 FK

    public void setUser(User user) {
        this.user = user;
    }
}
