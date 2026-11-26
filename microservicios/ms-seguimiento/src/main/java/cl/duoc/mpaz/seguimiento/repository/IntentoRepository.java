package cl.duoc.mpaz.seguimiento.repository;

import cl.duoc.mpaz.seguimiento.model.Intento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface IntentoRepository extends JpaRepository<Intento, Long> {

    List<Intento> findByStudentNameIgnoreCaseOrderByIdAsc(String studentName);

    long countByStudentNameIgnoreCaseAndSubjectId(String studentName, String subjectId);

    @Transactional
    long deleteByStudentNameIgnoreCaseAndSubjectId(String studentName, String subjectId);

    @Transactional
    long deleteByStudentNameIgnoreCase(String studentName);

    // Si el docente cambia el nombre de un estudiante, sus intentos siguen asociados a él
    @Modifying
    @Transactional
    @Query("update Intento i set i.studentName = :newName where lower(i.studentName) = lower(:oldName)")
    int renombrarEstudiante(@Param("oldName") String oldName, @Param("newName") String newName);
}
