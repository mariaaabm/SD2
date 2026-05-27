package pt.ubi.gruposd.loja;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

// Ponto de entrada da aplicação Spring Boot da SportFlow.
// @SpringBootApplication ativa o component scan, auto-configuração e registo de beans automaticamente.
// @EnableAsync permite que métodos anotados com @Async (como o envio de email) sejam executados
// num thread separado, sem bloquear a resposta HTTP do checkout.
@SpringBootApplication
@EnableAsync
public class Application {

    // Arranca o servidor embebido Tomcat e inicializa o contexto Spring com todos os beans.
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Regista a documentação Swagger/OpenAPI disponível em /swagger-ui.html e /v3/api-docs.
    // É útil durante o desenvolvimento para testar os endpoints sem precisar do frontend.
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SportFlow API")
                .version("0.0.1")
                .description("REST API para loja online de artigos desportivos."));
    }
}
