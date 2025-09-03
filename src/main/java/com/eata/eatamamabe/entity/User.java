package com.eata.eatamamabe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // 내부 PK

    @Column(length = 50)
    private String email;  // 이메일 (nullable)

    @Column(nullable = false, length = 50)
    private String nickname;  // 닉네임

    @Column(nullable = false, length = 50)
    private String provider;  // 소셜 로그인 제공자 (ex: "kakao")

    @Column(nullable = false, length = 255, unique = true)
    private String providerId;  // 카카오 고유 ID

    @Column
    private Long height;   // 키

    @Column
    private Long weight;   // 몸무게

    @Column
    private Long week;     // 주차
}
