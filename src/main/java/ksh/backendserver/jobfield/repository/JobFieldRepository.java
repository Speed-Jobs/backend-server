package ksh.backendserver.jobfield.repository;

import ksh.backendserver.jobfield.entity.JobField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobFieldRepository extends JpaRepository<JobField, Long> {

    Optional<JobField> findByName(String name);
}
