package ksh.backendserver.role.repository;

import ksh.backendserver.role.entity.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRoleRepository extends JpaRepository<JobRole,Long> {
}
