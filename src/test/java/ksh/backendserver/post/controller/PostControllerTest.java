package ksh.backendserver.post.controller;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.enums.ExperienceLevel;
import ksh.backendserver.post.model.PostSummary;
import ksh.backendserver.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PostService postService;

    @DisplayName("회사 id 리스트를 넘겨 최근 공고 간단 조회를 할 수 있다.")
    @Test
    void recentPostSummaries1() throws Exception {
        //given
        Company company1 = Company.builder()
            .id(1L)
            .name("네이버")
            .build();

        Company company2 = Company.builder()
            .id(2L)
            .name("카카오")
            .build();

        List<PostSummary> mockSummaries = List.of(
            new PostSummary(1L, "백엔드 개발자 모집", "Backend Developer",
                ExperienceLevel.JUNIOR, 15, company1),
            new PostSummary(2L, "프론트엔드 개발자 모집", "Frontend Developer",
                ExperienceLevel.SENIOR, 20, company2)
        );

        given(postService.findRecentPostSummaries(any(), any(Integer.class)))
            .willReturn(mockSummaries);

        //when & then
        mockMvc.perform(get("/api/v1/posts/simple")
                .param("companyIds", "1,2")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.code").value("OK"))
            .andExpect(jsonPath("$.data.posts").isArray())
            .andExpect(jsonPath("$.data.posts.length()").value(2))
            .andExpect(jsonPath("$.data.posts[0].id").value(1))
            .andExpect(jsonPath("$.data.posts[0].title").value("백엔드 개발자 모집"))
            .andExpect(jsonPath("$.data.posts[0].role").value("Backend Developer"))
            .andExpect(jsonPath("$.data.posts[0].experience").value("JUNIOR"))
            .andExpect(jsonPath("$.data.posts[0].daysLeft").value(15))
            .andExpect(jsonPath("$.data.posts[1].id").value(2))
            .andExpect(jsonPath("$.data.posts[1].title").value("프론트엔드 개발자 모집"))
            .andExpect(jsonPath("$.data.posts[1].role").value("Frontend Developer"));
    }
}
