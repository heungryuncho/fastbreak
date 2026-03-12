package com.portfolio.fastbreak.domain.member.controller;

import com.portfolio.fastbreak.domain.member.dto.MemberRequest;
import com.portfolio.fastbreak.domain.member.dto.MemberResponse;
import com.portfolio.fastbreak.domain.member.service.MemberService;
import com.portfolio.fastbreak.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ApiResponse<MemberResponse.MemberInfoResponse> signup(@RequestBody MemberRequest.SignUpRequest request) {
        MemberResponse.MemberInfoResponse response = memberService.signup(request);
        return ApiResponse.success(response, "회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ApiResponse<MemberResponse.LoginResponse> login(@RequestBody MemberRequest.LoginRequest request) {
        MemberResponse.LoginResponse response = memberService.login(request);
        return ApiResponse.success(response, "로그인 성공");
    }

}
