package ksh.backendserver.post.service;

import ksh.backendserver.post.model.PostSummary;
import ksh.backendserver.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<PostSummary> findRecentPostSummaries(
        List<Long> companyIds,
        int size
    ) {
        return postRepository
            .findByIdInOrderByCreatedAtDesc(companyIds, size)
            .stream()
            .map(post -> PostSummary.from(post, LocalDate.now(clock)))
            .toList();

    }
}
