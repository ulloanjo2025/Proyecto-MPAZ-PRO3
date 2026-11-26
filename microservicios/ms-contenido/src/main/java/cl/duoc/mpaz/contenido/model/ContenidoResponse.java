package cl.duoc.mpaz.contenido.model;

import java.util.List;

// Respuesta de GET /api/contenido: todo el contenido en un solo JSON (lo consume la app con Retrofit)
public record ContenidoResponse(int version, List<Objetivo> objectives, List<Pregunta> questions) {
}
