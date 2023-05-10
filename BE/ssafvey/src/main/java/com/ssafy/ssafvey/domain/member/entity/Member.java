package com.ssafy.ssafvey.domain.member.entity;

import com.ssafy.ssafvey.domain.shop.entity.Order;
import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci")
    private String name;

    @Nullable
    private int age;

    @Nullable
    private String password;

    @Nullable
    private String email;

    @Nullable
    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    @OneToMany(mappedBy = "member")
    @ToString.Exclude
    private List<MemberJob> memberJobs = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    @ToString.Exclude
    private List<MemberSurvey> memberSurveys = new ArrayList<>();

    @Nullable
    @Column(columnDefinition = "int default 0")
    private int couponCount;

    @Nullable
    private String refreshToken;

    @Nullable
    private Boolean isActive;
    @NotNull
    private Boolean isRegistered;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @Column(columnDefinition = "int default 0")
    private int point;


    @ManyToMany
    @JoinTable(
            name = "user_authority_join",
            joinColumns = {@JoinColumn(name = "member_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    @ToString.Exclude
    private Set<Authority> authorities;
}