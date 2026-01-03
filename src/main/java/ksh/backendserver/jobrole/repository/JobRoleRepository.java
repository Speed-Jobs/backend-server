package ksh.backendserver.jobrole.repository;

import ksh.backendserver.jobrole.entity.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRoleRepository extends JpaRepository<JobRole, Long> {
    List<JobRole> findByIdIn(List<Long> ids);
}
