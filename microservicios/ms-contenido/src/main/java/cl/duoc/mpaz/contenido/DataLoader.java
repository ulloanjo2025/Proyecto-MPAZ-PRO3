package cl.duoc.mpaz.contenido;

import cl.duoc.mpaz.contenido.model.ContenidoResponse;
import cl.duoc.mpaz.contenido.repository.ObjetivoRepository;
import cl.duoc.mpaz.contenido.repository.PreguntaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

// Al encender el servicio por primera vez carga los 8 OA y las 24 preguntas desde contenido.json
@Component
public class DataLoader implements CommandLineRunner {

    private final ObjetivoRepository objetivos;
    private final PreguntaRepository preguntas;
    private final ObjectMapper mapper;

    public DataLoader(ObjetivoRepository objetivos, PreguntaRepository preguntas, ObjectMapper mapper) {
        this.objetivos = objetivos;
        this.preguntas = preguntas;
        this.mapper = mapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (objetivos.count() > 0) {
            return;
        }
        try (InputStream in = new ClassPathResource("contenido.json").getInputStream()) {
            ContenidoResponse contenido = mapper.readValue(in, ContenidoResponse.class);
            objetivos.saveAll(contenido.objectives());
            preguntas.saveAll(contenido.questions());
        }
    }
}
