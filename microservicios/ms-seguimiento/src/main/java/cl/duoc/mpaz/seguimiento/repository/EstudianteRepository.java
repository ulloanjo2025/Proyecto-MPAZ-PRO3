package cl.duoc.mpaz.seguimiento.repository;

import cl.duoc.mpaz.seguimiento.model.Estudiante;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
