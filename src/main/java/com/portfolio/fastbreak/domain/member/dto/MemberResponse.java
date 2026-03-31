package com.portfolio.fastbreak.domain.member.dto;

public class MemberResponse {
    public record MemberInfoResponse(
            Long id,
            String email,
            String name
    ) {}

    public record LoginResponse(
            String email,
            String token
    ) {}
}
