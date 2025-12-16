package ksh.backendserver.group.service;

import ksh.backendserver.group.entity.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionRepository {

    private final PositionRepository positionRepository;

    public List<Position> findAll() {
        return positionRepository.findAll();
    }
}
