package ksh.backendserver.member.entity;

import jakarta.persistence.*;
import ksh.backendserver.BaseEntity;
import ksh.backendserver.member.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update member set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean notificationEnabled;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;
}
