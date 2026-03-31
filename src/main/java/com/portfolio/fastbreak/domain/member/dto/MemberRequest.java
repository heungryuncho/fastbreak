package com.portfolio.fastbreak.domain.member.dto;

public class MemberRequest {
    public record SignUpRequest(
            String email,
            String password,
            String name
    ) {}

    public record LoginRequest(
            String email,
            String password
    ) {}
}
