package ksh.backendserver.role.repository;

import ksh.backendserver.role.entity.Industry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndustryRepository extends JpaRepository<Industry, Long> {
    List<Industry> findByIdIn(List<Long> ids);
}