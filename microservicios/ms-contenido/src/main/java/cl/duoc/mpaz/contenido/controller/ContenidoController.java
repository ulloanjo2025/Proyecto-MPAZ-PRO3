package cl.duoc.mpaz.contenido.controller;

import cl.duoc.mpaz.contenido.model.ContenidoResponse;
import cl.duoc.mpaz.contenido.model.Objetivo;
import cl.duoc.mpaz.contenido.model.Pregunta;
import cl.duoc.mpaz.contenido.repository.ObjetivoRepository;
import cl.duoc.mpaz.contenido.repository.PreguntaRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Endpoints REST del contenido: CRUD de Objetivos de Aprendizaje y de preguntas.
@RestController
@RequestMapping("/api")
public class ContenidoController {

    private static final Set<String> ESTADOS = Set.of("PENDIENTE", "AUTORIZADO", "RECHAZADO");

    private final ObjetivoRepository objetivos;
    private final PreguntaRepository preguntas;

    public ContenidoController(ObjetivoRepository objetivos, PreguntaRepository preguntas) {
        this.objetivos = objetivos;
        this.preguntas = preguntas;
    }

    // ---------- Todo el contenido (lo usa la app para sincronizar) ----------

    @GetMapping("/contenido")
    public ContenidoResponse contenido() {
        return new ContenidoResponse(1, objetivos.findAll(Sort.by("code")), preguntas.findAll(Sort.by("id")));
    }

    // ---------- Objetivos de Aprendizaje ----------

    @GetMapping("/objetivos")
    public List<Objetivo> listarObjetivos() {
        return objetivos.findAll(Sort.by("code"));
    }

    @GetMapping("/objetivos/{code}")
    public Objetivo obtenerObjetivo(@PathVariable String code) {
        return objetivos.findById(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el OA " + code));
    }

    @PostMapping("/objetivos")
    public ResponseEntity<Objetivo> crearObjetivo(@Valid @RequestBody Objetivo objetivo) {
        if (objetivos.existsById(objetivo.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe el OA " + objetivo.getCode());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(objetivos.save(objetivo));
    }

    @PutMapping("/objetivos/{code}")
    public Objetivo actualizarObjetivo(@PathVariable String code, @Valid @RequestBody Objetivo datos) {
        if (!objetivos.existsById(code)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el OA " + code);
        }
        datos.setCode(code);
        return objetivos.save(datos);
    }

    // Cambia solo el estado de autorización: {"status": "AUTORIZADO"}
    @PatchMapping("/objetivos/{code}/estado")
    public Objetivo cambiarEstado(@PathVariable String code, @RequestBody Map<String, String> body) {
        String estado = body.get("status");
        if (estado == null || !ESTADOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado debe ser PENDIENTE, AUTORIZADO o RECHAZADO");
        }
        Objetivo objetivo = obtenerObjetivo(code);
        objetivo.setStatus(estado);
        return objetivos.save(objetivo);
    }

    // Elimina el OA y también sus preguntas
    @Transactional
    @DeleteMapping("/objetivos/{code}")
    public ResponseEntity<Void> eliminarObjetivo(@PathVariable String code) {
        if (!objetivos.existsById(code)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el OA " + code);
        }
        preguntas.deleteByObjectiveCode(code);
        objetivos.deleteById(code);
        return ResponseEntity.noContent().build();
    }

    // ---------- Preguntas ----------

    @GetMapping("/preguntas")
    public List<Pregunta> listarPreguntas(@RequestParam(required = false) String objetivo) {
        if (objetivo == null || objetivo.isBlank()) {
            return preguntas.findAll(Sort.by("id"));
        }
        return preguntas.findByObjectiveCode(objetivo, Sort.by("difficulty", "id"));
    }

    @GetMapping("/preguntas/{id}")
    public Pregunta obtenerPregunta(@PathVariable Integer id) {
        return preguntas.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la pregunta " + id));
    }

    @PostMapping("/preguntas")
    public ResponseEntity<Pregunta> crearPregunta(@Valid @RequestBody Pregunta pregunta) {
        if (!objetivos.existsById(pregunta.getObjectiveCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El OA " + pregunta.getObjectiveCode() + " no existe");
        }
        if (pregunta.getId() == null) {
            pregunta.setId(siguienteId());
        } else if (preguntas.existsById(pregunta.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe la pregunta " + pregunta.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(preguntas.save(pregunta));
    }

    @PutMapping("/preguntas/{id}")
    public Pregunta actualizarPregunta(@PathVariable Integer id, @Valid @RequestBody Pregunta datos) {
        if (!preguntas.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la pregunta " + id);
        }
        datos.setId(id);
        return preguntas.save(datos);
    }

    @DeleteMapping("/preguntas/{id}")
    public ResponseEntity<Void> eliminarPregunta(@PathVariable Integer id) {
        if (!preguntas.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la pregunta " + id);
        }
        preguntas.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private int siguienteId() {
        return preguntas.findAll().stream().mapToInt(Pregunta::getId).max().orElse(0) + 1;
    }
}
