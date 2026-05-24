package pt.ubi.gruposd.loja.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configura o CORS para a API permitir pedidos vindos dos URL do frontend em desenvolvimento (Vite na porta 5173 e Docker na porta 3000), e ativa allowCredentials para que os cookies HttpOnly de autenticação sejam enviados nos pedidos cross-origin.
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:5173", "http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
