package querydsl.stydy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import querydsl.stydy.dto.MemberSearchCondition;
import querydsl.stydy.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCondition condition);

    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition,
                                         Pageable pageable);
    Page<MemberTeamDto> searchPageSimple2(MemberSearchCondition condition,
                                         Pageable pageable);
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition,
                                          Pageable pageable);
}
