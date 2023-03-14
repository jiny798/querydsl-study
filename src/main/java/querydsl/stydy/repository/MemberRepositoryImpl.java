package querydsl.stydy.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import querydsl.stydy.dto.MemberSearchCondition;
import querydsl.stydy.dto.MemberTeamDto;
import querydsl.stydy.dto.QMemberTeamDto;
import querydsl.stydy.entity.Member;
import querydsl.stydy.entity.QMember;
import querydsl.stydy.entity.QTeam;

import javax.persistence.EntityManager;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static querydsl.stydy.entity.QMember.member;
import static querydsl.stydy.entity.QTeam.team;

public class MemberRepositoryImpl extends QuerydslRepositorySupport implements MemberRepositoryCustom{
    private final JPAQueryFactory queryFactory;
//
//    public MemberRepositoryImpl(EntityManager em){
//        this.queryFactory = new JPAQueryFactory(em);
//    }
    public MemberRepositoryImpl(){
        super(Member.class);
        this.queryFactory = new JPAQueryFactory(getEntityManager());
    }
    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition){

        EntityManager entityManager = getEntityManager() ; //필요하면 호출해서 사용가능

        List<MemberTeamDto> result = from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name))
                .fetch();
        return result;
//        return queryFactory
//                .select(new QMemberTeamDto(
//                        member.id,
//                        member.username,
//                        member.age,
//                        team.id,
//                        team.name
//                )).from(member)
//                .leftJoin(member.team, team)
//                .where(usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe()))
//                .fetch();
    }

    private BooleanExpression usernameEq(String username) {
        return !hasText(username) ? null : member.username.eq(username);
    }
    private BooleanExpression teamNameEq(String teamName) {
        return !hasText(teamName) ? null : team.name.eq(teamName);
    }
    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe == null ? null : member.age.goe(ageGoe);
    }
    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe == null ? null : member.age.loe(ageLoe);
    }

    /**
     * 단순한 페이징, fetchResults() 사용
     */
    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition,
                                                Pageable pageable) {
        QueryResults<MemberTeamDto> results = queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MemberTeamDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple2(MemberSearchCondition condition,
                                                Pageable pageable) {
        JPQLQuery<MemberTeamDto> jpqlQuery = from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name));
        JPQLQuery<MemberTeamDto> query = getQuerydsl().applyPagination(pageable,jpqlQuery); //offset, limit 적용
        QueryResults<MemberTeamDto> results = query.fetchResults();

        List<MemberTeamDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition,Pageable pageable){
        JPAQuery<MemberTeamDto> query = queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name
                )).from(member)
                .leftJoin(member.team,team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                        )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(member.getType(),
                    member.getMetadata());
            query.orderBy(new OrderSpecifier<>(o.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(o.getProperty())));
        }
        List<MemberTeamDto> content = query.fetch();

//        long total = queryFactory
//                .select(member)
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe()))
//                .fetch().size();
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );


        //return new PageImpl<>(content,pageable,total);
        return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
    }
}
