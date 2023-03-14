package querydsl.stydy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import querydsl.stydy.dto.MemberSearchCondition;
import querydsl.stydy.dto.MemberTeamDto;
import querydsl.stydy.entity.Member;
import querydsl.stydy.repository.MemberJpaRepository;
import querydsl.stydy.repository.MemberRepository;
import querydsl.stydy.repository.MemberTestRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;

    private final MemberTestRepository memberTestRepository;
    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition){
        return memberJpaRepository.searchByParam(condition);
    }

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition,
                                              Pageable pageable) {
        return memberRepository.searchPageSimple(condition, pageable);
    }
    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition condition,
                                              Pageable pageable) {
        return memberRepository.searchPageComplex(condition, pageable);
    }
    @GetMapping("/v4/members")
    public Page<Member> searchMemberV4(MemberSearchCondition condition,
                                       Pageable pageable) {
        return memberTestRepository.applyPagination2(condition, pageable);
    }

    @GetMapping("/v5/members")
    public List<Member> searchMemberV5(MemberSearchCondition condition,
                                       Pageable pageable) {
        return memberTestRepository.basicSelect();
    }
}
