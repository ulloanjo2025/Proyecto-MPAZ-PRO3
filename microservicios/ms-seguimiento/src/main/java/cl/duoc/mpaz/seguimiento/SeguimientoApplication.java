package cl.duoc.mpaz.seguimiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Microservicio de seguimiento: estudiantes (CRUD) e intentos de las evaluaciones
@SpringBootApplication
public class SeguimientoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeguimientoApplication.class, args);
    }
}
