package pt.ubi.gruposd.loja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// Ponto de entrada da aplicação Spring Boot que arranca o contexto, ativa a deteção automática de componentes em todo o pacote pt.ubi.gruposd.loja e habilita o processamento assíncrono para o EmailService poder enviar emails em background sem bloquear a resposta do checkout.
@SpringBootApplication
@EnableAsync
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

