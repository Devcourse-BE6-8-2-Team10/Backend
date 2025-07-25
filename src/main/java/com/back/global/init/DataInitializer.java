package com.back.global.init;

import com.back.domain.member.entity.Member;
import com.back.domain.member.entity.Role;
import com.back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationContext context;


    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            DataInitializer self = context.getBean(DataInitializer.class);
            self.initAdmin();
            self.initUser();
        };
    }


    // 관리자 계정 생성
    @Transactional
    public void initAdmin() {
        if (!memberRepository.existsByEmail("admin@admin.com")) {
            Member admin = Member.builder()
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("admin"))
                    .name("관리자")
                    .role(Role.ADMIN) // enum 또는 String 값 사용
                    .build();
            memberRepository.save(admin);
        }
    }

    // 일반 사용자 계정 생성
    @Transactional
    public void initUser() {
        if (!memberRepository.existsByEmail("user1@user.com")) {
            Member user = Member.builder()
                    .email("user1@user.com")
                    .password(passwordEncoder.encode("user"))
                    .name("사용자1")
                    .role(Role.USER)
                    .build();
            memberRepository.save(user);
        }
        if (!memberRepository.existsByEmail("user2@user.com")) {
            Member user = Member.builder()
                    .email("user2@user.com")
                    .password(passwordEncoder.encode("user"))
                    .name("사용자2")
                    .role(Role.USER)
                    .build();
            memberRepository.save(user);
        }
    }
}
