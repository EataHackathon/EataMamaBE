package com.eata.eatamamabe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "allergies") // 안전한 복수형 테이블명
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allergy extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allergyId;   // 내부 PK

    @Column(length = 50)
    private String allergyName; // 알레르기 이름 (nullable)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private User user;          // 유저 FK

    public void setUser(User user) {
        this.user = user;
    }
}
