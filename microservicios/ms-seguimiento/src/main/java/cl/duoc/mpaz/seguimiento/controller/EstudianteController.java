package cl.duoc.mpaz.seguimiento.controller;

import cl.duoc.mpaz.seguimiento.model.Estudiante;
import cl.duoc.mpaz.seguimiento.repository.EstudianteRepository;
import cl.duoc.mpaz.seguimiento.repository.IntentoRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// CRUD de estudiantes: Create (POST), Read (GET), Update (PUT) y Delete (DELETE)
@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteRepository estudiantes;
    private final IntentoRepository intentos;

    public EstudianteController(EstudianteRepository estudiantes, IntentoRepository intentos) {
        this.estudiantes = estudiantes;
        this.intentos = intentos;
    }

    @GetMapping
    public List<Estudiante> listar() {
        return estudiantes.findAll(Sort.by("fullName"));
    }

    @GetMapping("/{id}")
    public Estudiante obtener(@PathVariable Long id) {
        return buscar(id);
    }

    @PostMapping
    public ResponseEntity<Estudiante> crear(@Valid @RequestBody Estudiante estudiante) {
        estudiante.setId(null);
        estudiante.setUsername(estudiante.getUsername().trim().toUpperCase());
        estudiante.setFullName(estudiante.getFullName().trim());
        if (estudiantes.existsByUsernameIgnoreCase(estudiante.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un estudiante con el código " + estudiante.getUsername());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(estudiantes.save(estudiante));
    }

    // Actualiza el nombre y el PIN (el código del estudiante no cambia)
    @PutMapping("/{id}")
    public Estudiante actualizar(@PathVariable Long id, @Valid @RequestBody Estudiante datos) {
        Estudiante existente = buscar(id);
        String nombreAnterior = existente.getFullName();
        existente.setFullName(datos.getFullName().trim());
        existente.setPin(datos.getPin());
        Estudiante guardado = estudiantes.save(existente);
        if (!nombreAnterior.equalsIgnoreCase(guardado.getFullName())) {
            intentos.renombrarEstudiante(nombreAnterior, guardado.getFullName());
        }
        return guardado;
    }

    // Elimina al estudiante y sus intentos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Estudiante existente = buscar(id);
        intentos.deleteByStudentNameIgnoreCase(existente.getFullName());
        estudiantes.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Ingreso del estudiante: {"username": "JP01", "pin": "1234"}
    @PostMapping("/login")
    public Estudiante login(@RequestBody Map<String, String> credenciales) {
        String codigo = credenciales.getOrDefault("username", "").trim();
        String pin = credenciales.getOrDefault("pin", "").trim();
        return estudiantes.findByUsernameIgnoreCase(codigo)
                .filter(e -> e.getPin().equals(pin))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Código o PIN incorrecto"));
    }

    private Estudiante buscar(Long id) {
        return estudiantes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el estudiante " + id));
    }
}
