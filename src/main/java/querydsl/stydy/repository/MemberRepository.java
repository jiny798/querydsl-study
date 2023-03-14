package querydsl.stydy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import querydsl.stydy.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long>,MemberRepositoryCustom {
    List<Member> findByUsername(String username);

}
