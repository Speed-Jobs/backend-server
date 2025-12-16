package ksh.backendserver.skill.service;

import ksh.backendserver.skill.entity.Skill;
import ksh.backendserver.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<Skill> findMajorSkills() {
        return skillRepository.findByIsMajor(true);
    }
}
