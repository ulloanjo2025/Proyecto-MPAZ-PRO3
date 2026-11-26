package cl.duoc.mpaz.seguimiento.controller;

import cl.duoc.mpaz.seguimiento.model.Intento;
import cl.duoc.mpaz.seguimiento.repository.IntentoRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Intentos (evaluaciones) de los estudiantes: máximo 2 por asignatura
@RestController
@RequestMapping("/api/intentos")
public class IntentoController {

    private static final int MAXIMO_INTENTOS = 2;

    private final IntentoRepository intentos;

    public IntentoController(IntentoRepository intentos) {
        this.intentos = intentos;
    }

    @GetMapping
    public List<Intento> listar(@RequestParam(required = false) String student) {
        if (student == null || student.isBlank()) {
            return intentos.findAll(Sort.by("id"));
        }
        return intentos.findByStudentNameIgnoreCaseOrderByIdAsc(student);
    }

    @PostMapping
    public ResponseEntity<Intento> registrar(@Valid @RequestBody Intento intento) {
        long previos = intentos.countByStudentNameIgnoreCaseAndSubjectId(intento.getStudentName(), intento.getSubjectId());
        if (previos >= MAXIMO_INTENTOS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El estudiante ya usó los " + MAXIMO_INTENTOS + " intentos de esta asignatura");
        }
        intento.setId(null);
        intento.setCreatedAt(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(intentos.save(intento));
    }

    // Desbloquea una asignatura: borra los intentos del estudiante en esa asignatura
    @DeleteMapping
    public ResponseEntity<Void> desbloquear(@RequestParam String student, @RequestParam String subject) {
        intentos.deleteByStudentNameIgnoreCaseAndSubjectId(student, subject);
        return ResponseEntity.noContent().build();
    }
}
