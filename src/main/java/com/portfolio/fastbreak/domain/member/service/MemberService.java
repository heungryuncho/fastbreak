package com.portfolio.fastbreak.domain.member.service;

import com.portfolio.fastbreak.domain.member.dto.MemberRequest;
import com.portfolio.fastbreak.domain.member.dto.MemberResponse;
import com.portfolio.fastbreak.domain.member.entity.Member;
import com.portfolio.fastbreak.domain.member.repository.MemberRepository;
import com.portfolio.fastbreak.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public MemberResponse.MemberInfoResponse signup(MemberRequest.SignUpRequest request) {
        // 1. 이메일 중복 확인
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 2. Member 엔티티 생성 및 저장
        Member member = Member.builder()
                .email(request.email())
                .password(request.password())
                .name(request.name())
                .build();

        Member savedMember = memberRepository.save(member);

        // 3. Response DTO로 변환하여 저장
        return new MemberResponse.MemberInfoResponse(
                savedMember.getId(),
                savedMember.getEmail(),
                savedMember.getName()
        );
    }

    @Transactional(readOnly = true)
    public MemberResponse.LoginResponse login(MemberRequest.LoginRequest request) {
        // 1. 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 일치 여부 확인
        if (!member.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 로그인 성공 시 JWT 토큰 발급
        String token = jwtUtil.generateToken(member.getEmail());

        // 4. 발급된 토큰 반환
        return new MemberResponse.LoginResponse(member.getEmail(), token);
    }
}
