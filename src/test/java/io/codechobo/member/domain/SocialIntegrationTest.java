package io.codechobo.member.domain;

import io.codechobo.member.domain.repository.MemberRepository;
import io.codechobo.member.domain.repository.SocialRepository;
import io.codechobo.member.domain.support.MemberDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

/**
 * @author loustler
 * @since 10/02/2016 10:21
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(value = "test")
public class SocialIntegrationTest {
    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Social social;

    private Date now;

    @Before
    public void setUp() {
        this.member = memberFactory();
        this.social = socialFactory();

        this.now = Calendar.getInstance().getTime();

        List<Social> socialList = new ArrayList<>();
        socialList.add(this.social);
        this.member.setSocials(socialList);

        this.member = this.memberRepository.save(this.member);
    }

    @Test
    public void 설정테스트_config_test() {
    }

    @Test
    public void 소셜추가_another_social_add() {
        // given
        Social anotherSocial = new Social();
        anotherSocial.setMember(memberRepository.findOne(this.member.getSeq()));
        anotherSocial.setToken("accessToken2");
        anotherSocial.setType("google");

        // when
        Social result = this.socialRepository.save(anotherSocial);

        // then
        assertNotNull(result);
        assertThat(result.getMember().getSeq(), is(this.member.getSeq()));
        this.member.getSocials().add(result);

        Iterator<Social> socialIterator = this.member.getSocials().iterator();
        while (socialIterator.hasNext())
            assertThat(socialIterator.next().getMember().getSeq(), is(this.member.getSeq()));
    }

    @Test
    public void 소셜가져오기_social_find() {

        // given
        Member newMember = new Member(
                new MemberDto.Builder()
                .id("id1")
                .password("password2")
                .email("email@gmail.com")
                .nickName("nickName3")
                .regiDate(this.now)
                .point(new Integer(0))
                .build()
        );

        newMember = this.memberRepository.save(newMember);
        newMember.setSocials(new ArrayList<>());

        // then
        Social social1 = new Social();
        social1.setMember(newMember);
        social1.setType("stackoverflow");
        social1.setToken("accessToken3");

        newMember.getSocials().add(social1);
        social1 = this.socialRepository.save(social1);

        // then
        assertNotNull(social1);

        Social find = this.socialRepository.findOne(social1.getSeq());
        assertNotNull(find);
        assertThat(social1.getSeq(), is(find.getSeq()));
    }

    private Member memberFactory() {
        return memberRepository.save(new Member(
                new MemberDto.Builder()
                .id("anyone")
                .password("password")
                .email("email@provider.com")
                .nickName("anonymouse")
                .regiDate(this.now)
                .point(new Integer(0))
                .build())
        );
    }

    private Social socialFactory() {
        Social social = new Social();
        social.setMember(this.member);
        social.setToken("2kd8fd7s9xkcfsl22kldkfysdf1");
        social.setType("github");

        return socialRepository.save(social);
    }
}
