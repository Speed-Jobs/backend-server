package ksh.backendserver.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Job Posting Aggregation API")
                .description("채용 공고 수집 및 관리를 위한 REST API 문서입니다.")
                .version("v1.0.0"))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("로컬 개발 서버"),
                new Server()
                    .url("https://speedjobs-spring.skala25a.project.skala-ai.com")
                    .description("운영 서버")
            ));
    }
}
