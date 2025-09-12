package com.eata.eatamamabe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;  // 닉네임

    @Column(nullable = false, length = 50)
    private String provider;  // 소셜 로그인 제공자 (ex: "kakao")

    @Column(nullable = false, length = 255, unique = true)
    private String providerId;  // 카카오 고유 ID

    @Column(length = 512)
    private String profileImageUrl;

    @Column
    private Integer height;

    @Column
    private Integer weight;

    @Column
    private Integer week;

    // ====== 연관관계 ======
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Condition> conditions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Allergy> allergies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DayLog> dayLogs = new ArrayList<>();

    // ====== 연관관계 편의 메서드 ======
    public void addCondition(Condition condition) {
        conditions.add(condition);
        condition.setUser(this);
    }

    public void addAllergy(Allergy allergy) {
        allergies.add(allergy);
        allergy.setUser(this);
    }

    // ====== 편의 메서드 ======
    public void updateHealth(Integer h, Integer w, Integer wk) {
        if (h != null) this.height = h;
        if (w != null) this.weight = w;
        if (wk != null) this.week = wk;
    }
}
