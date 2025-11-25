package ksh.backendserver.role.entity;

import jakarta.persistence.*;
import ksh.backendserver.BaseEntity;
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
@SQLDelete(sql = "update industry set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class Industry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Long positionId;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;
}
