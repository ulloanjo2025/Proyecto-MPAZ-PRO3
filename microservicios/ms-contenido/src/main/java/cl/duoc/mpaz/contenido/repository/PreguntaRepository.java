package cl.duoc.mpaz.contenido.repository;

import cl.duoc.mpaz.contenido.model.Pregunta;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PreguntaRepository extends JpaRepository<Pregunta, Integer> {

    List<Pregunta> findByObjectiveCode(String objectiveCode, Sort sort);

    @Transactional
    long deleteByObjectiveCode(String objectiveCode);
}
