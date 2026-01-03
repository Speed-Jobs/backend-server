package ksh.backendserver.jobfield.service;

import ksh.backendserver.jobfield.entity.JobField;
import ksh.backendserver.jobfield.repository.JobFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobFieldService {

    private final JobFieldRepository jobFieldRepository;

    public List<JobField> findAll() {
        return jobFieldRepository.findAll();
    }
}
