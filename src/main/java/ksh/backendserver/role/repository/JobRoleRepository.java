package ksh.backendserver.role.repository;

import ksh.backendserver.role.entity.Industry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRoleRepository extends JpaRepository<Industry, Long> {
}
