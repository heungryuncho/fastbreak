package com.portfolio.fastbreak.global.security.userdetails;

import com.portfolio.fastbreak.domain.member.entity.Member;
import com.portfolio.fastbreak.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. 요청받음 이메일로 DB에서 실제 회원 정보를 찾음
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 회원을 찾을 수 없습니다 : " + email));

        // 2. 찾은 회원 정보를 CustomUserDetails로 감싸서 리턴
        return new CustomUserDetails(member);
    }
}
