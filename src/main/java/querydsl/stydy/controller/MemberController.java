package querydsl.stydy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import querydsl.stydy.dto.MemberSearchCondition;
import querydsl.stydy.dto.MemberTeamDto;
import querydsl.stydy.repository.MemberJpaRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberJpaRepository memberJpaRepository;
    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition)
    {
        return memberJpaRepository.searchByParam(condition);
    }
}
