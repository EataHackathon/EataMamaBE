package com.eata.eatamamabe.service;

import com.eata.eatamamabe.dto.user.AllergyDto;
import com.eata.eatamamabe.dto.user.ConditionDto;
import com.eata.eatamamabe.dto.user.MyInfoResponseDTO;
import com.eata.eatamamabe.entity.User;
import com.eata.eatamamabe.repository.AllergyRepository;
import com.eata.eatamamabe.repository.ConditionRepository;
import com.eata.eatamamabe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final ConditionRepository conditionRepository;
    private final AllergyRepository allergyRepository;

    public MyInfoResponseDTO getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + userId));

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
}
