package com.eata.eatamamabe.service;

import com.eata.eatamamabe.config.exception.CustomException;
import com.eata.eatamamabe.dto.user.*;
import com.eata.eatamamabe.entity.Allergy;
import com.eata.eatamamabe.entity.Condition;
import com.eata.eatamamabe.entity.User;
import com.eata.eatamamabe.repository.AllergyRepository;
import com.eata.eatamamabe.repository.ConditionRepository;
import com.eata.eatamamabe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ConditionRepository conditionRepository;
    private final AllergyRepository allergyRepository;

    @Transactional(readOnly = true)
    public MyInfoResponseDTO getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CustomException.notFound("유저를 찾을 수 없습니다"));

        List<ConditionDto> conditionDtos = conditionRepository.findAllByUserId(userId).stream()
                .map(c -> new ConditionDto(c.getConditionId(), c.getDiseaseName()))
                .toList();

        List<AllergyDto> allergyDtos = allergyRepository.findAllByUserId(userId).stream()
                .map(a -> new AllergyDto(a.getAllergyId(), a.getAllergyName()))
                .toList();

        return new MyInfoResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getHeight(),
                user.getWeight(),
                user.getWeek(),
                conditionDtos,
                allergyDtos,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    public MyInfoCreateResponseDTO createMyInfo(Long userId, MyInfoCreateRequestDTO req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CustomException.notFound("유저를 찾을 수 없습니다"));

        user.updateHealth(req.getHeight(), req.getWeight(), req.getWeek());
        System.out.println(user.getHeight());

        // 요청에 들어온 경우에만 전체 교체
        if (req.getConditions() != null) {
            conditionRepository.deleteAllByUserId(userId);
            for (ConditionDto c : req.getConditions()) {
                if (c == null || c.getDiseaseName() == null || c.getDiseaseName().isBlank()) continue;
                user.addCondition(
                        Condition.builder()
                                .diseaseName(c.getDiseaseName())
                                .user(user)
                                .build()
                );
            }
        }

        // 요청에 들어온 경우에만 전체 교체
        if (req.getAllergies() != null) {
            allergyRepository.deleteAllByUserId(userId);
            for (AllergyDto a : req.getAllergies()) {
                if (a == null || a.getAllergyName() == null || a.getAllergyName().isBlank()) continue;
                user.addAllergy(
                        Allergy.builder()
                                .allergyName(a.getAllergyName())
                                .user(user)
                                .build()
                );
            }
        }

        System.out.println(user.getHeight());

        userRepository.save(user);
        return new MyInfoCreateResponseDTO(user.getId());
    }
}
