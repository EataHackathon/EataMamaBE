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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ConditionRepository conditionRepository;
    private final AllergyRepository allergyRepository;

    @Transactional(readOnly = true)
    public MyInfoGetResponseDTO getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CustomException.notFound("유저를 찾을 수 없습니다"));

        List<ConditionDto> conditionDtos = conditionRepository.findAllByUserId(userId).stream()
                .map(c -> new ConditionDto(c.getConditionId(), c.getDiseaseName()))
                .toList();

        List<AllergyDto> allergyDtos = allergyRepository.findAllByUserId(userId).stream()
                .map(a -> new AllergyDto(a.getAllergyId(), a.getAllergyName()))
                .toList();

        return new MyInfoGetResponseDTO(
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

    @Transactional
    public MyInfoUpdateResponseDTO patchMyInfo(Long userId, MyInfoUpdateRequestDTO req) {
        User user = userRepository.findWithConditionsAndAllergiesById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + userId));

        // 숫자 필드: 값 있을 때만 수정
        user.updateHealth(req.getHeight(), req.getWeight(), req.getWeek());

        // 질환: 수정 + 추가 + 삭제(요청에 빠진 건 제거)
        if (req.getConditions() != null) {
            Map<Long, Condition> current = user.getConditions().stream()
                    .filter(c -> c.getConditionId() != null)
                    .collect(Collectors.toMap(Condition::getConditionId, Function.identity()));
            Set<Long> keep = new HashSet<>();

            for (ConditionDto dto : req.getConditions()) {
                if (dto == null) continue;
                Long id = dto.getConditionId();
                String name = dto.getDiseaseName();

                if (id != null) { // 수정
                    Condition c = current.get(id);
                    if (c == null) throw new IllegalArgumentException("존재/소유하지 않는 conditionId=" + id);
                    if (name != null && !name.isBlank() && !Objects.equals(c.getDiseaseName(), name)) {
                        c.setDiseaseName(name); // 더티체킹 UPDATE
                    }
                    keep.add(id);
                } else if (name != null && !name.isBlank()) { // 추가
                    user.addCondition(Condition.builder()
                            .diseaseName(name)
                            .build()); // 더티체킹 INSERT
                }
            }
            // 삭제: 요청에 없는 기존 항목 제거
            user.getConditions().removeIf(c -> c.getConditionId() != null && !keep.contains(c.getConditionId()));
        }

        // 알레르기: 수정 + 추가 + 삭제
        if (req.getAllergies() != null) {
            Map<Long, Allergy> current = user.getAllergies().stream()
                    .filter(a -> a.getAllergyId() != null)
                    .collect(Collectors.toMap(Allergy::getAllergyId, Function.identity()));
            Set<Long> keep = new HashSet<>();

            for (AllergyDto dto : req.getAllergies()) {
                if (dto == null) continue;
                Long id = dto.getAllergyId();
                String name = dto.getAllergyName();

                if (id != null) { // 수정
                    Allergy a = current.get(id);
                    if (a == null) throw new IllegalArgumentException("존재/소유하지 않는 allergyId=" + id);
                    if (name != null && !name.isBlank() && !Objects.equals(a.getAllergyName(), name)) {
                        a.setAllergyName(name); // 더티체킹 UPDATE
                    }
                    keep.add(id);
                } else if (name != null && !name.isBlank()) { // 추가
                    user.addAllergy(Allergy.builder()
                            .allergyName(name)
                            .build()); // 더티체킹 INSERT
                }
            }
            user.getAllergies().removeIf(a -> a.getAllergyId() != null && !keep.contains(a.getAllergyId()));
        }

        return new MyInfoUpdateResponseDTO(user.getId());
    }
}
