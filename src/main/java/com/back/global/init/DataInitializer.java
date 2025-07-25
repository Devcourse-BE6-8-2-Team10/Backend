package com.back.global.init;

import com.back.domain.member.entity.Member;
import com.back.domain.member.entity.Role;
import com.back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationContext context;


    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            log.info("=====데이터 초기화 시작=====");
            DataInitializer self = context.getBean(DataInitializer.class);
            self.initAdmin();
            self.initUser();
            log.info("=====데이터 초기화 완료=====");
        };
    }


    // 관리자 계정 생성
    @Transactional
    public void initAdmin() {
        if (!memberRepository.existsByEmail("admin@admin.com")) {
            Member admin = Member.builder()
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("admin1234!"))
                    .name("관리자")
                    .role(Role.ADMIN) // enum 또는 String 값 사용
                    .build();
            memberRepository.save(admin);
            log.info("관리자 계정이 생성되었습니다.");
        }
    }

    // 일반 사용자 계정 생성
    @Transactional
    public void initUser() {
        createUserIfNotExists("user1@user.com", "사용자1");
        createUserIfNotExists("user2@user.com", "사용자2");
    }

    private void createUserIfNotExists(String email, String name) {
        if (!memberRepository.existsByEmail(email)) {
            Member user = Member.builder()
                    .email(email)
                    .password(passwordEncoder.encode("user1234!"))
                    .name(name)
                    .role(Role.USER)
                    .build();
            memberRepository.save(user);
            log.info("{} 계정이 생성되었습니다.", name);
        }
    }
}
